import 'dart:ffi';
import 'package:flutter/material.dart';

class ExerciseSimplified {
  final int idExercise;
  final String name;
  final String exerciseImageStartUrl;
  final String muscleGroupName;
  final String difficultyName;
  final String categoryName;

  ExerciseSimplified(
      {required this.idExercise,
      required this.name,
      this.exerciseImageStartUrl = '',
      required this.muscleGroupName,
      required this.difficultyName,
      required this.categoryName});

  factory ExerciseSimplified.fromJson(Map<String, dynamic> json) {
    return ExerciseSimplified(
      idExercise: json['idExercise'],
      name: json['name'],
      exerciseImageStartUrl: json['exerciseImageStartUrl'] ?? '',
      muscleGroupName: json['muscleGroupName'],
      difficultyName: json['difficultyName'],
      categoryName: json['categoryName'],
    );
  }

  int getIdExercise(){return idExercise;}
}
