import 'package:fittnes_frontend/pages/start_exercise_page.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/exercise.dart';

class ExercisePage extends StatelessWidget {
  final List<Exercise> exercises;
  final String workoutName;

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
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => StartExercisePage(exercises: exercises,exerciseIndex: 0,workoutName: workoutName,),
            ),
          );
        },
        child: Icon(Icons.play_arrow),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }
}
