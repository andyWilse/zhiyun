package com.religion.zhiyun.patrol.dao;


import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PatrolMapper {

    List<Map<String,Object>> getOuHai();

    List<Map<String,Object>> getTown();

    List<Map<String,Object>> getTownCode();

    List<Map<String,Object>> getFireDetail(@Param("stat") String stat,@Param("town") String town);
    List<Map<String,Object>> getSelfDetail(@Param("stat") String stat,@Param("town") String town);
    List<Map<String,Object>> getBuildDetail(@Param("stat") String stat,@Param("town") String town);

    List<Map<String,Object>> getTownSummary(@Param("town") String town);
    Map<String,Object> getVenuesScore(@Param("venuesId") String venuesId,@Param("venuesName") String venuesName);

    List<Map<String,Object>>  getTotalItem(@Param("venuesId") String venuesId,@Param("venuesName") String venuesName);
    List<Map<String,Object>>  getFireItem(@Param("venuesId") String venuesId,@Param("venuesName") String venuesName);
    List<Map<String,Object>>  getSelfItem(@Param("venuesId") String venuesId,@Param("venuesName") String venuesName);

    List<Map<String,Object>>  getVenuesRank(@Param("type") String type);
    List<Map<String,Object>>  getTownRank();

}
