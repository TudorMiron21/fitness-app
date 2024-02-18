package tudor.work.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tudor.work.model.UserHistoryExercise;
import tudor.work.model.UserHistoryModule;
import tudor.work.model.UserHistoryWorkout;
import tudor.work.service.AuthorityService;
import tudor.work.service.UserHistoryExerciseService;
import tudor.work.service.UserHistoryModuleService;
import tudor.work.service.UserHistoryWorkoutService;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Data
public class StatisticsUtils {
    private final AuthorityService authorityService;
    private final UserHistoryWorkoutService userHistoryWorkoutService;
    private final UserHistoryExerciseService userHistoryExerciseService;
    private final UserHistoryModuleService userHistoryModuleService;


    public Long totalTimeDuringExercises(Long userHistoryWorkoutId,String email) {

        List<Long> seconds = userHistoryWorkoutService
                .findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(userHistoryWorkoutId, email);

        Long totalSeconds = seconds.stream().reduce(0L, Long::sum);

        return totalSeconds;
//        Duration duration = Duration.ofSeconds(totalSeconds);

//        return String.format("%02d:%02d:%02d",
//                duration.toHours(),
//                duration.toMinutesPart(),
//                duration.toSecondsPart());
    }

    public Double totalCaloriesBurned(Long userHistoryWorkoutId,String email) {

        List<Double> calories = userHistoryWorkoutService
                .findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(userHistoryWorkoutId, email);

//        return calories.stream().reduce((double) 0,Double::sum);

        return (double)0;
    }



}
