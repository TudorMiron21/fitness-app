package tudor.work.utils;


import javassist.NotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tudor.work.model.*;
import tudor.work.service.*;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Data
public class StatisticsUtils {
    private final AuthorityService authorityService;
    private final UserHistoryWorkoutService userHistoryWorkoutService;
    private final UserHistoryExerciseService userHistoryExerciseService;
    private final UserHistoryModuleService userHistoryModuleService;
    private final WorkoutResultService workoutResultService;
    private final PersonalRecordService personalRecordService;

    public Long getTotalTimeDuringExercises(Long userHistoryWorkoutId) {

        List<Long> seconds = userHistoryWorkoutService
                .findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(userHistoryWorkoutId);

        Long totalSeconds = seconds.stream().reduce(0L, Long::sum);

        return totalSeconds;
//        Duration duration = Duration.ofSeconds(totalSeconds);

//        return String.format("%02d:%02d:%02d",
//                duration.toHours(),
//                duration.toMinutesPart(),
//                duration.toSecondsPart());
    }

    public Double getTotalCaloriesBurned(Long userHistoryWorkoutId) {

        List<Double> calories = userHistoryWorkoutService
                .findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(userHistoryWorkoutId);

        return calories.stream().reduce((double) 0, Double::sum);

//        return (double) 0;
    }

    public Double getTotalVolume(Long userHistoryWorkoutId) {
        List<Double> volumes = userHistoryWorkoutService
                .findAllUserHistoryVolumesWeightsByUserHistoryWorkoutId(userHistoryWorkoutId);

        return volumes.stream().reduce((double) 0, Double::sum);
    }


    public Set<ResultCategoryPercentage> getResultCategoryPercentages(Long userHistoryWorkoutId, Long workoutResultId) {

        List<Category> categoryEntries = userHistoryWorkoutService.
                findAllCategoriesByUserHistoryWorkoutId(userHistoryWorkoutId);


        Integer totalCategoryEntries = categoryEntries.size();

        return categoryEntries
                .stream()
                .collect(Collectors.groupingBy(category -> category, Collectors.counting()))
                .entrySet()
                .stream()
                .map(
                        entry ->
                        {
                            try {
                                return ResultCategoryPercentage
                                        .builder()
                                        .category(entry.getKey())
                                        .workoutResult(workoutResultService
                                                .findById(workoutResultId)
                                                .orElseThrow(() ->
                                                        new NotFoundException("workout result with id " + workoutResultId + " not found")
                                                )
                                        )
                                        .percentage((double) entry.getValue() / totalCategoryEntries)
                                        .build();
                            } catch (NotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).collect(Collectors.toSet());
    }

    public Set<ResultDifficultyPercentage> getResultDifficultyPercentages(Long userHistoryWorkoutId, Long workoutResultId) {

        List<Difficulty> difficulties = userHistoryWorkoutService
                .findAllDifficultiesByUserHistoryWorkoutId(userHistoryWorkoutId);

        Integer totalDifficultyEntries = difficulties.size();

        return difficulties
                .stream()
                .collect(Collectors.groupingBy(difficulty -> difficulty, Collectors.counting()))
                .entrySet()
                .stream()
                .map(
                        entry ->
                        {
                            try {
                                return ResultDifficultyPercentage
                                        .builder()
                                        .difficulty(entry.getKey())
                                        .workoutResult(
                                                workoutResultService
                                                        .findById(workoutResultId)
                                                        .orElseThrow(() ->
                                                                new NotFoundException("workout result with id " + workoutResultId + " not found")
                                                        )
                                        )
                                        .percentage((double) entry.getValue() / totalDifficultyEntries)
                                        .build();
                            } catch (NotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).collect(Collectors.toSet());

    }

    public Set<ResultMuscleGroupPercentage> getResultMuscleGroupPercentages(Long userHistoryWorkoutId, Long workoutResultId) {
        List<MuscleGroup> muscleGroupEntries = userHistoryWorkoutService
                .findAllMuscleGroupsByUserHistoryWorkoutId(userHistoryWorkoutId);

        Integer totalMuscleGroupEntries = muscleGroupEntries.size();

        return muscleGroupEntries
                .stream()
                .collect(Collectors.groupingBy(muscleGroup -> muscleGroup, Collectors.counting()))
                .entrySet()
                .stream()
                .map(
                        entry ->
                        {
                            try {
                                return ResultMuscleGroupPercentage
                                        .builder()
                                        .muscleGroup(entry.getKey())
                                        .workoutResult(
                                                workoutResultService
                                                        .findById(workoutResultId)
                                                        .orElseThrow(() ->
                                                                new NotFoundException("workout result with id " + workoutResultId + " not found")
                                                        )
                                        )
                                        .percentage((double) entry.getValue() / totalMuscleGroupEntries)
                                        .build();
                            } catch (NotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).collect(Collectors.toSet());
    }

//    public List<ResultExercisePersonalRecord> getResultExercisePersonalRecords(String userEmail,Long userHistoryWorkoutId) {
//        //get all the users workout results and compare the current with the previous ones
//
//        List<WorkoutResult> previousWorkoutResults = workoutResultService.findByUserEmail(userEmail);
//
//        if (!previousWorkoutResults.isEmpty()) {
//            previousWorkoutResults.remove(previousWorkoutResults.size() - 1);
//        }
//
//
//        List<UserHistoryExercise> doneExercises = userHistoryWorkoutService
//                .findAllUserHistoryExercisesByUserHistoryWorkoutId(userHistoryWorkoutId);
//
//
//    }
//


    @Transactional
    public void updatePersonalRecords(Long userHistoryWorkoutId, User user) throws NotFoundException {

        List<UserHistoryExercise> doneExercises = userHistoryWorkoutService
                .findAllUserHistoryExercisesByUserHistoryWorkoutId(userHistoryWorkoutId);


        Map<Exercise, UserHistoryExercise> exerciseMap = new HashMap<>();

        doneExercises.forEach(uhe -> {
            Exercise currentExercise = uhe.getExercise();
            UserHistoryExercise existingEntry = exerciseMap.get(currentExercise);

            // Determine the merge function based on the exercise category
            BinaryOperator<UserHistoryExercise> mergeFunction = (exercise1, exercise2) -> {
                if ("Cardio".equals(currentExercise.getCategory().getName())) {

                    return exercise1.getCaloriesBurned() > exercise2.getCaloriesBurned() ? exercise1 : exercise2;

                } else if ("Stretching".equals(currentExercise.getCategory().getName())) {
                    return exercise1.getCurrNoSeconds() > exercise2.getCurrNoSeconds() ? exercise1 : exercise2;
                } else if ("Plyometrics".equals(currentExercise.getCategory().getName())) {
                    return exercise1.getNoReps() > exercise2.getNoReps() ? exercise1 : exercise2;
                } else if("Body Only".equals(currentExercise.getEquipment().getName())) {
                    return exercise1.getNoReps() > exercise2.getNoReps() ? exercise1 : exercise2;
                }
                else{
                    //weight personal records based exercises
                    return exercise1.getWeight() > exercise2.getWeight() ? exercise1 : exercise2;
                }
            };

            UserHistoryExercise mergedResult = existingEntry == null ? uhe : mergeFunction.apply(existingEntry, uhe);
            exerciseMap.put(currentExercise, mergedResult);
        });

        List<UserHistoryExercise> uniqueExercisesList = new ArrayList<>(exerciseMap.values());

        List<PersonalRecord> personalRecords = personalRecordService.findByUserEmail(user.getEmail());


        for (UserHistoryExercise uhe : uniqueExercisesList) {
            PersonalRecord matchingRecord = personalRecords.stream()
                    .filter(pr -> pr.getExercise().getId().equals(uhe.getExercise().getId()))
                    .findFirst()
                    .orElse(null);

            boolean isCaloriesOriented = false;
            boolean isTimeOriented = false;
            boolean isNoRepsOriented = false;
            boolean isWeightOriented = false;

            if (uhe.getExercise().getCategory().getName().equals("Cardio")) {
                isCaloriesOriented = true;
            } else if (uhe.getExercise().getCategory().getName().equals("Stretching")) {
                isTimeOriented = true;
            } else if (uhe.getExercise().getCategory().getName().equals("Plyometrics")) {
                isNoRepsOriented = true;
            } else if (uhe.getExercise().getEquipment().getName().equals("Body Only")) {
                isNoRepsOriented = true;
            }else {
                isWeightOriented = true;
            }

            if (matchingRecord != null) {
                if (isCaloriesOriented) {
                    if (uhe.getCaloriesBurned() > matchingRecord.getMaxCalories())
                        matchingRecord.setMaxCalories((double) 0);
                } else if (isTimeOriented) {
                    if (uhe.getCurrNoSeconds() > matchingRecord.getMaxTime())
                        matchingRecord.setMaxTime(uhe.getCurrNoSeconds());
                } else if (isNoRepsOriented) {
                    if (uhe.getNoReps() > matchingRecord.getMaxNoReps())
                        matchingRecord.setMaxNoReps(uhe.getNoReps());
                } else {
                    if (uhe.getWeight() > matchingRecord.getMaxWeight())
                        matchingRecord.setMaxWeight(uhe.getWeight());
                }

            } else {
                PersonalRecord.PersonalRecordBuilder builder = PersonalRecord.builder()
                        .user(user)
                        .exercise(uhe.getExercise());

                if (isCaloriesOriented) {
//                    builder.maxCalories(uhe.getCaloriesBurned());
                    builder.maxCalories((double) 0);
                } else if (isTimeOriented) {
                    builder.maxTime(uhe.getCurrNoSeconds());
                } else if (isNoRepsOriented) {
                    builder.maxNoReps(uhe.getNoReps());
                } else {
                    builder.maxWeight(uhe.getWeight());
                }

                personalRecordService.save(builder.build());
            }
        }
    }

    public Integer calculatePointsPerWorkout() {
        return null;
    }


}
