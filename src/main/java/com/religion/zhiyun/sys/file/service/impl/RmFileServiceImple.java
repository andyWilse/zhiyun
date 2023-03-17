package com.religion.zhiyun.sys.file.service.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.response.ResultCode;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.fileutil.FileToBase;
import com.religion.zhiyun.utils.fileutil.FileUpDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RmFileServiceImple implements RmFileService {

    @Autowired
    private RmFileMapper rmFileMapper;
    @Value("${images.upload.url}")
    private String  pathUpload;


    @Override
    public int add(FileEntity fileEntity) {
        return rmFileMapper.add(fileEntity);
    }

    @Override
    public RespPageBean uploadImage(HttpServletRequest request) {
        RespPageBean page =new RespPageBean();
        int code=ResultCode.SUCCESS.code();
        String refpath="";
        try {
            List<FileEntity> fileEntities = FileUpDown.imagesUpload(request, pathUpload);
            if(null!=fileEntities && fileEntities.size()>0){
                for (int i=0;i<fileEntities.size();i++){
                    FileEntity fileEntity = fileEntities.get(i);
                    fileEntity.setFileType(ParamCode.FILE_TYPE_01.getCode());
                    fileEntity.setCreateTime(TimeTool.getYmdHms());
                    //String nbr = request.getHeader("login-name");
                    fileEntity.setCreator("admin");
                    rmFileMapper.add(fileEntity);
                    if(i==fileEntities.size()-1){
                        refpath+=fileEntity.getFileId()+"";
                    }else{
                        refpath+=fileEntity.getFileId()+",";
                    }
                }
            }
            page.setResult(refpath);
            if(StringUtils.isEmpty(refpath)){
                code=ResultCode.ERROR.code();
            }
        }catch (Exception e){
            code=ResultCode.ERROR.code();
        }
        page.setCode(code);
        return  page;
    }

    @Override
    public RespPageBean showPicture(String picture) {
        RespPageBean page =new RespPageBean();
        int code=ResultCode.SUCCESS.code();
        List<FileEntity> plist=new ArrayList<>();
        try{
            String[] split = picture.split(",");
            List<FileEntity> fileEntities = rmFileMapper.queryPath(split);
            if(null!=fileEntities && fileEntities.size()>0) {
                String[] path=new String[fileEntities.size()];
                for (int i = 0; i < fileEntities.size(); i++) {
                    FileEntity fileEntity = fileEntities.get(i);
                    String image = FileToBase.getImage(fileEntity.getFilePath(), fileEntity.getFileName());
                    fileEntity.setFilePath(image);
                    plist.add(fileEntity);
                }
            }
        }catch(Exception e){
            code=ResultCode.ERROR.code();
        }
        page.setCode(code);
        page.setDatas(plist.toArray());
        return page;
    }
}
