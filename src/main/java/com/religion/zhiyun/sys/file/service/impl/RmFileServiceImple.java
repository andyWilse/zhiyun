package com.religion.zhiyun.sys.file.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.fileutil.FileToBase;
import com.religion.zhiyun.utils.fileutil.FileUpDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RmFileServiceImple implements RmFileService {

    @Value("${images.upload.url}")
    private String  pathUpload;

    @Value("${images.down.url}")
    private String  pathDown;

    @Autowired
    private RmFileMapper rmFileMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public int add(FileEntity fileEntity) {
        return rmFileMapper.add(fileEntity);
    }

    @Override
    public RespPageBean uploadImage(HttpServletRequest request) {
        RespPageBean page =new RespPageBean();
        long code= ResultCode.SUCCESS.getCode();
        String refpath="";
        try {
            List<FileEntity> fileEntities = FileUpDown.imagesUpload(request, pathUpload);
            if(null!=fileEntities && fileEntities.size()>0){
                for (int i=0;i<fileEntities.size();i++){
                    FileEntity fileEntity = fileEntities.get(i);
                    String filePath =pathDown + fileEntity.getFilePath();
                    fileEntity.setFilePath(filePath);
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
                code=ResultCode.FAILED.getCode();
            }
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
        }
        page.setCode(code);
        return  page;
    }

    @Override
    public RespPageBean showPicture(String picture) {
        RespPageBean page =new RespPageBean();
        long code=ResultCode.SUCCESS.getCode();
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
            code=ResultCode.FAILED.getCode();
        }
        page.setCode(code);
        page.setDatas(plist.toArray());
        return page;
    }

    @Override
    public String buildUserPic(String token) {
        //姓名+手机号码后四位
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
        String userNm = sysUserEntity.getUserNm();
        String userMo = sysUserEntity.getUserMobile();
        String userMobile = userMo.substring(7);

        return userNm+userMobile;
    }


}
