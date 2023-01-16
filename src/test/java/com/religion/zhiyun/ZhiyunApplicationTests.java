package com.religion.zhiyun;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@SpringBootTest
class ZhiyunApplicationTests {

    @Test
    void contextLoads() throws FileNotFoundException {

        String str="123";
        String s = DigestUtils.md5Hex(str);
        System.out.println(s);

    }

}
