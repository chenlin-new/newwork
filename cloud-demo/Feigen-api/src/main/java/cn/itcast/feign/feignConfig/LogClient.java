package cn.itcast.feign.feignConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author LiChenLin
 * @Date 2022/5/2 20:15
 */
@FeignClient("logservice")
public interface LogClient {
    //调用日志微服务,保存到本地
    @PostMapping("/log")
    public void log(@RequestBody String log);
}
