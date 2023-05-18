package com.religion.zhiyun.news.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_NEWS_INFO")
public class NewsEntity {
    @Id
    @Column(name = "NEWS_ID")
    private int newsId;//主键ID

    @Column(name = "NEWS_VIEWS")
    private int newsViews;//浏览量

    @Column(name = "NEWS_TITLE")
    private String newsTitle;//新闻标题

    @Column(name = "NEWS_KEYWORD")
    private String newsKeyword;//新闻关键字

    @Column(name = "NEWS_CONTENT")
    private String newsContent;//新闻内容

    @Column(name = "NEWS_FROM")
    private String newsFrom;//来源

    @Column(name = "NEWS_REF")
    private String newsRef;//链接

    @Column(name = "NEWS_TYPE")
    private String newsType;//新闻分类：01-宗教文化；02-政策学习;03-瓯海动态

    @Column(name = "NEWS_DOWN")
    private String newsDown;

    @Column(name = "NEWS_PICTURES_PATH")
    private String newsPicturesPath;//新闻照片地址

    @Column(name = "NEWS_VIDEOS_PATH")
    private String newsVideosPath;//新闻视频地址

    @Column(name = "CREATOR")
    private String creator;//发布者名称

    @Column(name = "CREATE_TIME")
    private String createTime;//发布日期

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//最后修改人

    @Column(name = "LAST_MODIFY_TIME")
    private String lastModifyTime;//最后修改时间

    @Column(name = "NEWS_FOR")
    private String newsFor;//阅读群体：01：监管人员；02：管理人员

    @Column(name = "NEWS_REF_TYPE")
    private String newsRefType;//链接类型 01：一般新闻；02：图片新闻

    private String releaseYear;//发布日期(年)
    private String releaseMonth;//发布日期(月)
    private String releaseDay;//发布日期(日)

    private String picturesPathRemove;
    private Object[] fileList;

    private Object[] videoList;

    private String newsOpera;
}
