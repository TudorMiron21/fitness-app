package tudor.work.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login/oauth2/code/google")
public class GoogleController {

//    @GetMapping
//    public ResponseEntity<?> googleAuth(OAuth2AuthenticationToken oAuth2AuthenticationToken)
//    {
//        if (oAuth2AuthenticationToken != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(oAuth2AuthenticationToken.getPrincipal().getAttributes());
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 token is null");
//        }    }
}
