package com.religion.zhiyun.task.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ActReProcdefMapper {

    List<Map<String,Object>> queryTasks();
}
