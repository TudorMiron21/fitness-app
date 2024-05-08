package tudor.work.controller;

import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.AchievementRequestDto;
import tudor.work.dto.CoachDetailsResponseDto;
import tudor.work.model.CoachDetails;
import tudor.work.service.AdminService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    @PutMapping("/invalidateCoachRequest/{idCoachDetails}")
    public ResponseEntity<?> invalidateCoachRequest(@PathVariable("idCoachDetails") Long idCoachDetails) {
        try {
            adminService.invalidateCoachRequest(idCoachDetails);
            return ResponseEntity.status(HttpStatus.OK).body("coach details certificate with id " + idCoachDetails + " was invalidated");

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }
    @PostMapping("/addAchievement")
    public ResponseEntity<?> addAchievement(@ModelAttribute AchievementRequestDto achievementRequestDto)
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.addAchievement(achievementRequestDto));
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }




}
