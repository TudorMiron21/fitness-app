import 'dart:async';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class StartExercisePage extends StatefulWidget {
  final List<Exercise> exercises;
  final int exerciseIndex;
  final String workoutName;

  const StartExercisePage({
    Key? key,
    required this.exercises,
    required this.exerciseIndex,
    required this.workoutName,
  }) : super(key: key);

  @override
  State<StartExercisePage> createState() => _StartExercisePageState();
}

class _StartExercisePageState extends State<StartExercisePage> {
  Timer? countdownTimer;
  Duration myDuration = Duration(days: 5);
  bool isTimerRunning = false;
  Color pageBackgroundColor = Colors.white; // Initial background color

  void initState() {
    super.initState();
  }

  void toggleTimer() {
    if (countdownTimer == null || !countdownTimer!.isActive) {
      startTimer();
      setState(() {
        isTimerRunning = true;
        pageBackgroundColor = Colors.green; // Change background color when started
      });
    } else {
      stopTimer();
      setState(() {
        isTimerRunning = false;
        pageBackgroundColor = Colors.white; // Change background color when stopped
      });
    }
  }

  void startTimer() {
    countdownTimer =
        Timer.periodic(Duration(seconds: 1), (_) => setCountDown());
  }

  void stopTimer() {
    setState(() => countdownTimer!.cancel());
  }

  void resetTimer() {
    stopTimer();
    setState(() => myDuration = Duration(days: 5));
  }

  void setCountDown() {
    final reduceSecondsBy = 1;
    setState(() {
      final seconds = myDuration.inSeconds + reduceSecondsBy;
      if (seconds < 0) {
        countdownTimer!.cancel();
      } else {
        myDuration = Duration(seconds: seconds);
      }
    });
  }

  void goBack() {
    Navigator.pop(context);
  }

  void goToNextExercise() {
    int nextIndex = widget.exerciseIndex + 1;
    if (nextIndex < widget.exercises.length) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => StartExercisePage(
            exercises: widget.exercises,
            exerciseIndex: nextIndex,
            workoutName: widget.workoutName,
          ),
        ),
      );
    } else {
      // Navigate to ExercisePage or perform any other action when the workout finishes.
      Navigator.pop(context);
    }
  }

  Widget buildTimer() {
    String strDigits(int n) => n.toString().padLeft(2, '0');
    final days = strDigits(myDuration.inDays);
    final hours = strDigits(myDuration.inHours.remainder(24));
    final minutes = strDigits(myDuration.inMinutes.remainder(60));
    final seconds = strDigits(myDuration.inSeconds.remainder(60));

    return Column(
      children: [
        SizedBox(height: 30),
        Text(
          'Time Elapsed',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        SizedBox(height: 10),
        Text(
          '$hours:$minutes:$seconds',
          style: TextStyle(
            fontWeight: FontWeight.normal,
            color: Colors.black,
            fontSize: 48,
          ),
        ),
        SizedBox(height: 20),
        ElevatedButton(
          onPressed: toggleTimer,
          style: ElevatedButton.styleFrom(
            primary: isTimerRunning ? Colors.red : Colors.green,
          ),
          child: Text(
            isTimerRunning ? 'Stop' : 'Start',
            style: TextStyle(fontSize: 18),
          ),
        ),
      ],
    );
  }

  Widget buildExerciseDetails() {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 20, horizontal: 16),
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[200],
        borderRadius: BorderRadius.circular(10),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Exercise Details',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          SizedBox(height: 10),
          Text(
            'Name: ${widget.exercises[widget.exerciseIndex].name}',
            style: TextStyle(fontSize: 16),
          ),
          Text(
            'Description: ${widget.exercises[widget.exerciseIndex].description}',
            style: TextStyle(fontSize: 16),
          ),
          Text(
            'Difficulty: ${widget.exercises[widget.exerciseIndex].difficulty}',
            style: TextStyle(fontSize: 16),
          ),
          // Add more details as needed
        ],
      ),
    );
  }

  Widget buildNextExerciseButton() {
    return Align(
      alignment: Alignment.bottomRight,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: ElevatedButton(
          onPressed: goToNextExercise,
          child: Text(
            widget.exerciseIndex < widget.exercises.length - 1
                ? 'Next Exercise'
                : 'Finish Workout',
            style: TextStyle(
              fontSize: 18,
            ),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.exercises[widget.exerciseIndex].name),
        leading: IconButton(
          icon: Icon(Icons.arrow_back),
          onPressed: goBack,
        ),
      ),
      body: Container(
        color: pageBackgroundColor, // Set the background color
        child: Column(
          children: [
            buildTimer(),
            buildExerciseDetails(),
            Expanded(
              child: Container(),
            ),
            buildNextExerciseButton(),
          ],
        ),
      ),
    );
  }
}