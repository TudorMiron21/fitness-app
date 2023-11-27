package tudor.work.auth;

import javassist.NotFoundException;
import lombok.Data;
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
            String resetPasswordLink = "http://localhost:8080/resetPassword?token=" + randToken;
            userService.sendResetPasswdLink(email, resetPasswordLink);
            return ResponseEntity.status(HttpStatus.OK).body(resetPasswordLink);
        } catch (EmailNotFoundException enf) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email " + email + " not found");
        }
    }


//    @GetMapping("/resetPassword")
//    public String resetPasswordHandler(@RequestParam("token") String randToken) {
//        try {
//            ModelAndView mav = new ModelAndView("reset_password_form");
//            userService.getUserByResetToken(randToken);
//            mav.addObject("token", randToken);
//            return mav;
//        } catch (NotFoundException nfe) {
////`            model.addAttribute("title","Reset your password");
////            model.addAttribute("message", "Invalid token");`
//            return null;
//        }
//    }

//    @GetMapping("/resetPassword")
//
//    public String resetPassword(@RequestParam(name = "token")String token, Model page){
//        try {
//            userService.getUserByResetToken(token);
//            page.addAttribute("token",token);
//            return "reset_password_form.html";
//        } catch (NotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @GetMapping("/googleAuth")
    public ResponseEntity<?> googleAuth(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        if (oAuth2AuthenticationToken != null) {
            return ResponseEntity.status(HttpStatus.OK).body(oAuth2AuthenticationToken.getPrincipal().getAttributes());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 token is null");
        }
    }


}


