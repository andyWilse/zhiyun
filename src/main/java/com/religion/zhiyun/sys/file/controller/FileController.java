package com.religion.zhiyun.sys.file.controller;

import com.religion.zhiyun.sys.file.entity.ResponseVo;
import org.springframework.web.bind.annotation.*;


import java.io.*;
import java.util.Base64;

@RestController
@RequestMapping("/file")
public class FileController {

    @GetMapping("/image")
    public ResponseVo<String> image() throws IOException {
        String path = "F:\\3filestore\\11.png";
        return ResponseVo.success(getImage(path));
    }

    private String getImage(String path) throws IOException {

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
        String data = encoder.encodeToString(fileByte);
        return data;
    }
}
