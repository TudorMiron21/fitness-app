package tudor.work.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.AuthResponse;
import tudor.work.dto.LoginRequest;
import tudor.work.dto.RegisterRequest;
import tudor.work.jwt.JwtService;
import tudor.work.service.AuthService;

@RestController
@RequestMapping(path = "/api/v1/auth")
@Data
public class AuthController {


    private final AuthService userService;

    @Autowired
    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
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
}
