package com.religion.zhiyun.staff.dao;

import com.religion.zhiyun.staff.entity.StaffEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmStaffInfoMapper {
    /**
     * 新增
     * @param staffEntity
     */
    void add(StaffEntity staffEntity);

    //查询
    List<StaffEntity> all();

    /**
     * 修改
      * @param staffEntity
     */
    void update(StaffEntity staffEntity);

    /**
     * 删除
     * @param staffId
     */
    void delete(String staffId);

}
