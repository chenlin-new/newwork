package cn.itcast.order;

import cn.itcast.feign.feignConfig.UserClient;
import cn.itcast.feign.log.DefaultFeignConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("cn.itcast.order.mapper")
@SpringBootApplication
@EnableFeignClients(defaultConfiguration = DefaultFeignConfiguration.class,clients = {UserClient.class})
//@EnableFeignClients(basePackages = {"cn.itcast.feign.feignConfig"})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }


//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate(){
//
//        return new RestTemplate();
//    }
}