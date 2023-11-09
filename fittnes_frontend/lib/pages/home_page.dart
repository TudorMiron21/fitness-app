import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/models/workout.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;

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
              return Center(
                  child:
                      CircularProgressIndicator()); // Center the loading indicator
            } else if (snapshot.hasError) {
              return Center(child: Text('Error: ${snapshot.error}'));
            } else {
              // Display the list of workouts using ListView.builder
// Inside the ListView.builder
// Inside the ListView.builder
              return ListView.builder(
                itemCount: snapshot.data!.length,
                itemBuilder: (context, index) {
                  Workout workout = snapshot.data![index];
                  return Card(
                    elevation: 5,
                    margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: Container(
                      decoration: BoxDecoration(
                        image: DecorationImage(
                          image: NetworkImage(workout.coverPhotoUrl),
                          fit: BoxFit.cover,
                          opacity: 0.45,
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
                                color:
                                    Colors.black, // Text color over the photo
                              ),
                            ),
                            SizedBox(height: 8),
                            Text(
                              workout.description,
                              style: TextStyle(
                                color:
                                    Colors.black, // Text color over the photo
                              ),
                            ),
                            SizedBox(height: 8),
                            Text(
                              'Exercises: ${workout.exercises.length}',
                              style: TextStyle(
                                color:
                                    Colors.black, // Text color over the photo
                              ),
                            ),
                          ],
                        ),
                        onTap: () {
                          // Add navigation or other actions when ListTile is tapped
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
            primary: Colors.blue, // Background color of the button
            padding: EdgeInsets.symmetric(
                horizontal: 8, vertical: 6), // Adjust padding
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10), // Adjust border radius
            ),
            elevation: 5, // Add elevation for a subtle shadow effect
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.add, // You can use a different icon if preferred
                color: Colors.white,
              ),
              SizedBox(width: 2), // Add spacing between icon and text
              Text(
                'Add Workout',
                style: TextStyle(
                  color: Colors.white, // Text color of the button
                  fontWeight: FontWeight.bold,
                  fontSize: 14, // Adjust font size
                ),
              ),
            ],
          ),
        ));
  }
}
