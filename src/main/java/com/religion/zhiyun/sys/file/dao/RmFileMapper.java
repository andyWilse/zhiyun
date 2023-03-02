package com.religion.zhiyun.sys.file.dao;

import com.religion.zhiyun.sys.file.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface RmFileMapper {
    int add(FileEntity fileEntity);

    List<FileEntity> queryPath(@Param("fileIds") String[] fileIds);

    /**
     * 获取图片地址
     * @param fileIds
     * @return
     */
    List<Map<String,Object>> getPath(@Param("fileIds") String[] fileIds);


}
