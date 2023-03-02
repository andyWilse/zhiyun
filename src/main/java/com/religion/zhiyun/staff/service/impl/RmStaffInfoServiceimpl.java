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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public AppResponse getStaffById(String StaffId) {
        long code = ResultCode.FAILED.getCode();
        String message="获取教职人员详情失败！";
        StaffEntity staffEntity = new StaffEntity();
        List<StaffEntity> list=new ArrayList<>();
        try {
            staffEntity = staffInfoMapper.getStaffById(StaffId);
            if (staffEntity!=null){
                list.add(staffEntity);
            }else {
                code=ResultCode.FAILED.getCode();
                message="获取教职人员详情失败！";
                throw new RuntimeException(message);
            }
            code=ResultCode.SUCCESS.getCode();
            message="获取教职人员详情成功！";
        }catch (RuntimeException r ){
            r.printStackTrace();
        } catch(Exception e) {
            code= ResultCode.FAILED.getCode();
            message="获取教职人员详情失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,list.toArray());
    }
}
