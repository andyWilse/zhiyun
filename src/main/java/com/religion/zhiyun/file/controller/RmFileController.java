package com.religion.zhiyun.file.controller;

import com.religion.zhiyun.file.entity.FileEntity;
import com.religion.zhiyun.file.service.RmFileService;
import com.religion.zhiyun.login.api.CommonResult;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.fileutil.FileUpDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/file")
public class RmFileController {
    @Autowired
    private RmFileService rmFileService;

    @Value("${images.upload.url}")
    private String  pathUpload;

    @PostMapping("/add")
    @ResponseBody
    public CommonResult add(String formdata) throws IOException {
        System.out.println(formdata);
        return CommonResult.success("添加成功！");
    }

    @RequestMapping("/images/upload")
    public String uploadImage(HttpServletRequest request) {
        String refpath="";
        List<FileEntity> fileEntities = FileUpDown.imagesUpload(request, pathUpload);
        if(null!=fileEntities && fileEntities.size()>0){
            for (int i=0;i<fileEntities.size();i++){
                FileEntity fileEntity = fileEntities.get(i);
                fileEntity.setFileType(ParamCode.FILE_TYPE_01.getCode());
                Timestamp timestamp = new Timestamp(new Date().getTime());
                fileEntity.setCreateTime(timestamp);
                fileEntity.setCreator("first");
                rmFileService.add(fileEntity);
                if(i==fileEntities.size()-1){
                    refpath+=fileEntity.getFileId()+"";
                }else{
                    refpath+=fileEntity.getFileId()+",";
                }
            }
        }
        return refpath;
    }

}
