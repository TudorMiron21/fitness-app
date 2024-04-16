import 'package:fittnes_frontend/models/DetailsUserHistoryExercise.dart';
import 'package:fittnes_frontend/models/DetailsUserHistoryModule.dart';
import 'package:fittnes_frontend/pages/exercise_details_page.dart';
import 'package:fittnes_frontend/pages/start_exercise_page.dart';
import 'package:fittnes_frontend/security/jwt_utils.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ExercisePage extends StatefulWidget {
  final List<Exercise> exercises;
  final String workoutName;
  final int workoutId;
  ExercisePage(
      {required this.exercises,
      required this.workoutName,
      required this.workoutId});

  @override
  State<ExercisePage> createState() => _ExercisePageState();
}

class _ExercisePageState extends State<ExercisePage> {
  late bool isWorkoutDone;
  late int userHistoryWorkoutId;
  bool isWorkoutStarted = false; // Initial flag for workout status
  late int exerciseIndex;
  late int noSets= 1;
  late bool isFirstExercise;

  late int lastUserHistoryModuleId;
  late int lastUserHistoryExerciseId;

  late int initialNoSets = 1;
  late int initialNoReps = 0;
  late double initialWeight = 0;
  late int initialNoSeconds = 0;

  @override
  void initState() {
    super.initState();
    _checkWorkoutStatus(); // Check workout status on initialization
  }

  Future<void> _checkWorkoutStatus() async {
    bool workoutStarted = await isWorkoutPresentInUserHistory(widget.workoutId);
    setState(() {
      isWorkoutStarted = workoutStarted;
    });
  }

  Future<DetailsUserHistoryModule> getUserHistoryModuleDetails(
      int userHistoryModuleId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getUserHistoryModuleDetails/$userHistoryModuleId'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      var decodedJson = json.decode(response.body);

      int noSets = decodedJson['noSets'];
      return DetailsUserHistoryModule(noSets: noSets);
    } else {
      throw Exception(
          'Failed to fetch user history module with id $userHistoryModuleId. Status code: ${response.statusCode}');
    }
  }

  Future<DetailsUserHistoryExercise> getUserHistoryExerciseDetails(
      int userHistoryExerciseId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    
    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getUserHistoryExerciseDetails/$userHistoryExerciseId'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      var decodedJson = json.decode(response.body);
      int currentNoSeconds = decodedJson['currentNoSeconds'];
      int noReps = decodedJson['noReps'];
      double weight = decodedJson['weight'];
      return DetailsUserHistoryExercise(currentNoSeconds: currentNoSeconds, noReps: noReps, weight: weight);
    } else {
      throw Exception(
          'Failed to fetch user history exercise with id $userHistoryExerciseId. Status code: ${response.statusCode}');
    }
  }

  Future<void> saveWorkoutToHistory(int workoutId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final workoutExistsInUserHistoryResponse = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/isWorkoutPresentInUserHistory/' +
              workoutId.toString() +
              '/' +
              JwtUtils.extractSubject(accessToken)),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (workoutExistsInUserHistoryResponse.statusCode == 200) {
      var decodedJson = json.decode(workoutExistsInUserHistoryResponse.body);
      userHistoryWorkoutId = decodedJson['userHistoryWorkoutId'];
      exerciseIndex = decodedJson['moduleIndex'];
      isFirstExercise = false;

      lastUserHistoryExerciseId = decodedJson['userHistoryExerciseId'];
      lastUserHistoryModuleId = decodedJson['userHistoryModuleId'];

      // noSets = decodedJson['noSetsLastModule'];

      DetailsUserHistoryExercise detailsUserHistoryExercise = await getUserHistoryExerciseDetails(lastUserHistoryExerciseId);
      DetailsUserHistoryModule detailsUserHistoryModule = await getUserHistoryModuleDetails(lastUserHistoryModuleId);

      initialNoSets = detailsUserHistoryModule.noSets;
      initialNoReps = detailsUserHistoryExercise.noReps;
      initialNoSeconds = detailsUserHistoryExercise.currentNoSeconds;
      initialWeight = detailsUserHistoryExercise.weight;

    } else {
      exerciseIndex = 0;
      noSets = 1;
      isFirstExercise = true;
      final response = await http.post(
        Uri.parse(
            'http://localhost:8080/api/selfCoach/user/startWorkout/$workoutId'),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        print("workout " + widget.workoutName + " added to history");
        userHistoryWorkoutId = int.parse(response.body);
      } else {
        print(
            'http://localhost:8080/api/selfCoach/user/startWorkout/$workoutId');
        throw Exception(
            'Failed to save workout to history. Status code: ${response.statusCode}');
      }
    }
  }

  Future<bool> isWorkoutPresentInUserHistory(int workoutId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final workoutExistsInUserHistoryResponse = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/isWorkoutPresentInUserHistory/' +
              workoutId.toString() +
              '/' +
              JwtUtils.extractSubject(accessToken)),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (workoutExistsInUserHistoryResponse.statusCode == 200) {
      return true;
    } else {
      return false;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor:
          Colors.blue.shade800, // Use any color from the Colors class
      appBar: AppBar(
        title: Text(this.widget.workoutName),
        elevation: 0, // Set elevation to 0 for a flatter look, if preferred
        backgroundColor: Colors.blue, // A more modern color
      ),
      body: ListView.builder(
        itemCount: widget.exercises.length,
        itemBuilder: (context, index) {
          Exercise exercise = widget.exercises[index];
          return Card(
            margin: EdgeInsets.all(12.0),
            elevation: 4,
            child: ListTile(
              tileColor: Colors.grey.withOpacity(0.3),
              contentPadding:
                  EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
              leading: InkWell(
                child: SizedBox(
                  width: 56.0,
                  height: 56.0,
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(8.0),
                    child: Image.network(
                      exercise.exerciseImageStartUrl,
                      fit: BoxFit.cover,
                      errorBuilder: (context, error, stackTrace) =>
                          Icon(Icons.error),
                      loadingBuilder: (BuildContext context, Widget child,
                          ImageChunkEvent? loadingProgress) {
                        if (loadingProgress == null) return child;
                        return Center(
                          child: CircularProgressIndicator(
                            value: loadingProgress.expectedTotalBytes != null
                                ? loadingProgress.cumulativeBytesLoaded /
                                    loadingProgress.expectedTotalBytes!
                                : null,
                          ),
                        );
                      },
                    ),
                  ),
                ),
              ),
              title: Text(
                exercise.name,
                style: TextStyle(
                  color: Colors.blue,
                  fontSize: 20.0,
                  fontWeight: FontWeight.bold,
                ),
              ),
              subtitle: Text(
                exercise.description,
                style: TextStyle(
                  color: Colors.grey.shade600,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              onTap: () {
                // You can still have the ListTile itself be tappable as well
                // and navigate to the ExerciseDetailPage if needed.
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () async {
          await saveWorkoutToHistory(widget.workoutId);
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => StartExercisePage(
                exercises: widget.exercises,
                exerciseIndex: exerciseIndex,
                workoutId: widget.workoutId,
                noSets: noSets,
                isFirstExercise: true,
                userHistoryModuleId: 0,
                userHistoryWorkoutId: userHistoryWorkoutId,
                initialNoSets: initialNoSets,
                initialNoReps: initialNoReps,
                initialWeight: initialWeight,
                initialNoSeconds: initialNoSeconds,
              ),
            ),
          );
        },
        icon: Icon(Icons.play_arrow),
        label: isWorkoutStarted
            ? Text('Continue Workout')
            : Text('Start Workout'), // Label for clarity
        backgroundColor: Colors.blue,
        elevation: 5.0,
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }
}
