package com.religion.zhiyun.venues.services;

import com.religion.zhiyun.venues.entity.VenuesEntity;

public interface RmVenuesInfoService {

    void add(VenuesEntity venuesEntity);
    void update(VenuesEntity venuesEntity);
    void delete(String venuesId);
    VenuesEntity getByResponsiblePerson(String responsiblePerson);
}
