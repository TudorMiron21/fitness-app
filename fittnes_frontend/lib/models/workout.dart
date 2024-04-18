import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter/material.dart';

class Workout extends ChangeNotifier {
  final int id;
  final String name;
  final String description;
  final String coverPhotoUrl;
  final double difficultyLevel;
  final List<Exercise> exercises;
  bool likedByUser;

  Workout({
    required this.id,
    required this.name,
    required this.description,
    this.coverPhotoUrl = '',
    required this.difficultyLevel,
    required this.exercises,
    required this.likedByUser,
  });

  factory Workout.fromJson(Map<String, dynamic> json) {
    List<Exercise> parsedExercises = [];
    if (json['exercises'] != null) {
      parsedExercises =
          (json['exercises'] as List).map((e) => Exercise.fromJson(e)).toList();
    }

    return Workout(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      coverPhotoUrl: json['coverPhotoUrl'] ?? '',
      difficultyLevel: json['difficultyLevel'] ?? 0.0,
      exercises: parsedExercises,
      likedByUser: json['likedByUser'] ?? false,
    );
  }
  // Getter for likedByUser
  bool get isLiked => likedByUser;

  // Method to toggle likedByUser
  void toggleLikedStatus() {
    likedByUser = !likedByUser;
    // Notify all the listeners about the update.
    // This will trigger a rebuild of the widgets that depend on this model.
    notifyListeners();
  }

  // static fromJson(workoutJson) {}
}
