import 'dart:async';
import 'dart:convert';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:flutter/material.dart';
import 'dart:ui';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:url_launcher/url_launcher.dart';

class SetSelectionDialogContent extends StatefulWidget {
  final int initialSelectedSets;
  final ValueChanged<int> onSelectedSetsChanged;

  const SetSelectionDialogContent({
    Key? key,
    required this.initialSelectedSets,
    required this.onSelectedSetsChanged,
  }) : super(key: key);

  @override
  _SetSelectionDialogContentState createState() =>
      _SetSelectionDialogContentState();
}

class _SetSelectionDialogContentState extends State<SetSelectionDialogContent> {
  late int selectedSets;

  @override
  void initState() {
    super.initState();
    selectedSets = widget.initialSelectedSets;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Text('Choose the number of sets for this exercise:'),
        Slider(
          value: selectedSets.toDouble(),
          min: 1,
          max: 10,
          divisions: 9,
          onChanged: (double value) {
            setState(() {
              selectedSets = value.round();
            });
            widget.onSelectedSetsChanged(selectedSets);
          },
        ),
        Text('$selectedSets sets'),
      ],
    );
  }
}

class SelectionDialogContent extends StatefulWidget {
  final double initialValue;
  final ValueChanged<double> onValueChanged;
  final String labelText;
  final double min;
  final double max;
  final int divisions;
  final String unit;

  const SelectionDialogContent({
    Key? key,
    required this.initialValue,
    required this.onValueChanged,
    required this.labelText,
    required this.min,
    required this.max,
    required this.divisions,
    required this.unit,
  }) : super(key: key);

  @override
  _SelectionDialogContentState createState() => _SelectionDialogContentState();
}

class _SelectionDialogContentState extends State<SelectionDialogContent> {
  late double currentValue;

  @override
  void initState() {
    super.initState();
    currentValue = widget.initialValue;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(widget.labelText),
        Slider(
          value: currentValue,
          min: widget.min,
          max: widget.max,
          divisions: widget.divisions,
          onChanged: (double value) {
            setState(() {
              currentValue = value;
            });
            widget.onValueChanged(currentValue);
          },
        ),
        Text('$currentValue ${widget.unit}'),
      ],
    );
  }
}

class StartExercisePage extends StatefulWidget {
  final List<Exercise> exercises;
  final int exerciseIndex;
  final int workoutId;
  final int noSets;
  final bool isFirstExercise;
  late int userHistoryModuleId;

  StartExercisePage(
      {Key? key,
      required this.exercises,
      required this.exerciseIndex,
      required this.workoutId,
      required this.noSets,
      required this.isFirstExercise,
      required this.userHistoryModuleId})
      : super(key: key);

  @override
  State<StartExercisePage> createState() => _StartExercisePageState();
}

class _StartExercisePageState extends State<StartExercisePage> {
  Timer? countdownTimer;
  Duration myDuration = Duration(days: 5);
  bool isTimerRunning = false;
  Color pageBackgroundColor = Colors.white; // Initial background color
  late int numberOfSets; // Default value
  late double numberOfReps;
  late double weight;


  int userHistoryModuleIdStore = 1;

  Future<void> saveModule() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.post(
      Uri.parse('http://192.168.0.229:8080/api/selfCoach/user/saveModule'),
      body: jsonEncode({
        "parentUserHistoryWorkoutId": widget.workoutId.toString(),
      }),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );
    if (response.statusCode == 200) //OK
    {
      print(
          'module has been added to history workout with id ${widget.workoutId}');

      widget.userHistoryModuleId = int.parse(response.body);
      print("this is the module id $widget.userHistoryModuleId");
    } else {
      throw Exception(
          'Failed to save module to history workout with id ${widget.workoutId}. Status code: ${response.statusCode}');
    }
  }

  Future<void> saveExerciseToModule(int userHistoryModuleId, int currNoSeconds,
      bool isDone, int noReps, int weight) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.put(
      Uri.parse(
          'http://192.168.0.229:8080/api/selfCoach/user/addExerciseToModule/$userHistoryModuleId'),
      body: jsonEncode({
        "exercise": widget.exercises[widget.exerciseIndex].id,
        "userHistoryModule": userHistoryModuleId.toString(),
        "currNoSeconds": currNoSeconds.toString(),
        "isDone": isDone.toString(),
        "noReps": noReps.toString(),
        "weight": weight.toString(),
      }),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode != 200) {
      throw Exception(
          'Failed to save exercise to history. Status code: ${response.statusCode}');
    }
  }

  Future<void> getExercisedDetails(String exerciseName) async {}

  @override
  void initState() {
    super.initState();
    numberOfSets = widget.noSets;
  }

  Future<void> _showSetSelectionDialog() async {
    await showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Select Number of Sets'),
          content: SetSelectionDialogContent(
            initialSelectedSets: numberOfSets,
            onSelectedSetsChanged: (int value) {
              setState(() {
                numberOfSets = value;
              });
            },
          ),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('OK'),
            ),
          ],
        );
      },
    );
  }

  void _showRepSelectionDialog() {
    showDialog<double>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Select Number of Reps'),
          content: SelectionDialogContent(
            initialValue: 10.0,
            onValueChanged: (value) {
              setState(() {
                numberOfReps = value;
              });
            },
            labelText: 'Choose the number of reps for this exercise:',
            min: 1.0,
            max: 20.0,
            divisions: 19,
            unit: 'reps',
          ),
          actions: <Widget>[
            TextButton(
              child: Text('Cancel'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
            TextButton(
              child: Text('OK'),
              onPressed: () {
                // Do something when OK is pressed
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

 

  void toggleTimer() {
    if (countdownTimer == null || !countdownTimer!.isActive) {
      startTimer();
      setState(() {
        isTimerRunning = true;
        pageBackgroundColor =
            Colors.green; // Change background color when started
      });
    } else {
      stopTimer();
      setState(() {
        isTimerRunning = false;
        pageBackgroundColor =
            Colors.white; // Change background color when stopped
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

  void goToNextModule(bool isFirstExercise) {
    int nextIndex = widget.exerciseIndex + 1;
    if (nextIndex < widget.exercises.length) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => StartExercisePage(
            exercises: widget.exercises,
            exerciseIndex: nextIndex,
            workoutId: widget.workoutId,
            noSets: 1,
            isFirstExercise: isFirstExercise,
            userHistoryModuleId: widget.userHistoryModuleId,
          ),
        ),
      );
    } else {
      // Navigate to ExercisePage or perform any other action when the workout finishes.
      Navigator.pop(context);
    }
  }

  void goToNextExercise(bool isFirstExercise) {
    setState(() {
      numberOfSets = numberOfSets - 1;
    });

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (context) => StartExercisePage(
          exercises: widget.exercises,
          exerciseIndex: widget.exerciseIndex,
          workoutId: widget.workoutId,
          noSets: numberOfSets,
          isFirstExercise: isFirstExercise,
          userHistoryModuleId: widget.userHistoryModuleId,
        ),
      ),
    );
  }

  void _startExercise(int numberOfSets) {
    // Add your logic to start the exercise with the selected number of sets
    // You can use the 'numberOfSets' variable in your implementation
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
            color: Colors.white.withOpacity(0.5),
          ),
        ),
        SizedBox(height: 10),
        Text(
          '$hours:$minutes:$seconds',
          style: TextStyle(
            fontWeight: FontWeight.normal,
            color: Colors.white.withOpacity(0.9),
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
    final exercise = widget.exercises[widget
        .exerciseIndex]; // Accessing the current exercise for convenience.

    return Container(
      margin: EdgeInsets.symmetric(vertical: 20, horizontal: 16),
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey.withOpacity(0.6),
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
            'Name: ${exercise.name}',
            style: TextStyle(fontSize: 16),
          ),
          SizedBox(height: 10),
          Text(
            'Description: ${exercise.description}',
            style: TextStyle(fontSize: 16),
          ),
          SizedBox(height: 10),
          Text(
            'Difficulty: ${exercise.difficulty}',
            style: TextStyle(fontSize: 16),
          ),

          Text(
            'Targeted Muscle Group: ${exercise.muscleGroup}',
            style: TextStyle(fontSize: 16),
          ),

          Text(
            'Equipment Necesary: ${exercise.equipment}',
            style: TextStyle(fontSize: 16),
          ),
          SizedBox(height: 10),
          Text(
            'Number of sets left: ${this.numberOfSets}',
            style: TextStyle(fontSize: 16),
          ),
          SizedBox(height: 20),
          // Check if the start image URL is not null or empty before displaying the image
          if (exercise.exerciseImageStartUrl != null &&
              exercise.exerciseImageStartUrl!.isNotEmpty)
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Start Position:',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 10),
                Center(
                  // Center the image
                  child: Image.network(
                    exercise.exerciseImageEndUrl!,
                    fit: BoxFit.cover,
                  ),
                ),
              ],
            ),
          SizedBox(height: 20),
          // Check if the end image URL is not null or empty before displaying the image
          if (exercise.exerciseImageEndUrl != null &&
              exercise.exerciseImageEndUrl!.isNotEmpty)
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'End Position:',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 10),
                Center(
                  // Center the image
                  child: Image.network(
                    exercise.exerciseImageEndUrl!,
                    fit: BoxFit.cover,
                  ),
                ),
              ],
            ),

          if (exercise.descriptionUrl.isNotEmpty)
                   Text(
            'For more details access this link:',
            style: TextStyle(fontSize: 16),
          ),
            InkWell(
              onTap: () async {
                final Uri url = Uri.parse(exercise.descriptionUrl);
                if (await canLaunchUrl(url)) {
                  await launchUrl(url);
                } else {
                  // Handle the error or show a message when the URL cannot be launched.
                  print('Could not launch $url');
                }
              },
              
              child: Text(
                '${exercise.descriptionUrl}',
                style: TextStyle(
                  fontSize: 16,
                  color: Colors
                      .blue, // Typically links are styled with the color blue
                  decoration: TextDecoration
                      .underline, // Underline to indicate it's a link
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget buildNextExerciseButton(int noSets) {
    return Align(
      alignment: Alignment.bottomRight,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: ElevatedButton(
          onPressed: () async {
            if (noSets == 1) {
              if (widget.isFirstExercise == true) {
                await saveModule();
              }
              await saveExerciseToModule(
                  widget.userHistoryModuleId, 0, false, numberOfReps.toInt(), 0);
              goToNextModule(true);
            } else {
              if (widget.isFirstExercise) {
                await saveModule();
              }
              await saveExerciseToModule(
                  widget.userHistoryModuleId, 0, false, numberOfReps.toInt(), 0);

              goToNextExercise(false);
            }
          },
          child: Text(
            noSets > 1
                ? 'Next Set'
                : (widget.exerciseIndex < widget.exercises.length - 1
                    ? 'Next Exercise'
                    : 'Finish Workout'),
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
      body: Stack(
        children: [
          Image.asset(
            'lib/images/fitness_background.png',
            fit: BoxFit.cover,
            height: double.infinity,
            width: double.infinity,
            alignment: Alignment.center,
          ),
          BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              color: Colors.black.withOpacity(0.5),
            ),
          ),
          Column(
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Column(
                    children: [
                      buildTimer(),
                      buildExerciseDetails(),

                      Container(
                        margin: EdgeInsets.all(10.0),
                        child: ElevatedButton(
                          onPressed: _showSetSelectionDialog,
                          child: Text('Set Number of Sets'),
                        ),
                      ),

                      if (widget.exercises[widget.exerciseIndex].hasNoReps)
                        Container(
                          margin:
                              EdgeInsets.all(10.0), // Add the desired margin
                          child: ElevatedButton(
                            onPressed: _showRepSelectionDialog,
                            child: Text('Set Number of Reps'),
                          ),
                        ),

                      if (widget.exercises[widget.exerciseIndex].hasWeight)
                        Container(
                          margin:
                              EdgeInsets.all(10.0), // Add the desired margin
                          child: ElevatedButton(
                            onPressed: _showRepSelectionDialog,
                            child: Text('Set Weight'),
                          ),
                        ),

                      // Additional spacing to ensure content scrolls above the button
                      SizedBox(height: 80),
                    ],
                  ),
                ),
              ),
              Align(
                alignment: Alignment.bottomCenter,
                child: buildNextExerciseButton(numberOfSets),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
