package com.religion.zhiyun.user.service.impl;

import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysRoleMapper;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.dao.SysUserRoleRelMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserRoleEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.userLogs.dao.RmUserLogsInfoMapper;
import com.religion.zhiyun.userLogs.entity.LogsEntity;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.base.HttpServletRequestReader;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
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
    private RmUserLogsInfoMapper rmUserLogsInfoMapper;

    @Autowired
    private SysUserRoleRelMapper sysUserRoleRelMapper;

    @Override
    public RespPageBean getUsersByPage(Map<String, Object> map) throws IOException {

        RespPageBean respPageBean = new RespPageBean();
        long code= ResultCode.SUCCESS.getCode();

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
            List<SysUserEntity> dataList=sysUserMapper.getUsersByPage(page,size,identity,loginNm);
            if(null!=dataList && dataList.size()>0){
                for(int i=0;i<dataList.size();i++){
                    SysUserEntity sysUserEntity = dataList.get(i);
                    String ident = sysUserEntity.getIdentity();
                    String roleNm = sysRoleMapper.getRoleNm(ident);
                    sysUserEntity.setIdentity(roleNm);
                }
            }
            Object[] objects = dataList.toArray();
            Long total=sysUserMapper.getTotal();
            respPageBean.setDatas(objects);
            respPageBean.setTotal(total);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        //rmUserLogsInfoService.savelogs( JsonUtils.objectTOJSONString(respPageBean), InfoEnums.USER_FIND.getName());
        respPageBean.setCode(code);
        return respPageBean;
    }

    @Override
    public RespPageBean add(SysUserEntity sysUserEntity) {
        long code= ResultCode.SUCCESS.getCode();
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new Date().getTime());
            sysUserEntity.setCreateTime(timestamp);
            sysUserEntity.setLastModifyTime(timestamp);
            Long maxCustCd = sysUserMapper.getMaxUserNbr();
            maxCustCd++;
            sysUserEntity.setUserNbr(String.valueOf(maxCustCd));
            //密码加密
            String passwords = sysUserEntity.getPasswords();
            sysUserEntity.setWeakPwInd(passwords);
            String pass =DigestUtils.md5Hex(passwords);
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

        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);
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

    /**
     * 日志信息封存
     * @param response
     * @param cnName
     */
    public void savelogs(String response,String cnName) {
        LogsEntity logsEntity=new LogsEntity();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String nbr = request.getHeader("login-name");
        logsEntity.setUserNbr(nbr);
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        if(null!=sysUserEntity){
            logsEntity.setUserName(sysUserEntity.getLoginNm());
        }
        String servletPath = request.getServletPath();
        logsEntity.setInterfaceCode(servletPath);
        logsEntity.setInterfaceName(cnName);
        String body = HttpServletRequestReader.ReadAsChars(request);
        logsEntity.setRequestParam(body);
        logsEntity.setResponseResult(response);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        logsEntity.setFkDate(timestamp);
        rmUserLogsInfoMapper.addlogs(logsEntity);
    }
}
