package com.religion.zhiyun.news.dao;

import com.religion.zhiyun.news.entity.NewsEntity;
import org.apache.ibatis.annotations.Mapper;
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
     * 下架
     * @param newsId
     */
    void delete(int newsId);

    /**
     * 总条数
     * @return
     */
    long getTotal();

    /**
     * 分页查询
     * @param page
     * @param size
     * @param newsTitle
     * @return
     * @throws IOException
     */
    List<NewsEntity> getNewsByPage(Integer page, Integer size, String newsTitle) throws IOException;

}
