package com.religion.zhiyun;

import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.dao.VenuesManagerMapper;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class InitPassword {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private VenuesManagerMapper venuesManagerMapper;
    @Test
    void contextLoads(){
        ParamsVo vo=new ParamsVo();
        vo.setPage(1);
        vo.setSize(100000);
        /*List<SysUserEntity> usersByPage = sysUserMapper.getUsersByPage(vo);
        for(int i=0;i<usersByPage.size();i++){
            SysUserEntity sy = usersByPage.get(i);
            String passwords = sy.getPasswords();
                String identity = sy.getIdentity();
                String userMobile = sy.getUserMobile();
                String passwor ="ASqw@!12";
                String s = this.passwordSalt(userMobile, passwor, identity);
                sy.setPasswords(s);
                sysUserMapper.updatePassword(s,sy.getUserId(), TimeTool.getYmdHms());

        }*/

        List<Map<String, Object>> manager = venuesManagerMapper.findManager(vo);
        for(int i=0;i<manager.size();i++){
            Map<String, Object> aa = manager.get(i);
            String passwor ="ASqw@!12";
            String managerMobile = (String) aa.get("managerMobile");
            String typeCd = (String) aa.get("typeCd");
            Integer managerId = (Integer) aa.get("managerId");
            String passwors = this.passwordSalt(managerMobile, passwor, typeCd);
            venuesManagerMapper.updatePass(managerId,passwors);
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
