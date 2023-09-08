package com.religion.zhiyun.init;

import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class InitOrgan {
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;
    @Test
    void contextLoads(){
        List<VenuesEntity> venuesEntities = rmVenuesInfoMapper.queryOra("");
        for(int i=0;i<venuesEntities.size();i++) {
            VenuesEntity ve = venuesEntities.get(i);
            int venuesId = ve.getVenuesId();
            String organization = ve.getOrganization();
            rmVenuesInfoMapper.updateOra(organization,venuesId);

        }
        }
}
