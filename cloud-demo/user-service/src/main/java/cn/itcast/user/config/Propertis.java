package cn.itcast.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author LiChenLin
 * @Date 2022/4/29 10:08
 */
@Component
@Data
@ConfigurationProperties(prefix = "pattern")
public class Propertis {

    private String dateformart;
}
