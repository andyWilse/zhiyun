package com.religion.zhiyun.record.dao;

import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OperateRecordMapper {
    /**
     * 新增数据
     * @param logsEntity
     */
    void add(RecordEntity logsEntity);
    /**
     * 分页查询
     * @param vo
     * @return
     */

    List<VenuesEntity> findRecordByPage(@Param("vo") ParamsVo vo);

    /**
     * 获取总条数
     * @return
     */
    Long getTotal(@Param("vo") ParamsVo vo);
}
