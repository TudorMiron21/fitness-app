package tudor.work.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String lastName;
    private String firstName;
    private String profilePictureUrl;
}
