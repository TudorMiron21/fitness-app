package tudor.work.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
public class JwtVerifierFilter extends OncePerRequestFilter {


    final private RestTemplate template;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        //TODO:modifica aici
        String username = template.getForObject("http://localhost:8082/api/v1/auth/getUsername?token=" + authHeader, String.class);

        String[] authorities = template.getForObject("http://localhost:8082/api/v1/auth/getUserAuthorities?token=" + authHeader, String[].class);

//        String username = template.getForObject("http://fitness-auth-service.default.svc.cluster.local:8082/api/v1/auth/getUsername?token=" + authHeader, String.class);
//
//        String[] authorities = template.getForObject("http://fitness-auth-service.default.svc.cluster.local:8082/api/v1/auth/getUserAuthorities?token=" + authHeader, String[].class);

        List<GrantedAuthority> simpleGrantedAuthorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);


    }
}
