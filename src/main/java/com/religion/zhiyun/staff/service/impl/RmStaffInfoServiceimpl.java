package com.religion.zhiyun.staff.service.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
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
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String nbr = request.getHeader("login-name");
            staffEntity.setCreator(nbr);
            staffEntity.setLastModifier(nbr);
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
            Timestamp timestamp = new Timestamp(new Date().getTime());
            staffEntity.setLastModifyTime(timestamp);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String nbr = request.getHeader("login-name");
            staffEntity.setLastModifier(nbr);
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
}
