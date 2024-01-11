import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter/material.dart';
import 'package:search_choices/search_choices.dart';

class CreateWorkoutPage extends StatefulWidget {
  const CreateWorkoutPage({super.key});

  @override
  State<CreateWorkoutPage> createState() => _CreateWorkoutPageState();
}

class _CreateWorkoutPageState extends State<CreateWorkoutPage> {
  final TextEditingController _workoutNameController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  String selectedExercise = '';
  List<DropdownMenuItem> exercises = []; // This should be filled with your data



  @override
  void initState() {
    super.initState();
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
              value: selectedExercise,
              hint: "Select one exercise",
              searchHint: "Search Exercises",
              onChanged: (value) {
                setState(() {
                  selectedExercise = value;
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
                onPressed: () {
                  // TODO: Implement the logic to create the workout
                },
                style: ElevatedButton.styleFrom(
                  primary: Theme.of(context).primaryColor,
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
          ],
        ),
      ),
    );
  }
}