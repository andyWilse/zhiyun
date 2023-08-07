package com.religion.zhiyun.venues.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Setter
@Getter
public class ParamsVo {
    private String province;
    private String city;
    private String area;
    private String town;

    private String venues;
    private String[]  venuesArr;

    private Integer page;
    private Integer size;

    private String searchOne;
    private String searchTwo;
    private String searchThree;
    private String searchFour;
    private String searchFive;

    private String[]  searchArr;
}
