package com.religion.zhiyun.sys.file.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/chairman")
public class ChairmanController {
    /**
     * 图片上传
     */
    @PostMapping("/uploadimg")
    public String uploadimg(MultipartFile mFile) throws IOException {
        // 定义存储图片的地址
        String folder = "D:/WebFile/file/img";
        // 文件夹
        File imgFolder = new File(folder);
        // 获取文件名
        String fname = mFile.getOriginalFilename();
        // 获取文件后缀
        String ext = "." + fname.substring(fname.lastIndexOf(".")+1);
        // 获取时间字符串
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        // 生成新的文件名
        //String newFileName = dateTimeFormatter + UUID.randomUUID().toString().replaceAll("-","") + ext;
        String newFileName = dateTimeFormatter;
        // 文件在本机的全路径
        File filePath = new File(imgFolder, newFileName);
        if (!filePath.getParentFile().exists()){
            filePath.getParentFile().mkdirs();
        }
        try{
            mFile.transferTo(filePath);
            // 返回文件名
            return filePath.getName();
        }catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }
}
