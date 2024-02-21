package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tudor.work.model.*;
import tudor.work.utils.StatisticsUtils;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final StatisticsUtils statisticsUtils;

    @Async
    CompletableFuture<WorkoutResult> getStatistics(Long userHistoryWorkoutId, Long workoutResultId, User user) {


        CompletableFuture<Long> futureTotalTime = CompletableFuture.supplyAsync(
                () -> {
                    return statisticsUtils.getTotalTimeDuringExercises(userHistoryWorkoutId);
                }
        );

        CompletableFuture<Double> futureTotalCalories = CompletableFuture.supplyAsync(
                () -> {
                    return statisticsUtils.getTotalCaloriesBurned(userHistoryWorkoutId);
                }
        );

        CompletableFuture<Double> futureTotalVolume = CompletableFuture.supplyAsync(
                () -> {
                    return statisticsUtils.getTotalVolume(userHistoryWorkoutId);
                }
        );


        CompletableFuture<Set<ResultCategoryPercentage>> futureCategoryPercentageSet = CompletableFuture.supplyAsync(
                () -> {
                    return statisticsUtils.getResultCategoryPercentages(userHistoryWorkoutId, workoutResultId);
                }
        );

        CompletableFuture<Set<ResultDifficultyPercentage>> futureDifficultyPercentageSet = CompletableFuture.supplyAsync(
                () -> {
                    return statisticsUtils.getResultDifficultyPercentages(userHistoryWorkoutId, workoutResultId);
                }
        );

        CompletableFuture<Set<ResultMuscleGroupPercentage>> futureMuscleGroupPercentageSet = CompletableFuture.supplyAsync(
                () -> {
                    return statisticsUtils.getResultMuscleGroupPercentages(userHistoryWorkoutId, workoutResultId);
                }
        );

        CompletableFuture<Void> futurePersonalRecords = CompletableFuture.runAsync(

                () ->
                {
                    try {
                        statisticsUtils.updatePersonalRecords(userHistoryWorkoutId, user);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(
                        futureTotalTime,
                        futureTotalCalories,
                        futureCategoryPercentageSet,
                        futureDifficultyPercentageSet,
                        futureMuscleGroupPercentageSet,
                        futurePersonalRecords
                );

        return allFutures.thenApply(v -> {
            Long totalTime = futureTotalTime.join();
            Double totalCalories = futureTotalCalories.join();
            Double totalVolume = futureTotalVolume.join();
            Set<ResultCategoryPercentage> categoryPercentageSet = futureCategoryPercentageSet.join();
            Set<ResultDifficultyPercentage> difficultyPercentageSet = futureDifficultyPercentageSet.join();
            Set<ResultMuscleGroupPercentage> muscleGroupPercentageSet = futureMuscleGroupPercentageSet.join();
            return WorkoutResult.builder()
                    .totalTime(totalTime)
                    .totalCaloriesBurned(totalCalories)
                    .totalVolume(totalVolume)
                    .resultCategoryPercentages(categoryPercentageSet)
                    .resultDifficultyPercentages(difficultyPercentageSet)
                    .resultMuscleGroupPercentages(muscleGroupPercentageSet)
                    .build();
        });
    }

}
