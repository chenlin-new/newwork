package cn.itcast.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author LiChenLin
 * @Date 2022/4/29 20:41
 */
@Component
public class AuthorizeFilter  implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        MultiValueMap<String, String> params = request.getQueryParams();
        String authorization = params.getFirst("auth");

        if ("admin".equals(authorization)) {
            return chain.filter(exchange);
        }
            response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }
}
