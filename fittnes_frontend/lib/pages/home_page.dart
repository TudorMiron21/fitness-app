import 'dart:async';

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
  List<Workout> mostLikedWorkouts = [];

  Future<void> fetchirstSixMostLikedWorkouts() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/workouts/getFirstSixMostLikedWorkouts'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      // Map the API response to a list of Workout objects
      mostLikedWorkouts = data.map((json) {
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
  }

  Future<List<Workout>> fetchirstSixMostLikedWorkoutsFromDifficultyLevel(
      double lowerLimit, double upperLimit) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/workouts/getTopWorkoutsForDifficultyLevel/' +
              lowerLimit.toString() +
              '/' +
              upperLimit.toString()),
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

      return workouts;
    } else {
      throw Exception(
          'Failed to load workouts. Status code: ${response.statusCode}');
    }
  }

  Future<List<List<Workout>>> fetchTopWorkoutsByDifficulty() async {
    List<Future<List<Workout>>> futures = [];

    // Fetch top 6 workouts for each difficulty level
    for (double difficulty = 0; difficulty < 3; difficulty++) {
      futures.add(fetchirstSixMostLikedWorkoutsFromDifficultyLevel(
          difficulty, difficulty + 1));
    }

    return Future.wait(futures);
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
      body: SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(
              height: 300,
              child: FutureBuilder<void>(
                future: fetchirstSixMostLikedWorkouts(),
                builder: (context, mostLikedWorkoutsSnapshot) {
                  if (mostLikedWorkoutsSnapshot.connectionState ==
                      ConnectionState.waiting) {
                    return Center(child: CircularProgressIndicator());
                  } else if (mostLikedWorkoutsSnapshot.hasError) {
                    return Center(
                        child:
                            Text('Error: ${mostLikedWorkoutsSnapshot.error}'));
                  } else {
                    return PageView.builder(
                      itemCount: mostLikedWorkouts.length + 1,
                      itemBuilder: (context, index) {
                        if (index == 0) {
                          return buildPageBackground(
                              'lib/images/background_top_workouts.jpg',
                              'Top Most Liked Workouts');
                        }

                        Workout workout = mostLikedWorkouts[index - 1];

                        return buildWorkoutCard(workout);

                        // Remaining code remains the same...
                      },
                    );
                  }
                },
              ),
            ),
            SizedBox(
              height: 200,
              //easy workouts
              child: FutureBuilder<List<Workout>>(
                future:
                    fetchirstSixMostLikedWorkoutsFromDifficultyLevel(0.0, 1.0),
                builder: (context, mostLikedWorkoutsSnapshot) {
                  if (mostLikedWorkoutsSnapshot.connectionState ==
                      ConnectionState.waiting) {
                    return Center(child: CircularProgressIndicator());
                  } else if (mostLikedWorkoutsSnapshot.hasError) {
                    return Center(
                        child:
                            Text('Error: ${mostLikedWorkoutsSnapshot.error}'));
                  } else {
                    List<Workout> mostLikedWorkouts =
                        mostLikedWorkoutsSnapshot.data!;

                    return PageView.builder(
                      itemCount: mostLikedWorkouts.length + 1,
                      itemBuilder: (context, index) {
                        if (index == 0) {
                          return buildPageBackground(
                              'lib/images/background_top_workouts.jpg',
                              'Top Beginner Workouts');
                        }

                        Workout workout = mostLikedWorkouts[index - 1];

                        return buildWorkoutCard(workout);

                        // Remaining code remains the same...
                      },
                    );
                  }
                },
              ),
            ),
            SizedBox(
              height: 200,
              child: FutureBuilder<List<Workout>>(
                future:
                    fetchirstSixMostLikedWorkoutsFromDifficultyLevel(1.0, 2.0),
                builder: (context, mostLikedWorkoutsSnapshot) {
                  if (mostLikedWorkoutsSnapshot.connectionState ==
                      ConnectionState.waiting) {
                    return Center(child: CircularProgressIndicator());
                  } else if (mostLikedWorkoutsSnapshot.hasError) {
                    return Center(
                        child:
                            Text('Error: ${mostLikedWorkoutsSnapshot.error}'));
                  } else {
                    List<Workout> mostLikedWorkouts =
                        mostLikedWorkoutsSnapshot.data!;

                    return PageView.builder(
                      itemCount: mostLikedWorkouts.length + 1,
                      itemBuilder: (context, index) {
                        if (index == 0) {
                          return buildPageBackground(
                              'lib/images/background_top_workouts.jpg',
                              'Top Intermediate Workouts');
                        }

                        Workout workout = mostLikedWorkouts[index - 1];

                        return buildWorkoutCard(workout);

                        // Remaining code remains the same...
                      },
                    );
                  }
                },
              ),
            ),
            SizedBox(
              height: 200,
              child: FutureBuilder<List<Workout>>(
                future:
                    fetchirstSixMostLikedWorkoutsFromDifficultyLevel(2.0, 3.0),
                builder: (context, mostLikedWorkoutsSnapshot) {
                  if (mostLikedWorkoutsSnapshot.connectionState ==
                      ConnectionState.waiting) {
                    return Center(child: CircularProgressIndicator());
                  } else if (mostLikedWorkoutsSnapshot.hasError) {
                    return Center(
                        child:
                            Text('Error: ${mostLikedWorkoutsSnapshot.error}'));
                  } else {
                    List<Workout> mostLikedWorkouts =
                        mostLikedWorkoutsSnapshot.data!;

                    return PageView.builder(
                      itemCount: mostLikedWorkouts.length + 1,
                      itemBuilder: (context, index) {
                        if (index == 0) {
                          return buildPageBackground(
                              'lib/images/background_top_workouts.jpg',
                              'Top Expert Workouts');
                        }
                        Workout workout = mostLikedWorkouts[index - 1];

                        buildWorkoutCard(workout);

                        // Remaining code remains the same...
                      },
                    );
                  }
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget buildPageBackground(String imgPath, String text) {
    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: 25.0,
        vertical: 10,
      ),
      decoration: BoxDecoration(
        image: DecorationImage(
          image: AssetImage(imgPath), // Set your background image here
          fit: BoxFit.cover,
          colorFilter: ColorFilter.mode(
            Colors.black
                .withOpacity(0.6), // Adjust the opacity for a darker filter
            BlendMode.darken,
          ),
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            text,
            style: TextStyle(
              color: Colors.white,
              fontSize: 18, // Adjust the font size as needed
              fontWeight: FontWeight.bold,
            ),
          ),
          SizedBox(width: 10), // Adjust the spacing between text and arrow
          Icon(
            Icons.arrow_forward,
            color: Colors.white,
          ),
        ],
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
            image: NetworkImage(workout.coverPhotoUrl),
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
