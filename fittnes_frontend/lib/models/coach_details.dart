class CoachDetails {
  int numberOfSubscribers;
  final int numberOfExercises;
  final int numberOfWorkouts;
  final int numberOfPrograms;
  final int numberOfChallenges;

  CoachDetails(
      {this.numberOfSubscribers = 0,
      required this.numberOfExercises,
      required this.numberOfWorkouts,
      required this.numberOfPrograms,
      required this.numberOfChallenges});

  factory CoachDetails.fromJson(Map<String, dynamic> json) {
    return CoachDetails(
        numberOfSubscribers: json['numberOfSubscribers'],
        numberOfExercises: json['numberOfExercises'],
        numberOfWorkouts: json['numberOfWorkouts'],
        numberOfPrograms: json['numberOfPrograms'],
        numberOfChallenges: json['numberOfChallenges']);
  }
}
