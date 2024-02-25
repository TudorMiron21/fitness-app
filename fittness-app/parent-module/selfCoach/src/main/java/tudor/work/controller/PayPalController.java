package tudor.work.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import tudor.work.paypal.dtos.AccessTokenResponseDTO;
import tudor.work.service.PayPalService;

import javax.ws.rs.core.Request;

@Controller
@RequiredArgsConstructor
public class PayPalController {
    @RequestMapping(path = "/api/selfCoach/paypal/getPayPalSubscriptionButton")
    public String getPayPalSubscriptionButton() {
        return "paypal"; // Name of the Thymeleaf template
    }

}
