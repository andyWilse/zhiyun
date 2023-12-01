package com.religion.zhiyun.record.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.record.dao.OperateRecordMapper;
import com.religion.zhiyun.record.entity.RecordEntity;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public AppResponse addRecord(Map<String, Object> opMap) {
        long code= ResultCode.FAILED.getCode();
        String message="新增操作记录失败！";

        try {
            String operator = (String) opMap.get("operator");
            String operateContent = (String) opMap.get("operateContent");
            String operateDetail = (String) opMap.get("operateDetail");
            Date operateTime = (Date) opMap.get("operateTime");
            String operateRef = (String) opMap.get("operateRef");
            String operateType = (String) opMap.get("operateType");

            RecordEntity logsEntity=new RecordEntity();
            logsEntity.setOperator(operator);
            logsEntity.setOperateContent(operateContent);
            logsEntity.setOperateDetail(operateDetail);
            logsEntity.setOperateTime(operateTime);
            logsEntity.setOperateRef(operateRef);
            logsEntity.setOperateType(operateType);
            //logsEntity.setOperateTm();
            rmUserLogsInfoMapper.add(logsEntity);

            code= ResultCode.SUCCESS.getCode();
            message="新增操作记录成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AppResponse(code,message);
    }

    @Override
    public RespPageBean findRecordByPage(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="操作记录信息查询！";
        List<VenuesEntity> dataList=new ArrayList<>();
        Long total=0l;
        try {
            String userName = (String)map.get("userName");
            String type = (String)map.get("type");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            String start = (String)map.get("start");
            String end = (String)map.get("end");

            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);

            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //ParamsVo auth = this.getAuth(token);
            ParamsVo auth =new ParamsVo();
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(userName);
            auth.setSearchTwo(type);
            auth.setSearchThree(start);
            auth.setSearchFour(end);
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

    @Override
    public AppResponse addMonitRecord(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="监控查看记录保存！";

        try {
            String venuesId = (String) map.get("venuesId");
            String accessNumber = (String) map.get("accessNumber");
            String type = (String) map.get("type");//01-例行巡查；02-警报排查；03-维修检测；04-其他；
            String content = (String) map.get("content");
            if(null==content || content.isEmpty()){
                content="监控查看";
            }
            if("01".equals(type)){
                type="例行巡查";
            }else if("02".equals(type)){
                type="警报排查";
            }else if("03".equals(type)){
                type="维修检测";
            }else if("04".equals(type)){
                type="其他";
            }
            RecordEntity recordEntity=new RecordEntity();
            String login = this.getLogin(token);
            recordEntity.setOperator(login);
            recordEntity.setOperateTime(new Date());
            recordEntity.setOperateType("11");
            recordEntity.setOperateRef(accessNumber);
            recordEntity.setOperateDetail(content);
            recordEntity.setOperateContent(type);
            rmUserLogsInfoMapper.add(recordEntity);

            code= ResultCode.SUCCESS.getCode();
            message="操作记录信息查询成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message);
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
