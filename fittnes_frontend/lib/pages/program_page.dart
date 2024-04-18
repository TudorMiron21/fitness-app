import 'package:fittnes_frontend/models/program.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/workout.dart';

class ProgramPage extends StatefulWidget {
  final Program program;
  ProgramPage({required this.program});
  @override
  _ProgramPageState createState() => _ProgramPageState();
}

class _ProgramPageState extends State<ProgramPage> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    // Group workouts by week
    Map<int, List<Workout>> weeks = {};

    // int noWeeks = (widget.program.durationInDays / 7).ceil();


    widget.program.indexedWorkouts.forEach((index, workout) {
      int weekIndex = (index / 7).ceil();
      weeks.putIfAbsent(weekIndex, () => []).add(workout);
    });


    return Scaffold(
      appBar: AppBar(
        title: Text(widget.program.name),
      ),
      body: ListView.builder(
        itemCount: weeks.length,
        itemBuilder: (context, index) {
          int weekNumber = index + 1;
          return ExpansionTile(
            title: Text("Week $weekNumber"),
            children: weeks[weekNumber]!
                .map((workout) => ListTile(
                      title: Text(workout.name),
                    ))
                .toList(),
          );
        },
      ),
    );
  }
}
