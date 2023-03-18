package com.religion.zhiyun.task.dao;

import com.religion.zhiyun.task.entity.ProcdefEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
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
    List<Map<String,Object>> queryTasks(@Param("vo") ParamsVo vo);
    Long queryTasksTotal(@Param("vo") ParamsVo vo);
    /**流转意见**/
    List<Map<String,Object>> queryTaskCommon(String procInstId);

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

    /**获取历史备案信息**/
    List<Map<String,Object>>  getFillHistory( @Param("search") String search,
                                              @Param("login") String login,
                                              @Param("page") Integer page,
                                              @Param("size") Integer size);

    /**获取历史备案信息**/
    List<Map<String,Object>>  getFillHisDetail( @Param("filingId") String filingId);

    /**获取维设备信息**/
    List<Map<String,Object>>  getMonitorTask( @Param("taskId") String taskId);

    /**获取**/
    String  getTaskContent(@Param("procInstId") String procInstId);

    /**
     * 月（10个月，传-9）
     * @param num
     * @return
     */
    List<Map<String,Object>> getTaskMonth(@Param("num") int num,@Param("login") String login);

    /**
     * 日(10天，传-10)
     * @param num
     * @return
     */
    List<Map<String,Object>> getTaskDay(@Param("num") int num,@Param("login") String login);

    /**
     * 周
     * @param num（10周，传-10）
     * @param （=7*(num+1)-1）
     * @return
     */
    List<Map<String,Object>> getTaskWeek(@Param("num") int num,@Param("login") String login);

    /**
     * 比较
     * @param num
     * @param handle
     * @return
     */
    List<Map<String,Object>> getTaskGather(@Param("num") int num,@Param("handle") String handle);

    /**
     *获取我的任务（app）
     * @param vo
     * @return
     */
    List<Map<String,Object>> getMyTask(@Param("vo") ParamsVo vo);
    Long getMyTaskTotal(@Param("vo") ParamsVo vo);
    /**
     * 发起人
     * @param assignee
     * @param procInstId
     */
    void updateHiActinst(@Param("assignee") String assignee,@Param("procInstId") String procInstId);

}
