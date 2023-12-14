package com.religion.zhiyun.init;

import com.religion.zhiyun.login.service.SysLoginService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.utils.fileutil.DownloadPicture;
import com.religion.zhiyun.venues.dao.VenuesManagerMapper;
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
    @Autowired
    private VenuesManagerMapper venuesManagerMapper;

    @Autowired
    private SysLoginService LoginService;
    @Test
    void contextLoads(){
        DownloadPicture.downloadPic("http://39.174.220.72:8084/20230717/20230717222923.jpg","");
       /* String username = "18514260203";//电话
        String verifyCode = "";//验证码
        String password= "ASqw@!34";//密码
        LoginService.updatePassword(verifyCode,password,username);*/
        /*int userId = 1001;
        String userNbr = "1001";
        String passwor ="ASqw@!12";
        String s = this.passwordSalt(String.valueOf(userId), passwor, userNbr);
        //sy.setPasswords(s);
        sysUserMapper.updatePassword(s,userId, TimeTool.getYmdHms());*/
        /*ParamsVo vo=new ParamsVo();
        vo.setPage(0);
        vo.setSize(100000);
        List<SysUserEntity> usersByPage = sysUserMapper.getUsersByPage(vo);
        for(int i=0;i<usersByPage.size();i++){
            SysUserEntity sy = usersByPage.get(i);
            String passwords = sy.getPasswords();
            //String identity = sy.getIdentity();
            //String userMobile = sy.getUserMobile();
            int userId = sy.getUserId();
            String userNbr = sy.getUserNbr();
            String passwor ="ASqw@!12";
            String s = this.passwordSalt(String.valueOf(userId), passwor, userNbr);
            sy.setPasswords(s);
            sysUserMapper.updatePassword(s,sy.getUserId(), TimeTool.getYmdHms());

        }*/

       /* List<Map<String, Object>> manager = venuesManagerMapper.findManager(vo);
        for(int i=0;i<manager.size();i++){
            Map<String, Object> aa = manager.get(i);
            String passwor ="ASqw@!12";
            String managerMobile = (String) aa.get("managerMobile");
            String typeCd = (String) aa.get("typeCd");
            Integer managerId = (Integer) aa.get("managerId");
            String passwors = this.passwordSalt(managerMobile, passwor, typeCd);
            venuesManagerMapper.updatePass(managerId,passwors);
        }*/

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
