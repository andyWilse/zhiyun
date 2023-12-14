package com.religion.zhiyun.init;

import com.religion.zhiyun.sys.menus.dao.MenuInfoMapper;
import com.religion.zhiyun.sys.menus.dao.RolePesnMapper;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GrandInit {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RolePesnMapper rolePesnMapper;

    @Autowired
    private MenuInfoMapper menuInfoMapper;

    @Test
    void contextLoads(){
       /* String menus = menuInfoMapper.getMenus();
        System.out.println(menus);
*/
        ParamsVo vo=new ParamsVo();
        vo.setPage(0);
        vo.setSize(100000);
        List<SysUserEntity> usersByPage = sysUserMapper.getUsersByPage(vo);
        String qu="1001,1002,1003,1004,1006,1007,1008,10010001,10010002,10010003,10030002,10030003,10060001,10060002,10060003,1001000101,1001000102,1001000103,1001000201,1001000301,1001000302,1001000303,1002000101,1002000102,1002000103,1002000104,1002000105,1007000101,1007000102,1007000103";
        String jie="1001,1002,1003,1004,1006,10010001,10010002,10010003,10030002,10030003,10060001,10060002,10060003,1001000101,1001000102,1001000103,1001000201,1001000301,1001000302,1001000303,1002000101,1002000102,1002000103,1002000104,1002000105";
        String zhu="1001,1003,1004,10010001,10010002,10010003,10030002,10030003,1001000101,1001000102,1001000103,1001000201,1001000301,1001000302,1001000303";
        String[] split = qu.split(",");//30

        String[] split1 = jie.split(",");//25

        String[] split2 = zhu.split(",");//15
        String[] split3 = zhu.split(",");
  /*      String[] split ={};
        for(int i=0;i<usersByPage.size();i++){
            SysUserEntity sy = usersByPage.get(i);

            int userId = sy.getUserId();
            String identity= sy.getIdentity();
            if(identity.equals("10000002") || identity.equals("10000003")){
                split =qu.split(",");
            }else if(identity.equals("10000004") || identity.equals("10000005")){
                split =jie.split(",");
            }else if(identity.equals("10000006") || identity.equals("10000007")){
                split =zhu.split(",");
            }

            if(null!=split && split.length>0){
                long delete = rolePesnMapper.deleteUserGrand(String.valueOf(userId));
                //新增数据
                if(null!=split && split.length>0){
                    for (String postCd : split) {
                        rolePesnMapper.addUserGrand(postCd,String.valueOf(userId));
                    }
                }
            }
        }*/
    }
}
