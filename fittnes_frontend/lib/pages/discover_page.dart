import 'dart:convert';

import 'package:fittnes_frontend/components/custom_divider.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/models/workout.dart';
import 'package:fittnes_frontend/pages/create_workout_page.dart';
import 'package:fittnes_frontend/pages/exercise_page.dart';
import 'package:fittnes_frontend/security/jwt_utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class DiscoverPage extends StatefulWidget {
  const DiscoverPage({super.key});

  @override
  State<DiscoverPage> createState() => _DiscoverPageState();
}

class _DiscoverPageState extends State<DiscoverPage> {
  int currentPageIndex = 1;

  List<Workout> startedWorkouts = List.empty();
  List<Workout> personalWorkouts = List.empty();

  Future<List<Workout>> fetchStartedWorkouts() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }
    String email = JwtUtils.extractSubject(accessToken);

    final response = await http.get(
      Uri.parse('http://localhost:8080/api/selfCoach/user/getStartedWorkouts/' +
          email),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );
    List<Workout> workouts = List.empty();
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      // Map the API response to a list of Workout objects
      workouts = data.map((json) {
        List<Exercise> exerciseList = (json['exercises'] as List)
            .map((exerciseJson) => Exercise(
                  id: exerciseJson['id'],
                  name: exerciseJson['name'],
                  description: exerciseJson['description'] ?? "",
                  descriptionUrl: exerciseJson['descriptionUrl'] ?? "",
                  exerciseImageStartUrl:
                      exerciseJson['exerciseImageStartUrl'] ?? "",
                  exerciseImageEndUrl:
                      exerciseJson['exerciseImageEndUrl'] ?? "",
                  exerciseVideoUrl: exerciseJson['exerciseVideoUrl'] ?? "",
                  difficulty: exerciseJson['difficulty']['dificultyLevel'],
                  category: exerciseJson['category']['name'],
                  exerciseExclusive: exerciseJson['exerciseExclusive'],
                  equipment: exerciseJson['equipment']['name'],
                  muscleGroup: exerciseJson['muscleGroup']['name'],
                  rating: exerciseJson['rating'],
                  hasNoReps: exerciseJson['hasNoReps'],
                  hasWeight: exerciseJson['hasWeight'],
                ))
            .toList() as List<Exercise>;

        return Workout(
            id: json['id'],
            name: json['name'],
            description: json['description'],
            difficultyLevel: json['difficultyLevel'],
            coverPhotoUrl: json['coverPhotoUrl'] ?? "",
            exercises: exerciseList,
            likedByUser: json['likedByUser']);
      }).toList();
    } else {
      throw Exception(
          'Failed to load workouts. Status code: ${response.statusCode}');
    }
    return workouts;
  }

  Future<List<Workout>> fetchPersonalWorkouts() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }
    String email = JwtUtils.extractSubject(accessToken);

    final response = await http.get(
      Uri.parse('http://localhost:8080/api/selfCoach/user/getPersonalWorkouts'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );
    List<Workout> workouts = List.empty();
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      // Map the API response to a list of Workout objects
      workouts = data.map((json) {
        List<Exercise> exerciseList = (json['exercises'] as List)
            .map((exerciseJson) => Exercise(
                  id: exerciseJson['id'],
                  name: exerciseJson['name'],
                  description: exerciseJson['description'] ?? "",
                  descriptionUrl: exerciseJson['descriptionUrl'] ?? "",
                  exerciseImageStartUrl:
                      exerciseJson['exerciseImageStartUrl'] ?? "",
                  exerciseImageEndUrl:
                      exerciseJson['exerciseImageEndUrl'] ?? "",
                  exerciseVideoUrl: exerciseJson['exerciseVideoUrl'] ?? "",
                  difficulty: exerciseJson['difficulty']['dificultyLevel'],
                  category: exerciseJson['category']['name'],
                  exerciseExclusive: exerciseJson['exerciseExclusive'],
                  equipment: exerciseJson['equipment']['name'],
                  muscleGroup: exerciseJson['muscleGroup']['name'],
                  rating: exerciseJson['rating'],
                  hasNoReps: exerciseJson['hasNoReps'],
                  hasWeight: exerciseJson['hasWeight'],
                ))
            .toList() as List<Exercise>;

        return Workout(
            id: json['id'],
            name: json['name'],
            description: json['description'],
            difficultyLevel: json['difficultyLevel'],
            coverPhotoUrl: json['coverPhotoUrl'] ?? "",
            exercises: exerciseList,
            likedByUser: json['likedByUser']);
      }).toList();
    } else {
      throw Exception(
          'Failed to load workouts. Status code: ${response.statusCode}');
    }
    return workouts;
  }

  Future<void> likeWorkout(Workout workout) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.put(
      Uri.parse('http://localhost:8080/api/selfCoach/user/likeWorkout/' +
          workout.id.toString()),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) //OK
    {
      setState(() {
        workout.likedByUser = true;
      });
    } else {
      throw Exception(
          'Failed to like workout. Status code: ${response.statusCode}');
    }
  }

  Future<void> unlikeWorkout(Workout workout) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    // TODO: Implement the API call to unlike the workout
    // Use the workout.id to identify the workout to unlike

    final response = await http.delete(
      Uri.parse('http://localhost:8080/api/selfCoach/user/unlikeWorkout/' +
          workout.id.toString()),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) //OK
    {
      setState(() {
        workout.likedByUser = false;
      });
    } else {
      throw Exception(
          'Failed to like workout. Status code: ${response.statusCode}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // appBar: AppBar(
      //     title: Text(
      //       'Your Workouts',
      //     ),
      //     centerTitle: true,
      //     toolbarHeight: 30,
      //     backgroundColor: Colors.blue,
      //     elevation: 4,
      //     automaticallyImplyLeading: false),
      body: Column(
        children: [
          // SizedBox(
          //   height: 200,
          //   child: FutureBuilder<List<Workout>>(
          //     future: fetchStartedWorkouts(),
          //     builder: (context, startedWorkoutsSnapshot) {
          //       if (startedWorkoutsSnapshot.connectionState ==
          //           ConnectionState.waiting) {
          //         return Center(child: CircularProgressIndicator());
          //       } else if (startedWorkoutsSnapshot.hasError) {
          //         return Center(
          //             child: Text('Error: ${startedWorkoutsSnapshot.error}'));
          //       } else {
          //         List<Workout> mostLikedWorkouts =
          //             startedWorkoutsSnapshot.data!;

          //         return PageView.builder(
          //           itemCount: mostLikedWorkouts.length,
          //           itemBuilder: (context, index) {
          //             Workout workout = mostLikedWorkouts[index];
          //             return buildWorkoutCard(workout);
          //           },

          //         );
          //       }
          //     },
          //   ),
          // ),

          CustomDivider(dividerText: "Started Workouts"),
          Expanded(
            flex: 1, // Adjust the flex factor as needed for balance
            child: FutureBuilder<List<Workout>>(
              future: fetchStartedWorkouts(),
              builder: (context, startedWorkoutsSnapshot) {
                if (startedWorkoutsSnapshot.connectionState ==
                    ConnectionState.waiting) {
                  return Center(child: CircularProgressIndicator());
                } else if (startedWorkoutsSnapshot.hasError) {
                  return Center(
                      child: Text('Error: ${startedWorkoutsSnapshot.error}'));
                } else {
                  List<Workout> savedWorkouts = startedWorkoutsSnapshot.data!;

                  return PageView.builder(
                    itemCount: savedWorkouts.length,
                    itemBuilder: (context, index) {
                      Workout workout = savedWorkouts[index];
                      return buildWorkoutCard(workout);
                    },
                  );
                }
              },
            ),
          ),

          SizedBox(
            height: 10,
          ),
          CustomDivider(dividerText: "Your Workouts"),

          Expanded(
            flex: 1, // Adjust the flex factor as needed for balance
            child: FutureBuilder<List<Workout>>(
              future: fetchPersonalWorkouts(),
              builder: (context, personalWorkoutsSnapshot) {
                if (personalWorkoutsSnapshot.connectionState ==
                    ConnectionState.waiting) {
                  return Center(child: CircularProgressIndicator());
                } else if (personalWorkoutsSnapshot.hasError) {
                  return Center(
                      child: Text('Error: ${personalWorkoutsSnapshot.error}'));
                } else {
                  List<Workout> personalWorkouts =
                      personalWorkoutsSnapshot.data!;

                  return PageView.builder(
                    itemCount: personalWorkouts.length,
                    itemBuilder: (context, index) {
                      Workout workout = personalWorkouts[index];
                      return buildWorkoutCard(workout);
                    },
                  );
                }
              },
            ),
          ),
        ],
      ),
      
      floatingActionButton: ElevatedButton(
        onPressed: () {
          Navigator.of(context).push(
              MaterialPageRoute(builder: (context) => CreateWorkoutPage()));
        },
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.blue,
          padding: EdgeInsets.symmetric(horizontal: 8, vertical: 6),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
          elevation: 5,
        ),
        child: Row(
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

  Widget buildWorkoutCard(Workout workout) {
    // Calculate the difficulty level representation
    int maxDifficulty = 3; // Adjust the maximum difficulty level
    double fractionalPart =
        workout.difficultyLevel - workout.difficultyLevel.floor();
    List<Widget> difficultyCircles = List.generate(
      maxDifficulty,
      (index) {
        Color circleColor;
        if (index < workout.difficultyLevel.floor()) {
          circleColor = Colors.blue;
        } else if (index == workout.difficultyLevel.floor()) {
          circleColor =
              Color.lerp(Colors.blue, Colors.blue.shade200, fractionalPart)!;
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
            image: workout.coverPhotoUrl.isNotEmpty? NetworkImage(workout.coverPhotoUrl):const NetworkImage("https://i.ytimg.com/vi/gey73xiS8F4/maxresdefault.jpg"),
            fit: BoxFit.cover,
            colorFilter: ColorFilter.mode(
              Colors.black
                  .withOpacity(0.65), // Adjust opacity of the background image
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
          trailing: GestureDetector(
            onTap: () {
              if (workout.likedByUser) {
                unlikeWorkout(workout);
              } else {
                likeWorkout(workout);
              }
            },
            child: Icon(
              workout.likedByUser ? Icons.favorite : Icons.favorite_border,
              color: workout.likedByUser ? Colors.red : Colors.white,
            ),
          ),
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => ExercisePage(
                  exercises: workout.exercises,
                  workoutName: workout.name,
                  workoutId: workout.id,
                  programId: -1,
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  String getDifficultyText(double difficultyLevel) {
    if (difficultyLevel >= 0 && difficultyLevel < 1) {
      return 'Beginner';
    } else if (difficultyLevel >= 1 && difficultyLevel < 2) {
      return 'Intermediate';
    } else if (difficultyLevel >= 2 && difficultyLevel < 3) {
      return 'Expert';
    } else {
      return 'Unknown';
    }
  }
}
