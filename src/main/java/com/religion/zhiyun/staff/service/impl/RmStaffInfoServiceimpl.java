package com.religion.zhiyun.staff.service.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.TokenUtils;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class RmStaffInfoServiceimpl implements RmStaffInfoService {

    @Autowired
    private RmStaffInfoMapper staffInfoMapper;
    @Autowired
    private RmFileMapper rmFileMapper;

    long code= ResultCode.FAILED.getCode();
    String message="教职人员信息处理！";
    @Override
    public RespPageBean add(StaffEntity staffEntity) {
        long code= ResultCode.SUCCESS.getCode();
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new Date().getTime());
            staffEntity.setCreateTime(timestamp);
            staffEntity.setLastModifyTime(timestamp);
            SysUserEntity entity = TokenUtils.getToken();
            if(null==entity){
                throw new RuntimeException("登录人信息丢失，请登陆后重试！");
            }
            staffEntity.setCreator(entity.getLoginNm());
            staffEntity.setLastModifier(entity.getLoginNm());
            staffEntity.setStaffStatus(ParamCode.STAFF_STATUS_01.getCode());
            Long maxStaffCd = staffInfoMapper.getMaxStaffCd();
            if(null==maxStaffCd){
                maxStaffCd=1001l;
            }
            maxStaffCd++;
            staffEntity.setStaffCd(String.valueOf(maxStaffCd));
            staffInfoMapper.add(staffEntity);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);
    }

    @Override
    public PageResponse findStaffSelect(Map<String, Object> map) {
        long code= ResultCode.FAILED.getCode();
        String message="教职人员获取";
        List<Map<String, Object>> allStaff=new ArrayList<>();
        long total=0l;
        try {
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            String search = (String)map.get("search");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            allStaff = staffInfoMapper.getAllStaff(page,size,search);
            total=staffInfoMapper.getAllStaffTotal(search);
            code= ResultCode.SUCCESS.getCode();
            message="教职人员获取成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResponse(code,message,total,allStaff.toArray());
    }

    @Override
    public RespPageBean update(StaffEntity staffEntity) {
        long code= ResultCode.SUCCESS.getCode();
        try {
            SysUserEntity entity = TokenUtils.getToken();
            if(null==entity){
                throw new RuntimeException("登录人信息丢失，请登陆后重试！");
            }
            staffEntity.setLastModifier(entity.getLoginNm());
            Timestamp timestamp = new Timestamp(new Date().getTime());
            staffEntity.setLastModifyTime(timestamp);
            staffInfoMapper.update(staffEntity);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }
        return new RespPageBean(code);
    }

    @Override
    public PageResponse delete(String staffVenues,String staffId) {
        code= ResultCode.FAILED.getCode();
        message="教职人员删除！";
        try {
            String venuesStaff = staffInfoMapper.getVenuesStaff(staffVenues);
            String venuesStaffs="";
            if(null!=venuesStaff && !venuesStaff.isEmpty()){
                String[] split = venuesStaff.split(",");
                for(int i=0;i<split.length;i++){
                    if(!staffId.equals(split[i])){
                        venuesStaffs=venuesStaffs+split[i]+",";
                    }
                }
                staffInfoMapper.updateVenuesStaff(venuesStaffs,staffVenues);
            }
            code= ResultCode.SUCCESS.getCode();
            message="教职人员删除成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PageResponse(code,message);
    }

    @Override
    public PageResponse getStaffByPage(Integer page, Integer size, String staffName, String staffVenues){
        code= ResultCode.FAILED.getCode();
        message="教职人员信息获取！";
        List<Map<String, Object>> staffByVenues =new ArrayList<>();
        Long total=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //根据场所获取教职人员信息
            String venuesStaff = staffInfoMapper.getVenuesStaff(staffVenues);
            if(null!=venuesStaff && !venuesStaff.isEmpty()){
                String[] split = venuesStaff.split(",");
                staffByVenues = staffInfoMapper.getStaffByVenues(split,page,size);
                total=Integer.toUnsignedLong(split.length);
            }

            code= ResultCode.SUCCESS.getCode();
            message="教职人员信息获取成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResponse(code,message,total,staffByVenues.toArray());
    }

    @Override
    public Long getMaxStaffCd() {
        return staffInfoMapper.getMaxStaffCd();
    }

    @Override
    public AppDetailRes getManagerById(String ManagerId) {
        long code = ResultCode.FAILED.getCode();
        String message="获取负责人详情失败！";
        Map<String,Object> managerMap=new HashMap<>();
        List<Map<String, Object>> pathsImg =new ArrayList<>();
        try {
            managerMap = staffInfoMapper.getManagerById(ManagerId);
            //1.获取图片地址
            if (null!=managerMap&&managerMap.size()>0){
                String imaPath = (String) managerMap.get("imapath");
                if(imaPath!=null && !imaPath.isEmpty()){
                    //查询地图地址
                    String[] split = imaPath.split(",");

                    pathsImg = rmFileMapper.getPath(split);
                }
                managerMap.put("images",pathsImg.toArray());

                //2.关联场所
                List<Map<String, Object>> venuesList = staffInfoMapper.getVenuesByManagerId(ManagerId);
                managerMap.put("venuesList",venuesList.toArray());

                //关联活动
                List<Map<String, Object>> filing = staffInfoMapper.getFilingByManagerId(ManagerId);
                managerMap.put("filing",filing.toArray());

            }else{
                message="获取人员信息详情失败！！";
                throw  new RuntimeException(message);
            }
            code= ResultCode.SUCCESS.getCode();
            message="获取人员信息详情成功！";
        }catch (RuntimeException r ){
            r.printStackTrace();
        } catch(Exception e) {
            message="获取人员信息详情失败！！";
            e.printStackTrace();
        }

        return new AppDetailRes(code,message,managerMap);
    }
}
