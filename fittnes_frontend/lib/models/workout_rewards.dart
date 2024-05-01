import 'package:fittnes_frontend/models/achievement.dart';

class WorkoutRewards {
  final double numberOfPoints;

  final List<Achievement> achievements;

  WorkoutRewards({required this.numberOfPoints, required this.achievements});

  factory WorkoutRewards.fromJson(Map<String, dynamic> json) {
    List<dynamic> achievementsJson = json['achievements'];
    List<Achievement> parsedAchievements = [];
    for (var achievementJson in achievementsJson) {
      parsedAchievements.add(Achievement.fromJson(achievementJson));
    }
    return WorkoutRewards(
      numberOfPoints: json['numberOfPoints'],
      achievements: parsedAchievements,
    );
  }
}
