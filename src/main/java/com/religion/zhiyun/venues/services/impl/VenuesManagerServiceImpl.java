package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.utils.response.PcResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.dao.VenuesManagerMapper;
import com.religion.zhiyun.venues.entity.VenuesManagerEntity;
import com.religion.zhiyun.venues.services.VenuesManagerService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class VenuesManagerServiceImpl implements VenuesManagerService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private VenuesManagerMapper venuesManagerMapper;


    @Override
    public RespPageBean add(VenuesManagerEntity venuesManagerEntity, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="添加数据！";

        try {
            String managerMobile = venuesManagerEntity.getManagerMobile();
            String passwords = venuesManagerEntity.getPasswords();
            String pass = this.passwordSalt(managerMobile, passwords, managerMobile);
            venuesManagerEntity.setPasswords(pass);

            String login = this.getLogin(token);
            venuesManagerEntity.setValidInd("1");//有效
            venuesManagerEntity.setCreator(login);
            venuesManagerEntity.setCreateTime(new Date());
            venuesManagerEntity.setLastModifier(login);
            venuesManagerEntity.setLastModifyTime(new Date());
            venuesManagerMapper.add(venuesManagerEntity);code= ResultCode.SUCCESS.getCode();
            message="添加数据成功！";
        } catch (RuntimeException r ){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="添加数据失败！";
            e.printStackTrace();
        }

        return new RespPageBean(code,message);
    }

    @Override
    public void update(VenuesManagerEntity venuesManagerEntity) {

    }

    @Override
    public int delete(int managerId) {
        return 0;
    }

    @Override
    public PcResponse query() {
        return null;
    }

    @Override
    public PcResponse getManager(String search, String token) {
        return null;
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
     * 密码加密
     * @param userName
     * @param password
     * @return
     */
    public String passwordSalt(String userName,String password,String identity) {
        //String saltOrigin=null;
        Object salt= ByteSource.Util.bytes(userName+identity);
        String hashAlgorithmName = "MD5";//加密方式
        char[]  credential=(char[])(password != null ? password.toCharArray() : null);//密码
        int hashIterations = 1024;//加密1024次
        Hash result = new SimpleHash(hashAlgorithmName,credential,salt,hashIterations);
        return String.valueOf(result);
    }
}
