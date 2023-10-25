package tudor.work.dto;

import lombok.*;
import tudor.work.models.Roles;




@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class RegisterRequest {

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Roles role;

}
