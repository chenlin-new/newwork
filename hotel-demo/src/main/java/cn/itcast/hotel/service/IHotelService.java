package cn.itcast.hotel.service;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IHotelService extends IService<Hotel> {

    //根据条件搜索
    PageResult list(RequestParams params);

    //聚合查询
    Map<String, List<String>> filters(RequestParams params);

    //补全查询
    List<String> getSuggestions(String prefix);

    //新增或者修改
    void insertOrUpdate(Long id);

    //删除
    void deleteById(Long id);

    void insertById(Long id);
}
