package tudor.work.service;


import javassist.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import tudor.work.dto.AuthResponse;
import tudor.work.dto.LoginRequest;
import tudor.work.dto.RegisterRequest;
import tudor.work.exception.EmailNotFoundException;
import tudor.work.jwt.JwtService;
import tudor.work.models.User;
import tudor.work.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


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

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailSenderService emailSenderService;

    @Test
    public void AuthService_register_returnJwtToken() {
        String testEmail = "test.email@test.com";
        String testPassword = "testPass";
        String token = "generated token";

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        RegisterRequest request = RegisterRequest
                .builder()
                .email(testEmail)
                .password(testPassword)
                .build();

        String hashedPass = "hashedPass";
        when(passwordEncoder.encode(testPassword)).thenReturn(hashedPass);

        User user = User.builder().email(request.getEmail()).password(hashedPass).build();
        when(userRepository.save(any(User.class))).thenReturn(user);

        when(jwtService.generateToken(any(User.class))).thenReturn(token);

        AuthResponse response = authService.register(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getAccessToken()).isEqualTo(token);
    }

    @Test
    public void AuthService_login_returnJwtToken() throws NotFoundException {

        LoginRequest testLoginRequest = LoginRequest
                .builder()
                .email("test.email@test.com")
                .password("hashed password")
                .build();

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        testLoginRequest.getEmail(),
                        testLoginRequest.getPassword()
                )
        )).thenReturn(mock(Authentication.class));


        when(userRepository.findByEmail(testLoginRequest.getEmail())).thenReturn(Optional.of(mock(User.class)));


        String token = "generated token";
        when(jwtService.generateToken(any(User.class))).thenReturn(token);


        AuthResponse response = authService.login(testLoginRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getAccessToken()).isEqualTo(token);

    }


    @Test
    public void AuthService_isTokenValid_returnBoolean() {

        String testEmail = "test.email@test.com";
        String token = "token";

        when(jwtService.extractUsername(token)).thenReturn(testEmail);
        User user = User.builder().email(testEmail).build();

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

        when(jwtService.isTokenValid(token)).thenReturn(true);

        Boolean result = authService.isTokenValid(token);

        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(result).isNotNull();

    }


    @Test
    public void AuthService_resetPassword_returnToken() throws EmailNotFoundException {
        String testEmail = "test.email@test.com";


        User user = User
                .builder()
                .email(testEmail)
                .build();


        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(user)
                );

        String responseToken = authService.resetPassword(testEmail);

        Assertions.assertThat(responseToken).isNotNull();
    }


    @Test
    public void AuthService_resetPassword_throwEmailNotFoundException() throws EmailNotFoundException {
        String testEmail = "test.email@test.com";

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.empty()
                );

        assertThrows(EmailNotFoundException.class, () -> {
            authService.resetPassword(testEmail);
        });
    }


    @Test
    public void AuthService_sendResetPasswdLink()
    {
        String email = "test.mail@test.com";
        String resetPasswordLink= "resetPasswordLink";


        authService.sendResetPasswdLink(email,resetPasswordLink);

        verify(emailSenderService).sendEmail(eq(email),any(),any());


    }
}
