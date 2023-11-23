package tudor.work.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tudor.work.jwt.JwtAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

import static tudor.work.models.Permissions.*;

import static tudor.work.models.Roles.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
             "/api/v1/auth/**",
            "/oauth2/authorization/google", "/login/oauth2/code/google",
            "/resetPassword?token=*",
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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                                req.antMatchers(WHITE_LIST_URL)
                                        .permitAll()
////                                .antMatchers(GET,"/api/v1/admin/**").hasAnyAuthority(ADMIN_READ.name())
//////                                .antMatchers(POST,"/api/v1/admin/**").hasAnyAuthority(ADMIN_WRITE.name())
//////                                .antMatchers(PUT,"/api/v1/admin/**").hasAnyAuthority(ADMIN_UPDATE.name())
//////                                .antMatchers(DELETE,"/api/v1/admin/**").hasAnyAuthority(ADMIN_DELETE.name())
//                                .antMatchers("/api/v1/admin/**").hasRole(ADMIN.name())
////                                .antMatchers(GET,"/api/v1/coach/**").hasAnyAuthority(ADMIN_READ.name(),COACH_READ.name())
////                                .antMatchers(POST,"/api/v1/coach/**").hasAnyAuthority(ADMIN_WRITE.name(),COACH_WRITE.name())
////                                .antMatchers(PUT,"/api/v1/coach/**").hasAnyAuthority(ADMIN_UPDATE.name(),COACH_UPDATE.name())
////                                .antMatchers(DELETE,"/api/v1/coach/**").hasAnyAuthority(ADMIN_DELETE.name(),COACH_DELETE.name())
//                                .antMatchers("/api/v1/coach/**").hasAnyRole(ADMIN.name(),COACH.name())
////                                .antMatchers(GET,"/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(),COACH_READ.name(),PAYING_USER_READ.name())
////                                .antMatchers(POST,"/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(),COACH_READ.name(),PAYING_USER_WRITE.name())
////                                .antMatchers(PUT,"/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(),COACH_READ.name(),PAYING_USER_UPDATE.name())
////                                .antMatchers(DELETE,"/api/v1/payingUser/**").hasAnyAuthority(ADMIN_READ.name(),COACH_READ.name(),PAYING_USER_DELETE.name())
//                                .antMatchers("/api/v1/payingUser/**").hasAnyRole(ADMIN.name(),COACH.name(), PAYING_USER.name())
                                        .anyRequest()
                                        .authenticated()


                )
                .oauth2Login().and()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)

                //.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                ;

        return http.build();
    }

}
