package com.religion.zhiyun.task.dao;

import com.religion.zhiyun.task.entity.ProcdefEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TaskInfoMapper {
    /**新增**/
    void addTask(TaskEntity taskEntity);

    /**修改**/
    void updateTask(TaskEntity taskEntity);

    /**分页查询**/
    List<TaskEntity> queryTasks(@Param("page") Integer page,
                                @Param("size") Integer size,
                                @Param("taskName") String taskName,
                                @Param("taskContent") String taskContent,
                                @Param("loginNm") String loginNm
                                );

    /** 任务数量统计 **/
    Map<String,Object> getTaskNum(String loginNm);

    /** 根据任务id查询 **/
    TaskEntity queryByInstId(String procInstId);

    /*** 获取流程部署信息  **/
    List<ProcdefEntity> getProcdef(@Param("page") Integer page,
                                   @Param("size") Integer size,
                                   @Param("taskName") String taskName);

    /**根据手机号及身份，获取上级信息**/


}
