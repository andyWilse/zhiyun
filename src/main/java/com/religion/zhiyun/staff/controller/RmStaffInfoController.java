package com.religion.zhiyun.staff.controller;

import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.DetailVo.AppDetailRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping("/select")
    public PageResponse findSelect(@RequestParam Map<String, Object> map){
        return  rmStaffInfoService.findStaffSelect(map);
    }

    @PostMapping("/update")
    public RespPageBean update(@RequestBody StaffEntity staffEntity) {
       return rmStaffInfoService.update(staffEntity);
    }

    @PostMapping("/delete")
    public PageResponse delete(@RequestBody Map<String, Object> map,@RequestHeader("token")String token) {
        return rmStaffInfoService.delete( map,token);
    }

    @GetMapping("/find")
    public PageResponse getStaffByPage(@RequestParam Map<String, Object> map){

        String staffName = (String)map.get("staffName");
        String staffVenues = (String)map.get("staffVenues");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmStaffInfoService.getStaffByPage(page,size,staffName,staffVenues);
    }

    @GetMapping("/getStaff")
    public PageResponse getAllStaffByPage(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        return rmStaffInfoService.getAllStaffByPage(map,token);
    }

    /*
    根据教职人员id查询教职人员信息
     */
    @RequestMapping("/getManagerById")
    public AppDetailRes getManagerById(@RequestParam String ManagerId){
        AppDetailRes staffList = rmStaffInfoService.getManagerById(ManagerId);
        return staffList;
    }

}
