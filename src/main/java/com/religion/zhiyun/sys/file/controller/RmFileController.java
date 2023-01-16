package com.religion.zhiyun.sys.file.controller;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.utils.config.ResultCode;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.fileutil.FileToBase;
import com.religion.zhiyun.utils.fileutil.FileUpDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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
