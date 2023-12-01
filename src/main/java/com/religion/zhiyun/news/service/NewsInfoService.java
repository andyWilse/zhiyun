package com.religion.zhiyun.news.service;

import com.religion.zhiyun.news.entity.NewsEntity;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import java.io.IOException;
import java.util.Map;

public interface NewsInfoService {
    /**
     * 新增
     * @param newsEntity
     */
    PageResponse add(NewsEntity newsEntity,String token);

    /**
     * 修改
     * @param newsEntity
     */
    PageResponse update(NewsEntity newsEntity,String token);

    /**
     * 获取修改信息
     * @param newsId
     * @return
     */
    PageResponse getNewsById(int newsId);

    /**
     * 下架
     * @param newsId
     */
    void delete(int newsId,String token);

    /**
     * 上下架
     * @param map
     */
    PageResponse newsDown(Map<String, Object> map, String token);

    /**
     * 分页查询
     * @param map
     * @throws IOException
     */
    PageResponse getNewsByPage(Map<String, Object> map, String token);

    /**
     * 分页查询(pc)
     * @param map
     * @throws IOException
     */
    PageResponse getNewsPage(Map<String, Object> map, String token);

    /**
     * 新闻链接
     * @param newId
     * @return
     */
    PageResponse getNewsContent(int newId);
}
