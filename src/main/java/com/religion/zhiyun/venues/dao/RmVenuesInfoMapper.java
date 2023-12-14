package com.religion.zhiyun.venues.dao;

import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RmVenuesInfoMapper {
    /**
     * pc下拉框获取
     * @param vo
     * @return
     */
    public List<VenuesEntity> querySelect(@Param("vo") ParamsVo vo);

    /**
     * app下拉使用(监管)
     * @param vo
     * @return
     */
    public List<Map<String,Object>> queryVenues(@Param("vo") ParamsVo vo);

    /**
     *场所更新：下拉使用(管理)
     * @param login
     * @param search
     * @return
     */
    public List<Map<String,Object>> queryVenuesJz(@Param("login") String login,@Param("search") String search);

    /**
     * 新增
     * @param venuesEntity
     */
    int add(VenuesEntity venuesEntity);

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
     * 场所更新流程
     * @param venuesEntity
     */
    void updateFillVenues(VenuesEntity venuesEntity);


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

    //根据id获取该教堂
    Map<String,Object> getVenuesByID(String venuesId);

    /**
     * 获取各教堂数量
     * @return
     */
    Map<String,Object> getAllNum(@Param("vo") ParamsVo vo );

    List<Map<String,Object>> getDialogVenue(@Param("vo") ParamsVo vo );
    Long getDialogVenueTotal(@Param("vo") ParamsVo vo );

    /**
     * 分页查询
     * @param
     * @return
     */
    List<VenuesEntity> getVenuesByPage(@Param("page")Integer page,
                                       @Param("size") Integer size,
                                       @Param("venue")VenuesEntity venue ,
                                       @Param("relVenuesArr")String[] relVenuesArr);

    /**
     * 总条数
     * @return
     */
    Long getTotal( @Param("venue")VenuesEntity venue , @Param("relVenuesArr")String[] relVenuesArr);

    /**
     * 更新经纬度
     * @param longitude
     * @param Latitude
     * @param venuesId
     */
    void updateLngLat(@Param("longitude") String longitude,@Param("Latitude") String Latitude,@Param("venuesId") Integer venuesId);

    //查询
    public List<Map<String,Object>> getMapVenues(@Param("vo") ParamsVo vo);

    /**
     * 获取备案信息
     * @param relVenuesId
     * @return
     */
    public List<Map<String,Object>> getFiling(@Param("relVenuesId") String relVenuesId);

    /**
     * 获取三人驻堂信息
     * @param relVenuesId
     * @return
     */
    public List<Map<String,Object>> getUsers(@Param("relVenuesId") String relVenuesId);

    /**
     * 获取监管干部
     * @param relVenuesId
     * @return
     */
    public List<Map<String,Object>> getGanUsers(@Param("relVenuesId") String relVenuesId);

    /**
     * 获取教职人员
     * @param relVenuesId
     * @return
     */
    public List<Map<String,Object>> getStaffs(@Param("relVenuesId") String relVenuesId);
    public List<Map<String,Object>> getVenuesStaffs(@Param("venuesStaffArr") String[] venuesStaffArr);

    /**
     * 根据场所名获取信息
     * @param venuesName
     * @return
     */
    public List<Map<String,Object>> getVenuesByNm(@Param("venuesName") String venuesName);

    /**
     * 获取教职人员（场所更新使用）
     * @param relVenuesId
     * @return
     */
    public String getStaffJz(@Param("relVenuesId") String relVenuesId);

    //根据教派类别获取场所信息
    List<Map<String,Object>> getvenuesByType(@Param("Type")String Type);


    List<Map<String,Object>> getVenuesScore(@Param("num")Integer num);

    List<VenuesEntity> queryOra(String religiousSect);
    void updateOra(@Param("organization") String organization,@Param("venuesId") Integer venuesId);

}
