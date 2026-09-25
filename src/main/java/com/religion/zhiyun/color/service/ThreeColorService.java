package com.religion.zhiyun.color.service;

import com.religion.zhiyun.color.entity.ThreeColorEntity;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.venues.entity.ParamsVo;

import java.util.List;
import java.util.Map;

public interface ThreeColorService {

    /** 三色要素（新增）**/
    AppResponse threeColorAdd(ThreeColorEntity threeColorEntity);

    /** 三色要素（excel导入）**/
    AppResponse threeColorUpload(Map<String,Object> map);

    //保存数据
    AppResponse threeColorImport(Map<String,Object> map);

    /** 三色要素（修改）**/
    AppResponse threeColorUpdate(ThreeColorEntity threeColorEntity);

    /** 三色要素（删除）**/
    AppResponse threeColorDelete(Map<String,Object> map);

    /** 三色要素（单条数据获取）**/
    AppResponse getThreeColor(int coId);

    /** 三色要素（列表数据获取）**/
    AppResponse getThreeColorList(ParamsVo vo);

    //三色要素（是否纳入统计（是否在终端展示））
    AppResponse threeColorShow(Map<String,Object> map);
}
