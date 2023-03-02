package com.religion.zhiyun.staff.service.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.response.AppResponse;
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
    public List<StaffEntity> all() {
        return staffInfoMapper.all();
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
    public void delete(int staffId) {
        staffInfoMapper.delete(staffId);
    }

    @Override
    public RespPageBean getStaffByPage(Integer page, Integer size, String staffName, String staffPost, String religiousSect) throws IOException {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<StaffEntity> dataList=staffInfoMapper.getStaffByPage(page,size,staffName,staffPost,religiousSect);
        /*if(null!=dataList && dataList.size()>0){
            for(int j=0;j<dataList.size();j++){
                String picturesPath = dataList.get(j).getStaffPicture();
                String[] split = picturesPath.split(",");
                List<FileEntity> fileEntities = rmFileMapper.queryPath(split);
                if(null!=fileEntities && fileEntities.size()>0) {
                    String[] path=new String[fileEntities.size()];
                    for (int i = 0; i < fileEntities.size(); i++) {
                        FileEntity fileEntity = fileEntities.get(i);
                        String image = FileToBase.getImage(fileEntity.getFilePath(), fileEntity.getFileName());
                        path[i]=image;
                    }
                    dataList.get(j).setStaffPicture(Arrays.toString(path));
                }
            }
        }*/

        Object[] objects = dataList.toArray();
        /*VenuesEntity[] date = new VenuesEntity[dataList.size()];
        VenuesEntity[] datas = dataList.toArray(date);*/
        Long total=staffInfoMapper.getTotal();
        RespPageBean respPageBean = new RespPageBean();
        respPageBean.setDatas(objects);
        respPageBean.setTotal(total);
        return respPageBean;
    }

    @Override
    public Long getMaxStaffCd() {
        return staffInfoMapper.getMaxStaffCd();
    }

    @Override
    public AppDetailRes getStaffByName(String StaffName) {
        long code = ResultCode.FAILED.getCode();
        String message="获取教职人员详情失败！";
        Map<String,Object> staffMap=new HashMap<>();
        try {
            staffMap = staffInfoMapper.getStaffByName(StaffName);
            if (null!=staffMap&&staffMap.size()>0){
                //获取图片地址
                String picture= (String) staffMap.get("staffPicture");
                Object[] file ={};
                if(!picture.isEmpty()){
                    //查询地图地址
                    String[] split = picture.split(",");
                    List<Map<String,Object>> imgMap=rmFileMapper.getPath(split);
                    if(null!=imgMap && imgMap.size()>0){
                        file = imgMap.toArray();
                    }
                }
                staffMap.put("images",file);


                //关联场所
                List<Map<String, Object>> venuesList = staffInfoMapper.getVenuesByStaffName(StaffName);
                staffMap.put("venuesList",venuesList.toArray());

                //关联活动
                List<Map<String, Object>> filing = staffInfoMapper.getFiling(StaffName);
                staffMap.put("filing",filing.toArray());

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

        return new AppDetailRes(code,message,staffMap);
    }
}
