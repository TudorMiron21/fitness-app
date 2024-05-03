class Achievement {
  final int id;
  final String name;
  final String description;
  final double numberOfPoints;
  final String achievementPicturePath;

  Achievement(
      {required this.id,
      required this.name,
      required this.description,
      required this.numberOfPoints,
      required this.achievementPicturePath});

  factory Achievement.fromJson(Map<String, dynamic> json) {
    return Achievement(
        id: json['id'],
        name: json['name'],
        description: json['description'],
        numberOfPoints: json['numberOfPoints'],
        achievementPicturePath: json['achievementPicturePath']?? '');
  }

  
}
