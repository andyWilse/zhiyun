package com.religion.zhiyun.initdata;


import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.RmUserVenuesMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.entity.UserVenuesEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class InitServiceImpl implements InitService{
    @Autowired
    private RmUserVenuesMapper initMapper;
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;
    @Override
    public AppResponse userVenues() {
        long code= ResultCode.FAILED.getCode();
        String  message="用户场所关联表处理失败！";
        try {
            //获取用户
            List<SysUserEntity> userList = initMapper.getUsers();
            Timestamp timestamp = new Timestamp(new Date().getTime());

            for(int i=0;i<userList.size();i++){
                SysUserEntity sysUserEntity = userList.get(i);
                int userId = sysUserEntity.getUserId();
                String relVenuesId = sysUserEntity.getRelVenuesId();
                if(null!=relVenuesId && ""!=relVenuesId){
                    String[] split = relVenuesId.split(",");
                    if(null!=split){
                        for(int j=0;j<split.length;j++){
                            UserVenuesEntity uvEntity=new UserVenuesEntity();
                            //查询场所
                            String ver = split[j];
                            uvEntity.setUserId(userId);
                            uvEntity.setValidInd("1");
                            uvEntity.setCreateTime(timestamp);
                            uvEntity.setLastModifyTime(timestamp);
                            VenuesEntity venueByID = rmVenuesInfoMapper.getVenueByID(ver);
                            if(null!=venueByID){
                                uvEntity.setVenuesId(Integer.parseInt(ver));
                                uvEntity.setMark("success");
                            }else{
                                uvEntity.setMark("false");
                            }
                            //增加
                            initMapper.add(uvEntity);

                        }
                    }
                }else{
                    UserVenuesEntity uvEntity=new UserVenuesEntity();
                    uvEntity.setUserId(userId);
                    uvEntity.setValidInd("1");
                    uvEntity.setCreateTime(timestamp);
                    uvEntity.setLastModifyTime(timestamp);
                    uvEntity.setMark("error");
                    System.out.println("用户"+userId+"场所为空！");
                }
            }
            code= ResultCode.SUCCESS.getCode();
            message="用户场所关联表处理成功！";
        } catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());
        }


        return new AppResponse(code,message);
    }
}
