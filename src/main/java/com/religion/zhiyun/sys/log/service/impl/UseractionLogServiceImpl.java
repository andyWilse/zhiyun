package com.religion.zhiyun.sys.log.service.impl;

import com.religion.zhiyun.sys.log.dao.UseractionLogMapper;
import com.religion.zhiyun.sys.log.entity.UseractionLogEntity;
import com.religion.zhiyun.sys.log.service.UseractionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class UseractionLogServiceImpl implements UseractionLogService {
    @Autowired
    private UseractionLogMapper useractionLogMapper;
    @Override
    public int add(UseractionLogEntity useractionLogEntity) {

        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = useractionLogEntity.getStartTime();
        Date overTime = useractionLogEntity.getOverTime();
        long per = overTime.getTime() - startTime.getTime();
        useractionLogEntity.setActionTime(format.format(startTime));
        useractionLogEntity.setActionDuration((int) per);

        useractionLogEntity.setCreateTime(new Date());
        useractionLogEntity.setUserId("5630839");
        useractionLogEntity.setUserRole("政府工作人员");
        useractionLogEntity.setAreaCode("330304");
        useractionLogEntity.setAppCode("A330000100000202105005924");

        return useractionLogMapper.add(useractionLogEntity);
    }
}
