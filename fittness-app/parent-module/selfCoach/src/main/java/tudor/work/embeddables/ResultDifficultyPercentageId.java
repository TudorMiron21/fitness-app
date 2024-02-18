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
public class ResultDifficultyPercentageId implements Serializable {

    @Column(name = "id_workout_result")
    private Long idWorkoutResult;

    @Column(name = "id_difficulty")
    private Long idDifficulty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ResultDifficultyPercentageId that = (ResultDifficultyPercentageId) o;
        return Objects.equals(idWorkoutResult, that.idWorkoutResult) &&
                Objects.equals(idDifficulty, that.idDifficulty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idWorkoutResult, idDifficulty);
    }

}
