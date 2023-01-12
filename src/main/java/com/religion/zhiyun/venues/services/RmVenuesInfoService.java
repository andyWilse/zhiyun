package com.religion.zhiyun.venues.services;

import com.religion.zhiyun.venues.entity.VenuesEntity;

import java.util.List;
import java.util.Map;

public interface RmVenuesInfoService {

    void add(VenuesEntity venuesEntity);
    void update(VenuesEntity venuesEntity);
    void delete(String venuesId);
    VenuesEntity getByResponsiblePerson(String responsiblePerson);

    List<VenuesEntity> queryAll();

    List<VenuesEntity> querySectAll(String religiousSect);
    List<VenuesEntity> getByVenuesFaculty(String venuesName,String responsiblePerson);
    VenuesEntity getVenueByID(String venuesId);
    List<Map<String,Object>> getAllNum();
}
