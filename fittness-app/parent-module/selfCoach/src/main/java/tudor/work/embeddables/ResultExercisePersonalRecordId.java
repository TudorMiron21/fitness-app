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
public class ResultExercisePersonalRecordId implements Serializable {

    @Column(name = "id_workout_result")
    private Long idWorkoutResult;

    @Column(name = "id_exercise")
    private Long idExercise;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ResultExercisePersonalRecordId that = (ResultExercisePersonalRecordId) o;
        return Objects.equals(idWorkoutResult, that.idWorkoutResult) &&
                Objects.equals(idExercise, that.idExercise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idWorkoutResult, idExercise);
    }

}
