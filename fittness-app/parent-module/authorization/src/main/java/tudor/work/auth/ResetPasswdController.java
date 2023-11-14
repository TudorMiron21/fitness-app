package tudor.work.auth;

import javassist.NotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tudor.work.service.AuthService;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/resetPassword")
@Data
public class ResetPasswdController {

    private final AuthService authService;

//    @RequestMapping(method = RequestMethod.GET)
    @RequestMapping()
    public String resetPassword(@RequestParam(name = "token")String token, Model page){
        try {
            authService.getUserByResetToken(token);
            page.addAttribute("token",token);
            return "reset_password_form.html";
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
