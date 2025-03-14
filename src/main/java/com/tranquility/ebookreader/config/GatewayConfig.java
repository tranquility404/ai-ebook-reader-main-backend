package com.tranquility.ebookreader.config;

import com.tranquility.ebookreader.utils.AuthUtils;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> getMainRoute() {
        return GatewayRouterFunctions.route("main")
                .before(BeforeFilterFunctions.stripPrefix())
                .route(RequestPredicates.path("/main/**"), HandlerFunctions.http("http://localhost:8081"))
//                .route(RequestPredicates.path("/main/**"), HandlerFunctions.http("lb://EBOOK-READER-MAIN-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> getMLRoute() {
        return GatewayRouterFunctions.route("ml")
                .before(BeforeFilterFunctions.stripPrefix())
                .before(serverRequest -> ServerRequest.from(serverRequest)
                            .headers(httpHeaders -> httpHeaders.add("X-User-Roles", AuthUtils.getUserRoles()))
                            .build())
                .route(RequestPredicates.path("/ml/**"), HandlerFunctions.http("http://localhost:8082"))
//                .route(RequestPredicates.path("/ml/**"), HandlerFunctions.http("lb://EBOOK-READER-ML-SERVICE"))
                .build();
    }
}
