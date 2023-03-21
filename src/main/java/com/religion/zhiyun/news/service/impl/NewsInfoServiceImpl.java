package com.religion.zhiyun.news.service.impl;

import com.religion.zhiyun.news.dao.RmNewsInfoMapper;
import com.religion.zhiyun.news.entity.NewsEntity;
import com.religion.zhiyun.news.service.NewsInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.TokenUtils;
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


    @Override
    public RespPageBean add(NewsEntity newsEntity,String token) {
        long code= ResultCode.SUCCESS.getCode();
        try {
            String login = this.getLogin(token);
            newsEntity.setCreator(login);
            newsEntity.setLastModifier(login);
            //Timestamp timestamp = new Timestamp(new Date().getTime());
            newsEntity.setCreateTime(TimeTool.getYmdHms());
            newsEntity.setLastModifyTime(TimeTool.getYmdHms());
            newsEntity.setNewsDown("1");
            rmNewsInfoMapper.add(newsEntity);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);
    }

    @Override
    public RespPageBean update(NewsEntity newsEntity,String token) {
        long code= ResultCode.SUCCESS.getCode();
        try {
            String login = this.getLogin(token);
            /*SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
            if(null==sysUserEntity){
                throw new RuntimeException("登录人信息丢失，请登陆后重试！");
            }*/
            newsEntity.setLastModifier(login);
            //Timestamp timestamp = new Timestamp(new Date().getTime());
            newsEntity.setLastModifyTime(TimeTool.getYmdHms());
            rmNewsInfoMapper.update(newsEntity);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }

        return new RespPageBean(code);
    }

    @Override
    public void delete(int newsId) {
        rmNewsInfoMapper.delete(newsId);
    }

    @Override
    public PageResponse getNewsByPage(Map<String, Object> map,String token){
        long code= ResultCode.FAILED.getCode();
        String message="新闻信息查询";
        List<NewsEntity> dataList =new ArrayList<>();
        long total=0l;
        try {
            String newsTitle = (String)map.get("newsTitle");
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
                    newsFor="01";
                }
            }else{
                newsFor="02";
            }
            //查询
            dataList = rmNewsInfoMapper.getNewsByPage(page, size, newsTitle,newsFor);
            if(null!=dataList && dataList.size()>0){
                for(int i=0;i< dataList.size();i++){
                    NewsEntity newsEntity = dataList.get(i);
                    //Timestamp createTime = newsEntity.getCreateTime();
                    newsEntity.setReleaseYear(TimeTool.getCurrentYear(new Date()));
                    newsEntity.setReleaseMonth(TimeTool.getCurrentMonth(new Date()));
                    newsEntity.setReleaseDay(TimeTool.getCurrentDay(new Date()));
                }
            }
            //总条数
            total=rmNewsInfoMapper.getTotal(newsTitle,newsFor);

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
