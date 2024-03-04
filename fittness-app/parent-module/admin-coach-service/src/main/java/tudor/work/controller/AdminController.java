package tudor.work.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.CoachDetailsResponseDto;
import tudor.work.model.CoachDetails;
import tudor.work.service.AdminService;

import java.util.Set;

@RestController
@RequestMapping(path = "/api/v1/adminCoachService/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/hello")
    public String getAdminHelloMessage() {
        return "hello admin";
    }

    @GetMapping("/getAllInvalidatedCoachRequests")
    public ResponseEntity<?> getAllInvalidatedCoachRequests() {

        Set<CoachDetailsResponseDto> coachDetailsResponseDto = adminService.getAllInvalidatedCoachRequests();
        return ResponseEntity.status(HttpStatus.OK).body(coachDetailsResponseDto);
    }

    @PutMapping("/validateCoachRequest/{idCoachDetails}")
    public ResponseEntity<?> validateCoachRequest(@PathVariable("idCoachDetails") Long idCoachDetails) {
        try {
            adminService.validateCoachRequest(idCoachDetails);
            return ResponseEntity.status(HttpStatus.OK).body("coach details certificate with id " + idCoachDetails + " was validated");

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

    }

}
