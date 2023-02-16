package com.religion.zhiyun.task.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ACT_RE_PROCDEF")
public class ProcdefEntity {
    @Id
    @Column(name = "ID_")
    private int id;//主键ID

    private String name;//流程名称
    private String taskKey;
    private String version;
    private String resourceName;
    private String description;

}
