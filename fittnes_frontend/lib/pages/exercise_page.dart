import 'dart:convert';
import 'dart:ffi';

import 'package:fittnes_frontend/pages/start_exercise_page.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class ExercisePage extends StatelessWidget {
  final List<Exercise> exercises;
  final String workoutName;
  late int workoutId;

  Future<void> saveWorkoutToHistory(String workoutName) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.post(
      Uri.parse(
          'http://192.168.0.229:8080/api/selfCoach/user/startWorkout/$workoutName'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      print("workout " + workoutName + " added to history");
      this.workoutId = int.parse(response.body);
    } else {
      print(
          'http://192.168.0.229:8080/api/selfCoach/user/saveWorkout/$workoutName');
      throw Exception(
          'Failed to save workout to history. Status code: ${response.statusCode}');
    }
  }

  ExercisePage({required this.exercises, required this.workoutName});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('${this.workoutName}'),
        elevation: 10.0,
        backgroundColor: Colors.blueAccent,
      ),
      body: ListView.builder(
        itemCount: exercises.length,
        itemBuilder: (context, index) {
          Exercise exercise = exercises[index];
          return Card(
            margin: EdgeInsets.all(10.0),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(15.0),
            ),
            child: ListTile(
              tileColor: Colors.grey.shade300,
              contentPadding: EdgeInsets.all(16.0),
              leading: Container(
                width: 120.0,
                height: 120.0,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(12.0),
                  image: DecorationImage(
                    image: NetworkImage(exercise.coverPhotoUrl),
                    fit: BoxFit.cover,
                  ),
                ),
              ),
              title: Padding(
                padding: EdgeInsets.only(bottom: 8.0),
                child: Text(
                  exercise.name,
                  style: TextStyle(
                    fontSize: 18.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              subtitle: Text(
                exercise.description,
                style: TextStyle(
                  color: Colors.grey[600],
                ),
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: ()async {
          await saveWorkoutToHistory(workoutName);
          Navigator.push(
            context,
             MaterialPageRoute(
              builder: (context) => StartExercisePage(
                exercises: exercises,
                exerciseIndex: 0,
                workoutId: workoutId,
                noSets: 1,
                isFirstExercise: true,
                userHistoryModuleId: 0,
              ),
            ),
          );
        },
        child: Icon(Icons.play_arrow),
        backgroundColor: Colors.blueAccent,
        elevation: 5.0,
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }
}
