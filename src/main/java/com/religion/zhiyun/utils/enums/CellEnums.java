package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum CellEnums {
    cell_A(0, "A"),
    cell_B(1, "B"),
    cell_C(2, "C"),
    cell_D(3, "D"),
    cell_E(4, "E"),
    cell_F(5, "F"),
    cell_G(6, "G"),
    cell_H(7, "H"),
    cell_I(8, "I"),
    cell_J(9, "G"),
    cell_K(10, "K"),
    cell_L(11, "L"),
    cell_M(12, "M"),
    cell_N(13, "N"),
    cell_O(14, "O"),
    ;

    private Integer code;
    private String name;

    private CellEnums(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(Integer code) {
        for (CellEnums item : CellEnums.values()) {
            if (item.code.equals(code)) {
                return item.name;
            }
        }
        return String.valueOf(code);
    }
}
