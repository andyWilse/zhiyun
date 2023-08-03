package com.religion.zhiyun.venues.dao;

import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.entity.VenuesManagerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface VenuesManagerMapper {

    /**
     * 新增
     * @param venuesManagerEntity
     */
    void add(VenuesManagerEntity venuesManagerEntity);

    /**
     * 修改
     * @param venuesManagerEntity
     */
    void update(VenuesManagerEntity venuesManagerEntity);

    /**
     * 删除
     * @param managerId
     */
    int delete(int managerId, String lastModifier, String lastModifyTime);

    /**
     * 查询
     * @param search
     * @return
     */
     List<VenuesManagerEntity> query(@Param("search") String search);

    /**
     * 查询
     * @param name
     * @return
     */
    List<Map<String,Object>> getManagerByNm(@Param("name") String name);

    /**
     * 查询
     * @param managerId
     * @return
     */
    String getManagerById(@Param("managerId") String managerId);

    /**
     * 分页查询（pc）
     * @param vo
     * @return
     */
    List<Map<String,Object>> findManager(@Param("vo") ParamsVo vo);
    Long findManagerTotal(@Param("vo") ParamsVo vo);

    /**
     *
     * @param managerId
     * @return
     */
    List<VenuesManagerEntity> getByManagerId(@Param("managerId") String managerId);


    void updatePass(@Param("managerId") int managerId,@Param("passwors") String passwors);
}
