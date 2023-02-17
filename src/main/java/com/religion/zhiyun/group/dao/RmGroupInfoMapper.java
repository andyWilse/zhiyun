package com.religion.zhiyun.group.dao;

import com.religion.zhiyun.group.entity.GroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmGroupInfoMapper {
    //分页查询
    List<GroupEntity> getGroupByPage(@Param("page") Integer page,@Param("size") Integer size,@Param("groupName") String groupName);

    Long getTotal();


}
