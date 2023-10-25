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

//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(req ->
//                                req.antMatchers(WHITE_LIST_URL)
//                                        .permitAll()
//                                        .antMatchers("/api/selfCoach/admin/**").hasRole(ADMIN.name())
////                                .antMatchers(GET, "/api/v1/admin/**").hasAnyAuthority(ADMIN_READ.name())
////                                .antMatchers(POST, "/api/v1/admin/**").hasAnyAuthority(ADMIN_WRITE.name())
////                                .antMatchers(PUT, "/api/v1/admin/**").hasAnyAuthority(ADMIN_UPDATE.name())
////                                .antMatchers(DELETE, "/api/v1/admin/**").hasAnyAuthority(ADMIN_DELETE.name())
//                                        .antMatchers("/api/selfCoach/coach/**").hasAnyRole(ADMIN.name(), COACH.name())
////                                .antMatchers(GET, "/api/v1/coach/**").hasAnyAuthority(ADMIN_READ.name(), COACH_READ.name())
////                                .antMatchers(POST, "/api/v1/coach/**").hasAnyAuthority(ADMIN_WRITE.name(), COACH_WRITE.name())
////                                .antMatchers(PUT, "/api/v1/coach/**").hasAnyAuthority(ADMIN_UPDATE.name(), COACH_UPDATE.name())
////                                .antMatchers(DELETE, "/api/v1/coach/**").hasAnyAuthority(ADMIN_DELETE.name(), COACH_DELETE.name())
//                                        .antMatchers("/api/selfCoach/payingUser/**").hasAnyRole(ADMIN.name(), COACH.name(), PAYING_USER.name())
////                                .antMatchers(GET, "/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(), COACH_READ.name(), PAYING_USER_READ.name())
////                                .antMatchers(POST, "/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(), COACH_READ.name(), PAYING_USER_WRITE.name())
////                                .antMatchers(PUT, "/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(), COACH_READ.name(), PAYING_USER_UPDATE.name())
////                                .antMatchers(DELETE, "/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(), COACH_READ.name(), PAYING_USER_DELETE.name())
//                                        .anyRequest()
//                                        .authenticated()
//
//
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authenticationProvider(authenticationProvider)
////
//        ;
//        return http.build();
//        http.csrf().disable()
//                .authorizeRequests(authorizeRequests ->
//                        authorizeRequests
//                                .antMatchers("/api/selfCoach/admin/**")
//                                .hasRole(ADMIN.name())
//                                .antMatchers("/api/selfCoach/coach/**")
//                                .hasAnyRole(ADMIN.name(), COACH.name())
//                                .antMatchers("/api/selfCoach/payingUser/**")
//                                .hasAnyRole(ADMIN.name(), COACH.name(), PAYING_USER.name())
//                                .antMatchers(WHITE_LIST_URL)
//                                .anonymous()
//                                .anyRequest()
//                                .authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

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
