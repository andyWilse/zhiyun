package com.religion.zhiyun.staff.service.impl;

import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.TokenUtils;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import com.religion.zhiyun.venues.entity.ParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class RmStaffInfoServiceimpl implements RmStaffInfoService {

    @Autowired
    private RmStaffInfoMapper staffInfoMapper;
    @Autowired
    private RmFileMapper rmFileMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;
    @Autowired
    private OperateRecordService operateRecordService;

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
    public PageResponse delete(Map<String, Object> map,String token) {
        code= ResultCode.FAILED.getCode();
        message="教职人员删除失败！";
        try {
            String staffVenues = (String)map.get("staffVenues");
            String staffId = (String)map.get("staffId");

            Map<String, Object> staffMap = staffInfoMapper.getStaffVenues(staffVenues);
            //String venuesStaff = staffInfoMapper.getVenuesStaff(staffVenues);
            String venuesStaff = (String) staffMap.get("venuesStaff");
            String venuesName = (String) staffMap.get("venuesName");
            String venuesAddres = (String) staffMap.get("venuesAddres");
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

            //增加日志信息
            StaffEntity staffById = staffInfoMapper.getStaffById(staffId);
            Map<String,Object> vuMap=new HashMap<>();
            vuMap.put("operator",this.getLogin(token));
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef",staffId);
            vuMap.put("operateType", OperaEnums.staff_delete.getCode());
            vuMap.put("operateContent", venuesName);
            String operateDetail="场所："+venuesName+"（"+venuesAddres+"）；已删除教职人员："+staffById.getStaffName();
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);

            code= ResultCode.SUCCESS.getCode();
            message="教职人员已删除！";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PageResponse(code,message);
    }

    @Override
    public PageResponse getStaffByPage(Integer page, Integer size, String staffName, String venuesId){
        code= ResultCode.FAILED.getCode();
        message="教职人员信息获取！";
        List<Map<String, Object>> staffByVenues =new ArrayList<>();
        Long total=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            ParamsVo vo=new ParamsVo();
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchThree(staffName);
            vo.setVenues(venuesId);
            //根据场所获取教职人员信息
            String venuesStaff = staffInfoMapper.getVenuesStaff(venuesId);
            if(null!=venuesStaff && !venuesStaff.isEmpty()){
                String[] split = venuesStaff.split(",");
                vo.setSearchArr(split);
                staffByVenues = staffInfoMapper.getStaffByVenues(vo);
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
    public PageResponse getAllStaffByPage(Map<String, Object> map, String token) {
        code= ResultCode.FAILED.getCode();
        message="教职人员信息获取！";
        List<Map<String, Object>> staffByVenues =new ArrayList<>();
        Long total=0l;
        try {
            //String staffName = (String)map.get("staffName");
            String venuesId = (String)map.get("staffVenues");
            String name = (String)map.get("one");
            String phone = (String)map.get("three");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }

            ParamsVo vo=new ParamsVo();
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchOne(name);
            vo.setSearchTwo(phone);
            //vo.setSearchThree(staffName);
            vo.setVenues(venuesId);

            if(!GeneTool.isEmpty(venuesId)){
                //根据场所获取教职人员信息
                String venuesStaff = staffInfoMapper.getVenuesStaff(venuesId);
                if(null!=venuesStaff && !venuesStaff.isEmpty()){
                    String[] split = venuesStaff.split(",");
                    vo.setSearchArr(split);
                    staffByVenues = staffInfoMapper.getStaffByVenues(vo);
                    total=Integer.toUnsignedLong(split.length);
                }
            }else{
                staffByVenues = staffInfoMapper.getStaffByVenues(vo);
                total=staffInfoMapper.getStaffTotal(vo);
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
                String imaPath = (String) managerMap.get("managerPhotoPath");
                if(imaPath!=null && !imaPath.isEmpty()){
                    //查询地图地址
                    String[] split = imaPath.split(",");

                    pathsImg = rmFileMapper.getFileUrl(split);
                }
                managerMap.put("managerPhotoPath",pathsImg.toArray());

                //2.关联场所
                List<Map<String, Object>> venuesList = staffInfoMapper.getVenuesByManagerId(ManagerId);
                if(null!=venuesList && venuesList.size()>0){
                    for (int j=0;j<venuesList.size();j++){
                        Map<String, Object> map = venuesList.get(j);
                        String picturesPath = (String)map .get("picturesPath");
                        List<Map<String, Object>> fileUrl =new ArrayList<>();
                        if(picturesPath!=null && !picturesPath.isEmpty()){
                            //查询地图地址
                            String[] split = picturesPath.split(",");
                            fileUrl = rmFileMapper.getFileUrl(split);
                        }
                        map.put("picturesPath",fileUrl.toArray());
                    }
                }
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
    /**
     * 获取
     * @return
     */
    public ParamsVo getAuth(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
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
}
