package com.religion.zhiyun.venues.dao;

import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
    int delete(int venuesId);

    /**
     * 根据负责人获取
     * @param responsiblePerson
     * @return
     */
    VenuesEntity getByResponsiblePerson(String responsiblePerson);


    /**
     * 根据教派类别查询教派
     * @param religiousSect
     * @return
     */
    List<VenuesEntity> querySectAll(String religiousSect);


    /**
     * 根据教堂名字和负责人查询教堂
     * @param venuesName
     * @param responsiblePerson
     * @return
     */
    List<VenuesEntity> getByVenuesFaculty(String venuesName,String responsiblePerson);

    //根据id获取该教堂
    VenuesEntity getVenueByID(String venuesId);
    /**
     * 获取各教堂数量
     * @return
     */
    Map<String,Object> getAllNum();

    /**
     * 分页查询
     * @param page
     * @param size
     * @param venuesName
     * @param responsiblePerson
     * @param religiousSect
     * @return
     */
    List<VenuesEntity> getVenuesByPage(@Param("page") Integer page,
                                    @Param("size") Integer size,
                                    @Param("venuesName") String venuesName,
                                    @Param("responsiblePerson") String responsiblePerson,
                                    @Param("religiousSect") String religiousSect
    );

    /**
     * 总条数
     * @return
     */
    Long getTotal();

}
