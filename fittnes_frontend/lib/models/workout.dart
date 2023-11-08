import 'package:fittnes_frontend/models/exercise.dart';

class Workout{

  final String name;
  final String description;
  final String coverPhotoUrl;
  final List<Exercise> exercises;

    Workout({
    required this.name,
    required this.description,
    required this.coverPhotoUrl,
    required this.exercises,
  });

}