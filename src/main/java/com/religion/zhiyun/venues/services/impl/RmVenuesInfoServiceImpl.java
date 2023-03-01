package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.utils.map.GeocoderLatitudeUtil;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.services.RmVenuesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RmVenuesInfoServiceImpl implements RmVenuesInfoService {
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;

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
    public AppResponse getMapVenuesDetail(String venuesId) {
        long code=ResultCode.FAILED.getCode();
        String message="获取地图场所详情失败！";
        VenuesEntity venuesDetail =new VenuesEntity();
        List<VenuesEntity> venuesList =new ArrayList<>();
        try {
            venuesDetail = rmVenuesInfoMapper.getVenueByID(venuesId);
            if(null!=venuesDetail){
                venuesList.add(venuesDetail);
            }else{
                code= ResultCode.FAILED.getCode();
                message="获取地图场所详情失败！！";
                throw  new RuntimeException(message);
            }
            code= ResultCode.SUCCESS.getCode();
            message="获取地图场所详情成功！";
        } catch (RuntimeException r ){
            r.printStackTrace();
        } catch(Exception e) {
            code= ResultCode.FAILED.getCode();
            message="获取地图场所详情失败！！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,venuesList.toArray());
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
