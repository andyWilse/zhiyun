package com.religion.zhiyun.task.controller;


import org.activiti.engine.*;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app")
public class TestController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    /*  @Autowired
      private IdentitySer identityService;*/
    /*@Autowired
    private ModelRollBack modelRollBack;
*/

    /**
     * 流程部署。部署一次就可以了。
     * @return
     */
    @RequestMapping("/start")
    @ResponseBody
    public Object get(){
        //第一步
        DeploymentBuilder builder=  repositoryService.createDeployment();
        builder.addClasspathResource("processes/testBpm.bpmn");
        String id = builder.deploy().getId();
        repositoryService.setDeploymentKey(id,"nnffnn4d");
        System.out.println(id);

        return null;
    }

    /**
     * 人员重新申请
     * @return
     */
    @RequestMapping("/rePack")
    @ResponseBody
    public Object rePack(){
        Authentication.setAuthenticatedUserId("wwz");
        Map<String, Object> variables = new HashMap<>();
        //根据流程id获取属于自己的待办。
        List<Task> list = taskService.createTaskQuery().taskAssignee("wwz").processInstanceId("60dbc3ce-9f1b-11ea-bbc3-16fd52790d6f").list();
        if(!ObjectUtils.isEmpty(list)){
            for(Task item:list){
                taskService.complete(item.getId(),variables);

            }
        }
        return list;
    }

    /**
     * 人员提交申请
     * @return
     */
    @RequestMapping("/app1")
    @ResponseBody
    public Object ff(){
        Authentication.setAuthenticatedUserId("wwz");
        Map<String, Object> variables = new HashMap<>();
        //inputUser就是在bpmn中Assignee配置的参数
        variables.put("inputUser", "wwz");
        //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance =runtimeService.startProcessInstanceByKey("test",variables);
        String processInstanceId = processInstance.getProcessInstanceId();
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
        tmp.setAssignee("wwz");
        //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
        taskService.complete(tmp.getId(),variables);
        return null;
    }

    /**
     * 人事经理审批
     * @return
     */
    @RequestMapping("/app2")
    @ResponseBody
    public Object ff2(){
        //根据角色信息获取自己的待办 act_ru_task
        List<Task> T = taskService.createTaskQuery().taskAssignee("goxcheer").list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("inputUser", "wwz1");
                variables.put("isSuccess", true);
                item.setAssignee("rs");
                //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                taskService.setVariableLocal(item.getId(),"isSuccess",true);
                //增加审批备注
                taskService.addComment(item.getId(),item.getProcessInstanceId(),"人事经理同意");
                //完成此次审批。由下节点审批
                taskService.complete(item.getId(), variables);
            }
        }
        return null;
    }

    /**
     * 总经理审批
     * @return
     */
    @RequestMapping("/app3")
    @ResponseBody
    public Object app3(){
        //根据角色信息获取自己的待办
        List<Task> T = taskService.createTaskQuery().taskAssignee("zj").list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("inputUser", "wwz2");
                item.setAssignee("zj");
                variables.put("isSuccess", true);
                //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                taskService.setVariableLocal(item.getId(),"isSuccess",true);
                //增加审批备注
                taskService.addComment(item.getId(),item.getProcessInstanceId(),"总经理同意");
                //完成此次审批。如果下节点为endEvent。结束流程
                taskService.complete(item.getId(), variables);
            }
        }
        return null;
    }

    /**
     * 查询wwz未完成的历史记录
     * @return
     */
    @RequestMapping("/unfinish")
    @ResponseBody
    public Object app4(){
        return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().startedBy("wwz").unfinished().list();

    }
    /**
     * 查询wwz完成的历史记录
     * @return
     */
    @RequestMapping("/finish")
    @ResponseBody
    public Object finish(){
        return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().startedBy("wwz").finished().list();
    }

    /**
     * 总经理审核不通过（不通过打回到发起人。发起人可以根据流程id。重新提交）rePack
     * @return
     */
    @RequestMapping("/reject")
    @ResponseBody
    public Object reject(){
        List<Task> T = taskService.createTaskQuery().taskAssignee("zj").list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = new HashMap<>();
                //isSuccess来决定流程走向。具体看bpmn图
                variables.put("isSuccess", false);
                item.setAssignee("zj");
                taskService.setVariableLocal(item.getId(),"isSuccess",false);
                taskService.complete(item.getId(), variables);
                // modelRollBack.init(item.getId());

            }
        }
        return null;
    }

    /**
     * 人事经理审核不通过。（不通过打回到发起人。发起人可以根据流程id。重新提交）。rePack
     * @return
     */
    @RequestMapping("/reject1")
    @ResponseBody
    public Object reject1(){
        List<Task> T = taskService.createTaskQuery().taskAssignee("rs").list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("isSuccess", false);
                item.setAssignee("rs");
                taskService.setVariableLocal(item.getId(),"isSuccess",false);
                taskService.complete(item.getId(), variables);
                // modelRollBack.init(item.getId());

            }
        }
        return null;
    }

}
