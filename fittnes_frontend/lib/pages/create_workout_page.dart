import 'dart:convert';

import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/models/exerciseSimplified.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:search_choices/search_choices.dart';
import 'package:http/http.dart' as http;

class CreateWorkoutPage extends StatefulWidget {
  const CreateWorkoutPage({super.key});

  @override
  State<CreateWorkoutPage> createState() => _CreateWorkoutPageState();
}

class _CreateWorkoutPageState extends State<CreateWorkoutPage> {
  final TextEditingController _workoutNameController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  String selectedExerciseName = '';
  List<DropdownMenuItem> exercises = [];
  List<ExerciseSimplified> selectedExercises = [];

  Future<List<DropdownMenuItem<ExerciseSimplified>>>
      getNonExclusiveExercises() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'https://www.fit-stack.online/api/selfCoach/user/getAllNonExclusiveExercises'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      List<ExerciseSimplified> exercises =
          data.map((item) => ExerciseSimplified.fromJson(item)).toList();

      List<DropdownMenuItem<ExerciseSimplified>> dropdownItems =
          exercises.map((exercise) {
        return DropdownMenuItem<ExerciseSimplified>(
          value: exercise,
          child: Text(exercise.name),
        );
      }).toList();

      return dropdownItems;
    } else {
      throw Exception(
          'Failed to load workouts. Status code: ${response.statusCode}');
    }
  }

  Future<void> createUserWorkout(String workoutName, String description,
      List<ExerciseSimplified> exercises) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    List<int> exerciseIds =
        exercises.map((exercise) => exercise.getIdExercise()).toList();

    String requestBody = jsonEncode({
      'workoutName': workoutName,
      'description': description,
      'exercisesIds': exerciseIds, 
    });

    final response = await http.post(
        Uri.parse('https://www.fit-stack.online/api/selfCoach/user/createUserWorkout'),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json'
        },
        body: requestBody);

    if (response.statusCode == 200) {
      print("personal workout added successfully");
    } else {
      throw Exception(
          "error with status code ${response.statusCode} on create personal workout");
    }
  }

  void loadExercises() async {
    try {
      this.exercises = await getNonExclusiveExercises();

      setState(() {});
    } catch (e) {
      // Handle the error, e.g., by showing an error message
      print('Error loading exercises: $e');
    }
  }

  @override
  void initState() {
    super.initState();
    loadExercises();
    // TODO: Fetch exercises from the database and add to the 'exercises' list
  }

  @override
  void dispose() {
    _workoutNameController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  InputDecoration _inputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(8.0),
      ),
      prefixIcon: Icon(
        label == 'Workout Name' ? Icons.fitness_center : Icons.description,
        color: Theme.of(context).primaryColor,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Workout'),
        centerTitle: true,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextFormField(
              controller: _workoutNameController,
              decoration: _inputDecoration('Workout Name'),
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: _inputDecoration('Description'),
              maxLines: 3,
            ),
            const SizedBox(height: 16),
            SearchChoices.single(
              items: exercises,
              value: selectedExerciseName,
              hint: "Select one exercise",
              searchHint: "Search Exercises",
              onChanged: (value) {
                setState(() {
                  if (value != null && !selectedExercises.contains(value)) {
                    selectedExercises.add(value);
                    selectedExerciseName = '';
                  }
                });
              },
              isExpanded: true,
              icon: const Icon(Icons.arrow_drop_down_circle),
              underline: Container(
                height: 2,
                color: Theme.of(context).primaryColor,
              ),
            ),
            const SizedBox(height: 24),
            Center(
              child: ElevatedButton(
                onPressed: () async {
                  await createUserWorkout(
                    this._workoutNameController.text,
                    this._descriptionController.text, 
                    this.selectedExercises);
                },
                style: ElevatedButton.styleFrom(
                  padding: EdgeInsets.symmetric(horizontal: 32, vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: const Text(
                  'Create Workout',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            const SizedBox(height: 16),
            if (selectedExercises.isNotEmpty)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8.0),
                child: Text(
                  'Selected Exercises:',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ListView.builder(
              shrinkWrap: true,
              physics:
                  NeverScrollableScrollPhysics(), // to disable ListView's scrolling
              itemCount: selectedExercises.length,
              itemBuilder: (context, index) {
                final exercise = selectedExercises[index];
                return Card(
                  elevation: 4,
                  margin: const EdgeInsets.symmetric(vertical: 10),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10)),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundImage:
                          NetworkImage(exercise.exerciseImageStartUrl),
                      radius: 30,
                      onBackgroundImageError: (exception, stackTrace) {
                        // Handle image load error, maybe set a placeholder image
                      },
                      backgroundColor: Colors.transparent,
                    ),
                    title: Text(
                      exercise.name,
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '${exercise.muscleGroupName} - ${exercise.difficultyName}',
                          style: TextStyle(
                            color: Colors.grey[600],
                          ),
                        ),
                        SizedBox(
                            height:
                                4), // Provides a small space between the text
                        Text(
                          'Category: ${exercise.categoryName}',
                          style: TextStyle(
                            color: Colors.grey[600],
                          ),
                        ),
                      ],
                    ),
                    trailing: IconButton(
                      icon: const Icon(Icons.delete_outline, color: Colors.red),
                      onPressed: () {
                        setState(() {
                          selectedExercises.removeAt(index);
                        });
                      },
                    ),
                    isThreeLine:
                        true, // Allow for three lines in the subtitle area
                    contentPadding:
                        EdgeInsets.symmetric(vertical: 10, horizontal: 16),
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
