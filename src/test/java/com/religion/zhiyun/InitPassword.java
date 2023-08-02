package com.religion.zhiyun;

import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class InitPassword {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Test
    void contextLoads(){
        ParamsVo vo=new ParamsVo();
        vo.setPage(1);
        vo.setSize(100000);
        List<SysUserEntity> usersByPage = sysUserMapper.getUsersByPage(vo);
        for(int i=0;i<usersByPage.size();i++){
            SysUserEntity sy = usersByPage.get(i);
            String passwords = sy.getPasswords();
            if(GeneTool.isEmpty(passwords)){
                String identity = sy.getIdentity();
                String userMobile = sy.getUserMobile();
                String passwor ="ASqw@!12";
                String s = this.passwordSalt(userMobile, passwor, identity);
                sy.setWeakPwInd(passwor);
                sy.setPasswords(s);
                sysUserMapper.updatePassword(s,passwor,sy.getUserId(), TimeTool.getYmdHms());
            }
        }


    }

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
