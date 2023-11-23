import 'package:fittnes_frontend/pages/start_exercise_page.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class ExercisePage extends StatelessWidget {
  final List<Exercise> exercises;
  final String workoutName;

  Future<void> saveWorkoutToHistory(String workouName) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.post(
      Uri.parse('http://192.168.215.182:8080/api/selfCoach/user/startWorkout/$workoutName'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) //ok
    {
      print("workout " + workoutName + " added to history");
    } else {

      print('http://192.168.215.182:8080/api/selfCoach/user/saveWorkout/$workoutName');
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
      ),
      body: ListView.builder(
        itemCount: exercises.length,
        itemBuilder: (context, index) {
          Exercise exercise = exercises[index];
          return Card(
            // Customize the card as needed
            child: ListTile(
              tileColor: Colors.grey.shade300,
              contentPadding: EdgeInsets.all(16.0),
              leading: Container(
                width: 120.0, // Adjust the width as needed
                height: 120.0, // Adjust the height as needed
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(
                      12.0), // Adjust the border radius as needed
                  image: DecorationImage(
                    image: NetworkImage(exercise.coverPhotoUrl),
                    fit: BoxFit.cover,
                  ),
                ),
              ),
              title: Text(
                exercise.name,
                style: TextStyle(
                  fontSize: 18.0,
                  fontWeight: FontWeight.bold,
                ),
              ),
              subtitle: Text(
                exercise.description,
                style: TextStyle(
                  color: Colors.grey[600],
                ),
              ),
              // Add more details if needed
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          saveWorkoutToHistory(workoutName);
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => StartExercisePage(
                exercises: exercises,
                exerciseIndex: 0,
                workoutName: workoutName,
              ),
            ),
          );
        },
        child: Icon(Icons.play_arrow),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }
}
