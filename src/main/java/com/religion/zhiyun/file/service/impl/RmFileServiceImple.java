package com.religion.zhiyun.file.service.impl;

import com.religion.zhiyun.file.dao.RmFileMapper;
import com.religion.zhiyun.file.entity.FileEntity;
import com.religion.zhiyun.file.service.RmFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RmFileServiceImple implements RmFileService {

    @Autowired
    private RmFileMapper rmFileMapper;
    @Override
    public int add(FileEntity fileEntity) {
        return rmFileMapper.add(fileEntity);
    }
}
