package com.religion.zhiyun.pic;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 生成图片工具
 * <p>
 * 注意，有有文字时，需要服务器有字体存在
 *
 * @author h
 * @date Created in 2020/5/30 17:54
 */
@Log4j2
public class ImageCreateUtil {
    /**
     * 渲染内容
     *
     * @param imageCreateEntity imageCreateEntity
     * @return java.awt.image.BufferedImage
     * @创建人: h
     * @创建时间: 2020/6/4 20:42
     * @修改人:
     * @修改时间:
     * @修改备注:
     * @version: 1.0
     */
    public static BufferedImage createImg(ImageCreateEntity imageCreateEntity) {
        Integer width = imageCreateEntity.getWidth();
        Integer height = imageCreateEntity.getHeight();
        int imgWidth = imageCreateEntity.getImgWidth();
        int imgHeight = imageCreateEntity.getImgHeight();
        BufferedImage bgImg = imageCreateEntity.getBackgroundImg();
        int imgX = imageCreateEntity.getImgX();
        int imgY = imageCreateEntity.getImgY();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d;
        g2d = getG2d(bufferedImage);
        boolean isFontTransparency = false;
        //背景是否透明
        if (ObjectUtil.isNotNull(bgImg)) {
            g2d.drawImage(bgImg.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
        } else if (imageCreateEntity.isTransparentBackground()) {
            bufferedImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = getG2d(bufferedImage);
        } else {
            g2d.setBackground(imageCreateEntity.getBackgroundColor());
            g2d.clearRect(0, 0, width, height);
            //设置字体透明度，字体透明度只在背景不透明时才生效
            isFontTransparency = true;
        }
        /*=====================渲染图片===================*/
        // 中间内容框画到背景图上
        BufferedImage imgContent = imageCreateEntity.getImgContent();
        g2d.drawImage(imgContent.getScaledInstance(imgWidth, imgHeight, Image.SCALE_DEFAULT), imgX, imgY, null);
        /*====================渲染文本==================*/
        return renderText(isFontTransparency, bufferedImage, imageCreateEntity);
    }
    /**
     * 渲染文本
     *
     * @param bufferedImage     bufferedImage
     * @param imageCreateEntity imageCreateEntity
     * @return java.awt.image.BufferedImage
     * @创建人: h
     * @创建时间: 2020/6/4 20:28
     * @修改人:
     * @修改时间:
     * @修改备注:
     * @version: 1.0
     */
    private static BufferedImage renderText(boolean isFontTransparency, BufferedImage bufferedImage, ImageCreateEntity imageCreateEntity) {
        Graphics2D g2d = initFont(imageCreateEntity, bufferedImage, isFontTransparency);
        if (ObjectUtil.isNull(g2d)) {
            return null;
        }
        String textContent = imageCreateEntity.getTextContent();
        if (StringUtils.isBlank(textContent)) {
            g2d.dispose();
            return bufferedImage;
        }
        //对文本进行分行处理
        Integer textLeftPadding = imageCreateEntity.getTextLeftPadding();
        int contentWith = imageCreateEntity.getWidth() - textLeftPadding - imageCreateEntity.getTextRightPadding();
        float fontSize = imageCreateEntity.getFontSize();
        Integer fontSpace = imageCreateEntity.getFontSpace();
        Integer linePadding = imageCreateEntity.getLinePadding();
        List<String> lines = getLines(textContent, contentWith, fontSpace, fontSize);
        //逐行渲染
        //当前行文字的长度
        boolean centerLine = imageCreateEntity.isCenterLine();
        float textX = imageCreateEntity.getTextX();
        Integer textY = imageCreateEntity.getTextY();
        float lineWidth;
        float paddingAdd = 0;
        if (CollectionUtil.isNotEmpty(lines)) {
            String contentLine;
            for (int i = 0; i < lines.size(); i++) {
                contentLine = lines.get(i).trim();
                //设置每行居中
                if (centerLine) {
                    lineWidth = ((1 + fontSpace) * fontSize) * contentLine.trim().length();
                    paddingAdd = (contentWith - lineWidth) / 2;
                    textX = paddingAdd + textLeftPadding;
                }
                g2d.drawString(contentLine, textX, textY + fontSize + (i * (fontSize + linePadding)));
            }
        }
        g2d.dispose();
        return bufferedImage;
    }
    /**
     * 获取字体分行后list
     *
     * @param textContent 文本内容
     * @param contentWith 文本宽度
     * @param fontSpace   字体间距
     * @param fontSize    字体尺寸
     * @return java.util.List<java.lang.String>
     * @创建人: h
     * @创建时间: 2020/6/4 18:55
     * @修改人:
     * @修改时间:
     * @修改备注:
     * @version: 1.0
     */
    private static List<String> getLines(String textContent, Integer contentWith, Integer fontSpace, float fontSize) {
        //每一行的字体个数
        int lineCont = (int) (contentWith / ((1 + fontSpace) * fontSize));
        char[] chars = textContent.toCharArray();
        List<String> lines = new ArrayList<>();
        char[] charArrayTemp = new char[lineCont];
        int indexTemp = 0;
        for (char c : chars) {
            if (indexTemp == lineCont) {
                //一行数据已满，开始新的一行
                lines.add(new String(charArrayTemp));
                indexTemp = 0;
                charArrayTemp = new char[lineCont];
            }
            charArrayTemp[indexTemp] = c;
            indexTemp++;
        }
        //最后一行
        if (ArrayUtil.isNotEmpty(charArrayTemp)) {
            lines.add(new String(charArrayTemp));
        }
        return lines;
    }
    public static Graphics2D getG2d(BufferedImage bf) {
        Graphics2D g2d = bf.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2d;
    }
    /**
     * 初始化字体信息
     *
     * @param imageCreateEntity  imageCreateEntity
     * @param bufferedImage      bufferedImage
     * @param isFontTransparency 字体是否透明
     * @return java.awt.Graphics2D
     * @创建人: h
     * @创建时间: 2020/6/5 10:07
     * @修改人:
     * @修改时间:
     * @修改备注:
     * @version: 1.0
     */
    private static Graphics2D initFont(ImageCreateEntity imageCreateEntity, BufferedImage bufferedImage, boolean isFontTransparency) {
        String fontName = imageCreateEntity.getFontName();
        float fontSize = imageCreateEntity.getFontSize();
        int fontStyle = imageCreateEntity.getFontStyle();
        //加载字体
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        boolean contains = ArrayUtil.contains(ge.getAvailableFontFamilyNames(), fontName);
        Font font = null;
        if (contains) {
            font = new Font(fontName, fontStyle, (int) fontSize);
        } else {
            boolean isOk = true;
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(imageCreateEntity.getFontFilePath())).deriveFont(fontStyle).deriveFont(fontSize);
                ge.registerFont(font);
            } catch (FontFormatException e) {
                log.error("加载字体失败！", e);
                isOk = false;
            } catch (IOException e) {
                isOk = false;
                log.error("加载字体文件失败！", e);
            }
            if (!isOk) {
                return null;
            }
        }
        Graphics2D g2d = ge.createGraphics(bufferedImage);
        //设置字体透明度，字体透明度只在背景不透明时才生效
        if (isFontTransparency) {
            if (imageCreateEntity.getTextTransparency() != 1.0f) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, imageCreateEntity.getTextTransparency()));
            }
        }
        //字体信息
        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.TRACKING, imageCreateEntity.getFontSpace());
        font = font.deriveFont(attributes);
        g2d.setFont(font);
        g2d.setColor(imageCreateEntity.getFontColor());
        return g2d;
    }
}
