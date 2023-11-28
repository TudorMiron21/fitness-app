import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter/material.dart';

class Workout extends ChangeNotifier {
  final String name;
  final String description;
  final String coverPhotoUrl;
  final double difficultyLevel;
  final List<Exercise> exercises;
  bool likedByUser;

  Workout({
    required this.name,
    required this.description,
    this.coverPhotoUrl = '',
    required this.difficultyLevel,
    required this.exercises,
    required this.likedByUser,
  });

  // Getter for likedByUser
  bool get isLiked => likedByUser;

  // Method to toggle likedByUser
  void toggleLikedStatus() {
    likedByUser = !likedByUser;
    // Notify all the listeners about the update.
    // This will trigger a rebuild of the widgets that depend on this model.
    notifyListeners();
  }
}