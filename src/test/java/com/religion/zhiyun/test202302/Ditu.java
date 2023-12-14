package com.religion.zhiyun.test202302;

import com.religion.zhiyun.utils.map.GeocoderLatitudeUtil;
import com.religion.zhiyun.utils.map.GetLngAndLagGaoDe;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SpringBootTest
class Ditu {

    @Autowired
    private RmVenuesInfoMapper  mVenuesInfoMapper;
    @Test

    void contextLoads() throws FileNotFoundException, UnsupportedEncodingException {
        ParamsVo vo=new ParamsVo();
        List<VenuesEntity> venuesEntities = mVenuesInfoMapper.querySelect(vo);
        for(int i=0;i<venuesEntities.size();i++){
            VenuesEntity venuesEntity = venuesEntities.get(i);
            String venuesAddres = venuesEntity.getVenuesAddres();
           /* String coordinate = GeocoderLatitudeUtil.getCoordinate(venuesAddres);
            String[] split = coordinate.split(",");
            String lng=split[0];
            String lat=split[1];*/
            //高德地图
            String lngAndLag = GetLngAndLagGaoDe.getLngAndLag(venuesAddres);
            String[] split = lngAndLag.split(",");
            String lng=split[0];
            String lat=split[1];
            mVenuesInfoMapper.updateLngLat(lng,lat,venuesEntity.getVenuesId());
        }

        //String add="浦东区张杨路1725号";
        //String add="北京市海淀区上地十街10号";
        /*String add="浙江省温州市瓯海区瞿溪街道瞿溪村";

        String coordinate = GeocoderLatitudeUtil.getCoordinate(add);
        System.out.println(coordinate);*/

    }

}
