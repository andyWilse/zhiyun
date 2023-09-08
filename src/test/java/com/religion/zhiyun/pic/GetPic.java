package com.religion.zhiyun.pic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GetPic {

    /**

     * 获取服务器上的图片信息
     *
     * @param iconUrl
     * @return
     * @date
     */
    private BufferedImage getIconInfo(String iconUrl) {
        BufferedImage sourceImg = null;
        try {
            InputStream murl = new URL("http://xxxxx/4744ecb7-a602-48e0-8a3d-6e7132ad8eaa.jpg").openStream();
            sourceImg = ImageIO.read(murl);
            System.out.println(sourceImg.getWidth()); // 源图宽度
            System.out.println(sourceImg.getHeight()); // 源图高度
            System.out.println(sourceImg.getWidth() * sourceImg.getHeight());
            System.out.println(sourceImg.getData());
        } catch (IOException e) {
            //logger.error("获取图片异常。", e);
        }


        return sourceImg;
    }

    /**
     * 获取服务器上的图片信息
     *
     * @throws
     * @throws IOException
     */
    public BufferedImage getImg(String iconUrl) {
        BufferedImage image = null;


        try {
            URL url = new URL("http://xxxxx/4744ecb7-a602-48e0-8a3d-6e7132ad8eaa.jpg");
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            image = ImageIO.read(connection.getInputStream());
            int srcWidth = image.getWidth(); // 源图宽度
            int srcHeight = image.getHeight(); // 源图高度
            System.out.println("srcWidth = " + srcWidth);
            System.out.println("srcHeight = " + srcHeight);
        } catch (Exception e) {
            //logger.error("获取图片异常。", e);
        }


        return image;
    }


}
