package com.religion.zhiyun.sys.file.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.fileutil.DrawTransparentPic;
import com.religion.zhiyun.utils.fileutil.VideoUpDown;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.fileutil.FileToBase;
import com.religion.zhiyun.utils.fileutil.FileUpDown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class RmFileServiceImpl implements RmFileService {

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
        long code=ResultCode.FAILED.getCode();
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
            code= ResultCode.SUCCESS.getCode();
        }catch (Exception e){
            e.printStackTrace();
        }
        page.setCode(code);
        return  page;
    }

    @Override
    public PageResponse uploadVideo(MultipartFile file, HttpServletRequest request){
        long code= ResultCode.FAILED.getCode();
        String message="视频上传失败！";
        Map<String, Object> resultMap=new HashMap<String, Object>();

        String basePath = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort()+"/mimi/upload/video/";

        try {
            Long time = new Date().getTime();

            String fileName = file.getOriginalFilename();//文件原始名称
            String suffixName = fileName.substring(fileName.lastIndexOf("."));//从最后一个.开始截取。截取fileName的后缀名
            String newFileName = time+suffixName; //文件新名称
            //设置文件存储路径，可以存放在你想要指定的路径里面
            //String rootPath="D:/mimi/"+File.separator+"upload/video/"; //上传视频存放位置
            String rootPath=pathUpload+File.separator+"/upload/video/";
            ;
            String filePath = rootPath+newFileName;
            File newFile = new File(filePath);
            //判断目标文件所在目录是否存在
            if(!newFile.getParentFile().exists()){
                //如果目标文件所在的目录不存在，则创建父目录
                newFile.getParentFile().mkdirs();
            }

            //将内存中的数据写入磁盘
            file.transferTo(newFile);
            //视频上传保存url
            String videoUrl = basePath + newFileName;

            //视频封面图处理
            String newImgName = time+".jpg";
            String framefile = rootPath + newImgName;
            String imgUrlSave = basePath+newImgName;//图片最终位置路径
            videoUrl="http://61.153.44.75:18088/images/mm.mp4";
            imgUrlSave="http://61.153.44.75:18088/images/1.jpg";
            //视频截取封面图
            String imgUrl= VideoUpDown.getVedioImg(videoUrl, framefile, imgUrlSave);

            //保存文件信息
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(newFileName);
            fileEntity.setFilePath(videoUrl);
            fileEntity.setFileType(ParamCode.FILE_TYPE_03.getCode());
            fileEntity.setCreateTime(TimeTool.getYmdHms());
            String nbr = request.getHeader("login-name");
            fileEntity.setCreator("admin");
            rmFileMapper.add(fileEntity);
            String fileId="";
            if(null!=fileEntity){
                fileId+=fileEntity.getFileId();
            }

            resultMap.put("videoLink", videoUrl);
            resultMap.put("url", "");
            resultMap.put("fileId", fileId);
            resultMap.put("isShowPopup", true);
            resultMap.put("imgUrl", imgUrl);

            code= ResultCode.SUCCESS.getCode();
            message="视频上传成功！";
        } catch (RuntimeException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResponse(code,message,resultMap);

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
    public AppResponse buildUserPic(String token) {
        long code=ResultCode.FAILED.getCode();
        String message="用户水印";
        String pictureUrl="";
        try {
            //姓名+手机号码后四位
            String loginNm = stringRedisTemplate.opsForValue().get(token);
            //查询是否存在，存在直接返回
            String userUrl = rmFileMapper.getUserUrl(loginNm);
            if(null!=userUrl && !userUrl.isEmpty()){
                pictureUrl=userUrl;
            }else{
                //不存在，则新增
                SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
                String userNm = sysUserEntity.getUserNm();
                String userMo = sysUserEntity.getUserMobile();
                String userMobile = userMo.substring(7);
                //生成图片
                BufferedImage bufferedImage = DrawTransparentPic.drawTransparent(userNm+userMobile);
                String filePath="user/"+userMo+".png";
                pictureUrl=pathDown+filePath;//图片地址
                //图片保存
                ImageIO.write(bufferedImage, "png", new File(pathUpload +filePath));
                //保存到数据库
                FileEntity file=new FileEntity();
                file.setFilePath(pictureUrl);
                file.setFileType("03");//图片水印
                file.setFileName(loginNm);
                file.setCreator(loginNm);
                file.setCreateTime(TimeTool.getYmdHms());
                file.setFileTitle("用户水印");
                rmFileMapper.add(file);
            }

            code=ResultCode.SUCCESS.getCode();
            message="用户水印成功！";

        } catch (IOException e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new AppResponse(code,message,pictureUrl);
    }

    @Override
    public void deletePicture(String pictures) {
        //String resultInfo = null;
        if(null!=pictures && !pictures.isEmpty()){
            String[] split = pictures.split(",");
            if(null!=split && split.length>0 ){
                List<FileEntity> fileEntities = rmFileMapper.queryPath(split);
                if(null!=fileEntities && fileEntities.size()>0) {
                    for (int i = 0; i < split.length; i++) {
                        //删除图片
                        String filePaths = fileEntities.get(i).getFilePath();
                        /*String[] supers = filePaths.split("/");
                        String aSuper = supers[3];
                        String aSuper = supers[3];
                        String filePath =pathUpload+aSuper;*/
                        String filePath = filePaths.replace(pathDown, pathUpload);

                        int fileId = fileEntities.get(i).getFileId();
                        File file = new File(filePath);
                        if (file.exists()) {//文件是否存在
                            if (file.delete()) {//存在就删了，返回1
                                //数据库
                                rmFileMapper.delete(Integer.toString(fileId));
                                log.info("文件id:" + fileId + ";已删除文件:" + filePath + ";");
                            } else {
                                log.info(filePath + "文件未被清理！");
                            }
                        } else {
                            log.info(filePath + "文件不存在！");
                        }
                    }
                }
            }
        }
    }

    @Override
    public PageResponse getFile(String picPath) {
        long code=ResultCode.FAILED.getCode();
        String message="获取图片地址";
        List<Map<String, Object>> fileUrl=new ArrayList<>();
        try {
            if(null!=picPath && !picPath.isEmpty()){
                String[] split = picPath.split(",");
                fileUrl = rmFileMapper.getFileUrl(split);
            }
            code=ResultCode.SUCCESS.getCode();
            message="获取图片地址成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="获取图片地址失败！";
            e.printStackTrace();
        }

        return new PageResponse(code,message,fileUrl.toArray());
    }

    @Override
    public List<Map<String, Object>> getFileUrl(String[] fileIds) {
        return rmFileMapper.getFileUrl(fileIds);
    }
    /**
     * 获取登录人
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        return loginNm;
    }

}
