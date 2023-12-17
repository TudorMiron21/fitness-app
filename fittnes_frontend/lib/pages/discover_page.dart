import 'package:flutter/material.dart';

class DiscoverPage extends StatefulWidget {
  const DiscoverPage({super.key});

  @override
  State<DiscoverPage> createState() => _DiscoverPageState();
}

class _DiscoverPageState extends State<DiscoverPage> {
  int currentPageIndex = 1;

  // Example list of workouts for the first list
  final List<String> workoutsList1 = [
    'Workout 1',
    'Workout 2',
    'Workout 3',
    // Add more workouts
  ];

  // Example list of workouts for the second list
  final List<String> workoutsList2 = [
    'Workout A',
    'Workout B',
    'Workout C',
    // Add more workouts
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Discover Workouts'),
      ),
      body: Column(
        children: [
          Expanded(
            flex: 1, // Adjust the flex factor as needed for balance
            child: ListView.builder(
              itemCount: workoutsList1.length,
              itemBuilder: (context, index) {
                return ListTile(
                  title: Text(workoutsList1[index]),
                  // Add other properties like leading, subtitle, onTap, etc.
                );
              },
            ),
          ),
          Expanded(
            flex: 1, // Adjust the flex factor as needed for balance
            child: ListView.builder(
              itemCount: workoutsList2.length,
              itemBuilder: (context, index) {
                return ListTile(
                  title: Text(workoutsList2[index]),
                  // Add other properties like leading, subtitle, onTap, etc.
                );
              },
            ),
          ),
        ],
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
}