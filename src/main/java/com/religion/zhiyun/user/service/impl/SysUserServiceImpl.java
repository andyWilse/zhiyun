package com.religion.zhiyun.user.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.SysRoleMapper;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.dao.SysUserRoleRelMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserRoleEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.record.dao.OperateRecordMapper;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Value("${zy.password.default}")
    private String passwordDefault;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private OperateRecordMapper rmUserLogsInfoMapper;
    @Autowired
    private SysUserRoleRelMapper sysUserRoleRelMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RmFileService rmFileService;
    @Autowired
    private RmFileMapper rmFileMapper;
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;
    @Autowired
    private OperateRecordService operateRecordService;

    @Override
    public PageResponse getUsersByPage(Map<String, Object> map,String token){

        long code= ResultCode.FAILED.getCode();
        String message="系统用户查询";

        List<SysUserEntity> dataList=new ArrayList<>();
        long total=0l;
        try {
            //参数
            String identity = (String)map.get("identity");
            String userNm = (String)map.get("userNm");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            String venue = (String)map.get("venue");
            //分页
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //封装查询参数
            ParamsVo vo = this.getAuth(token);
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchOne(userNm);
            vo.setSearchTwo(identity);
            vo.setSearchThree(venue);
            dataList=sysUserMapper.getUsersByPage(vo);
            //转为中文
            if(null!=dataList && dataList.size()>0){
                for(int i=0;i<dataList.size();i++){
                    SysUserEntity sysUserEntity = dataList.get(i);
                    //身份信息转换
                    String ident = sysUserEntity.getIdentity();
                    String roleNm = sysRoleMapper.getRoleNm(ident);
                    sysUserEntity.setIdentity(roleNm);
                    sysUserEntity.setIdentityType(ident);
                }
            }
            //查询条数
            total=sysUserMapper.getTotal(vo);

            code= ResultCode.SUCCESS.getCode();
            message="系统用户查询成功";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        //rmUserLogsInfoService.savelogs( JsonUtils.objectTOJSONString(respPageBean), InfoEnums.USER_FIND.getName());

        return new PageResponse(code,message,total, dataList.toArray());
    }

    @Override
    public RespPageBean add(SysUserEntity sysUserEntity,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="用户新增";
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new Date().getTime());
            //电话不能重复
            long numTel = sysUserMapper.queryTelNum(sysUserEntity.getUserMobile());
            if(numTel>0l){
                throw new RuntimeException("电话号码："+sysUserEntity.getUserMobile()+"已被占用");
            }
           // message=this.checkData(sysUserEntity);

            sysUserEntity.setCreateTime(timestamp);
            sysUserEntity.setLastModifyTime(timestamp);
            Long maxCustCd = sysUserMapper.getMaxUserNbr();
            maxCustCd++;
            sysUserEntity.setUserNbr(String.valueOf(maxCustCd));

            //图片处理
            String picturesPath = sysUserEntity.getUserPhotoUrl();
            String picturesPathRemove = sysUserEntity.getPicturesPathRemove();
            //清理图片
            if(null!=picturesPathRemove && !picturesPathRemove.isEmpty()){
                rmFileService.deletePicture(picturesPathRemove);
                String[] split = picturesPathRemove.split(",");
                if(null!=split && split.length>0 ){
                    for (int i=0;i<split.length;i++){
                        String re = split[i];
                        if(picturesPath.contains(re)){
                            String replace = picturesPath.replace(re+",",  "");
                            picturesPath=replace;
                        }
                    }
                    //保存图片
                    sysUserEntity.setUserPhotoUrl(picturesPath);
                }
            }
            //新增用户
            sysUserMapper.add(sysUserEntity);

            //密码加密
            String passwords = sysUserEntity.getPasswords();
            int userId = sysUserEntity.getUserId();
            String userNbr = sysUserEntity.getUserNbr();
            //MD5加密,salt加密
            String pass = this.passwordSalt(String.valueOf(userId),passwords,userNbr);
            sysUserMapper.updatePassword(pass,userId,TimeTool.getYmdHms());

            //新增用户角色关系
            UserRoleEntity userRoleEntity=new UserRoleEntity();
            userRoleEntity.setUserId(String.valueOf(sysUserEntity.getUserId()));
            userRoleEntity.setRoleId(String.valueOf(sysUserEntity.getIdentity()));
            sysUserRoleRelMapper.add(userRoleEntity);

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            String login = this.getLogin(token);
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(sysUserEntity.getUserId()));
            vuMap.put("operateType", OperaEnums.user_add.getCode());
            vuMap.put("operateContent", JsonUtils.beanToJson(sysUserEntity));
            String operateDetail="用户："+sysUserEntity.getUserNm();
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code= ResultCode.SUCCESS.getCode();
            message="用户新增成功";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message="用户新增失败";
            e.printStackTrace();
        }
        return new RespPageBean(code,message);
    }

    @Override
    public PageResponse update(SysUserEntity sysUserEntity,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="数据更新";
        String result="/userIndex";
        try {
            //电话校验
            String userMobile = sysUserEntity.getUserMobile();//原电话
            String userMobileOrigin = sysUserEntity.getUserMobileOrigin();//现电话
            if(!userMobile.equals(userMobileOrigin)){
                //电话不能重复
                long numTel = sysUserMapper.queryTelNum(userMobile);
                if(numTel>0l){
                    throw new RuntimeException("电话号码："+sysUserEntity.getUserMobile()+"已被占用");
                }
            }
            //message=this.checkData(sysUserEntity);
            Timestamp timestamp = new Timestamp(new Date().getTime());
            sysUserEntity.setLastModifyTime(timestamp);

            //图片处理
            String picturesPath = sysUserEntity.getUserPhotoUrl();
            String picturesPathRemove = sysUserEntity.getPicturesPathRemove();
            //清理图片
            if(null!=picturesPathRemove && !picturesPathRemove.isEmpty()){
                rmFileService.deletePicture(picturesPathRemove);
                String[] split = picturesPathRemove.split(",");
                if(null!=split && split.length>0 ){
                    for (int i=0;i<split.length;i++){
                        String re = split[i];
                        if(picturesPath.contains(re)){
                            String replace = picturesPath.replace(re+",",  "");
                            picturesPath=replace;
                        }
                    }
                    //保存图片
                    sysUserEntity.setUserPhotoUrl(picturesPath);
                }
            }
            sysUserMapper.update(sysUserEntity);
            String login = this.getLogin(token);
            if(login.equals(userMobileOrigin) && !userMobileOrigin.equals(userMobile)){
                result="/";
            }

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(sysUserEntity.getUserId()));
            vuMap.put("operateType", OperaEnums.user_update.getCode());
            vuMap.put("operateContent", JsonUtils.beanToJson(sysUserEntity));
            String operateDetail="用户："+sysUserEntity.getUserNm();
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code= ResultCode.SUCCESS.getCode();
            message="用户更新成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="用户更新失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,result);

    }

    @Override
    public PageResponse updatePassword(Map<String, String> map,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="用户密码更改失败！";
        try {
            String oldPass = map.get("oldPass");
            String newPass = map.get("newPass");
            String surePass = map.get("surePass");
            //新密码输入校验
            if(!newPass.equals(surePass)){
                throw new RuntimeException("两次密码输入不一致，请重新输入确认密码！");
            }
            //HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            //String nbr = request.getHeader("login-name");

            //获取登录用户信息
            String login = this.getLogin(token);
            List<SysUserEntity> sysUserEntities = sysUserMapper.queryByTel(login);
            if(null==sysUserEntities || sysUserEntities.size()<1){
                throw new RuntimeException("用户登录信息丢失，请联系管理员！");
            }else if(sysUserEntities.size()>1){
                throw new RuntimeException("用户登录信息重复，请联系管理员！");
            }
            SysUserEntity sysUserEntity = sysUserEntities.get(0);
            int userId = sysUserEntity.getUserId();
            String userNbr = sysUserEntity.getUserNbr();
            //旧密码校验
            if(null!=sysUserEntity){
                String oldPassWord = this.passwordSalt(String.valueOf(userId), oldPass, userNbr);
                String passwordOld = sysUserEntity.getPasswords();
                if(!passwordOld.equals(oldPassWord)){
                    throw new RuntimeException("原密码错误，请重新输入！");
                }
            }else{
                throw new RuntimeException("用户登录信息丢失，请联系管理员！");
            }
            //更新密码
            String newPassWord = this.passwordSalt(String.valueOf(userId), surePass, userNbr);
            sysUserMapper.updatePassword(newPassWord,sysUserEntity.getUserId(), TimeTool.getYmdHms());

            code= ResultCode.SUCCESS.getCode();
            message="密码更改成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,message);
    }

    @Override
    public PageResponse modifyPassword(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="用户管理密码更改失败！";
        try {
            String newPass = (String) map.get("newPass");
            String surePass = (String) map.get("surePass");
            Integer userId = (Integer) map.get("userId");
            String userNbr = (String) map.get("userNbr");
            String userNm = (String) map.get("userNm");

            //新密码输入校验
            if(!newPass.equals(surePass)){
                throw new RuntimeException("两次密码输入不一致，请重新输入确认密码！");
            }
            //更新密码
            String newPassWord = this.passwordSalt(String.valueOf(userId), surePass, userNbr);
            sysUserMapper.updatePassword(newPassWord,userId, TimeTool.getYmdHms());

            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            String login = this.getLogin(token);
            vuMap.put("operator",login);
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(userId));
            vuMap.put("operateType", OperaEnums.user_password_update.getCode());
            vuMap.put("operateContent", "");
            String operateDetail="用户："+userNm;
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code= ResultCode.SUCCESS.getCode();
            message="密码更改成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,message);
    }

    @Override
    public void delete(int userId,String token) {
        //删除
        sysUserMapper.delete(userId);
        //增加日志信息
        SysUserEntity sysUserEntity = sysUserMapper.queryByUserId(String.valueOf(userId));
        Map<String,Object> vuMap=new HashMap<>();
        vuMap.put("operator",this.getLogin(token));
        vuMap.put("operateTime",new Date());
        vuMap.put("operateRef",String.valueOf(sysUserEntity.getUserId()));
        vuMap.put("operateType", OperaEnums.user_delete.getCode());
        vuMap.put("operateContent", JsonUtils.beanToJson(sysUserEntity));
        String operateDetail="用户："+sysUserEntity.getUserNm();
        vuMap.put("operateDetail",operateDetail);
        operateRecordService.addRecord(vuMap);
        //this.savelogs( "删除成功", InfoEnums.USER_DELETE.getName());

    }

    @Override
    public SysUserEntity queryByNbr(String userNbr) {
        return sysUserMapper.queryByNbr(userNbr);
    }

    @Override
    public SysUserEntity queryByName(String userNm) {
        return sysUserMapper.queryByName(userNm);
    }

    @Override
    public SysUserEntity queryByTel(String userMobile) {
        List<SysUserEntity> sysUserList = sysUserMapper.queryByTel(userMobile);
        if(null==sysUserList || sysUserList.size()<1){
            throw new RuntimeException("用户信息丢失！");
        }
        return sysUserList.get(0);
    }

    @Override
    public PageResponse getUserInfo(String token) {
        long code= ResultCode.FAILED.getCode();
        String message="获取登录用户信息";

        List<Map<String,Object>> list=new ArrayList<>();
        try {
            String login = this.getLogin(token);
            Map<String,Object> sysUserEntity = sysUserMapper.queryBySearch(login);
            if(null==sysUserEntity || sysUserEntity.size()<1){
                throw new RuntimeException("用户信息丢失！");
            }
            String userPhotoUrl = (String) sysUserEntity.get("userPhotoUrl");
            List<Map<String, Object>> fileUrl =new ArrayList<>();
            if(null!=userPhotoUrl && !userPhotoUrl.isEmpty()){
                fileUrl = rmFileMapper.getFileUrl(userPhotoUrl.split(","));
            }
            sysUserEntity.put("userPhotoUrl",fileUrl.toArray());
            list.add(sysUserEntity);
            code= ResultCode.SUCCESS.getCode();
            message="获取登录用户信息成功";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="获取登录用户信息失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
    }

    @Override
    public PageResponse getModifyUser(String userId) {
        long code= ResultCode.FAILED.getCode();
        String message="获取登录用户信息";

        List<Map<String, Object>> list=new ArrayList<>();
        try {
            //SysUserEntity sysUserEntity = sysUserMapper.queryByUserId(userId);
            Map<String, Object> uMap = sysUserMapper.getUserId(userId);

            if(null!=uMap){
                //。图片回显
                //String picturesPath = sysUserEntity.getUserPhotoUrl();
                String picturesPath = (String) uMap.get("userPhotoUrl");
                if(null!=picturesPath && !picturesPath.isEmpty()){
                    String[] split = picturesPath.split(",");
                    List<Map<String, Object>> fileUrl = rmFileMapper.getFileUrl(split);
                    //sysUserEntity.setFileList(fileUrl.toArray());
                    uMap.put("fileList",fileUrl.toArray());
                }
                //2.场所地址
                //String relVenuesId = sysUserEntity.getRelVenuesId();
                String relVenuesId = (String) uMap.get("relVenuesId");
                if(null!=relVenuesId && !relVenuesId.isEmpty()){
                    String[] split = relVenuesId.split(",");
                    String venuesNm = sysUserMapper.getVenuesNm(split);
                    //sysUserEntity.setVenuesNm(venuesNm);
                    uMap.put("venuesNm",venuesNm);
                    //场所回显
                    ParamsVo vo=new ParamsVo();
                    vo.setVenues(relVenuesId);
                    vo.setVenuesArr(split);
                    List<VenuesEntity> venuesEntities = rmVenuesInfoMapper.querySelect(vo);
                    //sysUserEntity.setSelectVenues(venuesEntities);
                    uMap.put("selectVenues",venuesEntities.toArray());
                }

                //sysUserEntity.setIdentityInt(Integer.valueOf(sysUserEntity.getIdentity()));
                String identity = (String) uMap.get("identity");
                uMap.put("identityInt",Integer.valueOf(identity));

            }else{
                throw new RuntimeException("用户信息丢失！");
            }

            list.add(uMap);
            code= ResultCode.SUCCESS.getCode();
            message="获取登录用户信息成功";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="获取登录用户信息失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
    }

    @Override
    public PageResponse excelImport(MultipartFile file) {
        long code= ResultCode.FAILED.getCode();
        String message="三人驻堂信息Excel导入失败";
        List<SysUserEntity> userList=new ArrayList<>();
        try {
            //1. 创建一个 workbook 对象
            //获得原始文件名
            String oriName = file.getOriginalFilename();
            Workbook workbook=null;
            if (oriName.endsWith("xls")) {
                //使用 HSSFWorkbook 解析
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                //使用 XSSFWorkbook 解析
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            //2. 获取 workbook 中表单的数量
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                //3. 获取表单
                Sheet sheet = workbook.getSheetAt(i);
                //4. 获取表单中的行数
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                Long maxCustCd = sysUserMapper.getMaxUserNbr();
                for (int j = 0; j < physicalNumberOfRows; j++) {
                    //5. 跳过标题行
                    if (j == 0) {
                        continue;//跳过标题行
                    }
                    //6. 获取行
                    Row row = sheet.getRow(j);
                    if (row == null) {
                        continue;//防止数据中间有空行
                    }
                    //7. 获取列数
                    int physicalNumberOfCells =row.getPhysicalNumberOfCells();
                    SysUserEntity sysUserEntity=new SysUserEntity();
                    for (int k = 0; k < physicalNumberOfCells; k++) {
                        Cell cell = row.getCell(k);
                        int cellType = cell.getCellType();
                        String cellValue="";
                        int rowNo = j+1;
                        int cellNo = k+1;
                        if(0==cellType){
                            //数值型
                            cellValue= new BigDecimal(cell.getNumericCellValue()).toPlainString();
                        }else if(1==cellType){
                            //字符串
                            cellValue=cell.getStringCellValue();
                        }else if(4==cellType){
                            //布尔型
                            boolean booleanCellValue = cell.getBooleanCellValue();
                            cellValue=String.valueOf(booleanCellValue);
                        }else if(2==cellType){
                            //公式型
                            throw new RuntimeException("第"+rowNo+"行,第"+cellNo+"列,公式型单元格错误！");
                        }else if(3==cellType){
                            //单元格为空!
                            throw new RuntimeException("第"+rowNo+"行,第"+cellNo+"列,字段不能为空！");
                        }else if(5==cellType){
                            //单元格错误
                            throw new RuntimeException("第"+rowNo+"行,第"+cellNo+"列,单元格错误！");
                        }else{
                            throw new RuntimeException("未知错误！");
                        }

                        //数据校验
                        if(0==k){
                            sysUserEntity.setUserNm(cellValue);
                            sysUserEntity.setLoginNm(cellValue);
                        }if(1==k){
                            sysUserEntity.setIdentity("组长".equals(cellValue)?"10000006":"10000007");
                        }if(2==k){
                            //场所
                            //获取场所信息
                            VenuesEntity venue = rmVenuesInfoMapper.getVenueByID(cellValue);
                            if(null==venue){
                                throw new RuntimeException("第"+rowNo+"行,第"+cellNo+"列,场所信息填写错误！");
                            }
                            sysUserEntity.setRelVenuesId(cellValue);
                            sysUserEntity.setProvince(venue.getProvince());
                            sysUserEntity.setCity(venue.getCity());
                            sysUserEntity.setArea(venue.getArea());
                            sysUserEntity.setTown(venue.getTown());
                        }if(3==k){
                            //电话
                            sysUserEntity.setUserMobile(cellValue);
                        }
                    }
                    userList.add(sysUserEntity);
                    Timestamp timestamp = new Timestamp(new Date().getTime());
                    //电话不能重复
                    long numTel = sysUserMapper.queryTelNum(sysUserEntity.getUserMobile());
                    if(numTel>0l){
                        throw new RuntimeException("电话号码："+sysUserEntity.getUserMobile()+"已被占用");
                    }
                    sysUserEntity.setCreateTime(timestamp);
                    sysUserEntity.setLastModifyTime(timestamp);
                    maxCustCd++;
                    sysUserEntity.setUserNbr(String.valueOf(maxCustCd));

                }
            }
            code= ResultCode.SUCCESS.getCode();
            message="三人驻堂信息Excel导入成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return  new PageResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return  new PageResponse(code,message);
        }

        return  new PageResponse(code,message,userList);
    }

    @Override
    public PageResponse excelImportAdd(List<SysUserEntity> sysUserList, String token) {

        long code= ResultCode.FAILED.getCode();
        String message="三人驻堂Excel数据保存失败";

        List<Map<String, Object>> list=new ArrayList<>();
        try {
            if(null!=sysUserList && sysUserList.size()>0){
                for(int i=0;i<sysUserList.size();i++){
                    SysUserEntity sysUserEntity = sysUserList.get(i);
                    //1.新增用户
                    sysUserMapper.add(sysUserEntity);
                    //2.密码加密
                    String passwords = passwordDefault;
                    int userId = sysUserEntity.getUserId();
                    String userNbr = sysUserEntity.getUserNbr();
                    //MD5加密,salt加密
                    String pass = this.passwordSalt(String.valueOf(userId),passwords,userNbr);
                    sysUserMapper.updatePassword(pass,userId,TimeTool.getYmdHms());
                    //3.新增用户角色关系
                    UserRoleEntity userRoleEntity=new UserRoleEntity();
                    userRoleEntity.setUserId(String.valueOf(sysUserEntity.getUserId()));
                    userRoleEntity.setRoleId(String.valueOf(sysUserEntity.getIdentity()));
                    sysUserRoleRelMapper.add(userRoleEntity);
                }
                //日志记录
                //增加日志信息
                Map<String,Object> vuMap=new HashMap<>();
                String login = this.getLogin(token);
                vuMap.put("operator",login);
                vuMap.put("operateTime",new Date());
                vuMap.put("operateRef","三人驻堂");
                vuMap.put("operateType", OperaEnums.user_add.getCode());
                vuMap.put("operateContent", "三人驻堂excel数据");
                vuMap.put("operateDetail","三人驻堂excel导入共:"+sysUserList.size()+"条");
                operateRecordService.addRecord(vuMap);

            }
            code= ResultCode.SUCCESS.getCode();
            message="三人驻堂Excel数据保存成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return  new PageResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return  new PageResponse(code,message);
        }

        return  new PageResponse(code,message);
    }

    /**
     * 密码加密
     * @param userNbr
     * @param password
     * @return
     */
    public String passwordSalt(String userId,String password,String userNbr) {
        //String saltOrigin=null;
        Object salt=ByteSource.Util.bytes(userId+userNbr);
        String hashAlgorithmName = "MD5";//加密方式
        char[]  credential=(char[])(password != null ? password.toCharArray() : null);//密码
        int hashIterations = 1024;//加密1024次
        Hash result = new SimpleHash(hashAlgorithmName,credential,salt,hashIterations);
        return String.valueOf(result);
    }
    /**
     * 获取登录人
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        return loginNm;
    }

    /**
     * 获取
     * @return
     */
    public ParamsVo getAuth(String token){
        String login = this.getLogin(token);
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
        String area="";
        String town ="";
        String relVenuesId="";
        String[] venuesArr={};
        if(null!=sysUserEntity){
            relVenuesId = sysUserEntity.getRelVenuesId();
            town = sysUserEntity.getTown();
            area = sysUserEntity.getArea();
        }else{
            throw new RuntimeException("用户已过期，请重新登录！");
        }
        ParamsVo vo=new ParamsVo();
        vo.setArea(area);
        vo.setTown(town);
        vo.setVenues(relVenuesId);
        if(null!=relVenuesId && !relVenuesId.isEmpty()){
            venuesArr=relVenuesId.split(",");
            vo.setVenuesArr(venuesArr);
        }
        return vo;
    }

    /**
     * 数据校验
     * @param sysUserEntity
     * @return
     */
    public String checkData(SysUserEntity sysUserEntity){
        String message="";
        int userId = sysUserEntity.getUserId();
        //查询组员数量
        String relVenuesIds = sysUserEntity.getRelVenuesId();
        String identity = sysUserEntity.getIdentity();
        if(!GeneTool.isEmpty(relVenuesIds)){
            String[] split = relVenuesIds.split(",");
            for(int i=0;i<split.length;i++){
                String relVenuesId=split[i];
                //获取场所名称
                VenuesEntity ve = rmVenuesInfoMapper.getVenueByID(relVenuesId);
                String venuesName = ve.getVenuesName();
                if(RoleEnums.ZU_YUAN.getCode().equals(identity)){
                    int yuanNum = sysUserMapper.getYuanNum(relVenuesId,userId);
                    //只能两位组员
                    if(yuanNum==2){
                        throw new RuntimeException(venuesName+":该场所内三人驻堂组员数量已满2人！");
                    }
                }else if(RoleEnums.ZU_ZHANG.getCode().equals(identity)){
                    //组长添加必须满足3个组员
                    int yuanNum = sysUserMapper.getYuanNum(relVenuesId,userId);
                    if(yuanNum!=2){
                        message=venuesName+":该场所内三人驻堂组员数量不足2人，不能添加组长！";
                        throw new RuntimeException(message);
                    }
                    int zzhNum = sysUserMapper.getZzhNum(relVenuesId,userId);
                    if(zzhNum==1){
                        throw new RuntimeException(venuesName+":该场所内已存在三人驻堂组长");
                    }
                }
            }
        }

        return message;
    }

}
