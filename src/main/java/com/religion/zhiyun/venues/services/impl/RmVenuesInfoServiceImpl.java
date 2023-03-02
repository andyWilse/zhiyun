package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.utils.map.GeocoderLatitudeUtil;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.services.RmVenuesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

@Service
public class RmVenuesInfoServiceImpl implements RmVenuesInfoService {
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;
    @Autowired
    private RmFileMapper rmFileMapper;

    @Override
    public RespPageBean add(VenuesEntity venuesEntity) {
        long code= ResultCode.FAILED.getCode();
        String message="场所信息保存失败！";
        try{
            //获取经纬度
            String venuesAddres = venuesEntity.getVenuesAddres();
            if(null!=venuesAddres && ""!=venuesAddres){
                String coordinate = GeocoderLatitudeUtil.getCoordinate(venuesAddres);
                String[] split = coordinate.split(",");
                String lng=split[0];
                String lat=split[1];
                if("1".equals(lng) || "1".equals(lat) ){
                    message="无法获取经纬度，请重新填写详细地址！";
                    throw new RuntimeException(message);
                }
                venuesEntity.setLongitude(lng);
                venuesEntity.setLatitude(lat);
            }else{
                message="场所地址信息丢失！";
                throw new RuntimeException(message);
            }

            Timestamp timestamp = new Timestamp(new Date().getTime());
            venuesEntity.setCreateTime(timestamp);
            venuesEntity.setLastModifyTime(timestamp);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String nbr = request.getHeader("login-name");
            venuesEntity.setCreator(nbr);
            venuesEntity.setLastModifier(nbr);
            venuesEntity.setVenuesStatus(ParamCode.VENUES_STATUS_01.getCode());
            rmVenuesInfoMapper.add(venuesEntity);

            code=ResultCode.SUCCESS.getCode();
            message="新增场所信息成功！";

        }catch (RuntimeException e){
            e.printStackTrace();
        }
        catch (Exception e){
            message="新增场所失败,请联系管理员！";
            e.printStackTrace();
        }
        RespPageBean bean=new RespPageBean(code,message);
        return bean;
    }

    @Override
    public RespPageBean update(VenuesEntity venuesEntity) {
        long code= ResultCode.SUCCESS.getCode();
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new Date().getTime());
            venuesEntity.setLastModifyTime(timestamp);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String nbr = request.getHeader("login-name");
            venuesEntity.setLastModifier(nbr);
            rmVenuesInfoMapper.update(venuesEntity);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);
    }

    @Override
    public int delete(int venuesId) {
        return rmVenuesInfoMapper.delete(venuesId);
    }

    @Override
    public VenuesEntity getByResponsiblePerson(String responsiblePerson) {
        return rmVenuesInfoMapper.getByResponsiblePerson(responsiblePerson
        );
    }

    @Override
    public List<VenuesEntity> queryAll(String search) {
        return rmVenuesInfoMapper.queryAll(search);
    }

    @Override
    public List<VenuesEntity> querySectAll(String religiousSect) {
        return rmVenuesInfoMapper.querySectAll(religiousSect);
    }

    @Override
    public List<VenuesEntity> getByVenuesFaculty(String venuesName, String responsiblePerson) {
        return rmVenuesInfoMapper.getByVenuesFaculty(venuesName,responsiblePerson);
    }

    @Override
    public VenuesEntity getVenueByID(String venuesId) {
        return rmVenuesInfoMapper.getVenueByID(venuesId);
    }

    @Override
    public Map<String, Object> getAllNum() {
        return rmVenuesInfoMapper.getAllNum();
    }

    @Override
    public RespPageBean getVenuesByPage(Integer page, Integer size, String venuesName, String responsiblePerson, String religiousSect) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<VenuesEntity> dataList=rmVenuesInfoMapper.getVenuesByPage(page,size,venuesName,responsiblePerson,religiousSect);
        Object[] objects = dataList.toArray();
        /*VenuesEntity[] date = new VenuesEntity[dataList.size()];
        VenuesEntity[] datas = dataList.toArray(date);*/
        Long total=rmVenuesInfoMapper.getTotal(venuesName,responsiblePerson,religiousSect);
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }

    @Override
    public AppResponse queryVenues(String search) {
        long code=0l;
        String message="";
        List<Map<String, Object>> venuesList =null;
        try {
            venuesList = rmVenuesInfoMapper.queryVenues(search);
            code= ResultCode.SUCCESS.getCode();
            message="场所下拉数据获取成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="场所下拉数据获取失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,venuesList.toArray());
    }

    @Override
    public AppDetailRes getMapVenuesDetail(String venuesId) {
        long code=ResultCode.FAILED.getCode();
        String message="获取地图场所详情失败！";
        Map<String, Object> venuesMap =new HashMap<>();
        try {
            venuesMap = rmVenuesInfoMapper.getVenuesByID(venuesId);

            if(null!=venuesMap && venuesMap.size()>0){
                //获取图片地址
                String picturesPath= (String) venuesMap.get("picturesPath");
                if(!picturesPath.isEmpty()){
                    //查询地图地址
                    String[] split = picturesPath.split(",");
                    Object[] file ={};
                    List<String> fileEntities = rmFileMapper.getPath(split);
                    if(null!=fileEntities && fileEntities.size()>0){
                        file = fileEntities.toArray();
                    }
                    venuesMap.put("images",file);
                }
                //关联活动
                //String relVenuesId= (String) venuesMap.get("relVenuesId");
                List<Map<String, Object>> joinActivity = rmVenuesInfoMapper.getFiling(venuesId);
                venuesMap.put("joinActivity",joinActivity.toArray());

                //三人驻堂
                List<Map<String, Object>> users = rmVenuesInfoMapper.getUsers(venuesId);
                venuesMap.put("garrison",users.toArray());

                //监管干部
                List<Map<String, Object>> ganUsers = rmVenuesInfoMapper.getGanUsers(venuesId);
                venuesMap.put("janGan",ganUsers.toArray());

                //教职人员
                Map<String, Object> oneStaffDirector=new HashMap<>();
                Map<String, Object> twoStaffDirector=new HashMap<>();
                Map<String, Object> refStaffDirector=new HashMap<>();
                List<Map<String, Object>> staffs = rmVenuesInfoMapper.getStaffs(venuesId);
                if(null!=staffs && staffs.size()>0){
                    for(int i=0;i<staffs.size();i++){
                        if(i==0){
                            oneStaffDirector= staffs.get(0);
                            staffs.remove(0);
                        }else if(i==1){
                            twoStaffDirector= staffs.get(1);
                            staffs.remove(1);
                        }
                    }
                }
                venuesMap.put("oneStaffDirector",oneStaffDirector);
                venuesMap.put("twoStaffDirector",twoStaffDirector);
                venuesMap.put("refStaffDirector",refStaffDirector);

                //负责人
                Map<String, Object> oneDirector=new HashMap<>();
                String responsiblePerson= (String) venuesMap.get("responsiblePerson");
                oneDirector.put("id","1");
                oneDirector.put("name",responsiblePerson);
                oneDirector.put("phone","");
                Map<String, Object> twoDirector=new HashMap<>();
                twoDirector.put("id","2");
                twoDirector.put("name","");
                twoDirector.put("phone","");
                Map<String, Object> workDirector=new HashMap<>();
                String liaisonMan= (String) venuesMap.get("liaisonMan");
                workDirector.put("id","3");
                workDirector.put("name",liaisonMan);
                workDirector.put("phone","");

                venuesMap.put("oneDirector",oneDirector);
                venuesMap.put("twoDirector",twoDirector);
                venuesMap.put("workDirector",workDirector);

            }else{
                message="获取地图场所详情失败！！";
                throw  new RuntimeException(message);
            }
            code= ResultCode.SUCCESS.getCode();
            message="获取地图场所详情成功！";
        } catch (RuntimeException r ){
            r.printStackTrace();
        } catch(Exception e) {
            message="获取地图场所详情失败！！";
            e.printStackTrace();
        }

        return new AppDetailRes(code,message, venuesMap);
    }

    @Override
    public AppResponse getMapVenues(String search, String religiousSect) {
        long code= 0;
        String message= null;
        List<Map<String, Object>> mapVenues = null;
        try {
            code = ResultCode.FAILED.getCode();
            message = "获取地图场所信息失败！";
            mapVenues = rmVenuesInfoMapper.getMapVenues(search, religiousSect);
            if(mapVenues!=null && mapVenues.size()>0){
                code= ResultCode.SUCCESS.getCode();
                message="获取地图场所信息成功！";
            }
        } catch (RuntimeException r ){
            r.printStackTrace();
        } catch (Exception e) {
            code = ResultCode.FAILED.getCode();
            message = "获取地图场所信息失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,mapVenues.toArray());
    }
}
