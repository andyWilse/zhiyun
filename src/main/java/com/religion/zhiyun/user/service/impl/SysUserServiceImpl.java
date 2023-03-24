package com.religion.zhiyun.user.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.SysRoleMapper;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.dao.SysUserRoleRelMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserRoleEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.record.dao.OperateRecordMapper;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {
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

    @Override
    public PageResponse getUsersByPage(Map<String, Object> map,String token){

        long code= ResultCode.FAILED.getCode();
        String message="系统用户查询";

        List<SysUserEntity> dataList=new ArrayList<>();
        long total=0l;
        try {
            //参数
            String identity = (String)map.get("identity");
            String loginNm = (String)map.get("loginNm");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
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
            vo.setSearchOne(loginNm);
            vo.setSearchTwo(identity);
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
    public RespPageBean add(SysUserEntity sysUserEntity) {
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
            message=this.checkData(sysUserEntity);

            sysUserEntity.setCreateTime(timestamp);
            sysUserEntity.setLastModifyTime(timestamp);
            Long maxCustCd = sysUserMapper.getMaxUserNbr();
            maxCustCd++;
            sysUserEntity.setUserNbr(String.valueOf(maxCustCd));
            //密码加密
            String passwords = sysUserEntity.getPasswords();
            String identity = sysUserEntity.getIdentity();
            //String loginNm = sysUserEntity.getLoginNm();
            String userMobile = sysUserEntity.getUserMobile();
            sysUserEntity.setWeakPwInd(passwords);
            //MD5加密,salt加密
            String pass = this.passwordSalt(userMobile,passwords,identity);
            sysUserEntity.setPasswords(pass);

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

            //新增用户角色关系
            UserRoleEntity userRoleEntity=new UserRoleEntity();
            userRoleEntity.setUserId(String.valueOf(sysUserEntity.getUserId()));
            userRoleEntity.setRoleId(String.valueOf(sysUserEntity.getIdentity()));
            sysUserRoleRelMapper.add(userRoleEntity);

            //保存日志
            //this.savelogs( "新增成功", InfoEnums.USER_ADD.getName());

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
    public PageResponse update(SysUserEntity sysUserEntity) {
        long code= ResultCode.FAILED.getCode();
        String message="数据更新";
        try {
            //电话校验
            String userMobile = sysUserEntity.getUserMobile();
            String userMobileOrigin = sysUserEntity.getUserMobileOrigin();
            if(!userMobile.equals(userMobileOrigin)){
                //电话不能重复
                long numTel = sysUserMapper.queryTelNum(sysUserEntity.getUserMobile());
                if(numTel>0l){
                    throw new RuntimeException("电话号码："+sysUserEntity.getUserMobile()+"已被占用");
                }
            }
            message=this.checkData(sysUserEntity);
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
            //保存日志
           //this.savelogs( "修改成功", InfoEnums.USER_UPDATE.getName());

            code= ResultCode.SUCCESS.getCode();
            message="用户更新成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="用户更新失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message);

    }

    @Override
    public RespPageBean updatePassword(Map<String, String> map) {
        RespPageBean bean=new RespPageBean();
        String oldPass = map.get("oldPass");
        String newPass = map.get("newPass");
        String surePass = map.get("surePass");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String nbr = request.getHeader("login-name");
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        String result= ResultCode.SUCCESS.getMessage();
        long code=ResultCode.SUCCESS.getCode();
        if(null!=sysUserEntity){
            String weakPwInd = sysUserEntity.getWeakPwInd();
            if(!weakPwInd.equals(oldPass)){
                code=ResultCode.FAILED.getCode();
                result="旧密码错误，请重新输入！";
            }
        }else{
            code=ResultCode.FAILED.getCode();
            result=ResultCode.UNAUTHORIZED.getMessage();
        }

        if(!newPass.equals(surePass)){
            code=ResultCode.FAILED.getCode();
            result="两次密码输入不一致，请重新输入确认密码！";
        }
        bean.setCode(code);
        bean.setResult(result);
        if(code==ResultCode.SUCCESS.getCode()){
            sysUserEntity.setWeakPwInd(newPass);
            sysUserEntity.setPasswords(DigestUtils.md5Hex(newPass));
            Timestamp timestamp = new Timestamp(new Date().getTime());
            sysUserEntity.setLastModifyTime(timestamp);
            sysUserMapper.update(sysUserEntity);
        }
       return bean;
    }

    @Override
    public void delete(int userId) {
        sysUserMapper.delete(userId);
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
        return sysUserMapper.queryByTel(userMobile);
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

        List<SysUserEntity> list=new ArrayList<>();
        try {
            SysUserEntity sysUserEntity = sysUserMapper.queryByUserId(userId);

            if(null!=sysUserEntity){
                //。图片回显
                String picturesPath = sysUserEntity.getUserPhotoUrl();
                if(null!=picturesPath && !picturesPath.isEmpty()){
                    String[] split = picturesPath.split(",");
                    List<Map<String, Object>> fileUrl = rmFileMapper.getFileUrl(split);
                    sysUserEntity.setFileList(fileUrl.toArray());
                }
                //2.场所地址
                String relVenuesId = sysUserEntity.getRelVenuesId();
                if(null!=relVenuesId && !relVenuesId.isEmpty()){
                    String[] split = relVenuesId.split(",");
                    String venuesNm = sysUserMapper.getVenuesNm(split);
                    sysUserEntity.setVenuesNm(venuesNm);
                }
            }else{
                throw new RuntimeException("用户信息丢失！");
            }

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

    /**
     * 密码加密
     * @param userName
     * @param password
     * @return
     */
    public String passwordSalt(String userName,String password,String identity) {
        //String saltOrigin=null;
        Object salt=ByteSource.Util.bytes(userName+identity);
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
        //查询组员数量
        String relVenuesId = sysUserEntity.getRelVenuesId();
        int yuanNum = sysUserMapper.getYuanNum(relVenuesId);

        String identity = sysUserEntity.getIdentity();
        //只能两位组员
        if(RoleEnums.ZU_YUAN.getCode().equals(identity) && yuanNum==2){
            throw new RuntimeException("该场所内三人驻堂组员数量已满2人！");
        }
        //组长添加必须满足3个组员
        if(RoleEnums.ZU_ZHANG.getCode().equals(identity)){
            if(yuanNum!=2){
                message="该场所内三人驻堂组员数量不足2人，不能添加组长！";
                throw new RuntimeException(message);
            }
        }

        int zzhNum = sysUserMapper.getZzhNum(relVenuesId);
        if(RoleEnums.ZU_ZHANG.getCode().equals(identity) && zzhNum==1){
            throw new RuntimeException("该场所内已存在三人驻堂组长");
        }

        return message;
    }
}
