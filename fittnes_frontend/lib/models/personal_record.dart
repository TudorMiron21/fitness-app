class PersonalRecord {
  final int id;
  final String exerciseName;
  final double maxWeight;
  final int maxTime;
  final double? maxVolume;
  final int? maxNoReps;
  final double? maxCalories;

  PersonalRecord({
    required this.id,
    required this.exerciseName,
    required this.maxWeight,
    required this.maxTime,
    this.maxVolume,
    this.maxNoReps,
    this.maxCalories,
  });

  factory PersonalRecord.fromJson(Map<String, dynamic> json) {
    return PersonalRecord(
        id: json['id'],
        exerciseName: json['exercise']['name'],
        maxWeight: json['maxWeight']?.toDouble() ?? 0.0,
        maxTime: json['maxTime'],
        maxVolume: json['maxVolume']?.toDouble() ?? 0,
        maxNoReps: json['maxNoReps'] ?? 0,
        maxCalories: json['maxCalories'] ?? 0.0);
  }

  static List<PersonalRecord> fromJsonList(List<dynamic> jsonList) {
    return jsonList.map((json) => PersonalRecord.fromJson(json)).toList();
  }
}
