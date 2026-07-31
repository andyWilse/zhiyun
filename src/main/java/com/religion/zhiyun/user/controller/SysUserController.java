package com.religion.zhiyun.user.controller;

import com.religion.zhiyun.login.http.inter.DecryptRequest;
import com.religion.zhiyun.login.http.inter.EncryptResponse;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.SysUserService;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@DecryptRequest(true)
@EncryptResponse(true)
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/find")
    public PageResponse getUsersByPage(@RequestParam Map<String, Object> map,@RequestHeader("token")String token){
        return sysUserService.getUsersByPage(map,token);
    }

    @PostMapping("/add")
    @ResponseBody
    public RespPageBean add(@RequestBody SysUserEntity sysUserEntity,@RequestHeader("token")String token) {
        return sysUserService.addUser(sysUserEntity,token);
    }

    @PostMapping("/update")
    public PageResponse update(@RequestBody SysUserEntity sysUserEntity,@RequestHeader("token")String token) {
        return sysUserService.update(sysUserEntity,token);
    }

    @PostMapping("/delete/{userId}")
    public void delete(@PathVariable int userId,@RequestHeader("token")String token) {
        sysUserService.delete(userId,token);
    }

    @PostMapping("/update/password")
    public PageResponse updatePassword(@RequestBody Map<String,String> map,@RequestHeader("token")String token) {
        return sysUserService.updatePassword(map,token);
    }

    @GetMapping("/getUser")
    public PageResponse getUserInfo(@RequestHeader("token")String token){
        return sysUserService.getUserInfo(token);
    }

    @GetMapping("/getModify")
    public PageResponse getModifyUser(@RequestParam("userId")String userId){
        return sysUserService.getModifyUser(userId);
    }

    @PostMapping("/modify/password")
    public PageResponse modifyPassword(@RequestBody Map<String,Object> map,@RequestHeader("token")String token) {
        return sysUserService.modifyPassword(map,token);
    }

    //三人驻堂Excel数据导入
    @EncryptResponse(false)
    @PostMapping("/import")
       public PageResponse importData(@RequestParam("file") MultipartFile file){
        return sysUserService.excelImport(file);
    }
    @PostMapping("/import/add")
    public PageResponse excelImportAdd(@RequestBody List<SysUserEntity> sysUserList,@RequestHeader("token")String token) {
        return sysUserService.excelImportAdd(sysUserList,token);
    }

    //获取三人驻堂
    @GetMapping("/getSr")
    public PageResponse getSrUser(@RequestParam("venuesId")String venuesId){
        return sysUserService.getSrUser(venuesId);
    }
    //删除场所三人驻堂人员
    @PostMapping("/deleteSr")
    public PageResponse deleteSrUser(@RequestBody Map<String,Object> map,@RequestHeader("token")String token) {
        return sysUserService.deleteSrUser(map,token);
    }
    //删除三人驻堂人员关联场所
    @PostMapping("/deleteVe")
    public PageResponse deleteSrVenue(@RequestBody Map<String,Object> map,@RequestHeader("token")String token) {
        return sysUserService.deleteSrVenue(map,token);
    }
    @PostMapping("/addSr")
    public PageResponse addSr(@RequestBody Map<String,Object> map,@RequestHeader("token")String token) {
        return sysUserService.addSr(map,token);
    }
    @PostMapping("/addVenue")
    public PageResponse addVenue(@RequestBody Map<String,Object> map,@RequestHeader("token")String token) {
        return sysUserService.addVenue(map,token);
    }

}
