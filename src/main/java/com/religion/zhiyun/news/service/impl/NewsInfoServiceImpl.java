package com.religion.zhiyun.news.service.impl;

import com.religion.zhiyun.news.dao.RmNewsInfoMapper;
import com.religion.zhiyun.news.entity.NewsEntity;
import com.religion.zhiyun.news.service.NewsInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.service.RmFileService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.TokenUtils;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.activiti.engine.impl.util.json.XMLTokener.entity;

@Service
public class NewsInfoServiceImpl implements NewsInfoService {

    @Autowired
    private RmNewsInfoMapper rmNewsInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RmFileService rmFileService;


    @Override
    public PageResponse add(NewsEntity newsEntity,String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "新闻信息保存！";
        try {
            String login = this.getLogin(token);
            newsEntity.setCreator(login);
            newsEntity.setLastModifier(login);
            newsEntity.setCreateTime(TimeTool.getYmdHms());
            newsEntity.setLastModifyTime(TimeTool.getYmdHms());
            newsEntity.setNewsDown("1");
            //图片处理
            String picturesPath = newsEntity.getNewsPicturesPath();
            String picturesPathRemove = newsEntity.getPicturesPathRemove();
            //清理图片
            if(null!=picturesPathRemove && !picturesPathRemove.isEmpty()){
                rmFileService.deletePicture(picturesPathRemove);
                String[] split = picturesPathRemove.split(",");
                if(null!=split && split.length>0 ){
                    for (int i=0;i<split.length;i++){
                        String re = split[i];
                        if(picturesPath.contains(re)){
                            String replace = picturesPath.replace(re+",",  "");
                            picturesPath=replace;
                        }
                    }
                    //保存图片
                    newsEntity.setNewsPicturesPath(picturesPath);
                }
            }
            rmNewsInfoMapper.add(newsEntity);
            //添加链接
            if(newsEntity.getNewsOpera().equals("01")){
                int newsId = newsEntity.getNewsId();
                newsEntity.setNewsRef("http://localhost:8081/detail?aid="+newsId);
                rmNewsInfoMapper.update(newsEntity);

            }
            code=ResultCode.SUCCESS.getCode();
            message= "新闻信息保存成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message= "新闻信息保存失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message);
    }

    @Override
    public PageResponse update(NewsEntity newsEntity,String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "新闻信息修改！";
        try {
            String login = this.getLogin(token);
            newsEntity.setLastModifier(login);
            newsEntity.setLastModifyTime(TimeTool.getYmdHms());
            //图片处理
            String picturesPath = newsEntity.getNewsPicturesPath();
            String picturesPathRemove = newsEntity.getPicturesPathRemove();
            //清理图片
            if(null!=picturesPathRemove && !picturesPathRemove.isEmpty()){
                rmFileService.deletePicture(picturesPathRemove);
                String[] split = picturesPathRemove.split(",");
                if(null!=split && split.length>0 ){
                    for (int i=0;i<split.length;i++){
                        String re = split[i];
                        if(picturesPath.contains(re)){
                            String replace = picturesPath.replace(re+",",  "");
                            picturesPath=replace;
                        }
                    }
                    //保存图片
                    newsEntity.setNewsPicturesPath(picturesPath);
                }
            }
            rmNewsInfoMapper.update(newsEntity);

            code=ResultCode.SUCCESS.getCode();
            message= "新闻信息修改成功！";

        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message= "新闻信息修改失败！";
            e.printStackTrace();
        }

        return new PageResponse(code,message);
    }

    @Override
    public PageResponse getNewsById(int newsId) {
        long code= ResultCode.FAILED.getCode();
        String message= "获取新闻信息！";
        List<NewsEntity> list=new ArrayList<>();
        try {
            NewsEntity news = rmNewsInfoMapper.getNewsById(newsId);
            if(null==news){
                throw new RuntimeException("新闻信息丢失，请联系管理员！");
            }
            //图片处理
            String newsPicturesPath = news.getNewsPicturesPath();
            //清理图片
            if(null!=newsPicturesPath && !newsPicturesPath.isEmpty()){
                List<Map<String, Object>> fileUrl = rmFileService.getFileUrl(newsPicturesPath.split(","));
                news.setFileList(fileUrl.toArray());
            }
            list.add(news);

            code=ResultCode.SUCCESS.getCode();
            message= "新闻信息修改成功！";

        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message= "新闻信息修改失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
    }

    @Override
    public void delete(int newsId) {
        rmNewsInfoMapper.delete(newsId);
    }

    @Override
    public PageResponse newsDown(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="新闻上下架！";
        try {
            String newsId = (String)map.get("newsId");
            String newsDown = (String) map.get("newsDown");
            String login = this.getLogin(token);
            NewsEntity news=new NewsEntity();
            news.setNewsDown(newsDown);
            news.setNewsId(Integer.valueOf(newsId));
            news.setLastModifier(login);
            news.setLastModifyTime(TimeTool.getYmdHms());
            rmNewsInfoMapper.updateNewsDown(news);

            code=ResultCode.SUCCESS.getCode();
            if("1".equals(newsDown)){
                message="已上架！";
            }else{
                message="已下架！";
            }

        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception ex){
            message="新闻上下架处理失败";
            ex.printStackTrace();
        }
        return new PageResponse(code,message);
    }

    @Override
    public PageResponse getNewsByPage(Map<String, Object> map,String token){
        long code= ResultCode.FAILED.getCode();
        String message="新闻信息查询";
        List<NewsEntity> dataList =new ArrayList<>();
        long total=0l;
        try {
            String newsTitle = (String)map.get("newsTitle");
            String newsRefType = (String)map.get("newsRefType");
            String newsType = (String)map.get("newsType");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //获取登录用户
            String newsFor="";
            String login = this.getLogin(token);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
            if(null!=sysUserEntity){
                if("admin".equals(sysUserEntity.getUserNm())){
                    newsFor="";
                }else{
                    newsFor="02";
                }
            }else{
                newsFor="01";
            }
            ParamsVo vo=new ParamsVo();
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchOne(newsTitle);
            vo.setSearchTwo(newsRefType);
            vo.setSearchThree(newsFor);
            vo.setSearchFour(newsType);
            //查询
            dataList = rmNewsInfoMapper.getNewsByPage(vo);
            if("02".equals(newsRefType)){
                if(null!=dataList && dataList.size()>0){
                    for(int i=0;i<dataList.size();i++){
                        NewsEntity newsEntity = dataList.get(i);
                        String newsPicturesPath = newsEntity.getNewsPicturesPath();
                        if(null!=newsPicturesPath && !newsPicturesPath.isEmpty()){
                            List<Map<String, Object>> fileUrl = rmFileService.getFileUrl(newsPicturesPath.split(","));
                            newsEntity.setFileList(fileUrl.toArray());
                        }
                    }
                }
            }

            //总条数
            total=rmNewsInfoMapper.getTotal(vo);

            code=ResultCode.SUCCESS.getCode();
            message="新闻信息查询成功";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception ex){
            message="新闻信息查询失败";
            ex.printStackTrace();
        }
        return new PageResponse(code,message,total,dataList.toArray());
    }

    @Override
    public PageResponse getNewsPage(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="新闻信息查询";
        List<NewsEntity> dataList =new ArrayList<>();
        long total=0l;
        try {
            String newsTitle = (String)map.get("newsTitle");
            String newsRefType = (String)map.get("newsRefType");
            String newsFor = (String)map.get("newsFor");
            String newsType = (String)map.get("newsType");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //获取登录用户
            //查询
            ParamsVo vo=new ParamsVo();
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchOne(newsTitle);
            vo.setSearchTwo(newsRefType);
            vo.setSearchThree(newsFor);
            vo.setSearchFour(newsType);
            dataList = rmNewsInfoMapper.getPcNewsPage(vo);
            //总条数
            total=rmNewsInfoMapper.getPcNewsTotal(vo);

            code=ResultCode.SUCCESS.getCode();
            message="新闻信息查询成功";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception ex){
            message="新闻信息查询失败";
            ex.printStackTrace();
        }
        return new PageResponse(code,message,total,dataList.toArray());
    }

    @Override
    public PageResponse getNewsContent(int newId) {

        long code= ResultCode.FAILED.getCode();
        String message="获取新闻内容失败";
        List<NewsEntity> dataList =new ArrayList<>();
        try {
            dataList = rmNewsInfoMapper.getNewsContent(newId);

            if(null!=dataList && dataList.size()>0){
                NewsEntity enti = dataList.get(0);
                String newsPicturesPath = enti.getNewsPicturesPath();
                if(!GeneTool.isEmpty(newsPicturesPath)){
                    String[] split = newsPicturesPath.split(",");
                    List<Map<String, Object>> fileUrl = rmFileService.getFileUrl(split);
                    enti.setFileList(fileUrl.toArray());
                }
                String newsVideosPath = enti.getNewsVideosPath();
                if(!GeneTool.isEmpty(newsVideosPath)){
                    String[] split = newsVideosPath.split(",");
                    List<Map<String, Object>> fileUrl = rmFileService.getFileUrl(split);
                    enti.setVideoList(fileUrl.toArray());
                }
                //String replace = enti.getNewsContent().replace("\t", "  ");
                //enti.setNewsContent(replace);

            }
            code=ResultCode.SUCCESS.getCode();
            message="获取新闻内容成功";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception ex){
            message="获取新闻内容失败";
            ex.printStackTrace();
        }
        return new PageResponse(code,message,dataList.toArray());
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
