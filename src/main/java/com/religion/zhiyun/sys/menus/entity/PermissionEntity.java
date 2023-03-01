package com.religion.zhiyun.sys.menus.entity;

import lombok.*;

import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionEntity {

    private String id;
    private String name;
    private String children;
}
