import 'dart:ffi';
import 'package:flutter/material.dart';

class Exercise {
  final int id;
  final String name;
  final String description;
  final String descriptionUrl;
  final String exerciseImageStartUrl;
  final String exerciseImageEndUrl;
  final String exerciseVideoUrl;
  final String muscleGroup;
  final String equipment;
  final String difficulty;
  final String category;
  final bool exerciseExclusive;
  final double rating;
  final bool hasNoReps;
  final bool hasWeight;


  Exercise(
      {
      required this.id,
      required this.name,
      this.description = '',
      this.descriptionUrl = '',
      this.exerciseImageStartUrl ='',
      this.exerciseImageEndUrl = '',
      this.exerciseVideoUrl = '',
      required this.muscleGroup,
      required this.equipment,
      required this.difficulty,
      required this.category,
      required this.exerciseExclusive,
      required this.hasNoReps,
      required this.hasWeight,
      this.rating = 0.0
      });
}
