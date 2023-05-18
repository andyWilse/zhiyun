package com.religion.zhiyun.sys.file.controller;

import com.google.code.kaptcha.Producer;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.utils.fileutil.DrawTransparentPic;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/file")
public class RmFileController {
    @Autowired
    private RmFileService rmFileService;
    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping("/images/upload")
    public RespPageBean uploadImages( HttpServletRequest httpServletRequest) {
        return rmFileService.uploadImage(httpServletRequest);
    }

    @GetMapping("/show")
    public RespPageBean getSysDicts(@RequestParam("picture") String picture){
        return rmFileService.showPicture(picture);
    }

    @RequestMapping("/buildUserPic")
    public AppResponse buildUserPic(@RequestHeader("token")String token) {
        return  rmFileService.buildUserPic(token);
    }

    @RequestMapping("/buildErWei")
    public void buildErWei(HttpServletResponse response, HttpSession session, @RequestHeader("token")String token) {
        try {
            String text = "";
            //    生成验证码
            //String text= kaptchaProducer.createText();
            //    生成验证码图片
            BufferedImage image = kaptchaProducer.createImage(text);
            //    将图片传入session
            session.setAttribute(" kaptcha", text);
            //    将图片输出到前端(图片+格式)
            response.setContentType("image/png");

            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("响应验证码失败");
        }
    }

    @GetMapping("/getFile")
    public PageResponse getFile(@RequestParam("picPath") String picPath){
        return rmFileService.getFile(picPath);
    }

    /**
     * 视频文件上传
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @RequestMapping(value ="/uploadVideo")
    public PageResponse uploadVideo(HttpServletRequest httpServletRequest) throws Exception{
        MultipartFile multipartFile =null;
        MultipartHttpServletRequest mulReq = (MultipartHttpServletRequest) httpServletRequest;
        Map<String, MultipartFile> map = mulReq.getFileMap();
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            multipartFile = map.get(next);
        }
        return rmFileService.uploadVideo(multipartFile,httpServletRequest);
    }


}
