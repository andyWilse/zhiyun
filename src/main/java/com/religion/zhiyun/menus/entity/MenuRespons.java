package com.religion.zhiyun.menus.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class MenuRespons {
    private MenuEntity parents;
    private int menuId;
    private String menuNm;

    private Object[] childs;
}
