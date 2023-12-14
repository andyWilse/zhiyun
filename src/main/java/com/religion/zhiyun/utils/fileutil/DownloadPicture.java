package com.religion.zhiyun.utils.fileutil;

import com.religion.zhiyun.utils.fileutil.init.FilePropertiesVo;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.UUID;

public class DownloadPicture {
    /**
     * 把图片下载到本地
     * @param picturePath
     * @param filePathBase
     * @return
     */
    public static String downloadPic(String picturePath,String filePathBase){
        String filePath = null;
        try {
            // 构造URL
            URL url = new URL(picturePath);
            // 打开连接
            URLConnection con = url.openConnection();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            String name= String.valueOf(UUID.randomUUID()).replaceAll("-","");

            filePath = FilePropertiesVo.getBaseUrl()+filePathBase+"/"+name+ ".jpg";
            String filename = FilePropertiesVo.getUploadUrl() +filePathBase+"/"+ name+ ".jpg";
            //filename = "F:\\home/" + name+ ".jpg";//下载路径及下载图片名称uploadUrl
            File file = new File(filename);
            FileOutputStream os = new FileOutputStream(file, true);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }


    /**
     * 获取字节流
     * @param path
     * @return
     * @throws IOException
     */
    public static String getImageStream (String path){
        String data = null;

        try {
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
        /*BASE64Encoder encoder = new BASE64Encoder();
        String data = encoder.encode(fileByte);*/
            Base64.Encoder encoder = Base64.getEncoder();
            data = encoder.encodeToString(fileByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
