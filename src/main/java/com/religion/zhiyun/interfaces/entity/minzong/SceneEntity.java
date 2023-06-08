package com.religion.zhiyun.interfaces.entity.minzong;

import com.religion.zhiyun.interfaces.entity.minzong.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SceneEntity extends BaseEntity {

    private String key;//场景id,必输
    private String value;//场景名称,必输
    private String sort;//排序,必输
}
