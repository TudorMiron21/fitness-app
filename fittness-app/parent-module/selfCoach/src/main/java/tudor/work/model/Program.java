package tudor.work.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private Integer durationInDays;
    private String coverPhotoUrl;

    @OneToMany(
            mappedBy = "program",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<WorkoutProgram> workoutPrograms;
    
    @ManyToOne
    @JoinColumn(name = "adder_id")
    private User adder;

    public int getNextGreatestIndex(int currentIndex) {

        List<WorkoutProgram> sortedWorkoutPrograms = new ArrayList<>(workoutPrograms);
        sortedWorkoutPrograms.sort(Comparator.comparingInt(WorkoutProgram::getWorkoutIndex));

        for (WorkoutProgram wp : sortedWorkoutPrograms) {
            if (wp.getWorkoutIndex() > currentIndex) {
                return wp.getWorkoutIndex();
            }
        }

        //there is no greater index that the currentIndex
        return -1;
    }

    public int getLowestIndex() {
        OptionalInt minIndex = workoutPrograms.stream()
                .mapToInt(WorkoutProgram::getWorkoutIndex)
                .min();

        if (minIndex.isPresent()) {
            return minIndex.getAsInt();
        } else {
            throw new NoSuchElementException("No workout programs found");
        }
    }
}
