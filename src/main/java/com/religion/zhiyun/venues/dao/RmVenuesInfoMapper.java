package com.religion.zhiyun.venues.dao;

import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmVenuesInfoMapper {
    //查询
    public List<VenuesEntity> queryAll();

    /**
     * 新增
     * @param venuesEntity
     */
    void add(VenuesEntity venuesEntity);

    /**
     * 修改
     * @param venuesEntity
     */
    void update(VenuesEntity venuesEntity);

    /**
     * 删除
     * @param venuesId
     */
    void delete(String venuesId);

    /**
     * 根据负责人获取
     * @param responsiblePerson
     * @return
     */
    VenuesEntity getByResponsiblePerson(String responsiblePerson);


}
