package tudor.work.embeddables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
@Getter
public class ResultMuscleGroupPercentageId implements Serializable {

    @Column(name = "id_workout_result")
    private Long idWorkoutResult;

    @Column(name = "id_muscle_group")
    private Long idMuscleGroup;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ResultMuscleGroupPercentageId that = (ResultMuscleGroupPercentageId) o;
        return Objects.equals(idWorkoutResult, that.idWorkoutResult) &&
                Objects.equals(idMuscleGroup, that.idMuscleGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idWorkoutResult, idMuscleGroup);
    }
}
