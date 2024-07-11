package tudor.work.dto;

import lombok.*;
import tudor.work.model.Gender;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDetailsResponseDto {
    private Gender gender;
    private Integer age;
    private Integer height;
    private Double weight;
}
