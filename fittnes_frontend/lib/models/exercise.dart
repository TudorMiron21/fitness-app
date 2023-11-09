import 'dart:ffi';

class Exercise {
  final int id;
  final String name;
  final String description;
  final String mediaUrl;
  final String coverPhotoUrl;
  final String difficulty;
  final String category;
  final bool exerciseExclusive;

  Exercise({
    required this.id,
    required this.name,
    required this.description,
    required this.mediaUrl,
    this.coverPhotoUrl ='',
    required this.difficulty,
    required this.category,
    required this.exerciseExclusive
  });
}
