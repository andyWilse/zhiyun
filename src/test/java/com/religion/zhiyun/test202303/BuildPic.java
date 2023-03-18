package com.religion.zhiyun.test202303;

import java.io.IOException;

public class BuildPic {
    public static void main(String[] args) throws IOException {
        /*VerifyCode code = new VerifyCode();
        BufferedImage image = code.createImage("王3333");*/

        /*BufferedImage imgContent = new BufferedImage(70, 35, BufferedImage.TYPE_INT_RGB);
        ImageCreateEntity e=new ImageCreateEntity();
        e.setImgContent(imgContent);
        e.setTextContent("王3333");
        e.setFontName("Arial");

       // ImageCreateUtil.getG2d();
        BufferedImage image = ImageCreateUtil.createImg(e);
        //image.setRGB(0,0,100);
        ImageIO.write(image,"jpg",new File("F:\\java-draw0.jpg"));*/
        DrawTransparentPic.drawTransparent("释悟善（蔡晓梅）3333");
    }
}
