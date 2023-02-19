package com.religion.zhiyun.sys.file.controller;

import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@RequestMapping("/file")
public class RmFileController {
    @Autowired
    private RmFileService rmFileService;


    @RequestMapping("/images/upload")
    public RespPageBean uploadImage(HttpServletRequest request) {
        return rmFileService.uploadImage(request);
    }

    @GetMapping("/show")
    @ResponseBody
    public RespPageBean getSysDicts(@RequestParam("picture") String picture) throws IOException {
        return rmFileService.showPicture(picture);
    }

}
