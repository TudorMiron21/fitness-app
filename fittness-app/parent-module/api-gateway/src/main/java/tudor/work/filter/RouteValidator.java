package tudor.work.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/googleAuth",
            "/api/v1/auth/googleAuth",
            "/api/v1/auth/validateToken/**",
            "/api/selfCoach/paypal/getPayPalSubscriptionButton",
            "/api/selfCoach/user/webhookResponse",
            "/login/oauth2/code/google",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}