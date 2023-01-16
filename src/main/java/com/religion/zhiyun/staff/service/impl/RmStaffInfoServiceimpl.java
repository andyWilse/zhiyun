package com.religion.zhiyun.staff.service.impl;

import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RmStaffInfoServiceimpl implements RmStaffInfoService {
    @Autowired
    private RmStaffInfoMapper staffInfoMapper;
    @Autowired
    private RmFileMapper rmFileMapper;
    

    @Override
    public void add(StaffEntity staffEntity) {
        staffInfoMapper.add(staffEntity);
    }

    @Override
    public List<StaffEntity> all() {
        return staffInfoMapper.all();
    }

    @Override
    public void update(StaffEntity staffEntity) {
        staffInfoMapper.update(staffEntity);
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
