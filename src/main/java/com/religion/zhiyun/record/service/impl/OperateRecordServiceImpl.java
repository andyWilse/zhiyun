package com.religion.zhiyun.record.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.record.dao.OperateRecordMapper;
import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class OperateRecordServiceImpl implements OperateRecordService {
    @Autowired
    private OperateRecordMapper rmUserLogsInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void add(RecordEntity logsEntity) {
        rmUserLogsInfoMapper.add(logsEntity);
    }

    @Override
    public RespPageBean findRecordByPage(Integer page, Integer size, String userName, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="操作记录信息查询！";

        List<VenuesEntity> dataList=new ArrayList<>();
        Long total=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            ParamsVo auth = this.getAuth(token);
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(userName);
            //获取数据
            dataList=rmUserLogsInfoMapper.findRecordByPage(auth);
            //获取条数
            total=rmUserLogsInfoMapper.getTotal(auth);

            code= ResultCode.SUCCESS.getCode();
            message="操作记录信息查询成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RespPageBean(code,message,total,dataList.toArray());
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

    /**
     * 获取
     * @return
     */
    public ParamsVo getAuth(String token){
        String login = this.getLogin(token);
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
        String area="";
        String town ="";
        String relVenuesId="";
        String[] venuesArr={};
        if(null!=sysUserEntity){
            relVenuesId = sysUserEntity.getRelVenuesId();
            town = sysUserEntity.getTown();
            area = sysUserEntity.getArea();
        }else{
            throw new RuntimeException("用户已过期，请重新登录！");
        }
        ParamsVo vo=new ParamsVo();
        vo.setArea(area);
        vo.setTown(town);
        vo.setVenues(relVenuesId);
        if(null!=relVenuesId && !relVenuesId.isEmpty()){
            venuesArr=relVenuesId.split(",");
            vo.setVenuesArr(venuesArr);
        }
        return vo;

    }
}
