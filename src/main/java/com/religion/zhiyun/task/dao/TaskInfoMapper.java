package com.religion.zhiyun.task.dao;

import com.religion.zhiyun.interfaces.entity.minzong.SubmitEntity;
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
    List<Map<String,Object>> queryTaskCommon(String procInstId,String loginNm);
    List<Map<String,Object>> getTaskCommon(String procInstId);
    //获取推送人
    Map<String,Object> getTaskSend(@Param("procInstId")String procInstId,@Param("actId")String actId);
    /**一键上报人**/
    String getReportMen(String procInstId);
    /** 任务数量统计 **/
    Map<String,Object> getTaskNum(@Param("loginNm")String loginNm);

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
    List<Map<String,Object>> getTaskAgvGather(@Param("num") int num,
                                              @Param("handle") String handle,
                                              @Param("identityArr") String[] identityArr);

    /**
     *获取我的任务（app）
     * @param vo
     * @return
     */
    //我发起的
    List<Map<String,Object>> getMyLaunchTask(@Param("vo") ParamsVo vo);
    Long getMyLaunchTaskTotal(@Param("vo") ParamsVo vo);
    //我执行的
    List<Map<String,Object>> getMyHandleTask(@Param("vo") ParamsVo vo);
    Long getMyHandleTotal(@Param("vo") ParamsVo vo);
    //全部
    List<Map<String,Object>> getMyTask(@Param("vo") ParamsVo vo);
    Long getMyTaskTotal(@Param("vo") ParamsVo vo);
    //首页（非ai）
    List<Map<String,Object>> getFirstTask(@Param("vo") ParamsVo vo);
    Long getFirstTaskTotal(@Param("vo") ParamsVo vo);

    /**
     *获取我的任务（PC）
     * @param vo
     * @return
     */
    //待完成
    List<Map<String,Object>> getUnHandleTask(@Param("vo") ParamsVo vo);
    Long getUnHandleTaskTotal(@Param("vo") ParamsVo vo);
    //我经手的
    List<Map<String,Object>> getHandTask(@Param("vo") ParamsVo vo);
    Long getHandTaskTotal(@Param("vo") ParamsVo vo);
    /**
     * 任务详情
     * @param procInstId
     * @return
     */
    List<Map<String,Object>> getTaskDetail(@Param("login") String login,@Param("procInstId") String procInstId);
    /**
     * 发起人
     * @param assignee
     * @param procInstId
     */
    void updateHiActinst(@Param("assignee") String assignee,@Param("procInstId") String procInstId);

    /**
     * 维修设备任务
     * @param assignee
     * @param type
     * @param handleResults
     * @return
     */
    List<Map<String,Object>> getRepairTask(@Param("assignee") String assignee,@Param("type") String type,
                                           @Param("handleResults") String handleResults);

    /**
     * 根据事件id获取任务信息
     * @param eventId
     * @return
     */
    List<TaskEntity> getTaskByEventId(@Param("eventId") String eventId);

    /**
     * 根据流程获取事件信息
     * @param procInstId
     * @return
     */
    Map<String,Object> getEvTaDetail(@Param("procInstId") String procInstId);

    /**
     * 获取上报数据
     * @param procInstId
     * @param login
     * @return
     */
    Map<String,Object> getSubmitEvent(@Param("procInstId") String procInstId,@Param("login") String login);


}
