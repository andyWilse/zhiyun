package com.religion.zhiyun.venues.services;

import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesEntity;

import java.util.List;
import java.util.Map;

public interface RmVenuesInfoService {

    RespPageBean add(VenuesEntity venuesEntity);
    RespPageBean update(VenuesEntity venuesEntity);
    int delete(int venuesId);
    VenuesEntity getByResponsiblePerson(String responsiblePerson);

    List<VenuesEntity> queryAll();

    List<VenuesEntity> querySectAll(String religiousSect);
    List<VenuesEntity> getByVenuesFaculty(String venuesName,String responsiblePerson);
    VenuesEntity getVenueByID(String venuesId);
    List<Map<String,Object>> getAllNum();

    /**
     * 分页查询
     * @param page
     * @param size
     * @param venuesName
     * @param responsiblePerson
     * @param religiousSect
     * @return
     */
    RespPageBean getEmpByPage(Integer page, Integer size, String venuesName, String responsiblePerson, String religiousSect);

}
