import 'dart:convert';
import 'package:fittnes_frontend/models/program.dart';
import 'package:fittnes_frontend/pages/exercise_page.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/workout.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class ProgramPage extends StatefulWidget {
  final Program program;
  ProgramPage({required this.program});
  @override
  _ProgramPageState createState() => _ProgramPageState();
}

class _ProgramPageState extends State<ProgramPage> {
  Map<int, List<Workout>> weeks = {};
  int userHistoryProgramId = -1;
  bool isProgramStarted = false;
  int workoutIndex = 0;

  @override
  void initState() {
    super.initState();
    groupWorkoutsByWeek();
    initializeAsync();
  }

  Future<void> initializeAsync() async {
    await checkIfProgramIsStarted(widget.program.id);
    // Call setState to trigger a rebuild if the async operation affects the widget's state
    setState(() {});
  }

  Future<int> saveProgramToHistory(int programId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.post(
      Uri.parse(
          'https://www.fit-stack.online/api/selfCoach/payingUser/startProgram/$programId'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    var decodedJson = json.decode(response.body);

    workoutIndex = decodedJson['currentWorkoutIndex'];
    return decodedJson['id'];
  }

  Future<void> checkIfProgramIsStarted(int programId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'https://www.fit-stack.online/api/selfCoach/payingUser/isProgramStarted/$programId'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      isProgramStarted = true;
      var decodedJson = json.decode(response.body);
      workoutIndex = decodedJson['currentWorkoutIndex'];
      userHistoryProgramId = decodedJson['id'];
    } else if (response.statusCode == 202) {
      isProgramStarted = false;
    } else if (response.statusCode == 400) {
      throw Exception(
          'Failed to check if program is started. Status code: ${response.statusCode}');
    }
  }

  void groupWorkoutsByWeek() {
    for (int i = 1; i <= widget.program.durationInDays; i++) {
      int weekIndex = (i / 7).ceil();

      if (!weeks.containsKey(weekIndex)) {
        weeks[weekIndex] = [];
      }

      if (widget.program.indexedWorkouts.containsKey(i)) {
        weeks[weekIndex]!.add(widget.program.indexedWorkouts[i]!);
      } else {
        weeks[weekIndex]!.add(
          Workout(
            id: -1,
            description: "do what you please",
            difficultyLevel: 0.0,
            exercises: [],
            likedByUser: false,
            name: "Rest Day",
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.program.name),
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Upper section with program details and start/continue button
            Container(
              padding: EdgeInsets.all(16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  // Program details section
                  Row(
                    children: [
                      widget.program.coverPhotoUrl != null &&
                              widget.program.coverPhotoUrl!.isNotEmpty
                          ? ClipRRect(
                              borderRadius: BorderRadius.circular(8.0),
                              child: Image.network(
                                widget.program.coverPhotoUrl!,
                                width: 100,
                                height: 100,
                                fit: BoxFit.cover,
                              ),
                            )
                          : Icon(Icons.fitness_center, size: 100),
                      SizedBox(width: 16.0),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            widget.program.name,
                            style: TextStyle(
                              fontSize: 24.0,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          SizedBox(height: 8.0),
                          Text(
                              "Duration: ${widget.program.durationInDays} days"),
                        ],
                      ),
                    ],
                  ),
                  // Start/continue program button
                  ElevatedButton(
                    onPressed: () async {
                      if (userHistoryProgramId == -1) {
                        userHistoryProgramId =
                            await saveProgramToHistory(widget.program.id);
                      }

                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => ExercisePage(
                            exercises: weeks[(workoutIndex / 7).ceil()]![
                                    (workoutIndex % 7) - 1]
                                .exercises,
                            workoutName: weeks[(workoutIndex / 7).ceil()]![
                                    (workoutIndex % 7) - 1]
                                .name,
                            workoutId: weeks[(workoutIndex / 7).ceil()]![
                                    (workoutIndex % 7) - 1]
                                .id,
                            programId: userHistoryProgramId,
                          ),
                        ),
                      );
                    },
                    child: isProgramStarted
                        ? const Text("Continue Program")
                        : const Text("Start Program"),
                  ),
                ],
              ),
            ),
            Divider(), // Optional divider to separate from ListView
            // The ListView of workouts
            ListView.builder(
              shrinkWrap: true,
              physics: NeverScrollableScrollPhysics(), // Disable inner scroll
              itemCount: weeks.length,
              itemBuilder: (context, index) {
                int weekNumber = index + 1;
                return Card(
                  margin: EdgeInsets.all(8.0),
                  child: ExpansionTile(
                    title: Text(
                      "Week $weekNumber",
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 18.0,
                      ),
                    ),
                    children: weeks[weekNumber]!
                        .asMap() // Convert list to a map of index-value pairs
                        .entries // Get the key-value pairs from the map
                        .map((entry) {
                      int position =
                          entry.key; // Index position within the list
                      Workout workout =
                          entry.value; // Corresponding workout object

                      return ListTile(
                        leading: workout.coverPhotoUrl != null &&
                                workout.coverPhotoUrl!.isNotEmpty
                            ? ClipRRect(
                                borderRadius: BorderRadius.circular(4.0),
                                child: Image.network(
                                  workout.coverPhotoUrl!,
                                  width: 25,
                                  height: 25,
                                  fit: BoxFit.cover,
                                ),
                              )
                            : Icon(Icons.weekend), // Default icon for rest days
                        title: Text(
                          workout.name,
                          style: TextStyle(
                            fontSize: 16.0,
                            color: workout.name == "Rest Day"
                                ? Colors.grey // Grey for rest days
                                : (workoutIndex >
                                       (index * 7 ) + position + 1) // Adjusted logic
                                    ? Colors
                                        .green // Color when condition is met
                                    : Colors.black, // Default black color
                          ),
                        ),
                      );
                    }).toList(),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
