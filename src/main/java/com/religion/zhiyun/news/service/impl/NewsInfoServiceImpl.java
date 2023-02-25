package com.religion.zhiyun.news.service.impl;

import com.religion.zhiyun.news.dao.RmNewsInfoMapper;
import com.religion.zhiyun.news.entity.NewsEntity;
import com.religion.zhiyun.news.service.NewsInfoService;
import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NewsInfoServiceImpl implements NewsInfoService {

    @Autowired
    private RmNewsInfoMapper rmNewsInfoMapper;

    @Override
    public RespPageBean add(NewsEntity newsEntity) {
        long code= ResultCode.SUCCESS.getCode();
        try {
            SysUserEntity entity = TokenUtils.getToken();
            if(null==entity){
                throw new RuntimeException("登录人信息丢失，请登陆后重试！");
            }
            newsEntity.setCreator(entity.getLoginNm());
            newsEntity.setLastModifier(entity.getLoginNm());
            Timestamp timestamp = new Timestamp(new Date().getTime());
            newsEntity.setCreateTime(timestamp);
            newsEntity.setLastModifyTime(timestamp);
            newsEntity.setNewsDown("1");
            rmNewsInfoMapper.add(newsEntity);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);
    }

    @Override
    public RespPageBean update(NewsEntity newsEntity) {
        long code= ResultCode.SUCCESS.getCode();
        try {
            SysUserEntity entity = TokenUtils.getToken();
            if(null==entity){
                throw new RuntimeException("登录人信息丢失，请登陆后重试！");
            }
            newsEntity.setLastModifier(entity.getLoginNm());
            Timestamp timestamp = new Timestamp(new Date().getTime());
            newsEntity.setLastModifyTime(timestamp);
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
    public RespPageBean getNewsByPage(Map<String, Object> map){
        long code= ResultCode.SUCCESS.getCode();
        RespPageBean respPageBean = null;
        try {
            String newsTitle = (String)map.get("newsTitle");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            List<NewsEntity> dataList = rmNewsInfoMapper.getNewsByPage(page, size, newsTitle);
            Object[] objects=null;
            if(null!=dataList && dataList.size()>0){
                for(int i=0;i< dataList.size();i++){
                    NewsEntity newsEntity = dataList.get(i);
                    Timestamp createTime = newsEntity.getCreateTime();
                    newsEntity.setReleaseYear(TimeTool.getCurrentYear(createTime));
                    newsEntity.setReleaseMonth(TimeTool.getCurrentMonth(createTime));
                    newsEntity.setReleaseDay(TimeTool.getCurrentDay(createTime));
                }
                objects = dataList.toArray();
            }
            Long total=rmNewsInfoMapper.getTotal();
            respPageBean = new RespPageBean();
            respPageBean.setResultArr(objects);
            respPageBean.setTotal(total);
        } catch (IOException e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }catch (Exception ex){
            code=ResultCode.FAILED.getCode();
            ex.printStackTrace();
        }
        respPageBean.setCode(code);
        return respPageBean;
    }
}
