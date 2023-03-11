package com.religion.zhiyun.sys.menus.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuList {
    private Object[] children;
    private Object[] parent;
    private Object[] button;
    private Object[] typeOption;

}
