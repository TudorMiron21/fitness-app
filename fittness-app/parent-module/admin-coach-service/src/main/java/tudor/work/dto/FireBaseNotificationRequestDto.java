package tudor.work.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FireBaseNotificationRequestDto {
    private String title;
    private String body;
    private String token;
}
