package com.religion.zhiyun.patrol.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.patrol.dao.PatrolMapper;
import com.religion.zhiyun.patrol.entity.PatrolEntity;
import com.religion.zhiyun.patrol.service.PatrolService;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.response.AppResponse;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatrolServiceImpl implements PatrolService {

    @Autowired
    private PatrolMapper patrolMapper;


    @Override
    public AppResponse getOuHai() {
        long code= ResultCode.FAILED.getCode();
        String message="查询瓯海区巡查任务完成度失败！";
        List<Map<String, Object>> ouHaiList=new ArrayList<>();
        try {
            ouHaiList = patrolMapper.getOuHai();
            code= ResultCode.SUCCESS.getCode();
            message="查询瓯海区巡查任务完成度成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,ouHaiList.toArray());
    }

    @Override
    public AppResponse getTown() {
        long code= ResultCode.FAILED.getCode();
        String message="查询街镇巡查任务完成度失败！";
        List<Map<String, Object>> townList=new ArrayList<>();
        try {
            townList = patrolMapper.getTown();
            code= ResultCode.SUCCESS.getCode();
            message="查询街镇巡查任务完成度成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,townList.toArray());
    }

    @Override
    public AppResponse getVenuesDetail(Map<String, Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="查询场所巡查任务完成情况失败！";
        List<Map<String, Object>> townList=new ArrayList<>();
        try {

            String type = (String) map.get("type");
            String stat = (String) map.get("stat");
            if(GeneTool.isEmpty(type)){
                throw new RuntimeException("巡查类型为空！");
            }
            if(GeneTool.isEmpty(stat)){
                throw new RuntimeException("进度状态类型！");
            }else if("01".equals(stat)){
                stat="";
            }
            List<Map<String,Object>> townCode = patrolMapper.getTownCode();
            if(null!=townCode && townCode.size()>0){
                for(int i=0;i<townCode.size();i++){
                    Map<String, Object> mapTown=new HashMap<>();
                    Map<String, Object> coMap = townCode.get(i);
                    String codes = (String) coMap.get("codes");
                    String townNm = (String) coMap.get("townNm");
                    List<Map<String, Object>> detailList=new ArrayList<>();
                    if("01".equals(type)){
                        detailList=patrolMapper.getFireDetail(stat,codes);
                    }else if("02".equals(type)){
                        detailList=patrolMapper.getBuildDetail(stat,codes);
                    }else if("03".equals(type)){
                        detailList=patrolMapper.getSelfDetail(stat,codes);
                    }
                    mapTown.put("townName",townNm);
                    mapTown.put("townCode",codes);
                    mapTown.put("venuesList",detailList.toArray());

                    townList.add(mapTown);
                }
            }
            code= ResultCode.SUCCESS.getCode();
            message="查询场所巡查任务完成情况成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,townList.toArray());
    }

    @Override
    public AppResponse getTownSummary(Map<String,Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="街镇巡查任务汇总失败！";
        List<Map<String, Object>> townSummaryList=new ArrayList<>();
        try {
            String town = (String) map.get("town");
            if(GeneTool.isEmpty(town)){
                throw new RuntimeException("街镇不能为空！");
            }
            townSummaryList = patrolMapper.getTownSummary(town);

            code= ResultCode.SUCCESS.getCode();
            message="街镇巡查任务汇总成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,townSummaryList.toArray());
    }

    @Override
    public AppResponse getVenuesItems(Map<String,Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="查询场所巡查任务完成度失败！";
        List<Map<String, Object>> venuesList=new ArrayList<>();
        try {

            String venuesId = (String) map.get("venuesId");
            String venuesName = (String) map.get("venuesName");
            if(GeneTool.isEmpty(venuesId) && GeneTool.isEmpty(venuesName)){
                throw new RuntimeException("场所不能为空！");
            }else if(!GeneTool.isEmpty(venuesName)){
                venuesId="";
            }
            Map<String, Object> venuesMap = patrolMapper.getVenuesScore(venuesId, venuesName);
            if(null!=venuesMap){
                List<Map<String, Object>> totalItemList = patrolMapper.getTotalItem(venuesId, venuesName);
                if(null!=totalItemList && totalItemList.size()>0){
                    List<Map<String, Object>> fenList=new ArrayList<>();
                    for(int j=0;j<totalItemList.size();j++){
                        Map<String, Object> mapFen = totalItemList.get(j);
                        Long indexs = (Long) mapFen.get("indexs");
                        if(1==indexs){
                            fenList=patrolMapper.getFireItem(venuesId, venuesName);
                        }else if(2==indexs){
                            fenList=patrolMapper.getSelfItem(venuesId, venuesName);
                        }
                        mapFen.put("itemList",fenList.toArray());
                    }
                    venuesMap.put("patrolList",totalItemList.toArray());
                }
                venuesList.add(venuesMap);
            }
            code= ResultCode.SUCCESS.getCode();
            message="查询场所巡查任务完成度成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,venuesList.toArray());
    }

    @Override
    public AppResponse getVenuesRank(String type) {
        long code= ResultCode.FAILED.getCode();
        String message="场所综合得分排行榜获取失败！";
        List<Map<String, Object>> rankList=new ArrayList<>();
        try {
            if(GeneTool.isEmpty(type)){
                throw new RuntimeException("排行榜类型不能为空！");
            }
            rankList = patrolMapper.getVenuesRank(type);
            if(null!=rankList && rankList.size()>0){
                for(int i=0;i<rankList.size();i++){
                    Map<String, Object> map = rankList.get(i);
                    map.put("rank",i+1);
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message="场所综合得分排行榜获取成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,rankList.toArray());
    }

    @Override
    public AppResponse getTownRank() {
        long code= ResultCode.FAILED.getCode();
        String message="街镇综合得分获取失败！";
        List<Map<String, Object>> rankList=new ArrayList<>();
        try {
            rankList = patrolMapper.getTownRank();
            if(null!=rankList && rankList.size()>0){
                for(int i=0;i<rankList.size();i++){
                    Map<String, Object> map = rankList.get(i);
                    map.put("rank",i+1);
                }

            }

            code= ResultCode.SUCCESS.getCode();
            message="街镇综合得分获取成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message,rankList.toArray());
    }
}
