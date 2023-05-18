package com.religion.zhiyun.utils.fileutil;

import com.religion.zhiyun.sys.file.entity.FileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileUpDown {
    private static Logger logger = LoggerFactory.getLogger(FileUpDown.class);

    public static List<FileEntity> imagesUpload(HttpServletRequest request,String pathUpload){

        //获取文件在服务器的储存位置
        /*String path = request.getSession().getServletContext().getRealPath("/upload");
        File filePath = new File(path);
        logger.info("文件的保存路径：" + path);
        if (!filePath.exists() && !filePath.isDirectory()) {
            logger.info("目录不存在，创建目录:" + filePath);
            filePath.mkdir();
        }*/

        List<FileEntity> list=new ArrayList();
        MultipartHttpServletRequest mulReq = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> map = mulReq.getFileMap();
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            FileEntity upload = upload(map.get(next), pathUpload,request);
            list.add(upload);
        }

        return list;
    }

    /**
     * 文件上传
     * @param picture
     */
    public static FileEntity upload(MultipartFile picture,String pathUpload,HttpServletRequest request){
        System.out.println(picture.getContentType());
        //获取原始文件名称(包含格式)
        String originalFileName = picture.getOriginalFilename();
        //获取文件类型，以最后一个`.`为标识
        String type = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        //获取文件名称（不包含格式）
        String name = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        //设置文件新名称: 当前时间+文件名称（不包含格式）
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(d);
        String fileName = date  + "." + type;

        //在指定路径下创建一个文件
        //String newpath="F:\\3filestore\\";
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String dir = sd.format(d);
        pathUpload+=dir;
        File filePath = new File(pathUpload);
        logger.info("文件的保存路径：" + pathUpload);
        if (!filePath.exists() && !filePath.isDirectory()) {
            logger.info("目录不存在，创建目录:" + filePath);
            filePath.mkdirs();
        }

        File targetFile = new File(pathUpload, fileName);
        //将文件保存到服务器指定位置
        try {
            picture.transferTo(targetFile);
            //协议 :// ip地址 ：端口号 / 文件目录(/images/2020/03/15/xxx.jpg)
            //String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/images/" + dir+"/"  +fileName;
            //logger.info("图片上传，访问URL：" + url);
            logger.info("上传成功");
            //文件在服务器的存储路径
            //保存文件信息
            FileEntity vo=new FileEntity();
            vo.setFileName(fileName);
            vo.setFilePath(dir+"/"+fileName);
            return vo;
        } catch (IOException e) {
            logger.info("上传失败");
            e.printStackTrace();
            return null;
        }
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

        //进行base64编码
        /*BASE64Encoder encoder = new BASE64Encoder();
        String data = encoder.encode(fileByte);*/
        Base64.Encoder encoder = Base64.getEncoder();
        String data = encoder.encode(fileByte).toString();
        return data;
    }




}
