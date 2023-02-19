package com.religion.zhiyun.news.controller;

import com.religion.zhiyun.news.entity.NewsEntity;
import com.religion.zhiyun.news.service.NewsInfoService;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/news")
public class NewsInfoController {

    @Autowired
    private NewsInfoService newsInfoService;

    @PostMapping("/add")
    @ResponseBody
    public RespPageBean add(@RequestBody NewsEntity newsEntity) {
        return  newsInfoService.add(newsEntity);
    }

    @PostMapping("/update")
    public RespPageBean update(@RequestBody NewsEntity newsEntity) {
        return newsInfoService.update(newsEntity);
    }

    @PostMapping("/delete/{newsId}")
    public void delete(@PathVariable int newsId) {
        newsInfoService.delete(newsId);
    }

    @GetMapping("/find")
    public RespPageBean getEmpByPage(@RequestParam Map<String, Object> map){
        return newsInfoService.getNewsByPage(map);
    }


}
