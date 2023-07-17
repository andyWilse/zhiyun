package com.religion.zhiyun.patrol.service;

import com.religion.zhiyun.utils.response.AppResponse;

import java.util.Map;

public interface PatrolService {

    /**瓯海**/
    AppResponse getOuHai();

    /**街镇**/
    AppResponse getTown();

    /**详情**/
    AppResponse getVenuesDetail(Map<String,Object> map);

    /**街镇汇总**/
    AppResponse getTownSummary(Map<String,Object> map);

    /**场所巡查任务完成情况**/
    AppResponse getVenuesItems(Map<String,Object> map);

    /**场所综合得分排行榜**/
    AppResponse getVenuesRank(String type);

    /**街镇综合得分排行榜**/
    AppResponse getTownRank();
}
