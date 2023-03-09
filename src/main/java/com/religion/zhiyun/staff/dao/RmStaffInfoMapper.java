package com.religion.zhiyun.staff.dao;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
    void delete(int staffId);

    /**
     * 分页查询
     * @param page
     * @param size
     * @param staffName
     * @param staffPost
     * @param religiousSect
     * @return
     */
    List<StaffEntity> getStaffByPage(@Param("page") Integer page,
                                      @Param("size") Integer size,
                                      @Param("staffName") String staffName,
                                      @Param("staffPost") String staffPost,
                                      @Param("religiousSect") String religiousSect);

    /**
     * 获取总条数
     * @return
     */
    Long getTotal( @Param("staffName") String staffName,
                   @Param("staffPost") String staffPost,
                   @Param("religiousSect") String religiousSect);

    /**
     *获取编号
     */
    Long getMaxStaffCd();

    /**
     * 根据场所获取职员信息
     * @param relVenuesId
     * @return
     */
    List<StaffEntity> getStaffByVenuesId(@Param("relVenuesId") Integer relVenuesId);

    /**
     * 根据条件获取职员信息
     * @param username
     * @return
     */
    List<Map<String,Object>> getByTel(@Param("username") String username);

    /**
     * 密码修改
     * @param passwords
     * @param passwordsOrigin
     * @param staffId
     * @param lastModifier
     * @param lastModifyTime
     */
    void updatePassword(@Param("passwords") String passwords,
                        @Param("passwordsOrigin") String passwordsOrigin,
                        @Param("staffId") int staffId,
                        @Param("lastModifier") String lastModifier,
                        @Param("lastModifyTime") String lastModifyTime);

    /*
    根据id获取负责人画像
     */
        Map<String,Object> getManagerById(@Param("ManagerId")String ManagerId);

    /*
    根据ID获取关联场所
     */
    List<Map<String,Object>> getVenuesByManagerId(@Param("ManagerId")String ManagerId);

    //hu活动备案
    List<Map<String,Object>> getFilingByManagerId(@Param("ManagerId") String ManagerId);
}
