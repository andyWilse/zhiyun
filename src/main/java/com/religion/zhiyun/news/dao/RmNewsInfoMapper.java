package com.religion.zhiyun.news.dao;

import com.religion.zhiyun.news.entity.NewsEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface RmNewsInfoMapper {

    /**
     * 新增
     * @param newsEntity
     */
    void add(NewsEntity newsEntity);

    /**
     * 修改
     * @param newsEntity
     */
    void update(NewsEntity newsEntity);

    /**
     * 根据id获取
     * @param newsId
     */
   // NewsEntity getNewsById(int newsId);

    /**
     * 新闻上下架
     * @param newsEntity
     */
    void updateNewsDown(NewsEntity newsEntity);

    /**
     * 下架
     * @param newsId
     */
    void delete(int newsId);


    /**
     * 分页查询
     * @param vo
     * @return
     */
    List<NewsEntity> getNewsByPage(@Param("vo") ParamsVo vo);
    /**总条数**/
    long getTotal(@Param("vo") ParamsVo vo);

    /**
     * 分页查询(pc)
     * @param vo
     * @return
     */
    List<NewsEntity> getPcNewsPage(@Param("vo") ParamsVo vo);
    /**总条数**/
    long getPcNewsTotal(@Param("vo") ParamsVo vo);

    List<NewsEntity> getNewsContent(@Param("newId") int newId);

}
