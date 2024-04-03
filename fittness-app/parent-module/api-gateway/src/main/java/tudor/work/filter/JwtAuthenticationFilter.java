package tudor.work.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private RestTemplate template;


    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
//                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    throw new RuntimeException("missing authorization header");
//                }

//                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                String authHeader = extractToken(exchange);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    //REST call to AUTH service
                    //TODO:modifica aici
                    String response = template.getForObject("http://localhost:8082/api/v1/auth/validateToken?token=" + authHeader, String.class);
                    //String response = template.getForObject("http://authorization-service:8082/api/v1/auth/validateToken?token=" + authHeader, String.class);

                } catch (Exception e) {
                    System.out.println("Invalid access...!");
                    // Set a 403 Forbidden status
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        });
    }

    private String extractToken(ServerWebExchange exchange) {
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION) && !("websocket".equalsIgnoreCase(exchange.getRequest().getHeaders().getUpgrade()))) {
            throw new RuntimeException("missing authorization header");
        } else if (exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        } else if ("websocket".equalsIgnoreCase(exchange.getRequest().getHeaders().getUpgrade())) {
            return exchange.getRequest().getQueryParams().getFirst("token");
        }
        return null;
    }

    public static class Config {

    }
}