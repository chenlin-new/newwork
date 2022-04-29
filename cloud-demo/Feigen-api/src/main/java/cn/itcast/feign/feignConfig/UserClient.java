package cn.itcast.feign.feignConfig;


import cn.itcast.feign.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author LiChenLin
 * @Date 2022/4/29 11:34
 */
@FeignClient(value = "user")
public interface UserClient {

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable("id") Long id);
}
