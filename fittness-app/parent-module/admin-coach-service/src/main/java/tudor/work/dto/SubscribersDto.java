package tudor.work.dto;


import lombok.*;
import tudor.work.model.Roles;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribersDto {

    private Long id;

    private String email;

    private String lastName;

    private String firstName;

    private Roles role;

    private LocalDateTime subscriptionTime;

}
