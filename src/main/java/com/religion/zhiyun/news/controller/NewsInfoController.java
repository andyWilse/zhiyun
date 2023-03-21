package com.religion.zhiyun.news.controller;

import com.religion.zhiyun.news.entity.NewsEntity;
import com.religion.zhiyun.news.service.NewsInfoService;
import com.religion.zhiyun.utils.response.PageResponse;
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
    public RespPageBean add(@RequestBody NewsEntity newsEntity,@RequestHeader("token")String token) {
        return  newsInfoService.add(newsEntity,token);
    }

    @PostMapping("/update")
    public RespPageBean update(@RequestBody NewsEntity newsEntity,@RequestHeader("token")String token) {
        return newsInfoService.update(newsEntity,token);
    }

    @PostMapping("/delete/{newsId}")
    public void delete(@PathVariable int newsId) {
        newsInfoService.delete(newsId);
    }

    @GetMapping("/find")
    public PageResponse getNewsByPage(@RequestParam Map<String, Object> map, @RequestHeader("token")String token){
        return newsInfoService.getNewsByPage(map,token);
    }


}
