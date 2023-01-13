package com.religion.zhiyun.staff.controller;

import com.religion.zhiyun.login.api.CommonResult;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.utils.enums.ParamCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
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
    public CommonResult add(@RequestBody StaffEntity staffEntity) {

        Timestamp timestamp = new Timestamp(new Date().getTime());
        staffEntity.setCreateTime(timestamp);
        staffEntity.setLastModifyTime(timestamp);
        staffEntity.setCreator("first");
        staffEntity.setLastModifier("last");
        staffEntity.setStaffStatus(ParamCode.STAFF_STATUS_01.getCode());
        Long maxStaffCd = rmStaffInfoService.getMaxStaffCd();
        maxStaffCd++;
        staffEntity.setStaffCd(String.valueOf(maxStaffCd));
        rmStaffInfoService.add(staffEntity);
        return CommonResult.success("添加成功！");
    }

    @RequestMapping("/all")
    public String allStaff(){
        List<StaffEntity> list = rmStaffInfoService.all();
        return  JsonUtils.objectTOJSONString(list);
    }

    @PostMapping("/update")
    public void update(@RequestBody String staffJson) {
        StaffEntity staffEntity=JsonUtils.jsonTOBean(staffJson,StaffEntity.class);
        rmStaffInfoService.update(staffEntity);
    }

    @PostMapping("/delete")
    public void delete() {
        rmStaffInfoService.delete("staffId");
    }

    @GetMapping("/find")
    public RespPageBean getEmpByPage(@RequestParam Map<String, Object> map){

        String staffName = (String)map.get("staffName");
        String staffPost = (String)map.get("staffPost");
        String religiousSect = (String)map.get("religiousSect");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return rmStaffInfoService.getStaffByPage(page,size,staffName,staffPost,religiousSect);
    }

}
