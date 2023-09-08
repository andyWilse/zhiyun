package com.religion.zhiyun.pic;

import lombok.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
/**
 * 生成图片参数控制
 *
 * @author h
 * @date Created in 2020/5/30 17:25
 */
@Data
public class ImageCreateEntity {
    /**
     * 宽度
     */
    private Integer width = 100;
    /**
     * 高度
     */
    private Integer height = 100;
    /*==============图片内容==============*/
    /**
     * 图片内容
     */
    private BufferedImage imgContent;
    /**
     * 图片宽度
     */
    private int imgWidth = 100;
    /**
     * 图片宽度
     */
    private int imgHeight = 100;
    /**
     * 图片渲染X起点
     */
    private int imgX;
    /**
     * 图片渲染Y轴起点
     */
    private int imgY;
    /*=====================文本内容===================*/
    /**
     * 文本内容
     */
    private String textContent;
    /**
     * 字体名称
     */
    private String fontName = "";
    /**
     * 字体文件路径
     */
    private String fontFilePath = "";
    /**
     * 字体尺寸
     */
    private float fontSize = 20f;
    /**
     * 字体风格
     */
    private int fontStyle = Font.PLAIN;
    /**
     * 字体颜色
     */
    private Color fontColor = Color.BLACK;
    /**
     * 字体间距，默认值为零，与当前字体尺寸相关
     */
    private Integer fontSpace = 0;
    /**
     * 行距
     */
    private Integer linePadding = 10;
    /**
     * 文本透明度：值从0-1.0，依次变得不透明
     */
    private float textTransparency = 1.0f;
    /**
     * 文本渲染X起点
     */
    private Integer textX = 0;
    /**
     * 文本渲染Y轴起点
     */
    private Integer textY = 0;
    /**
     * 左边距
     */
    private Integer textLeftPadding = 0;
    /**
     * 右边距
     */
    private Integer textRightPadding = 0;
    /**
     * 每行居中
     */
    private boolean isCenterLine;
    /*=================背景==============*/
    /**
     * 背景颜色
     */
    private Color backgroundColor = Color.WHITE;
    /**
     * 背景是否透明
     */
    private boolean isTransparentBackground = true;
    /**
     * 背景图片
     */
    private BufferedImage backgroundImg;
}