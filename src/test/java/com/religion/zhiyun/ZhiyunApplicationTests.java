package com.religion.zhiyun;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZhiyunApplicationTests {

    @Test
    void contextLoads() {
        VenuesEntity venuesEntity =new VenuesEntity();

        venuesEntity.setVenuesId("1001");
        venuesEntity.setCreateTime("2022-01-01");
        venuesEntity.setCreator("a1");
        venuesEntity.setVenuesAddres("beijing");
        venuesEntity.setLastModifier("b1");
        venuesEntity.setLastModifyTime("2022-12-12");
        venuesEntity.setNumber("001");
        venuesEntity.setUseStatus("02");
        venuesEntity.setPicturesOne("c://001.pic");
        String s = JsonUtils.beanToJson(venuesEntity);
        System.out.println(s);
    }

}
