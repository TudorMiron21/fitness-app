import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/models/workout.dart';
import 'package:fittnes_frontend/pages/exercise_page.dart';

class HomePage extends StatefulWidget {
  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  Future<List<Workout>> fetchWorkouts() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse('http://192.168.191.182:8080/api/selfCoach/user/workouts'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      // Map the API response to a list of Workout objects
      List<Workout> workouts = data.map((json) {
        List<Exercise> exerciseList = (json['exercises'] as List)
            .map((exerciseJson) => Exercise(
                  id: exerciseJson['id'],
                  name: exerciseJson['name'],
                  description: exerciseJson['description'],
                  mediaUrl: exerciseJson['mediaUrl'],
                  coverPhotoUrl: exerciseJson['coverPhotoUrl'] ?? "",
                  difficulty: exerciseJson['difficulty']['dificultyLevel'],
                  category: exerciseJson['category']['name'],
                  exerciseExclusive: exerciseJson['exerciseExclusive'],
                ))
            .toList() as List<Exercise>;

        return Workout(
          name: json['name'],
          description: json['description'],
          difficultyLevel: json['difficultyLevel'],
          coverPhotoUrl: json['coverPhotoUrl'] ?? "",
          exercises: exerciseList,
        );
      }).toList();

      return workouts;
    } else {
      throw Exception(
          'Failed to load workouts. Status code: ${response.statusCode}');
    }
  }

  @override
  Widget build(BuildContext context) {
    // Your home page content here
    return Scaffold(
      body: FutureBuilder<List<Workout>>(
        future: fetchWorkouts(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else {
            return ListView.builder(
              itemCount: snapshot.data!.length,
              itemBuilder: (context, index) {
                Workout workout = snapshot.data![index];

                // Calculate the difficulty level representation
                int maxDifficulty = 4; // Adjust the maximum difficulty level
                double fractionalPart =
                    workout.difficultyLevel - workout.difficultyLevel.floor();
                List<Widget> difficultyCircles = List.generate(
                  maxDifficulty,
                  (index) {
                    Color circleColor;
                    if (index < workout.difficultyLevel.floor()) {
                      circleColor = Colors.blue;
                    } else if (index == workout.difficultyLevel.floor()) {
                      circleColor = Color.lerp(
                          Colors.blue, Colors.blue.shade200, fractionalPart)!;
                    } else {
                      circleColor = Colors.grey;
                    }
                    return Container(
                      width: 14,
                      height: 14,
                      margin: EdgeInsets.only(left: 4),
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: circleColor.withOpacity(1), // Adjust opacity
                      ),
                    );
                  },
                );

                return Card(
                  elevation: 5,
                  margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Container(
                    decoration: BoxDecoration(
                      image: DecorationImage(
                        image: NetworkImage(workout.coverPhotoUrl),
                        fit: BoxFit.cover,
                        colorFilter: ColorFilter.mode(
                          Colors.black.withOpacity(
                              0.65), // Adjust opacity of background image
                          BlendMode.darken,
                        ),
                      ),
                    ),
                    child: ListTile(
                      contentPadding: EdgeInsets.all(16),
                      title: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            workout.name,
                            style: TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                              color: Colors.white, // Adjust text color
                            ),
                          ),
                          SizedBox(height: 8),
                          Text(
                            workout.description,
                            style: TextStyle(
                              color: Colors.white, // Adjust text color
                            ),
                          ),
                          SizedBox(height: 8),
                          SizedBox(height: 8),
                          Text(
                            'Exercises: ${workout.exercises.length}',
                            style: TextStyle(
                              color: Colors.white, // Adjust text color
                            ),
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              // Difficulty Circles
                              ...difficultyCircles,
                              SizedBox(
                                  width:
                                      8), // Adjust spacing between circles and difficulty level
                              // Difficulty Level
                              Text(
                                getDifficultyText(workout.difficultyLevel),
                                style: TextStyle(
                                  color: Colors.white, // Adjust text color
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => ExercisePage(
                              exercises: workout.exercises,
                              workoutName: workout.name,
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                );
              },
            );
          }
        },
      ),
      floatingActionButton: ElevatedButton(
        onPressed: () {
          // Add the functionality you want when the button is pressed to add a workout
        },
        style: ElevatedButton.styleFrom(
          primary: Colors.blue,
          padding: EdgeInsets.symmetric(horizontal: 8, vertical: 6),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
          elevation: 5,
        ),
        child: const Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.add,
              color: Colors.white,
            ),
            SizedBox(width: 2),
            Text(
              'Add Workout',
              style: TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
                fontSize: 14,
              ),
            ),
          ],
        ),
      ),
    );
  }

  String getDifficultyText(double difficultyLevel) {
    if (difficultyLevel >= 0 && difficultyLevel < 1) {
      return 'Easy';
    } else if (difficultyLevel >= 1 && difficultyLevel < 2) {
      return 'Medium';
    } else if (difficultyLevel >= 2 && difficultyLevel < 3) {
      return 'Hard';
    } else if (difficultyLevel >= 3 && difficultyLevel <= 4) {
      return 'Expert';
    } else {
      return 'Unknown';
    }
  }
}
