package com.religion.zhiyun.staff.controller;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/staff")
@CrossOrigin
public class RmStaffInfoController {

    @Autowired
    private RmStaffInfoService rmStaffInfoService;

    @PostMapping("/add")
    @ResponseBody
    public RespPageBean add(@RequestBody StaffEntity staffEntity) {
        return  rmStaffInfoService.add(staffEntity);
    }

    @RequestMapping("/all")
    public String allStaff(){
        List<StaffEntity> list = rmStaffInfoService.all();
        return  JsonUtils.objectTOJSONString(list);
    }

    @PostMapping("/update")
    public RespPageBean update(@RequestBody StaffEntity staffEntity) {
       return rmStaffInfoService.update(staffEntity);
    }

    @PostMapping("/delete/{staffId}")
    public void delete(@PathVariable int staffId) {
        rmStaffInfoService.delete(staffId);
    }

    @GetMapping("/find")
    public RespPageBean getEmpByPage(@RequestParam Map<String, Object> map) throws IOException {

        String staffName = (String)map.get("staffName");
        String staffPost = (String)map.get("staffPost");
        String religiousSect = (String)map.get("religiousSect");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmStaffInfoService.getStaffByPage(page,size,staffName,staffPost,religiousSect);
    }

    /*
    根据教职人员id查询教职人员信息
     */
    @RequestMapping("/getStaffByName")
    public AppDetailRes getStaffByName(@RequestParam String StaffName){
        AppDetailRes staffList = rmStaffInfoService.getStaffByName(StaffName);
        return staffList;
    }

}
