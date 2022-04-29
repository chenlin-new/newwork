package cn.itcast.order.service;


import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.mapper.pojo.Order;

import cn.itcast.feign.feignConfig.UserClient;
import cn.itcast.feign.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    private UserClient userservice;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);
        //2.获取用户id
        Long userId = order.getUserId();
        //3.获取远程连接进行调用
//        String url="http://user/user/"+userId;
//        User user = restTemplate.getForObject(url, User.class);
        User user = userservice.queryById(userId);
        order.setUser(user);
        // 4.返回
        return order;
    }
}
