package com.religion.zhiyun.monitor.service.impl;

import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MoVenuesEntity;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RmMonitroInfoServiceimpl implements RmMonitroInfoService {
    @Autowired
    private RmMonitroInfoMapper rmMonitroInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SysUserMapper sysUserMapper;


    @Override
    public List<MonitroEntity> allMonitro() {
        return rmMonitroInfoMapper.allMonitro();
    }

    @Override
    public void addMonitro(MonitroEntity monitroEntity) {
        if(!monitroEntity.getMonitorUrl().isEmpty() && !monitroEntity.getAccessNumber().isEmpty()){
            rmMonitroInfoMapper.addMonitro(monitroEntity);
        }
    }

    @Override
    public void updateMonitro(MonitroEntity monitroEntity) {
        rmMonitroInfoMapper.updateMonitro(monitroEntity);
    }

    @Override
    public void deleteMonitro(String monitroId) {
        rmMonitroInfoMapper.deleteMonitro(monitroId);
    }

    @Override
    public PageResponse getAllNum(String token) {
        long code= ResultCode.FAILED.getCode();
        String result="监控数量统计失败！";
        List<Map<String, Object>> allNum = new ArrayList<>();
        try {
            ParamsVo auth = this.getAuth(token);
            allNum = rmMonitroInfoMapper.getAllNum(auth);
            Long venuesMonitorTotal = rmMonitroInfoMapper.getVenuesMonitorTotal(auth);
            if(null!=allNum && allNum.size()>0){
                Map<String, Object> map = allNum.get(0);
                map.put("religionsNum",venuesMonitorTotal);
            }
            code=ResultCode.SUCCESS.getCode();
            result="监控数量统计成功！";
        } catch (RuntimeException e) {
            result=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            result="监控数量统计失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,result,allNum.toArray());
    }

    @Override
    public List<MonitroEntity> getMonitorByState(String state) {
        return rmMonitroInfoMapper.getMonitorByState(state);
    }

    @Override
    public List<MonitroEntity> getMonitorByVenuesId(String state) {
        return rmMonitroInfoMapper.getMonitorByVenuesId(state);
    }

    @Override
    public RespPageBean getMonitrosByPage(Integer page, Integer size, String accessNumber) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<MonitroEntity> dataList=rmMonitroInfoMapper.getMonitrosByPage(page,size,accessNumber);
        Object[] objects = dataList.toArray();
        Long total=rmMonitroInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }

    @Override
    public String getMonitorURLByAccessNum(String accessNum) {
        return rmMonitroInfoMapper.getMonitorURLByAccessNum(accessNum);
    }

    @Override
    public RespPageBean getVenuesMonitor(Integer page, Integer size,String venuesName,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="场所监控查询";
        List<Map<String,Object>> list=new ArrayList<>();
        Long total =0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //获取用户
            ParamsVo auth = this.getAuth(token);
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(venuesName);

            //获取场所信息
            list = rmMonitroInfoMapper.getVenuesMonitor(auth);
            if(null!=list && list.size()>0){
                for(int i=0;i<list.size();i++){
                    Map<String, Object> map = list.get(i);
                    String monitors = (String) map.get("monitors");
                    String[] str={};
                    if(null!=monitors && !monitors.isEmpty()){
                        str=monitors.split(",");
                    }
                    map.put("monitors",str);
                }
            }
            total = rmMonitroInfoMapper.getVenuesMonitorTotal(auth);

            code= ResultCode.SUCCESS.getCode();
            message="场所监控查询成功";
        } catch (Exception e) {
            message="场所监控查询失败";
            e.printStackTrace();
        }
        return new RespPageBean(code,message,total,list.toArray());
    }

    @Override
    public RespPageBean getMonitors(Map<String, Object> map,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="监控详情查询";

        List<Map<String,Object>> list=new ArrayList<>();
        Long total=0l;
        try {
            //获取登录人
            ParamsVo auth = this.getAuth(token);
            //获取前端参数
            String search = (String) map.get("search");
            String state = (String) map.get("state");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer size = Integer.valueOf(sizes);
            Integer page = Integer.valueOf(pages);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(search);
            auth.setSearchTwo(state);

            list = rmMonitroInfoMapper.getMonitors(auth);
            total=rmMonitroInfoMapper.getMonitorsTotal(auth);

            code= ResultCode.SUCCESS.getCode();
            message="监控详情查询成功";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new RespPageBean(code,message,total,list.toArray());
    }

    @Override
    public PageResponse getMoDetail(String search,String type) {
        long code= ResultCode.FAILED.getCode();
        String message="监控详情查询失败";
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            if("01".equals(type)){//地图
                list = rmMonitroInfoMapper.getMoDetail("",search);
            }else if("02".equals(type)){//教职监控
                list = rmMonitroInfoMapper.getMoDetail(search,"");
            }
            //监控地址数据处理
            if(null!=list && list.size()>0){
                for(int i=0;i<list.size();i++){
                    Map<String, Object> map = list.get(i);
                    String monitors = (String) map.get("monitors");
                    String[] str={};
                    if(null!=monitors && !monitors.isEmpty()){
                        str=monitors.split(",");
                    }
                    map.put("monitors",str);
                }
            }
            code= ResultCode.SUCCESS.getCode();
            message="监控详情查询成功";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
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
