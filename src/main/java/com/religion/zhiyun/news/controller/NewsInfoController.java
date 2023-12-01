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
    public PageResponse add(@RequestBody NewsEntity newsEntity,@RequestHeader("token")String token) {
        return  newsInfoService.add(newsEntity,token);
    }

    @PostMapping("/update")
    public PageResponse update(@RequestBody NewsEntity newsEntity,@RequestHeader("token")String token) {
        return newsInfoService.update(newsEntity,token);
    }

    @GetMapping("/getNewsById/{newsId}")
    public PageResponse getNewsById(@PathVariable int newsId) {
        return newsInfoService.getNewsById(newsId);
    }

    @PostMapping("/delete/{newsId}")
    public void delete(@PathVariable int newsId,@RequestHeader("token")String token) {
        newsInfoService.delete(newsId,token);
    }

    @PostMapping("/newsDown")
    public PageResponse newsDown(@RequestBody Map<String, Object> map,@RequestHeader("token")String token) {
        return newsInfoService.newsDown(map,token);
    }

    @GetMapping("/find")
    public PageResponse getNewsByPage(@RequestParam Map<String, Object> map, @RequestHeader("token")String token){
        return newsInfoService.getNewsByPage(map,token);
    }

    @GetMapping("/getPcNews")
    public PageResponse getNewsPage(@RequestParam Map<String, Object> map, @RequestHeader("token")String token){
        return newsInfoService.getNewsPage(map,token);
    }
    @GetMapping("/getNewsContent")
    public PageResponse getNewsContent(@RequestParam String newsId){
        return newsInfoService.getNewsContent(Integer.parseInt(newsId));
    }
}
