package cn.itcast.order.service;


import cn.itcast.feign.feignConfig.LogClient;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.mapper.pojo.Order;

import cn.itcast.feign.feignConfig.UserClient;
import cn.itcast.feign.pojo.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    private LogClient logClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        String log="public Order queryOrderById(Long orderId)"+"方法再"+ LocalDate.now() +"被调用了,id为:"+userId;
        //***1.利用feign的远程调用 保存日志到本地 -->同步 不采用
       // logClient.log(log);
        //***2. 利用MQ rabbit 的direct 方式 异步 调用
        rabbitTemplate.convertAndSend("logexchange","log",log);
        // 4.返回
        return order;
    }
}
