package com.religion.zhiyun.sys.feedback.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_FEEDBACK_INFO")
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "FEEDBACK_ID")
    private int feedbackId;//主键ID

    @Column(name = "FEEDBACK_TYPE")
    private String feedbackType;//操作类型：01-使用反馈;02-坐标修改;

    @Column(name = "FEEDBACK_CONTENT")
    private String feedbackContent;//反馈内容

    @Column(name = "FEEDBACK_PICTURE")
    private String feedbackPicture;//反馈图片

    @Column(name = "REL_VENUES_ID")
    private int relVenuesId;//地址(关联场所主键ID）

    @Column(name = "MAP_PICTURE")
    private String mapPicture;//地图截屏

    @Column(name = "LATITUDE_LONGITUDE_ORIGIN")
    private String latitudeLongitudeOrigin;//经纬度（原）

    @Column(name = "LATITUDE_LONGITUDE_NEW")
    private String latitudeLongitudeNew;//经纬度（新）

    @Column(name = "FEEDBACK_OPERATOR")
    private String feedbackOperator;//操作人

    @Column(name = "FEEDBACK_TIME")
    private String feedbackTime;//操作时间

}
