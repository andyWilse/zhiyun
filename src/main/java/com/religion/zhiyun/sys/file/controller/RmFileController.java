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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/file")
public class RmFileController {
    @Autowired
    private RmFileService rmFileService;
    @Autowired
    private RmFileMapper rmFileMapper;

    @Value("${images.upload.url}")
    private String  pathUpload;

    @RequestMapping("/images/upload")
    public RespPageBean uploadImage(HttpServletRequest request) {
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

        RespPageBean page =new RespPageBean();
        page.setResult(refpath);
        page.setCode(ResultCode.SUCCESS.code());
        return page;
    }

    @GetMapping("/show")
    @ResponseBody
    public Object[] getSysDicts(@RequestParam("picture") String picture) throws IOException {
        String[] split = picture.split(",");
        List<FileEntity> fileEntities = rmFileMapper.queryPath(split);
        List<FileEntity> plist=new ArrayList<>();
        if(null!=fileEntities && fileEntities.size()>0) {
            String[] path=new String[fileEntities.size()];
            for (int i = 0; i < fileEntities.size(); i++) {
                FileEntity fileEntity = fileEntities.get(i);
                String image = FileToBase.getImage(fileEntity.getFilePath(), fileEntity.getFileName());
                fileEntity.setFilePath(image);
                plist.add(fileEntity);
            }
        }
        return plist.toArray();
    }

}
