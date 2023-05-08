package com.religion.zhiyun.sys.feedback.dao;

import com.religion.zhiyun.sys.feedback.entity.FeedbackEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FeedbackMapper {
    /**
     * 新增
     * @param feedbackEntity
     * @return
     */
    int add(FeedbackEntity feedbackEntity);
}
