package com.religion.zhiyun.user.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysRoleMapper;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.dao.SysUserRoleRelMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserRoleEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.record.dao.OperateRecordMapper;
import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.base.HttpServletRequestReader;
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
import java.io.IOException;
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

    @Override
    public RespPageBean getUsersByPage(Map<String, Object> map){

        long code= ResultCode.FAILED.getCode();
        String message="系统用户查询";

        List<SysUserEntity> dataList=new ArrayList<>();
        Long total=0l;
        try {
            String identity = (String)map.get("identity");
            String loginNm = (String)map.get("loginNm");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //封装查询参数
            ParamsVo vo = this.getAuth((String) map.get("token"));
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchOne(loginNm);
            vo.setSearchTwo(identity);
            dataList=sysUserMapper.getUsersByPage(vo);
            //转为中文
            if(null!=dataList && dataList.size()>0){
                for(int i=0;i<dataList.size();i++){
                    SysUserEntity sysUserEntity = dataList.get(i);
                    String ident = sysUserEntity.getIdentity();
                    String roleNm = sysRoleMapper.getRoleNm(ident);
                    sysUserEntity.setIdentity(roleNm);
                }
            }
            //查询条数
            total=sysUserMapper.getTotal(vo);

            code= ResultCode.SUCCESS.getCode();
            message="系统用户查询成功";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        }catch (IOException io) {
            message=io.getMessage();
            io.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        //rmUserLogsInfoService.savelogs( JsonUtils.objectTOJSONString(respPageBean), InfoEnums.USER_FIND.getName());

        return new RespPageBean(code,message,total, dataList.toArray());
    }

    @Override
    public RespPageBean add(SysUserEntity sysUserEntity) {
        long code= ResultCode.FAILED.getCode();
        String message="用户新增";
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new Date().getTime());
            //组长添加必须满足3个组员
            String identity = sysUserEntity.getIdentity();
            if(RoleEnums.ZU_ZHANG.getCode().equals(identity)){
                String relVenuesId = sysUserEntity.getRelVenuesId();
                //查询组员数量
                int yuanNum = sysUserMapper.getYuanNum(relVenuesId);
                if(yuanNum!=3){
                    message="该场所内三人驻堂组员数量不足，不能添加组长！";
                    throw new RuntimeException(message);
                }
            }
            sysUserEntity.setCreateTime(timestamp);
            sysUserEntity.setLastModifyTime(timestamp);
            Long maxCustCd = sysUserMapper.getMaxUserNbr();
            maxCustCd++;
            sysUserEntity.setUserNbr(String.valueOf(maxCustCd));
            //密码加密
            String passwords = sysUserEntity.getPasswords();
            //String loginNm = sysUserEntity.getLoginNm();
            String userMobile = sysUserEntity.getUserMobile();
            sysUserEntity.setWeakPwInd(passwords);
            //MD5加密,salt加密
            String pass = this.passwordSalt(userMobile,passwords,identity);
            sysUserEntity.setPasswords(pass);
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
    public RespPageBean update(SysUserEntity sysUserEntity) {
        long code= ResultCode.SUCCESS.getCode();
        try {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            sysUserEntity.setLastModifyTime(timestamp);
            sysUserMapper.update(sysUserEntity);
            //保存日志
           //this.savelogs( "修改成功", InfoEnums.USER_UPDATE.getName());
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);

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
}
