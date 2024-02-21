package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import tudor.work.embeddables.ResultCategoryPercentageId;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultCategoryPercentage {

    @EmbeddedId
    private ResultCategoryPercentageId id = new ResultCategoryPercentageId();

    @ManyToOne
    @MapsId("idWorkoutResult")
    @JoinColumn(name = "id_workout_result")
    @JsonBackReference
    private WorkoutResult workoutResult;

    @ManyToOne
    @MapsId("idCategory")
    @JoinColumn(name = "id_category")
    private Category category;

    private Double percentage;

}
