package com.religion.zhiyun.staff.dao;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 分页查询
     * @param page
     * @param size
     * @param staffName
     * @param staffPost
     * @param religiousSect
     * @return
     */
    List<VenuesEntity> getStaffByPage(@Param("page") Integer page,
                                      @Param("size") Integer size,
                                      @Param("staffName") String staffName,
                                      @Param("staffPost") String staffPost,
                                      @Param("religiousSect") String religiousSect);

    /**
     * 获取总条数
     * @return
     */
    Long getTotal();

    /**
     *
     */
    Long getMaxStaffCd();
}
