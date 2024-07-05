package tudor.work.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import tudor.work.dto.AuthResponse;
import tudor.work.dto.LoginRequest;
import tudor.work.dto.RegisterRequest;
import tudor.work.exception.EmailNotFoundException;
import tudor.work.exception.RegisterException;
import tudor.work.jwt.JwtService;
import tudor.work.models.Roles;
import tudor.work.service.AuthService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;


    @Test
    public void AuthController_regiser_ResponseEntity() throws Exception {

        String firstName = "TestName";
        String lastName = "TestName";
        String email = "test.email@test.com";
        String password = "testPassw";
        Roles role = Roles.USER;
        RegisterRequest request = RegisterRequest
                .builder()
                .firstname(firstName)
                .lastname(lastName)
                .email(email)
                .password(password)
                .role(role)
                .build();

        String token = "access token";

        AuthResponse authResponse = AuthResponse.builder().accessToken(token).build();
        when(authService.register(request)).thenReturn(authResponse);


        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(authResponse)));
    }

    @Test
    public void AuthController_regiser_ResponseEntity_BADREQUEST() throws Exception {

        String firstName = "TestName";
        String lastName = "TestName";
        String email = "test.email@test.com";
        String password = "testPassw";
        Roles role = Roles.USER;
        RegisterRequest request = RegisterRequest
                .builder()
                .firstname(firstName)
                .lastname(lastName)
                .email(email)
                .password(password)
                .role(role)
                .build();


        when(authService.register(request)).thenThrow(new RegisterException("user already registered"));


        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }


    @Test
    public void AuthController_login_ResponseEntitiy() throws Exception {
        String email = "test.email@test.com";
        String password = "testPasswd";

        LoginRequest loginRequest = LoginRequest
                .builder()
                .email(email)
                .password(password)
                .build();

        String token = "access token";

        AuthResponse authResponse = AuthResponse.builder().accessToken(token).build();
        when(authService.login(loginRequest)).thenReturn(authResponse);


        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(authResponse)));

    }

    @Test
    public void AuthController_login_ResponseEntitiy_FORBIDDEN() throws Exception {
        String email = "test.email@test.com";
        String password = "testPasswd";

        LoginRequest loginRequest = LoginRequest
                .builder()
                .email(email)
                .password(password)
                .build();

        when(authService.login(loginRequest)).thenThrow(new NotFoundException("user not found"));


        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());

    }

    @Test
    public void AuthController_validateToken_ResponseEntity() throws Exception {
        String token = "jwt token";

        when(authService.isTokenValid(token)).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/validateToken")
                .param("token", token)
        ).andExpect(status().isOk());

    }

    @Test
    public void AuthController_forgotPassword_ResponseEntity() throws Exception {
        String email = "test.email@test.com";
        String token = "random_token";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        when(authService.resetPassword(email)).thenReturn(token);

        String resetPasswordLink = "http://localhost:8080/api/v1/auth/resetPassword?token=" + token;


        authService.sendResetPasswdLink(email, resetPasswordLink);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        mockMvc.perform(post("/api/v1/auth/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk())
                .andExpect(content().string(resetPasswordLink));
    }

    @Test
    public void AuthController_forgotPassword_ResponseEntity_BADREQUEST() throws Exception {
        String email = "test.email@test.com";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        when(authService.resetPassword(email)).thenThrow(new EmailNotFoundException("user with email " +email+ " was not found"));

        mockMvc.perform(post("/api/v1/auth/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


    }
}
