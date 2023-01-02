package com.religion.zhiyun;

import com.religion.zhiyun.user.entity.UserLogin;
import com.religion.zhiyun.user.dao.UserLoginMapper;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
public class Weixin01ApplicationTests {
    @Autowired
    DataSource dataSource;
    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println(connection);

        //template模板，拿来即用
        connection.close();
    }
    @Autowired
    UserLoginMapper userLoginMapper;

    @Autowired
    RmVenuesInfoMapper rmVenuesInfoMapper;
    @Test
    public void toTest(){
        /*List<UserLogin> userLogins = userLoginMapper.queryAll();
        userLogins.forEach(e-> System.out.println(e));*/

        List<VenuesEntity> userLogins = rmVenuesInfoMapper.queryAll();
        userLogins.forEach(e-> System.out.println(e));
    }

}
