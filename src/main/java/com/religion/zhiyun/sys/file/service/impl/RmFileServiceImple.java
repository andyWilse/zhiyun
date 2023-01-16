package com.religion.zhiyun.sys.file.service.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.sys.file.service.RmFileService;
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
