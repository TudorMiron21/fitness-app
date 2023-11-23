package tudor.work.service;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import tudor.work.dto.AuthResponse;
import tudor.work.dto.RegisterRequest;
import tudor.work.jwt.JwtService;
import tudor.work.models.User;
import tudor.work.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;


    @Test
    public void AuthService_register_returnJwtToken() {
        String testEmail = "test.email@test.com";
        String testPassword = "testPass";
//        verify(userRepository).findByEmail(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        RegisterRequest request = RegisterRequest
                .builder()
                .email(testEmail)
                .password(testPassword)
                .build();

        String hashedPass = "hashedPass";

        when(passwordEncoder.encode(testPassword)).thenReturn(hashedPass);

        User user = User.builder().email(request.getEmail()).password(hashedPass).build();

        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(anyString());

        AuthResponse response = authService.register(request);

        Assertions.assertThat(response).isNotNull();

    }

    @Disabled
    @Test
    public void AuthService_login_returnJwtToken() {

    }

}
