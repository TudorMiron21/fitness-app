package tudor.work.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FireBaseNotificationResponseDto {
    private int status;
    private String message;
}

