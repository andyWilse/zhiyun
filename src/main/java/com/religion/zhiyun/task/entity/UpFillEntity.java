package com.religion.zhiyun.task.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpFillEntity {

     //公共参数
     private String procInstId;//'关联任务'
     private String flowType ;//  04 ,03,
     private String venuesId ;//  场所id
     private String managerMobile ;//  负责人电话
     private String managerCnNm ;//  负责人
     private String picturesPath ;//  图片
     private String taskVideo;//'视频'

     //场所更新
     private String venuesName ;//  场所名称 
     private String registerNbr ;//  场所编号 
     private String briefIntroduction ;//  简介 
     private String organization ;//  所属机构
     private String venuesPhone ;//  0577-88551612 
     private String venuesAddres ;//  浙江省温州市瓯海区景山街道雪山路153弄74号 
     private String religiousSect ;//  100100000001 

     //活动备案
     private String taskName;//任务名称,
     private String taskContent;//活动内容,
     private String taskTime;//活动时间,
     private String partNum;//参与人数,
     private Object[] picturesUrl ;//  图片
     private String taskPicture;


}
