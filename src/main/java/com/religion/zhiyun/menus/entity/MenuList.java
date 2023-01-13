package com.religion.zhiyun.menus.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuList {
    private Object[] children;
    private Object[] parent;

}
