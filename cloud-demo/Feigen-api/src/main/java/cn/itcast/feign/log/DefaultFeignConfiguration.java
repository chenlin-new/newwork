package cn.itcast.feign.log;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfiguration  {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL; // 日志级别为BASIC
    }
}