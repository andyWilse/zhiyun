package com.religion.zhiyun.user.dao;

import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysUserMapper {

    //根据用户名查询数据
    SysUserEntity queryByName(@Param("loginNm") String loginNm);

    //根据用户名查询数据
    SysUserEntity queryByUserId(@Param("userId") String userId);
    Map<String,Object> getUserId(@Param("userId") String userId);

    //根据用户编号查询数据
    SysUserEntity queryByNbr(@Param("userNbr") String userNbr);

    //根据电话查询用户
    List<SysUserEntity> queryByTel(@Param("userMobile") String userMobile);

    //用户查询
    Map<String,Object> queryBySearch(@Param("search") String search);

    //根据电话查询数量
    Long queryTelNum(@Param("userMobile") String userMobile);

    /**
     * 分页查询
     * @param vo
     * @return
     * @throws IOException
     */
    List<SysUserEntity>  getUsersByPage(@Param("vo") ParamsVo vo);
    /**总条数**/
    Long getTotal(@Param("vo") ParamsVo vo);

    /**
     * 获取客户编号
     * @return
     */
    Long getMaxUserNbr();



    /**
     * 新增
     * @param sysUserEntity
     */
    int add(SysUserEntity sysUserEntity);

    /**
     * 修改
     * @param sysUserEntity
     */
    void update(SysUserEntity sysUserEntity);

    /**
     * 删除
     * @param userId
     */
    void delete(int userId);

    /**
     * 查询发起人
     * @param procInstId
     * @return
     */
    String queryStarter(@Param("procInstId") String procInstId);

    /**
     * 获取三人驻堂的成员
     * @param relVenuesId
     * @return
     */
    List<Map<String,Object>> getSanByVenues(@Param("relVenuesId") Integer relVenuesId);
    //所有
    List<Map<String,Object>> getAllByVenues(@Param("relVenuesId") Integer relVenuesId);
    //街干、三人驻堂
    List<Map<String,Object>> getJgByVenues(@Param("relVenuesId") Integer relVenuesId);
    //街委
    List<Map<String,Object>> getJWVenues(@Param("relVenuesId") Integer relVenuesId);

    /**
     * 街
     * @param loginNm
     * @param identity
     * @return
     */
    public List<SysUserEntity> getJie(@Param("loginNm") String loginNm,@Param("identity") String identity);

    /**
     * 区
     * @param loginNm
     * @param identity
     * @return
     */
    public List<SysUserEntity> getQu(@Param("loginNm") String loginNm,@Param("identity") String identity);


    /**
     * 获取场所内组员数量
     * @return
     */
    int getYuanNum(@Param("relVenuesId") String relVenuesId,@Param("userId") int userId);
    int getZzhNum(@Param("relVenuesId") String relVenuesId,@Param("userId") int userId);


    /**
     * 密码修改
     * @param passwords
     * @param lastModifyTime
     */
    void updatePassword(@Param("passwords") String passwords,
                        @Param("userId") int userId,
                        @Param("lastModifyTime") String lastModifyTime);

    /**
     * 区委
     * @param userMobile
     * @param loginNm
     * @return
     */
    List<Map<String,Object>> getQuWei(@Param("userMobile") String userMobile,@Param("loginNm") String loginNm);

    /**
     * 区干
     * @param userMobile
     * @param loginNm
     * @return
     */
    List<Map<String,Object>> getQuGan(@Param("userMobile") String userMobile,@Param("loginNm") String loginNm);

    /**
     * 街委
     * @param userMobile
     * @param loginNm
     * @return
     */
    List<Map<String,Object>> getJieWei(@Param("userMobile") String userMobile,@Param("loginNm") String loginNm);

    /**
     * 街干
     * @param userMobile
     * @param loginNm
     * @return
     */
    List<Map<String,Object>> getJieGan(@Param("userMobile") String userMobile,@Param("loginNm") String loginNm);

    /**
     * 组长
     * @param userMobile
     * @param loginNm
     * @return
     */
    List<Map<String,Object>> getZuZhang(@Param("userMobile") String userMobile,
                                        @Param("loginNm") String loginNm,
                                        @Param("relVenuesId") String relVenuesId);

    /**
     * 获取场所名称
     * @param venuesIdArr
     * @return
     */
    String getVenuesNm(@Param("venuesIdArr") String[] venuesIdArr);

}
