import 'package:flutter/material.dart';

class LastWorkoutStatsPage extends StatefulWidget {
  const LastWorkoutStatsPage({super.key});

  @override
  State<LastWorkoutStatsPage> createState() => _LastWorkoutStatsPageState();
}

class _LastWorkoutStatsPageState extends State<LastWorkoutStatsPage> {
  @override

  

  Widget build(BuildContext context) {
    // Replace this with your actual UserInformation UI
    return Container(
      color: Colors.blue, // A background color to ensure visibility
      alignment: Alignment.center,
      child: Text(
        'Last Workout Stats Page',
        style: TextStyle(fontSize: 24, color: Colors.white),
      ),
    );
  }
}