package tudor.work.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v1/adminCoachService/coach")
public class CoachController {

    @PostMapping("/certificateCoach")
    public ResponseEntity<?> certificateCoach()
    {
        return null;
    }

    @PostMapping("/uploadCertificatePicture")
    public ResponseEntity<?> uploadCertificatePicture(){
        return null;
    }



}
