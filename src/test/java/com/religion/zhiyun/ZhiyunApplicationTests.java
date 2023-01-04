package com.religion.zhiyun;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class ZhiyunApplicationTests {

    @Test
    void contextLoads() {
        VenuesEntity venuesEntity =new VenuesEntity();
        venuesEntity.setVenuesId(1001);
        venuesEntity.setVenuesName("DAO_001");
        venuesEntity.setReligiousSect("DAO_JIOA");
        venuesEntity.setRegisterNbr("d001");
        venuesEntity.setVenuesPhone(1886523001);
        venuesEntity.setOrganization("瓯海街道");
        venuesEntity.setVenuesAddres("F:\\15outwork\\p001.pic");
        venuesEntity.setPicturesOne("");
        venuesEntity.setPicturesTwo("");
        venuesEntity.setPicturesThree("");
        venuesEntity.setResponsiblePerson("1001");
        venuesEntity.setLiaisonMan("2001");
        venuesEntity.setBriefIntroduction("");
        venuesEntity.setVenuesStatus("01");
        venuesEntity.setCreator("c0001");
        venuesEntity.setCreateTime((Timestamp) new Date());
        venuesEntity.setLastModifier("l0001");
        venuesEntity.setLastModifyTime((Timestamp) new Date());

        SimpleDateFormat s=new SimpleDateFormat("YYYY-MM-DD HH:SS:MM");

        System.out.println(JsonUtils.beanToJson(venuesEntity));
    }

}
