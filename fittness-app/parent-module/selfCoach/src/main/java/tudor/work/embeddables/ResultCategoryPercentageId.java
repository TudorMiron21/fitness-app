package tudor.work.embeddables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class ResultCategoryPercentageId implements Serializable {

    @Column(name = "id_workout_result")
    private Long idWorkoutResult;

    @Column(name = "id_category")
    private Long idCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ResultCategoryPercentageId that = (ResultCategoryPercentageId) o;
        return Objects.equals(idWorkoutResult, that.idWorkoutResult) &&
                Objects.equals(idCategory, that.idCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idWorkoutResult, idCategory);
    }
}
