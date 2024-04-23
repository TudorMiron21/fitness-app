import 'dart:convert';

import 'package:fittnes_frontend/components/custom_divider.dart';
import 'package:fittnes_frontend/components/program_tile.dart';
import 'package:fittnes_frontend/models/coach.dart';
import 'package:fittnes_frontend/models/coach_details.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/models/program.dart';
import 'package:fittnes_frontend/models/workout.dart';
import 'package:fittnes_frontend/pages/exercise_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class CoachProfile extends StatefulWidget {
  final Coach coach;
  const CoachProfile({super.key, required this.coach});
  @override
  State<CoachProfile> createState() => _CoachProfileState();
}

class _CoachProfileState extends State<CoachProfile> {
  bool isCoachFollowedByUser = false;
  late int numberOfFollowers;
  CoachDetails coachDetails = CoachDetails(
      numberOfSubscribers: 0,
      numberOfExercises: 0,
      numberOfWorkouts: 0,
      numberOfPrograms: 0,
      numberOfChallenges: 0);

  List<Workout> coachWorkouts = [];
  List<Program> coachPrograms = [];

  @override
  void initState() {
    super.initState();
    initIsCoachFollowedByUser();
    initCoachDetails();
    initCoachWorkouts();
    initCoachPrograms();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
  }

  Future<void> initCoachWorkouts() async {
    try {
      List<Workout> fetchedWorkouts = await fetchCoachWorkouts(widget.coach.id);
      setState(() {
        coachWorkouts = fetchedWorkouts;
      });
    } catch (e) {
      print('Error fetching workouts: $e');
    }
  }

  Future<List<Workout>> fetchCoachWorkouts(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/payingUser/getCoachWorkouts/$coachId'),
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
            .toList();

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

  Future<void> initCoachPrograms() async {
    try {
      List<Program> fetchedPrograms = await fetchCoachPrograms(widget.coach.id);
      setState(() {
        coachPrograms = fetchedPrograms;
      });
    } catch (e) {
      print('Error fetching workouts: $e');
    }
  }

  Future<List<Program>> fetchCoachPrograms(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/payingUser/getCoachPrograms/$coachId'),
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

  Future<void> initIsCoachFollowedByUser() async {
    try {
      bool fetchedResponse =
          await checkIfCoachIsFollowedByUser(widget.coach.id);
      setState(() {
        isCoachFollowedByUser = fetchedResponse;
      });
    } catch (e) {
      print('Error fetching response: $e');
    }
  }

  Future<bool> checkIfCoachIsFollowedByUser(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    try {
      final response = await http.get(
        Uri.parse(
            'http://localhost:8080/api/selfCoach/payingUser/isCoachFollowedByUser/$coachId'),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data is bool) {
          return data;
        } else {
          throw Exception('Unexpected data type received from the server.');
        }
      } else {
        throw Exception(
            'Failed to load following status. Status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to check if coach is followed by user: $e');
    }
  }

  Future<void> initCoachDetails() async {
    try {
      CoachDetails fetchedCoachDetails = await getCoachDetails(widget.coach.id);
      setState(() {
        coachDetails = fetchedCoachDetails;
      });
    } catch (e) {
      print('Error fetching response: $e');
    }
  }

  Future<CoachDetails> getCoachDetails(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    try {
      final response = await http.get(
        Uri.parse(
            'http://localhost:8080/api/selfCoach/payingUser/getCoachDetails/$coachId'),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return CoachDetails.fromJson(data);
      } else {
        throw Exception(
            'Failed to load coach details. Status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to fetch coach details $e');
    }
  }

  Future<void> toggleFollowCoach(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    setState(() {
      isCoachFollowedByUser = !isCoachFollowedByUser;
      if (isCoachFollowedByUser == true) {
        coachDetails.numberOfSubscribers += 1;
      } else {
        coachDetails.numberOfSubscribers -= 1;
      }
    });

    try {
      final response = await http.put(
        Uri.parse(
            'http://localhost:8080/api/selfCoach/payingUser/toggleFollowCoach/$coachId'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        print(response.body);
      } else {
        setState(() {
          isCoachFollowedByUser = !isCoachFollowedByUser;
          if (isCoachFollowedByUser == true) {
            coachDetails.numberOfSubscribers += 1;
          } else {
            coachDetails.numberOfSubscribers -= 1;
          }
        });
        throw Exception(
            'Failed to load following status. Status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to check if coach is followed by user: $e');
    }
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
      appBar: AppBar(
        title: Center(
          child: RichText(
            text: TextSpan(
              text: '${widget.coach.firstName} ${widget.coach.lastName}',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.black, // This should match AppBar's brightness
              ),
              children: <TextSpan>[
                TextSpan(
                  text: ' (${widget.coach.email})',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.normal,
                    color:
                        Colors.black.withOpacity(0.7), // Lighter and not bold
                  ),
                ),
              ],
            ),
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  CircleAvatar(
                    radius: 60,
                    backgroundImage:
                        NetworkImage(widget.coach.profilePictureUrl),
                    backgroundColor: Colors.transparent,
                  ),
                  SizedBox(width: 20),
                  Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Row(
                        children: <Widget>[
                          Icon(Icons.sports_gymnastics, color: Colors.black),
                          SizedBox(
                            width: 5,
                          ),
                          Text('Exercises: ${coachDetails.numberOfExercises}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.fitness_center, color: Colors.blue),
                          SizedBox(
                            width: 5,
                          ),
                          Text('Workouts: ${coachDetails.numberOfWorkouts}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.list_alt, color: Colors.green),
                          SizedBox(
                            width: 5,
                          ),
                          Text('Programs: ${coachDetails.numberOfPrograms}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.access_time, color: Colors.red),
                          SizedBox(
                            width: 5,
                          ),
                          Text('Challenges: ${coachDetails.numberOfChallenges}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.person, color: Colors.purple),
                          SizedBox(
                            width: 5,
                          ),
                          Text(
                              'Subscribers: ${coachDetails.numberOfSubscribers}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                    ],
                  ),
                ],
              ),
              SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  ElevatedButton(
                    onPressed: () async {
                      await toggleFollowCoach(widget.coach.id);
                    },
                    child: Text(isCoachFollowedByUser ? 'Unfollow' : 'Follow'),
                  ),
                  SizedBox(width: 20),
                  isCoachFollowedByUser
                      ? ElevatedButton(
                          onPressed: () {},
                          child: const Text("Send Message"),
                        )
                      : SizedBox(),
                ],
              ),
              SizedBox(height: 20),
              CustomDivider(dividerText: "Coach's Workouts"),
              SizedBox(
                height: 200,
                child: coachWorkouts.isNotEmpty
                    ? PageView.builder(
                        itemCount: coachWorkouts.length,
                        itemBuilder: (context, index) {
                          Workout workout = coachWorkouts[index];
                          return buildWorkoutCard(workout);
                        },
                      )
                    : Center(child: CircularProgressIndicator()),
              ),
              SizedBox(height: 20),
              CustomDivider(dividerText: "Coach's Programs"),
              SizedBox(
                height: 200,
                child: coachPrograms.isNotEmpty
                    ? PageView.builder(
                        itemCount: coachPrograms.length,
                        itemBuilder: (context, index) {
                          Program program = coachPrograms[index];
                          return ProgramTile(program: program);
                        },
                      )
                    : Center(child: CircularProgressIndicator()),
              ),
            ],
          ),
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
