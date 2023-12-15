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

    /**
     * 删除图片
     * @param fileId
     * @return
     */
    void delete(@Param("fileId") String fileId);

    List<FileEntity> queryPath(@Param("fileIds") String[] fileIds);

    /**
     * 获取图片地址
     * @param fileIds
     * @return
     */
    List<Map<String,Object>> getPath(@Param("fileIds") String[] fileIds);

    /**
     * url
     * @param fileIds
     * @return
     */
    List<Map<String,Object>> getFileUrl(@Param("fileIds") String[] fileIds);

    /**
     *获取用户水印
     * @param loginNm
     * @return
     */
    String getUserUrl(@Param("loginNm") String loginNm);

    /**
     * 更新地址
     * @param imgPath
     * @param fileId
     */
    void updateFilePath(@Param("imgPath") String imgPath,@Param("fileId") String fileId);


    List<Map<String,Object>> getInit(@Param("creator") String creator);
}
