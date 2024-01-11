package tudor.work.auth;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tudor.work.service.AuthService;



@Controller
@RequiredArgsConstructor
public class ResetPasswdController {

    private final AuthService authService;

    @RequestMapping(path = "/api/v1/auth/resetPassword")
    public String resetPassword(@RequestParam(name = "token")String token, Model page){
        try {
            authService.getUserByResetToken(token);
            page.addAttribute("token",token);
            return "reset_password_form";
        } catch (NotFoundException e) {
            return e.getMessage();
        }
    }
}
