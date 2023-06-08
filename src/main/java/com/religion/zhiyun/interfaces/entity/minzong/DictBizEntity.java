package com.religion.zhiyun.interfaces.entity.minzong;

import com.religion.zhiyun.interfaces.entity.minzong.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictBizEntity extends BaseEntity {
    private String dictCode;//字典码,必输
    private String dictKey;//字典值,必输
    private String dictValue;//字典名称,必输
    private String sort;//排序,必输
}
