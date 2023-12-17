import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/exercise.dart';

class ExerciseDetailPage extends StatelessWidget {
  final Exercise exercise;

  ExerciseDetailPage({required this.exercise});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(exercise.name),
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            // Display the start image
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Image.network(
                exercise.exerciseImageStartUrl,
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) =>
                    Icon(Icons.error),
              ),
            ),
            // Optionally display the end image if available
            if (exercise.exerciseImageEndUrl != null)
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Image.network(
                  exercise.exerciseImageEndUrl!,
                  fit: BoxFit.cover,
                  errorBuilder: (context, error, stackTrace) =>
                      Icon(Icons.error),
                ),
              ),
            Container(
              padding: EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Description:',
                    style: TextStyle(fontSize: 24.0, fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 10.0),
                  Text(
                    exercise.description,
                    style: TextStyle(fontSize: 16.0),
                  ),
                  SizedBox(height: 20.0),
                  // Add more details here, such as duration, sets, reps, etc.
                  // For example:
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}