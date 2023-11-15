import 'package:fittnes_frontend/models/exercise.dart';

class Workout{

  final String name;
  final String description;
  final String coverPhotoUrl;
  final double difficultyLevel;
  final List<Exercise> exercises;


    Workout({
    required this.name,
    required this.description,
    this.coverPhotoUrl ='',
    required this.difficultyLevel,
    required this.exercises,
  });



}