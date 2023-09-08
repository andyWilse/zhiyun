package com.religion.zhiyun.pic;

import java.io.File;
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
        //DrawTransparentPic.drawTransparent("释悟善（蔡晓梅）3333");
        //String pictures = ",";
        //String[] split = pictures.split(",");
        String picturesPath = "11,12,13,14,15";
        String picturesPaths = "11,14,15";
        String[] split = picturesPaths.split(",");
        String aa="";
        for (int i=0;i<split.length;i++){
            String re = split[i];
            if(picturesPath.contains(re)){
                String replace = picturesPath.replace(re+",",  "");
                picturesPath=replace;

            }
        }
        //保存图片
        //venuesEntity.setPicturesPath(picturesPath);


        String filePath="http://61.153.44.75:18088/images/20230317/2023031720575011.png";
        String[] supers = filePath.split("super");
        String aSuper = supers[1];
        System.out.println(aSuper);
    }
}
