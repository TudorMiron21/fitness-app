import 'package:flutter/material.dart';

class AuthButtonTile extends StatelessWidget {
  final String imagePath;

  const AuthButtonTile({Key? key, required this.imagePath}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Image.asset(imagePath, width: 50, height: 50);  // Adjust size as needed
  }
}