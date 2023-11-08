package tudor.work.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordRequestDto {

    private String email;
}
