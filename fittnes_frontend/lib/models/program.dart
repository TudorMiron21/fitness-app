import 'package:fittnes_frontend/models/workout.dart';

class Program {
  final int id;
  final String name;
  final String description;
  final int durationInDays;
  final String coverPhotoUrl;
  final Map<int, Workout> indexedWorkouts;

  Program({
    required this.id,
    required this.name,
    required this.description,
    required this.durationInDays,
    required this.coverPhotoUrl,
    required this.indexedWorkouts,
  });

factory Program.fromJson(Map<String, dynamic> json) {
    Map<int, Workout> indexedWorkouts = {};
    if (json['workoutProgramSet'] != null) {
      // Parse the `workoutProgramSet` array, assuming it has elements with `workout` and `workoutIndex`
      for (var element in json['workoutProgramSet']) {
        int index = element['workoutIndex']; // Assuming `workoutIndex` is directly in the objects of `workoutProgramSet`
        indexedWorkouts[index] = Workout.fromJson(element['workout']);
      }
    }

    return Program(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      durationInDays: json['durationInDays'],
      coverPhotoUrl: json['coverPhotoUrl'],
      indexedWorkouts: indexedWorkouts,
    );
  }
}
