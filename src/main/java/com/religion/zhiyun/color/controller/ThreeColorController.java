package com.religion.zhiyun.color.controller;

import com.religion.zhiyun.color.entity.ThreeColorEntity;
import com.religion.zhiyun.color.service.ThreeColorService;
import com.religion.zhiyun.login.http.inter.DecryptRequest;
import com.religion.zhiyun.login.http.inter.EncryptResponse;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.venues.entity.ParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@DecryptRequest(true)
@EncryptResponse(true)
@Slf4j
@RestController
@RequestMapping("/color")
public class ThreeColorController {
    @Autowired
    private ThreeColorService threeColorService;

    @PostMapping("/add")
    public AppResponse threeColorAdd(@RequestBody ThreeColorEntity threeColorEntity) {
        return threeColorService.threeColorAdd(threeColorEntity);
    }

    @PostMapping("/update")
    public AppResponse threeColorUpdate(@RequestBody ThreeColorEntity threeColorEntity) {
        return threeColorService.threeColorUpdate(threeColorEntity);
    }

    @PostMapping("/delete")
    public AppResponse threeColorDelete(@RequestBody Map<String,Object> map) {
        return threeColorService.threeColorDelete(map);
    }

    @GetMapping("/query/{coId}")
    public AppResponse getThreeColor(@PathVariable int coId) {
        return threeColorService.getThreeColor(coId);
    }

    @GetMapping("/list")
    public AppResponse getThreeColorList(ParamsVo vo) {
        return threeColorService.getThreeColorList(vo);
    }

    @PostMapping("/upload")
    public AppResponse coExcelUpload(@RequestBody Map<String,Object> map ) {
        return threeColorService.threeColorUpload(map);
    }

    @PostMapping("/import")
    public AppResponse excelImportAdd(@RequestBody Map<String,Object> map) {
        return threeColorService.threeColorImport(map);
    }

    @PostMapping("/show")
    public AppResponse showThreeColor(@RequestBody Map<String,Object> map) {
        return threeColorService.threeColorShow(map);
    }


}
