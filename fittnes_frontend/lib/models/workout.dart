import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter/material.dart';

class Workout{

  final String name;
  final String description;
  final String coverPhotoUrl;
  final double difficultyLevel;
  final List<Exercise> exercises;
  bool likedByUser;


    Workout({
    required this.name,
    required this.description,
    this.coverPhotoUrl ='',
    required this.difficultyLevel,
    required this.exercises,
    required this.likedByUser
  });



}