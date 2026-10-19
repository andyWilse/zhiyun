package com.religion.zhiyun.utils.base;

import com.religion.zhiyun.user.dao.RmUserVenuesMapper;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReParamPack {

    private static SysUserMapper sysUserMapper;

    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        ReParamPack.sysUserMapper = sysUserMapper;
    }

    private static RmUserVenuesMapper rmUserVenuesMapper;
    @Autowired
    public void setRmUserVenuesMapper(RmUserVenuesMapper rmUserVenuesMapper) {
        ReParamPack.rmUserVenuesMapper = rmUserVenuesMapper;
    }

    public static ParamsVo packAuthParam() {
        String login = TransParam.loginName;
        ParamsVo vo=new ParamsVo();
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
        if(null!=sysUserEntity){
            if(!"10000001".equals(sysUserEntity.getIdentity())){
                vo.setArea(sysUserEntity.getArea());
                vo.setTown(sysUserEntity.getTown());
                List<Integer> userVenues = rmUserVenuesMapper.getUserVenueList(sysUserEntity.getUserId(), 0);
                if(null!=userVenues && userVenues.size()>0){
                    vo.setVenuesList(userVenues);
                }
                vo.setSearchFive(login);
            }
        }else{
            throw new RuntimeException("用户已过期，请重新登录！");
        }
        return vo;
    }
}
