package com.religion.zhiyun.sys.dict.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_sys_dict")
public class SysEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "DICT_ID")
    private int dictId;//主键ID

    @Column(name = "DICT_TYPE_CD")
    private String dictTypeCd;//字典类型

    @Column(name = "DICT_CD")
    private String dictCd;//字典值

    @Column(name = "DICT_EN_DESC")
    private String dictEnDesc;//字典类型英文描述

    @Column(name = "DICT_CN_DESC")
    private String dictCnDesc;//字典类型中文描述

    @Column(name = "VALID_IND")
    private String validInd;//是否有效


    public String getDictTypeCd() {
        return dictTypeCd;
    }

    public void setDictTypeCd(String dictTypeCd) {
        this.dictTypeCd = dictTypeCd;
    }
}
