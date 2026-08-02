package com.religion.zhiyun.task.service.impl;


import com.religion.zhiyun.task.dao.TaskActInstMapper;
import com.religion.zhiyun.task.entity.ActInstEntity;
import com.religion.zhiyun.task.service.TaskActInstService;
import com.religion.zhiyun.utils.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskActInstServiceImpl implements TaskActInstService {
    @Autowired
    private TaskActInstMapper rmActActinstMapper;

    @Override
    public AppResponse add(ActInstEntity actEntity) {
        return null;
    }
}
