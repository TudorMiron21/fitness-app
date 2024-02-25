package tudor.work.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import tudor.work.filter.JwtVerifierFilter;

import static org.springframework.http.HttpMethod.*;

import static tudor.work.model.Permissions.*;

import static tudor.work.model.Roles.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {"/api/selfCoach/user/**",
            "/api/selfCoach/paypal/getPayPalSubscriptionButton",
            "/api-docs",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};

//    private final AuthenticationProvider authenticationProvider;

    private final RestTemplate template;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtVerifierFilter(template), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/selfCoach/admin/**")
                .hasRole(ADMIN.name())
                .antMatchers("/api/selfCoach/coach/**")
                .hasAnyRole(ADMIN.name(), COACH.name())
                .antMatchers("/api/selfCoach/payingUser/**")
                .hasAnyRole(ADMIN.name(), COACH.name(), PAYING_USER.name())
                .antMatchers(WHITE_LIST_URL).permitAll()
                .anyRequest().authenticated();

        return http.build();
    }


}
