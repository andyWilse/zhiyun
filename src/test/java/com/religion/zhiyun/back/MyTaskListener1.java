package com.religion.zhiyun.back;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyTaskListener1 implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
        ProcessEngine services = ProcessEngines.getDefaultProcessEngine();
        ExecutionEntity entity = (ExecutionEntity) execution;
        String  processInstId=entity.getProcessInstanceId();          //流程实例Id
//根据审批传入的参数判断是否打回
        Object value = execution.getVariableInstance("isSuccess").getValue();
        if(ObjectUtils.isEmpty(value)){
            value = execution.getParent().getVariableInstance("isSuccess").getValue();
        }
        ;
        if(!ObjectUtils.isEmpty(value)&&(boolean)(value)==true){
            return;
        }
        List<HistoricTaskInstance> list=services.getHistoryService().createHistoricTaskInstanceQuery().orderByTaskCreateTime().asc()
                .processInstanceId(processInstId)
                .list();
        if(list!=null){
            String user=list.get(0).getAssignee(); //获取最新的一个责任人信息回退给他
            execution.setVariable("inputUser", user);
        }

    }
}
