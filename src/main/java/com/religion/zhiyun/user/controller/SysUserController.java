package com.religion.zhiyun.user.controller;

import com.religion.zhiyun.sys.login.api.CommonResult;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/find")
    public RespPageBean getUsersByPage(@RequestParam Map<String, Object> map) throws IOException {

        String identity = (String)map.get("identity");
        String loginNm = (String)map.get("loginNm");
        String pages = (String) map.get("page");
        String sizes = (String)map.get("size");
        Integer page = Integer.valueOf(pages);
        Integer size = Integer.valueOf(sizes);

        return sysUserService.getUsersByPage(page,size,identity,loginNm);
    }

    @PostMapping("/add")
    @ResponseBody
    public CommonResult add(@RequestBody SysUserEntity sysUserEntity) {
        sysUserService.add(sysUserEntity);
        return CommonResult.success("添加成功！");
    }

    @PostMapping("/update")
    public void update(@RequestBody SysUserEntity sysUserEntity) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        sysUserEntity.setLastModifyTime(timestamp);
        sysUserService.update(sysUserEntity);
    }

    @PostMapping("/delete/{userId}")
    public void delete(@PathVariable int userId) {
        sysUserService.delete(userId);
    }

    @PostMapping("/update/password")
    public RespPageBean updatePassword(@RequestBody Map<String,String> map) {
        return sysUserService.updatePassword(map);
    }

}
