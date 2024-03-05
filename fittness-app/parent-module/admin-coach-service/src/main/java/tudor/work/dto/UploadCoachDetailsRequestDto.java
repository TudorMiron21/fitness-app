package tudor.work.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.model.CertificationType;
import tudor.work.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadCoachDetailsRequestDto {

//    private String emailUser;

    private MultipartFile certificateImg;

   private CertificationType certificationType;

    private Integer yearsOfExperience;

}
