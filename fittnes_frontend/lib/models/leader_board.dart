import 'package:fittnes_frontend/models/user.dart';

class LeaderBoard {
  final int id;
  final User user;
  final double numberOfPoints;
  final int numberOfDoneExercises;
  final int numberOfDoneWorkouts;
  final int numberOfDonePrograms;

  LeaderBoard(
      {required this.id,
      required this.user,
      this.numberOfPoints = 0.0,
      this.numberOfDoneExercises = 0,
      this.numberOfDoneWorkouts = 0,
      this.numberOfDonePrograms = 0});

  factory LeaderBoard.fromJson(Map<String, dynamic> json) {
    return LeaderBoard(
        id: json['id'],
        user: User.fromJson(json['user']),
        numberOfPoints: json['numberOfPoints'] ?? 0.0,
        numberOfDoneExercises: json['numberOfDoneExercises'] ?? 0,
        numberOfDoneWorkouts: json['numberOfDoneWorkouts'] ?? 0,
        numberOfDonePrograms: json['numberOfDonePrograms'] ?? 0);
  }

  static List<LeaderBoard> fromJsonList(List jsonList) {
        return jsonList.map((json) => LeaderBoard.fromJson(json)).toList();
  }
}
