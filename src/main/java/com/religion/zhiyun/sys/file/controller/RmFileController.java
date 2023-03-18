package com.religion.zhiyun.sys.file.controller;

import com.google.code.kaptcha.Producer;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.utils.fileutil.DrawTransparentPic;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class RmFileController {
    @Autowired
    private RmFileService rmFileService;
    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping("/images/upload")
    public RespPageBean uploadImage(HttpServletRequest request) {
        return rmFileService.uploadImage(request);
    }

    @GetMapping("/show")
    @ResponseBody
    public RespPageBean getSysDicts(@RequestParam("picture") String picture) throws IOException {
        return rmFileService.showPicture(picture);
    }

    @RequestMapping("/buildUserPic")
    public void buildUserPic(HttpServletResponse response, HttpSession session, @RequestHeader("token")String token) {
        try {
            String text = rmFileService.buildUserPic(token);
            BufferedImage bufferedImage = DrawTransparentPic.drawTransparent(text);
            session.setAttribute(" kaptcha", text);
            response.setContentType("image/png");
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("响应验证码失败");
        }
    }

    @RequestMapping("/buildErWei")
    public void buildErWei(HttpServletResponse response, HttpSession session, @RequestHeader("token")String token) {
        try {
            String text = rmFileService.buildUserPic(token);
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

}
