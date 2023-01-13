package com.religion.zhiyun.file.dao;

import com.religion.zhiyun.file.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RmFileMapper {
    int add(FileEntity fileEntity);
}
