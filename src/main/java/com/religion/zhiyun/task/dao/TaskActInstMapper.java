package com.religion.zhiyun.task.dao;

import com.religion.zhiyun.task.entity.ActInstEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TaskActInstMapper {

    /**新增**/
    void addAct(ActInstEntity actEntity);

    /**更新**/
    void updateAct(ActInstEntity actEntity);

    /**
     * 获取任务主信息
     * @param procInstId
     * @return
     */
    List<Map<String,Object>> getAiTaskDetail(@Param("login") String login,@Param("procInstId") String procInstId);

    /**
     * 获取任务流程信息
     * @param procInstId
     * @return
     */
    List<ActInstEntity> getAiTaskAct(@Param("procInstId") String procInstId,@Param("actId") Integer actId);

    /**
     * 获取任务流程详细信息
     * @param procInstId
     * @param actId
     * @return
     */
    List<Map<String,Object>> getAiTaskActDetail(@Param("procInstId") String procInstId,@Param("actId") Integer actId);

}
