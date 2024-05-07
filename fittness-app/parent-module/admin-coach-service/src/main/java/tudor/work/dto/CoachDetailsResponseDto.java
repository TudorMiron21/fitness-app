package tudor.work.dto;

import lombok.*;
import tudor.work.model.CertificationType;
import tudor.work.model.User;

import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoachDetailsResponseDto {

    private Long id;

    private String imageResourcePreSignedUrl;

    private User user;

    private CertificationType certificationType;

    private Integer yearsOfExperience;

    private Boolean isValidated;

}
