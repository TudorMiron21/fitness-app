package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.security.CodeSigner;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonBackReference
    private User user;

    private String coachCertificatePath;

    private CertificationType certificationType;

    private Integer yearsOfExperience;

    private Boolean isValidated;

}
