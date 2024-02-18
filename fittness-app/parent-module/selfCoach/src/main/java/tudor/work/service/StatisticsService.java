package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tudor.work.model.WorkoutResult;
import tudor.work.utils.StatisticsUtils;

import javax.transaction.Transactional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final StatisticsUtils statisticsUtils;

    @Async
    CompletableFuture<WorkoutResult> getStatistics(Long userHistoryWorkoutId, String email) {


        CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(() -> {
            return statisticsUtils.totalTimeDuringExercises(userHistoryWorkoutId,email);
        });

        CompletableFuture<Double> future2 = CompletableFuture.supplyAsync(
                ()->{
                    return statisticsUtils.totalCaloriesBurned(userHistoryWorkoutId,email);
                }
        );
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2);

        return allFutures.thenApply(v -> {
            Long totalTime = future1.join();
            Double totalCalories = future2.join();

            return WorkoutResult.builder()
                    .totalTime(totalTime)
                    .totalCaloriesBurned(totalCalories)
                    .build();
        });    }

}
