package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tudor.work.dto.*;
import tudor.work.models.User;
import tudor.work.repository.UserRepository;

import tudor.work.jwt.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthResponse register(RegisterRequest request) {

        var user = new User(
                null,
                request.getUsername(),
                request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder().accessToken(jwtToken).build();

    }


    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .build();
    }


    public Boolean isTokenValid(String token) {

        final String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("user not found"));
        System.out.println("user is:" + user.getUsername());
        if (jwtService.isTokenValid(token, user))
            return true;
        return false;
    }

    public String[] getUserAuthorities(String token)
    {
        return jwtService.extractUserAuthorities(token);
    }

    public String getUsername(String token)
    {
        return jwtService.extractUsername(token);
    }


}
