package tudor.work.auth;

import javassist.NotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.AuthResponse;
import tudor.work.dto.LoginRequest;
import tudor.work.dto.RegisterRequest;
import tudor.work.exception.EmailNotFoundException;
import tudor.work.exception.RegisterException;
import tudor.work.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/auth")
@Data
public class AuthController {


    private final AuthService userService;


    @Autowired
    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @GetMapping("/testController")
    public ResponseEntity<?> testController() {
        return ResponseEntity.ok("test controller");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RegisterException re) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(re.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = userService.login(request);
            return ResponseEntity.status(HttpStatus.OK).body(authResponse);
        } catch (NotFoundException ae) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ae.getMessage());
        }
    }

    @PostMapping("/registerGoogle")
    public ResponseEntity<?> registerGoogle(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.registerGoogle(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RegisterException re) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(re.getMessage());
        }
    }

    @PostMapping("/loginGoogle")
    public ResponseEntity<?> loginGoogle(@RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = userService.loginGoogle(request);
            return ResponseEntity.status(HttpStatus.OK).body(authResponse);
        } catch (NotFoundException ae) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ae.getMessage());
        }
    }


    @GetMapping("/validateToken")
    public Boolean isTokenValid(@RequestParam("token") String token) {

        return userService.isTokenValid(token);
    }

    @GetMapping("/getUserAuthorities")
    public String[] getUserAuthorities(@RequestParam("token") String token) {
        return userService.getUserAuthorities(token);
    }

    @GetMapping("/getUsername")
    public String getUsername(@RequestParam("token") String token) {
        return userService.getUsername(token);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPasswordHandler(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        try {
            String randToken = userService.resetPassword(email);
            String resetPasswordLink = "http://localhost:8080/api/v1/auth/resetPassword?token=" + randToken;
            userService.sendResetPasswdLink(email, resetPasswordLink);
            return ResponseEntity.status(HttpStatus.OK).body(resetPasswordLink);
        } catch (EmailNotFoundException enf) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email " + email + " not found");
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> updatePassword(
            @RequestParam("token") String token,
            @RequestParam("password") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Passwords do not match");
        }

        try {
            userService.updatePassword(token, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/googleAuth")
    public Map<String, Object> googleAuth(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return oAuth2AuthenticationToken.getPrincipal().getAttributes();
    }


}


