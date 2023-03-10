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
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
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
        String message="??????????????????";

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
            //??????????????????
            ParamsVo vo = this.getAuth((String) map.get("token"));
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchOne(loginNm);
            vo.setSearchTwo(identity);
            dataList=sysUserMapper.getUsersByPage(vo);
            //????????????
            if(null!=dataList && dataList.size()>0){
                for(int i=0;i<dataList.size();i++){
                    SysUserEntity sysUserEntity = dataList.get(i);
                    String ident = sysUserEntity.getIdentity();
                    String roleNm = sysRoleMapper.getRoleNm(ident);
                    sysUserEntity.setIdentity(roleNm);
                }
            }
            //????????????
            total=sysUserMapper.getTotal(vo);

            code= ResultCode.SUCCESS.getCode();
            message="????????????????????????";
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
        String message="????????????";
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new Date().getTime());
            //??????????????????
            String relVenuesId = sysUserEntity.getRelVenuesId();
            int yuanNum = sysUserMapper.getYuanNum(relVenuesId);

            String identity = sysUserEntity.getIdentity();
            //??????????????????
            if(RoleEnums.ZU_YUAN.getCode().equals(identity) && yuanNum==2){
                throw new RuntimeException("??????????????????????????????????????????2??????");
            }
            //????????????????????????3?????????
            if(RoleEnums.ZU_ZHANG.getCode().equals(identity)){
                if(yuanNum!=2){
                    message="??????????????????????????????????????????2???????????????????????????";
                    throw new RuntimeException(message);
                }
            }
            //??????????????????
            long numTel = sysUserMapper.queryTelNum(sysUserEntity.getUserMobile());
            if(numTel>0l){
                throw new RuntimeException("???????????????"+sysUserEntity.getUserMobile()+"????????????");
            }

            sysUserEntity.setCreateTime(timestamp);
            sysUserEntity.setLastModifyTime(timestamp);
            Long maxCustCd = sysUserMapper.getMaxUserNbr();
            maxCustCd++;
            sysUserEntity.setUserNbr(String.valueOf(maxCustCd));
            //????????????
            String passwords = sysUserEntity.getPasswords();
            //String loginNm = sysUserEntity.getLoginNm();
            String userMobile = sysUserEntity.getUserMobile();
            sysUserEntity.setWeakPwInd(passwords);
            //MD5??????,salt??????
            String pass = this.passwordSalt(userMobile,passwords,identity);
            sysUserEntity.setPasswords(pass);
            //????????????
            sysUserMapper.add(sysUserEntity);

            //????????????????????????
            UserRoleEntity userRoleEntity=new UserRoleEntity();
            userRoleEntity.setUserId(String.valueOf(sysUserEntity.getUserId()));
            userRoleEntity.setRoleId(String.valueOf(sysUserEntity.getIdentity()));
            sysUserRoleRelMapper.add(userRoleEntity);

            //????????????
            //this.savelogs( "????????????", InfoEnums.USER_ADD.getName());

            code= ResultCode.SUCCESS.getCode();
            message="??????????????????";
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message="??????????????????";
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
            //????????????
           //this.savelogs( "????????????", InfoEnums.USER_UPDATE.getName());
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
                result="????????????????????????????????????";
            }
        }else{
            code=ResultCode.FAILED.getCode();
            result=ResultCode.UNAUTHORIZED.getMessage();
        }

        if(!newPass.equals(surePass)){
            code=ResultCode.FAILED.getCode();
            result="????????????????????????????????????????????????????????????";
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
        //this.savelogs( "????????????", InfoEnums.USER_DELETE.getName());

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
        String message="????????????????????????";

        List<Map<String,Object>> list=new ArrayList<>();
        try {
            String login = this.getLogin(token);
            Map<String,Object> sysUserEntity = sysUserMapper.queryBySearch(login);
            if(null==sysUserEntity || sysUserEntity.size()<1){
                throw new RuntimeException("?????????????????????");
            }
            list.add(sysUserEntity);
            code= ResultCode.SUCCESS.getCode();
            message="??????????????????????????????";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="?????????????????????????????????";
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
    }

    /**
     * ????????????
     * @param userName
     * @param password
     * @return
     */
    public String passwordSalt(String userName,String password,String identity) {
        //String saltOrigin=null;
        Object salt=ByteSource.Util.bytes(userName+identity);
        String hashAlgorithmName = "MD5";//????????????
        char[]  credential=(char[])(password != null ? password.toCharArray() : null);//??????
        int hashIterations = 1024;//??????1024???
        Hash result = new SimpleHash(hashAlgorithmName,credential,salt,hashIterations);
        return String.valueOf(result);
    }
    /**
     * ???????????????
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("?????????????????????????????????");
        }
        return loginNm;
    }

    /**
     * ??????
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
            throw new RuntimeException("????????????????????????????????????");
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
