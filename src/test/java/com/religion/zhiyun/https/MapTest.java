package com.religion.zhiyun.https;

import com.religion.zhiyun.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class MapTest {
    public static void main(String[] args) {
        List<String> a=new ArrayList<>();

        a.add("1");
        a.add("2");
        a.add("3");
        String s = JsonUtils.listTOJson(a);
        System.out.println(s);
        System.out.println(s.replace("[","").replace("]","").replace("\"",""));
        /*try {
            // 加载图片到BufferedImage对象
            String outputPath="F:\\test\\222.jpg";
            BufferedImage bufferedImage = ImageIO.read(new File("E:\\3files\\11picture\\1786951452838.jpg"));
            if (bufferedImage != null) {
                int type = bufferedImage.getType();
                File outputFile = new File(outputPath);
                log.info("outputFile："+outputPath);
                ImageIO.write(bufferedImage, "jpg", outputFile);
                log.info("成功已保存至: " + outputPath);
            }

            // 创建一个空的ByteArrayOutputStream对象
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 将BufferedImage对象编码为JPEG格式的字节流
            String format = "jpg";
            ImageIO.write(bufferedImage, format, outputStream);

            // 将ByteArrayOutputStream对象转换为字节数组
            byte[] imageBytes = outputStream.toByteArray();

            // 打印字节数组长度，验证转换结果
            System.out.println("length:" + imageBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        /*HashMap<String, Object> map = new HashMap<>();
        map.put("a",1);
        map.put("d","sdsdf");

        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String name = entry.getKey();
            Object value = entry.getValue();
            String aa="";
            if(value instanceof String){
                aa=(String) value;
            }else if(value instanceof Integer){
                aa=String.valueOf(value);
            }
            System.out.println(aa);
            //query.append("&").append(name).append("=").append(URLEncoder.encode(value, charset));
        }*/
    }
}
