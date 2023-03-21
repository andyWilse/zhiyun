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
    RespPageBean add(NewsEntity newsEntity,String token);

    /**
     * 修改
     * @param newsEntity
     */
    RespPageBean update(NewsEntity newsEntity,String token);

    /**
     * 下架
     * @param newsId
     */
    void delete(int newsId);

    /**
     * 分页查询
     * @param map
     * @throws IOException
     */
    PageResponse getNewsByPage(Map<String, Object> map, String token);
}
