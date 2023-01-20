package com.religion.zhiyun.utils.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

public class FileToBase {

   /* public static ResponseVo<String> getImageBase(String imagePath,String imageName ) throws IOException {
        //String path = "F:\\3filestore\\11.png";
        String path =imagePath+ File.separator+imageName;
        return ResponseVo.success(getImage(path));
    }*/

    public static String getImage(String imagePath,String imageName){
        String data ="";
        try {
            String path = imagePath+ File.separator+imageName;
            //读取图片变成字节数组
            FileInputStream fileInputStream = new FileInputStream(path);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;
            while((len = fileInputStream.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            byte[] fileByte = bos.toByteArray();

            /*Base64.Encoder encoder = Base64.getEncoder();
            String data = encoder.encode(fileByte).toString();*/

            //进行base64编码
            Base64.Encoder encoder = Base64.getEncoder();
            data = encoder.encodeToString(fileByte);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
