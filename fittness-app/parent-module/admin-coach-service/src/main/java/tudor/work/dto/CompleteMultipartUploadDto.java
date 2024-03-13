package tudor.work.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteMultipartUploadDto {

    private Long exerciseId;
    private String uploadId;
    private String filename;
}

