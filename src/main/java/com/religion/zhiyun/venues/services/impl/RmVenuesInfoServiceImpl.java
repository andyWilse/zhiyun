package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.sys.login.api.ResultCode;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RmVenuesInfoServiceImpl implements RmVenuesInfoService {
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;

    @Override
    public RespPageBean add(VenuesEntity venuesEntity) {
        long code= ResultCode.SUCCESS.getCode();
        try{
            Timestamp timestamp = new Timestamp(new Date().getTime());
            venuesEntity.setCreateTime(timestamp);
            venuesEntity.setLastModifyTime(timestamp);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String nbr = request.getHeader("login-name");
            venuesEntity.setCreator(nbr);
            venuesEntity.setLastModifier(nbr);
            venuesEntity.setVenuesStatus(ParamCode.VENUES_STATUS_01.getCode());
            rmVenuesInfoMapper.add(venuesEntity);
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        RespPageBean bean=new RespPageBean(code);
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
}
