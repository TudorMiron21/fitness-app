package tudor.work.controller;

import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.UploadCoachDetailsRequestDto;
import tudor.work.service.CoachService;
import tudor.work.service.MinioService;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@RestController
@RequestMapping(path = "/api/v1/adminCoachService/coach")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @PostMapping("/certificateCoach")
    public ResponseEntity<?> certificateCoach() {
        return null;
    }

    @PostMapping("/uploadCoachDetails")
    public ResponseEntity<?> uploadCoachDetails(@ModelAttribute UploadCoachDetailsRequestDto uploadCoachDetailsRequestDto) {
        try {
            if (uploadCoachDetailsRequestDto.getCertificateImg().isEmpty()) {
                return ResponseEntity.badRequest().body("no file was uploaded");
            }

            coachService.uploadCoachDetails(uploadCoachDetailsRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body("coach details uploaded");
        } catch (ServerException | InvalidResponseException | InsufficientDataException | ErrorResponseException |
                 IOException | NoSuchAlgorithmException | InvalidKeyException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
