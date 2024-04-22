package tudor.work.dto;

import lombok.*;
import tudor.work.model.Exercise;
import tudor.work.model.User;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalRecordDto {
    private Long id;

    private Exercise exercise;

    private Double maxWeight;

    private Long maxTime;

    private Double maxCalories;

    private Integer maxNoReps;

    private Double maxVolume;
}
