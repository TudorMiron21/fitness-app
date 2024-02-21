import 'dart:ffi';

class WorkoutStatistics{

  final int totalTime;
  final double totalCaloriesBurned;
  final double totalVolume;

  final Map<String,double> categoryPercentage;

  final Map<String,double> muscleGroupPercentage;

  final Map<String,double> difficultyPercentage;

  WorkoutStatistics(
    {
      required this.totalTime,
      required this.totalCaloriesBurned,
      required this.totalVolume,
      required this.categoryPercentage,
      required this.muscleGroupPercentage,
      required this.difficultyPercentage
    }
  );

    factory WorkoutStatistics.fromJson(Map<String, dynamic> json) {
    Map<String, double> categoryPercentage = {};
    for (var result in json['resultCategoryPercentages']) {
      categoryPercentage[result['category']['name']] = result['percentage'];
    }

    Map<String, double> muscleGroupPercentage = {};
    for (var result in json['resultMuscleGroupPercentages']) {
      muscleGroupPercentage[result['muscleGroup']['name']] = result['percentage'];
    }

    Map<String, double> difficultyPercentage = {};
    for (var result in json['resultDifficultyPercentages']) {
      difficultyPercentage[result['difficulty']['dificultyLevel']] = result['percentage'];
    }

    return WorkoutStatistics(
      totalTime: json['totalTime'],
      totalCaloriesBurned: json['totalCaloriesBurned'],
      totalVolume: json['totalVolume'],
      categoryPercentage: categoryPercentage,
      muscleGroupPercentage: muscleGroupPercentage,
      difficultyPercentage: difficultyPercentage,
    );
  }

}