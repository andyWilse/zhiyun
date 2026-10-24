package com.religion.zhiyun.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.RmUserVenuesMapper;
import com.religion.zhiyun.user.dao.SysRoleMapper;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.dao.SysUserRoleRelMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserRoleEntity;
import com.religion.zhiyun.user.entity.UserVenuesEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.record.dao.OperateRecordMapper;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.base.TransParam;
import com.religion.zhiyun.utils.enums.CellEnums;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.excel.CellValueTrans;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    @Autowired
    private RmUserVenuesMapper rmUserVenuesMapper;
    @Autowired
    private RmUserVenuesMapper initMapper;

    @Override
    public PageResponse getUsersByPage(Map<String, Object> map,String token){

        long code= ResultCode.FAILED.getCode();
        String message="系统用户查询";

        List<SysUserEntity> dataList=new ArrayList<>();
        long total=0l;
        try {
            //参数
            String identity = map.get("identity")!=null?(String)map.get("identity"):"";
            String userNm = map.get("userNm")!=null?(String)map.get("userNm"):"";
            String pages = map.get("page")!=null?(String) map.get("page"):"";
            String sizes = map.get("size")!=null?(String)map.get("size"):"";
            String venue = map.get("venue")!=null?(String)map.get("venue"):"";
            String mark = map.get("mark")!=null?(String)map.get("mark"):"";
            String userMobile = map.get("userMobile")!=null?(String)map.get("userMobile"):"";
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
            vo.setSearchFour(mark);
            vo.setSearchFive(userMobile);
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
    public RespPageBean addUser(SysUserEntity sysUserEntity,String token) {
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
            sysUserMapper.addUser(sysUserEntity);

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

            //增加用户场所关系
            String relVenuesId = sysUserEntity.getRelVenuesId();
            if(null!=relVenuesId && relVenuesId!=""){
                String[] split = relVenuesId.split(",");
                if(null!=split && split.length>0){
                    for(int v=0;v<split.length;v++){
                        String venuesId = split[v];
                        if(null!=venuesId && venuesId!=""){
                            int veId = Integer.parseInt(venuesId);
                            //1.校验
                            Boolean uvFlag = this.checkUv(userId, veId);
                            if(uvFlag){
                                throw new RuntimeException("用户（"+sysUserEntity.getUserNm()+"）已存在，请勿重复添加！");
                            }
                            //2.增加
                            this.saveUv(userId,veId);
                        }
                    }
                }
            }
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
            //判断是否为三人驻堂，不是清理场所关系
            String identity = sysUserEntity.getIdentity();
            int userId = sysUserEntity.getUserId();
            if(identity!="10000006" || identity!="10000007"){
                List<UserVenuesEntity> userVenues = initMapper.getUserVenues(userId, 0);
                if(null!=userVenues && userVenues.size()>0){
                    //清理关系
                    UserVenuesEntity vo=new UserVenuesEntity();
                    vo.setUvUserId(userId);
                    initMapper.deleteSrMap(vo);
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
        //1.修改用户表
        sysUserMapper.delete(userId);
        //2.更新用户场所关联表
        Timestamp timestamp = new Timestamp(new Date().getTime());
        UserVenuesEntity vo=new UserVenuesEntity();
        vo.setUvUserId(userId);
        vo.setUvModifyTime(timestamp);
        rmUserVenuesMapper.deleteSrMap(vo);
        //3.增加日志信息
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
            //获取用户信息
            Map<String, Object> uMap = sysUserMapper.getUserId(userId);
            if(null!=uMap){
                //1.图片回显
                //String picturesPath = sysUserEntity.getUserPhotoUrl();
                String picturesPath = (String) uMap.get("userPhotoUrl");
                if(null!=picturesPath && !picturesPath.isEmpty()){
                    String[] split = picturesPath.split(",");
                    List<Map<String, Object>> fileUrl = rmFileMapper.getFileUrl(split);
                    //sysUserEntity.setFileList(fileUrl.toArray());
                    uMap.put("fileList",fileUrl.toArray());
                }
                //2.场所地址
                Integer uId = Integer.valueOf(userId);
                String venuesNm = sysUserMapper.getVenuesNm(uId);
                uMap.put("venuesNm",venuesNm);
                //List<VenuesEntity> venuesEntities = sysUserMapper.getVenuesByUser(uId);
                //uMap.put("selectVenues",venuesEntities.toArray());
                String identity = (String) uMap.get("identity");
                uMap.put("identityInt",Integer.valueOf(identity));

            }else{
                throw new RuntimeException("用户信息丢失！");
            }

            list.add(uMap);
            code= ResultCode.SUCCESS.getCode();
            message="获取登录用户信息成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return new PageResponse(code,r.getMessage());

        }catch (Exception e) {
            e.printStackTrace();
            return new PageResponse(code,e.getMessage());

        }
        return new PageResponse(code,message,list);
    }

    @Override
    public PageResponse excelImport(Map<String,Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="三人驻堂信息Excel导入失败";
        List<SysUserEntity> userList=new ArrayList<>();
        try {
            String fileContent = map.get("fileContent")==null?"": (String) map.get("fileContent");
            String fileName = map.get("fileName")==null?"": (String) map.get("fileName");
            //数据转换
            String[] parts = fileContent.split(",");
            String base64File = parts[1].split("\"")[0];
            byte[] bytes = Base64.getDecoder().decode(base64File);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            //1. 创建一个 workbook 对象
            //获得原始文件名
            //String oriName = file.getOriginalFilename();
            Workbook workbook=null;
            if (fileName.endsWith("xls")) {
                //使用 HSSFWorkbook 解析
                workbook = new HSSFWorkbook(inputStream);
            } else {
                //使用 XSSFWorkbook 解析
                workbook = new XSSFWorkbook(inputStream);
            }
            //2. 获取 workbook 中表单的数量
            int numberOfSheets = workbook.getNumberOfSheets();
            //for (int i = 0; i < numberOfSheets; i++) {
            if(numberOfSheets!=0){
                //3. 获取表单
                //Sheet sheet = workbook.getSheetAt(0);
                Sheet sheet = workbook.getSheet("info");
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
                    int[] colColumns={5,6,7,3,4};
                    boolean skipRow = CellValueTrans.checkRow(row, colColumns);
                    if (skipRow) {
                        continue;//防止数据中间有空行
                    }
                    //7. 获取列数
                    //int physicalNumberOfCells =row.getPhysicalNumberOfCells();
                    SysUserEntity sysUserEntity=new SysUserEntity();
                    List<Integer> listCellEmpty = Arrays.asList(5,6,7,3,4);
                    for (int k = 0; k < 8; k++) {
                        Cell cell = row.getCell(k);
                        int rowNo = j+1;
                        if(null==cell){
                            if(listCellEmpty.contains(k)){
                                throw new RuntimeException("第"+rowNo+"行"+ CellEnums.getName(k)+"列,字段不能为空！");
                            }
                            continue;
                        }
                        String cellValue = CellValueTrans.getCellValue(cell, workbook, j, k, listCellEmpty);
                        //数据校验
                        if(0==k){
                            //角色
                            sysUserEntity.setIdentity(cellValue);

                            if(!GeneTool.isEmpty(cellValue)){
                            }else{
                                sysUserEntity.setIdentity(cellValue);
                            }

                        }if(1==k){
                            //town
                            sysUserEntity.setTown(cellValue);
                        }if(2==k){
                            //场所
                            sysUserEntity.setRelVenuesId(cellValue);
                        }if(3==k){
                            sysUserEntity.setUserNm(cellValue);
                            sysUserEntity.setLoginNm(cellValue);
                        }if(4==k){
                            //电话
                            sysUserEntity.setUserMobile(cellValue);
                            //电话不能重复
                            long numTel = sysUserMapper.queryTelNum(cellValue);
                            if(numTel>0l){
                                throw new RuntimeException("电话号码："+cellValue+"已被占用");
                            }
                        }if(5==k && GeneTool.isEmpty(sysUserEntity.getIdentity())){
                            //角色
                            sysUserEntity.setIdentity("组长".equals(cellValue)?"10000006":"10000007");
                        }if(7==k && GeneTool.isEmpty(sysUserEntity.getRelVenuesId())){
                            //场所
                            //获取场所信息
                            List<Map<String, Object>> venues = rmVenuesInfoMapper.getVenuesByNm(cellValue);
                            if(null==venues || venues.size()<1){
                                throw new RuntimeException("第"+rowNo+"行,H列,场所信息填写错误！");
                            }
                            Integer venuesId = (Integer) venues.get(0).get("venuesId");
                            sysUserEntity.setRelVenuesId(String.valueOf(venuesId));
                            sysUserEntity.setTown((String) venues.get(0).get("town"));
                        }
                    }
                    userList.add(sysUserEntity);
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

        return  new PageResponse(code,message,userList.toArray());
    }

    @Override
    public PageResponse excelImportAdd(Map<String,Object> map) {

        long code= ResultCode.FAILED.getCode();
        String message="三人驻堂Excel数据保存失败";

        List<Map<String, Object>> list=new ArrayList<>();
        try {
            Object sysUserList = map.get("sysUserList");
            Object[] tcArr=null;
            if(null!=sysUserList){
                tcArr = ((List<?>) sysUserList).toArray();
            }

            if(null!=sysUserList){
                for(int i=0;i<tcArr.length;i++){
                    //SysUserEntity sysUserEntity = sysUserList.get(i);
                    HashMap<String, Object> user = (HashMap<String, Object>) tcArr[i];
                    SysUserEntity sysUserEntity = BeanUtil.mapToBean(user, SysUserEntity.class, false);
                    sysUserEntity.setCreateTime(TimeTool.getTimestamp());
                    sysUserEntity.setLastModifyTime(TimeTool.getTimestamp());
                    sysUserEntity.setProvince("330000");
                    sysUserEntity.setCity("330300");
                    sysUserEntity.setArea("330304");
                    //1.新增用户
                    sysUserMapper.addUser(sysUserEntity);
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

                    //4.增加用户场所关系
                    String relVenuesId = sysUserEntity.getRelVenuesId();
                    if(null!=relVenuesId && relVenuesId!=""){
                        String[] split = relVenuesId.split(",");
                        if(null!=split && split.length>0){
                            for(int v=0;v<split.length;v++){
                                String venuesId = split[v];
                                if(null!=venuesId && venuesId!=""){
                                    int veId = Integer.parseInt(venuesId);
                                    //1.校验
                                    Boolean uvFlag = this.checkUv(userId, veId);
                                    if(uvFlag){
                                        throw new RuntimeException("用户（"+sysUserEntity.getUserNm()+"）已存在，请勿重复添加！");
                                    }
                                    //2.增加
                                    this.saveUv(userId,veId);
                                }
                            }
                        }
                    }
                }
                //日志记录
                //增加日志信息
                Map<String,Object> vuMap=new HashMap<>();
                vuMap.put("operator", TransParam.loginName);
                vuMap.put("operateTime",new Date());
                vuMap.put("operateRef","三人驻堂");
                vuMap.put("operateType", OperaEnums.user_add.getCode());
                vuMap.put("operateContent", "三人驻堂excel数据");
                vuMap.put("operateDetail","三人驻堂excel导入共:"+tcArr.length+"条");
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

    @Override
    public PageResponse getSrUser(String venuesId) {
        long code= ResultCode.FAILED.getCode();
        String message="获取三人驻堂失败";

        List<SysUserEntity> list=new ArrayList<>();
        try {
            list=sysUserMapper.getSrUser(venuesId);

            code= ResultCode.SUCCESS.getCode();
            message="获取三人驻堂成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return new PageResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return  new PageResponse(code,e.getMessage());
        }

        return new PageResponse(code,message,list);
    }

    @Override
    public PageResponse deleteSrUser(Map<String,Object> map, String token) {

        long code= ResultCode.FAILED.getCode();
        String message="场所三人驻堂成员去除失败";

        try {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            String uvId = (String) map.get("uvId");
            String operation = (String) map.get("operation");

            //1.去除
            UserVenuesEntity vo=new UserVenuesEntity();
            vo.setUvId(Integer.valueOf(uvId));
            vo.setUvModifyTime(timestamp);
            rmUserVenuesMapper.deleteSrMap(vo);
            //日志
            //增加日志信息
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",this.getLogin(token));
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",String.valueOf(uvId));
            vuMap.put("operateType", OperaEnums.user_venue_delete.getCode());
            vuMap.put("operateContent", "去除场所三人驻堂成员");
            vuMap.put("operateDetail",operation);
            operateRecordService.addRecord(vuMap);
            code= ResultCode.SUCCESS.getCode();
            message="场所三人驻堂成员去除成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return  new PageResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return  new PageResponse(code,e.getMessage());
        }

        return  new PageResponse(code,message);
    }

    @Override
    public PageResponse addSr(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="场所添加三人驻堂成员失败";

        try {
            Integer venuesId = map.get("venuesId")!=null?(Integer) map.get("venuesId"):0;
            String venuesName =map.get("venuesName")!=null? (String) map.get("venuesName"):"";
            Object userList = map.get("userList");
            Object[] srArr=null;
            if(null!=userList){
                srArr = ((List<?>) userList).toArray();
            }
            for(int i=0;i<srArr.length;i++){
                HashMap<String,Object> user = (HashMap<String, Object>) srArr[i];
                Integer userId = (Integer) user.get("userId");
                String userNm = (String) user.get("userNm");
                //1.校验
                Boolean uvFlag = this.checkUv(userId, venuesId);
                if(uvFlag){
                    throw new RuntimeException("用户（"+userNm+"）已存在，请勿重复添加！");
                }
                //2.增加
                UserVenuesEntity uvEntity = this.saveUv(userId, venuesId);
                //3.增加日志信息
                Map<String,Object> vuMap=new HashMap<>();
                vuMap.put("operator",this.getLogin(token));
                vuMap.put("operateTime",new Date());
                vuMap.put("operateRef",String.valueOf(uvEntity.getUvId()));
                vuMap.put("operateType", OperaEnums.user_venue_add.getCode());
                vuMap.put("operateContent", "场所添加三人驻堂成员");
                vuMap.put("operateDetail","向（"+venuesName+"）内添加三人驻堂成员："+userNm);
                operateRecordService.addRecord(vuMap);
            }

            code= ResultCode.SUCCESS.getCode();
            message="场所添加三人驻堂成员成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return  new PageResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return  new PageResponse(code,e.getMessage());
        }

        return  new PageResponse(code,message);
    }

    @Override
    public PageResponse deleteSrVenue(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="删除三人驻堂人员关联场所失败";

        try {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Integer venuesId = map.get("venuesId")==null?0:(Integer) map.get("venuesId");
            if(0==venuesId){
                throw new RuntimeException("场所信息丢失，请联系管理员！");
            }
            String operation = (String) map.get("operation");
            //1.去除
            UserVenuesEntity vo=new UserVenuesEntity();
            vo.setUvVenuesId(venuesId);
            vo.setUvModifyTime(timestamp);
            rmUserVenuesMapper.deleteSrMap(vo);
            //2.增加日志信息
            this.addLog(this.getLogin(token),String.valueOf(venuesId),OperaEnums.venue_user_delete.getCode(),"删除三人驻堂人员关联场所",operation);
            code= ResultCode.SUCCESS.getCode();
            message="删除三人驻堂人员关联场所成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return  new PageResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return  new PageResponse(code,e.getMessage());
        }

        return  new PageResponse(code,message);
    }

    @Override
    public PageResponse addVenue(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="三人驻堂用户加入场所失败";

        try {
            String userId = map.get("userId")!=null?(String) map.get("userId"):"";
            String userNm =map.get("userNm")!=null? (String) map.get("userNm"):"";
            Object venueList = map.get("venueList");
            Object[] srArr=null;
            if(null!=venueList){
                srArr = ((List<?>) venueList).toArray();
            }
            for(int i=0;i<srArr.length;i++){
                HashMap<String,Object> user = (HashMap<String, Object>) srArr[i];
                Integer venuesId = (Integer) user.get("venuesId");
                String venuesName = (String) user.get("venuesName");
                //1.校验
                Boolean uvFlag = this.checkUv(Integer.parseInt(userId), venuesId);
                if(uvFlag){
                    throw new RuntimeException("场所（"+venuesName+"）已存在，请勿重复添加！");
                }
                //2.增加
                UserVenuesEntity uvEntity = this.saveUv(Integer.parseInt(userId), venuesId);
                //3.增加日志信息
                this.addLog(this.getLogin(token),
                        String.valueOf(uvEntity.getUvId()),
                        OperaEnums.venue_user_add.getCode(),
                        OperaEnums.venue_user_add.getName(),
                        "三人驻堂成员（"+userNm+"）加入场所："+venuesName);
            }

            code= ResultCode.SUCCESS.getCode();
            message="三人驻堂用户加入场所成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return  new PageResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return  new PageResponse(code,e.getMessage());
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

    /**
     * 保存用户场所关系
     * @param userId
     * @param venuesId
     * @return
     */
    public UserVenuesEntity saveUv(int userId,int venuesId) {
        UserVenuesEntity uvEntity = new UserVenuesEntity();
        try {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            uvEntity.setUvUserId(userId);
            uvEntity.setUvValidInd("1");
            uvEntity.setUvCreateTime(timestamp);
            uvEntity.setUvModifyTime(timestamp);
            uvEntity.setUvVenuesId(venuesId);
            uvEntity.setUvMark("success");
            //增加
            initMapper.add(uvEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uvEntity;
    }

    /**
     * 重复性判断
     * @param userId
     * @param venuesId
     * @return
     */
    public Boolean checkUv(int userId,int venuesId) {
        Boolean flag=false;
        //重复性校验
        List<UserVenuesEntity> userVenues = initMapper.getUserVenues(userId, venuesId);
        if(userVenues.size()>0){
            flag=true;
        }
        return flag;
    }

    /**
     * 添加日志
     * @param login
     * @param operateRef
     * @param operateType
     * @param operateContent
     * @param operateDetail
     */
    public void addLog(String login,String operateRef,String operateType,String operateContent,String operateDetail){
        //增加日志信息
        Map<String,Object> vuMap=new HashMap<>();
        vuMap.put("operator",login);
        vuMap.put("operateTime",new Date());
        vuMap.put("operateRef",operateRef);
        vuMap.put("operateType", operateType);
        vuMap.put("operateContent", operateContent);
        vuMap.put("operateDetail",operateDetail);
        operateRecordService.addRecord(vuMap);
    }


}
