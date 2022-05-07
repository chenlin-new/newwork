package cn.itcast.hotel.controller;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    //请求方式：POST
    //请求路径：/hotel/list
    @PostMapping("/list")
    public PageResult list(@RequestBody RequestParams params) {
        return hotelService.list(params);
    }

    //聚合查询
    //请求方式：`POST`
    //请求路径：`/hotel/filters`
    //请求参数：`RequestParams`，与搜索文档的参数一致
    //返回值类型：`Map<String, List<String>>`
    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody RequestParams params) {
        return hotelService.filters(params);
    }

    //自动补全查询
    //GET http://localhost:8089/hotel/suggestion?key=r
    @GetMapping("suggestion")
    public List<String> getSuggestions(@RequestParam("key") String prefix) {
        return hotelService.getSuggestions(prefix);
    }
}
