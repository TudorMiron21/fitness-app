class User {
  final int id;
  final String email;
  final String profilePictureUrl;
  final String firstName;
  final String lastName;

  User({
    required this.id,
    required this.email,
    required this.firstName,
    required this.lastName,
    required this.profilePictureUrl,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
        id: json['id'],
        email: json['email'] ?? "",
        profilePictureUrl: json['profilePictureUrl'] ?? "",
        firstName: json['firstName'] ?? "",
        lastName: json['lastName'] ?? "");
  }
}
