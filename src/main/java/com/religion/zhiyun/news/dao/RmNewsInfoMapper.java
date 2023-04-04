package com.religion.zhiyun.news.dao;

import com.religion.zhiyun.news.entity.NewsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

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
    NewsEntity getNewsById(int newsId);

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
     * @param page
     * @param size
     * @param newsTitle
     * @return
     * @throws IOException
     */
    List<NewsEntity> getNewsByPage(Integer page, Integer size, String newsTitle, String newsFor,String newsRefType);
    /**总条数**/
    long getTotal(String newsTitle, String newsFor,String newsRefType);

    /**
     * 分页查询(pc)
     * @param page
     * @param size
     * @param newsTitle
     * @return
     * @throws IOException
     */
    List<NewsEntity> getPcNewsPage(@Param("page")Integer page, @Param("size")Integer size, @Param("newsTitle")String newsTitle);
    /**总条数**/
    long getPcNewsTotal(@Param("newsTitle")String newsTitle);

}
