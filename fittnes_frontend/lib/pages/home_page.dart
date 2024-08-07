import 'dart:async';
import 'dart:math';

import 'package:fittnes_frontend/components/program_tile.dart';
import 'package:fittnes_frontend/models/user.dart';
import 'package:fittnes_frontend/models/program.dart';
import 'package:fittnes_frontend/pages/coach_profile.dart';
import 'package:fittnes_frontend/pages/follow_new_coaches.dart';
import 'package:fittnes_frontend/security/jwt_utils.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
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
  bool areWorkoutsLoading = true;
  String fetchWorkoutsErrorMeassage = '';
  List<Workout> mostLikedWorkouts = [];

  bool areCoachesLoading = true;
  String fetchCoachesErrorMeassage = '';
  List<User> followingCoaches = [];

  bool areProgramsLoading = true;
  String fetchProgramsErrorMeassage = '';
  List<Program> programs = [];

  late String accessToken = '';

  @override
  void initState() {
    super.initState();
    _fetchAccessToken();
    _fetchMostLikedWorkouts();
    _fetchFollowingCoaches();
    _fetchPrograms();
  }

  Future<void> _fetchMostLikedWorkouts() async {
    try {
      List<Workout> data = await fetchirstSixMostLikedWorkouts();
      setState(() {
        mostLikedWorkouts = data;
        areWorkoutsLoading = false;
      });
    } catch (error) {
      setState(() {
        fetchWorkoutsErrorMeassage = 'Error: $error';
        areWorkoutsLoading = false;
      });
    }
  }

  Future<void> _fetchFollowingCoaches() async {
    try {
      List<User> data = await fetchFollowingCoaches();
      setState(() {
        followingCoaches = data;
        areCoachesLoading = false;
      });
    } catch (error) {
      setState(() {
        fetchCoachesErrorMeassage = 'Error: $error';
        areCoachesLoading = false;
      });
    }
  }

  Future<void> _fetchPrograms() async {
    try {
      List<Program> data = await fetchPrograms();
      setState(() {
        programs = data;
        areProgramsLoading = false;
      });
    } catch (error) {
      setState(() {
        fetchProgramsErrorMeassage = 'Error: $error';
        areProgramsLoading = false;
      });
    }
  }

  Future<void> _fetchAccessToken() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    accessToken = await storage.read(key: 'accessToken') ?? '';
    if (accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }
    setState(() {});
  }

  Future<List<User>> fetchFollowingCoaches() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'https://www.fit-stack.online/api/selfCoach/payingUser/getFollowingCoaches'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      return data
          .map((json) => User(
                id: json['id'] ??
                    0, // Provide a default value for id if it's null
                email: json['email'] ??
                    '', // Provide a default value for email if it's null
                profilePictureUrl: json['profilePictureUrl'] as String? ??
                    "https://i.stack.imgur.com/l60Hf.png",
                firstName: json['firstName'] ??
                    '', // Provide a default value for firstName if it's null
                lastName: json['lastName'] ??
                    '', // Provide a default value for lastName if it's null
              ))
          .toList();
    } else {
      throw Exception(
          'Failed to load following coaches. Status code: ${response.statusCode}');
    }
  }

  Future<List<Program>> fetchPrograms() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse('https://www.fit-stack.online/api/selfCoach/user/getAllPrograms'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> jsonResponse = json.decode(response.body);
      return jsonResponse.map((programJson) {
        Map<int, Workout> indexedWorkouts = {};
        var workoutList = programJson['workoutProgramSet'] as List? ??
            []; // Handling potential null
        for (var workoutJson in workoutList) {
          var exercisesList = workoutJson['workout']['exercises'] as List? ??
              []; // Handling potential null
          var workout = Workout(
            id: workoutJson['workout']['id'],
            name: workoutJson['workout']['name'] ?? "",
            description: workoutJson['workout']['description'] ?? "",
            coverPhotoUrl: workoutJson['workout']['coverPhotoUrl'] ?? '',
            difficultyLevel: workoutJson['workout']['difficultyLevel'] ?? 0.0,
            exercises: exercisesList
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
                      difficulty:
                          exerciseJson['difficulty']['dificultyLevel'] ?? 0.0,
                      category: exerciseJson['category']['name'] ?? "",
                      exerciseExclusive:
                          exerciseJson['exerciseExclusive'] ?? false,
                      equipment: exerciseJson['equipment']['name'] ?? "",
                      muscleGroup: exerciseJson['muscleGroup']['name'] ?? "",
                      rating: exerciseJson['rating'] ?? 0.0,
                      hasNoReps: exerciseJson['hasNoReps'] ?? false,
                      hasWeight: exerciseJson['hasWeight'] ?? false,
                    ))
                .toList(),
            likedByUser: workoutJson['likedByUser'] ?? false,
          );
          indexedWorkouts[workoutJson['workoutIndex']] = workout;
        }

        return Program(
          id: programJson['id'],
          name: programJson['name'] ?? "",
          description: programJson['description'] ?? "",
          durationInDays: programJson['durationInDays'] ?? 0,
          coverPhotoUrl: programJson['coverPhotoUrl'] ?? "",
          indexedWorkouts: indexedWorkouts,
        );
      }).toList();
    } else {
      throw Exception(
          'Failed to load programs. Status code: ${response.statusCode}');
    }
  }

  Future<List<Workout>> fetchirstSixMostLikedWorkouts() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'https://www.fit-stack.online/api/selfCoach/user/workouts/getFirstSixMostLikedWorkouts'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      // Map the API response to a list of Workout objects
      return data.map((json) {
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
                  difficulty:
                      exerciseJson['difficulty']['dificultyLevel'] ?? 0.0,
                  category: exerciseJson['category']['name'] ?? "",
                  exerciseExclusive: exerciseJson['exerciseExclusive'] ?? false,
                  equipment: exerciseJson['equipment']['name'] ?? "",
                  muscleGroup: exerciseJson['muscleGroup']['name'] ?? "",
                  rating: exerciseJson['rating'] ?? 0.0,
                  hasNoReps: exerciseJson['hasNoReps'] ?? false,
                  hasWeight: exerciseJson['hasWeight'] ?? false,
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
          'https://www.fit-stack.online/api/selfCoach/user/workouts/getTopWorkoutsForDifficultyLevel/' +
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
                  rating: exerciseJson['rating'] ?? 0.0,
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
      Uri.parse('https://www.fit-stack.online/api/selfCoach/user/likeWorkout/' +
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
      Uri.parse('https://www.fit-stack.online/api/selfCoach/user/unlikeWorkout/' +
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
      backgroundColor: Colors.blue.shade800,
      body: Column(
        children: [
          Visibility(
            visible: JwtUtils.isPayingUser(accessToken),
            child: Expanded(
              flex: 1,
              child: areCoachesLoading
                  ? Center(child: CircularProgressIndicator())
                  : fetchCoachesErrorMeassage.isNotEmpty
                      ? Center(child: Text(fetchCoachesErrorMeassage))
                      : buildFollowingCoachesPageView(),
            ),
          ),
          Expanded(
            // height: constraints.maxHeight,
            flex: 1,
            child: areProgramsLoading
                ? Center(child: CircularProgressIndicator())
                : fetchProgramsErrorMeassage.isNotEmpty
                    ? Center(child: Text(fetchProgramsErrorMeassage))
                    : buildProgramPageView(),
          ),
          Expanded(
            flex: 1,
            child: areWorkoutsLoading
                ? Center(child: CircularProgressIndicator())
                : fetchWorkoutsErrorMeassage.isNotEmpty
                    ? Center(child: Text(fetchWorkoutsErrorMeassage))
                    : PageView.builder(
                        itemCount: mostLikedWorkouts.length + 1,
                        itemBuilder: (context, index) {
                          if (index == 0) {
                            return buildPageBackground(
                              'lib/images/homepage_background3.png',
                              'Top Workouts',
                            );
                          }
                          Workout workout = mostLikedWorkouts[index - 1];

                          return buildWorkoutCard(workout);
                        },
                      ),
          ),
          // Visibility(
          //   visible: JwtUtils.isUser(accessToken),
          //   child: Expanded(
          //     flex: 1, //easy workouts
          //     child: FutureBuilder<List<Workout>>(
          //       future:
          //           fetchirstSixMostLikedWorkoutsFromDifficultyLevel(0.0, 1.0),
          //       builder: (context, mostLikedWorkoutsSnapshot) {
          //         if (mostLikedWorkoutsSnapshot.connectionState ==
          //             ConnectionState.waiting) {
          //           return Center(child: CircularProgressIndicator());
          //         } else if (mostLikedWorkoutsSnapshot.hasError) {
          //           return Center(
          //               child:
          //                   Text('Error: ${mostLikedWorkoutsSnapshot.error}'));
          //         } else {
          //           List<Workout> mostLikedWorkouts =
          //               mostLikedWorkoutsSnapshot.data!;

          //           return PageView.builder(
          //             itemCount: mostLikedWorkouts.length + 1,
          //             itemBuilder: (context, index) {
          //               if (index == 0) {
          //                 return buildPageBackground(
          //                     'lib/images/background_top_workouts.jpg',
          //                     'Top Beginner Workouts');
          //               }
          //               Workout workout = mostLikedWorkouts[index - 1];

          //               return buildWorkoutCard(workout);

          //               // Remaining code remains the same...
          //             },
          //           );
          //         }
          //       },
          //     ),
          //   ),
          // ),
          // Visibility(
          //   visible: JwtUtils.isUser(accessToken),
          //   child: Expanded(
          //     flex: 1, //easy workouts
          //     child: FutureBuilder<List<Workout>>(
          //       future:
          //           fetchirstSixMostLikedWorkoutsFromDifficultyLevel(1.0, 2.0),
          //       builder: (context, mostLikedWorkoutsSnapshot) {
          //         if (mostLikedWorkoutsSnapshot.connectionState ==
          //             ConnectionState.waiting) {
          //           return Center(child: CircularProgressIndicator());
          //         } else if (mostLikedWorkoutsSnapshot.hasError) {
          //           return Center(
          //               child:
          //                   Text('Error: ${mostLikedWorkoutsSnapshot.error}'));
          //         } else {
          //           List<Workout> mostLikedWorkouts =
          //               mostLikedWorkoutsSnapshot.data!;

          //           return PageView.builder(
          //             itemCount: mostLikedWorkouts.length + 1,
          //             itemBuilder: (context, index) {
          //               if (index == 0) {
          //                 return buildPageBackground(
          //                     'lib/images/background_top_workouts.jpg',
          //                     'Top Intermediate Workouts');
          //               }

          //               Workout workout = mostLikedWorkouts[index - 1];

          //               return buildWorkoutCard(workout);

          //               // Remaining code remains the same...
          //             },
          //           );
          //         }
          //       },
          //     ),
          //   ),
          // ),
          // Visibility(
          //   visible: JwtUtils.isUser(accessToken),
          //   child: Expanded(
          //     flex: 1, //easy workouts
          //     child: FutureBuilder<List<Workout>>(
          //       future:
          //           fetchirstSixMostLikedWorkoutsFromDifficultyLevel(2.0, 3.0),
          //       builder: (context, mostLikedWorkoutsSnapshot) {
          //         if (mostLikedWorkoutsSnapshot.connectionState ==
          //             ConnectionState.waiting) {
          //           return Center(child: CircularProgressIndicator());
          //         } else if (mostLikedWorkoutsSnapshot.hasError) {
          //           return Center(
          //               child:
          //                   Text('Error: ${mostLikedWorkoutsSnapshot.error}'));
          //         } else {
          //           List<Workout> mostLikedWorkouts =
          //               mostLikedWorkoutsSnapshot.data!;

          //           return PageView.builder(
          //             itemCount: mostLikedWorkouts.length + 1,
          //             itemBuilder: (context, index) {
          //               if (index == 0) {
          //                 return buildPageBackground(
          //                     'lib/images/background_top_workouts.jpg',
          //                     'Top Expert Workouts');
          //               }
          //               Workout workout = mostLikedWorkouts[index - 1];

          //               buildWorkoutCard(workout);

          //               // Remaining code remains the same...
          //             },
          //           );
          //         }
          //       },
          //     ),
          //   ),
          // ),
        ],
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
                  programId: -1,
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  Widget buildCoachCard(User coach) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: <Widget>[
        const SizedBox(height: 20),
        InkWell(
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => CoachProfile(
                  coach: coach,
                ),
              ),
            );
          },
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: CircleAvatar(
              radius: 50,
              backgroundImage: NetworkImage(coach.profilePictureUrl),
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(bottom: 8.0),
          child: Text(
            '${coach.firstName} ${coach.lastName}',
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
        ),
        Text(
          coach.email,
          style: TextStyle(fontSize: 12),
        ),
      ],
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

  Widget buildFollowingCoachesPageView() {
    int pageCount = (followingCoaches.length / 3).ceil();
    return PageView.builder(
      itemCount: pageCount + 2,
      itemBuilder: (context, index) {
        if (index == 0) {
          return buildPageBackground(
            'lib/images/homepage_background1.png',
            'Followed Coaches',
          );
        } else if (index == pageCount + 1) {
          // Button page
          return Center(
            child: ElevatedButton(
              onPressed: () {
                // Implement the functionality to add more coaches
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => FollowNewCoaches()),
                );
              },
              child: Text("Follow More Coaches"),
            ),
          );
        }
        int startIndex = (index - 1) * 3;
        int endIndex = min(startIndex + 3, followingCoaches.length);
        return Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: List.generate(endIndex - startIndex, (i) {
            User coach = followingCoaches[startIndex + i];
            return Expanded(
              child: buildCoachCard(coach),
            );
          }),
        );
      },
    );
  }

  Widget buildProgramPageView() {
    return PageView.builder(
      itemCount: programs.length + 1,
      itemBuilder: (context, index) {
        if (index == 0) {
          return buildPageBackground(
            'lib/images/homepage_background2.png',
            'Top Most Liked Programs',
          );
        }

        Program program = programs[index - 1];
        return ProgramTile(program: program);
      },
    );
  }
}
