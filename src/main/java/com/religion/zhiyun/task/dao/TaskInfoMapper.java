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
    List<Map<String,Object>> queryByInstId(@Param("page") Integer page,
                             @Param("size") Integer size,
                             List<String> idList);

    /*** 获取流程部署信息  **/
    List<ProcdefEntity> getProcdef(@Param("page") Integer page,
                                   @Param("size") Integer size,
                                   @Param("taskName") String taskName);

    /**保存备案信息**/
    void addFill(TaskEntity taskEntity);

    /**更新备案信息**/
    void updateFill(TaskEntity taskEntity);

    /**获取下达范围**/
    List<String> getIssuedUsers(@Param("loginNm") String loginNm,@Param("province") String province,@Param("city") String city,
                          @Param("area") String area,@Param("town") String town,@Param("relVenuesId") String relVenuesId,
                          @Param("identityArr") String[] identityArr);

    /**获取街道干事**/
    List<String> getJieGanUsers(@Param("relVenuesId") String relVenuesId);
}
