package tudor.work.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tudor.work.paypal.dtos.AccessTokenResponseDTO;
import tudor.work.service.PayPalService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/selfCoach/paypal")
public class PayPalWebHookListenerController {


    private final PayPalService payPalService;



    @PostMapping("/webhookListener")
    public ResponseEntity<?> eventListener(HttpServletRequest request)
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payPalService.webhookSignatureVerification(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAuthToken")
    public ResponseEntity<?> getAuthToken()
    {
        try {
            AccessTokenResponseDTO accessTokenResponseDTO = payPalService.getAccessToken();
            return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponseDTO.getAccessToken());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
