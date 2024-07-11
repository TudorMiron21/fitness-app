package tudor.work.dto;

import lombok.*;
import tudor.work.models.Roles;




@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Roles role;

}
