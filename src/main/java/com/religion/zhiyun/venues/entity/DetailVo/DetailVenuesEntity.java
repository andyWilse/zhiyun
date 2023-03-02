package com.religion.zhiyun.venues.entity.DetailVo;

import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@Setter
public class DetailVenuesEntity {
    private String venuesId;
    private String venuesName;
    private String religiousSect;
    private String registerNbr;
    private String venuesPhone;
    private String organization;
    private String venuesAddres;
    private String picturesPath;
    private String responsiblePerson;
    private String liaisonMan;
    private String briefIntroduction;
    private String venuesStatus;
    private String longitude;
    private String  Latitude;

    private RenYuanEntity oneDirector;//第一负责人
    private RenYuanEntity twoDirector;//第二负责人
    private RenYuanEntity workDirector;//联络人

    private RenYuanEntity oneStaffDirector;//第一责任人
    private RenYuanEntity twoStaffDirector;//第二责任人
    private RenYuanEntity refStaffDirector;//关联责任人

    private RenYuanEntity oneUserDirector;//监管干部
    private RenYuanEntity twoUserDirector;//监管干部
    private RenYuanEntity threeUserDirector;//监管干部


    private RenYuanEntity zuHang;//三人驻堂组长
    private RenYuanEntity oneZuYuan;//三人驻堂组员
    private RenYuanEntity twoZuYuan;//三人驻堂组员
    private RenYuanEntity threeZuYuan;//三人驻堂组员

    private Object[] joinActivity;//关联活动
    private Object[] images;//图片
}
