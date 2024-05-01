import 'dart:async';
import 'dart:convert';
import 'package:fittnes_frontend/models/LastEntryUserHistoryExercise.dart';
import 'package:fittnes_frontend/models/UpdateExerciseToModule.dart';

import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/models/workout_rewards.dart';
import 'package:fittnes_frontend/pages/finish_workout_page.dart';
import 'package:fittnes_frontend/pages/profile_page.dart';
import 'package:fittnes_frontend/security/jwt_utils.dart';
import 'package:fittnes_frontend/utils/WebSocketManager.dart';
import 'package:flutter/material.dart';
import 'package:get/get_connect/http/src/interceptors/get_modifiers.dart';
import 'dart:ui';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:url_launcher/url_launcher.dart';

class ExerciseImageToggle extends StatefulWidget {
  final String startImageUrl;
  final String endImageUrl;

  ExerciseImageToggle({
    Key? key,
    required this.startImageUrl,
    required this.endImageUrl,
  }) : super(key: key);

  @override
  _ExerciseImageToggleState createState() => _ExerciseImageToggleState();
}

class _ExerciseImageToggleState extends State<ExerciseImageToggle> {
  bool showStartImage = true; // Initial state: show start image

  void _toggleImage() {
    setState(() {
      showStartImage = !showStartImage;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment:
          MainAxisAlignment.center, // Centers the column in the available space
      crossAxisAlignment: CrossAxisAlignment
          .stretch, // Stretches children across the screen width
      children: [
        GestureDetector(
          onTap:
              _toggleImage, // Make sure this method is defined in your state class
          child: AnimatedSwitcher(
            duration: const Duration(milliseconds: 300),
            child: showStartImage
                ? ClipRRect(
                    borderRadius: BorderRadius.circular(10.0),
                    child: Image.network(
                      widget.startImageUrl,
                      key: ValueKey('startImage'),
                      fit: BoxFit.cover,
                    ),
                  )
                : ClipRRect(
                    borderRadius: BorderRadius.circular(10.0),
                    child: Image.network(
                      widget.endImageUrl,
                      key: ValueKey('endImage'),
                      fit: BoxFit.cover,
                    ),
                  ),
          ),
        ),
        Padding(
          padding:
              const EdgeInsets.all(16.0), // Add some padding around the text
          child: Text(
            'Tap on the picture to see start and end positions',
            textAlign: TextAlign.center, // Center the text horizontally
            style: TextStyle(
              fontSize: 16.0, // Adjust the font size as needed
              fontWeight: FontWeight.normal, // Choose the font weight
              // Add other styles if necessary
            ),
          ),
        ),
      ],
    );
  }
}

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
  final int userHistoryWorkoutId;
  final int initialNoSets;
  final int initialNoReps;
  final double initialWeight;
  final int initialNoSeconds;

  StartExercisePage(
      {Key? key,
      required this.exercises,
      required this.exerciseIndex,
      required this.workoutId,
      required this.noSets,
      required this.isFirstExercise,
      required this.userHistoryModuleId,
      required this.userHistoryWorkoutId,
      required this.initialNoSets,
      required this.initialNoReps,
      required this.initialWeight,
      required this.initialNoSeconds})
      : super(key: key);

  @override
  State<StartExercisePage> createState() => _StartExercisePageState();
}

class _StartExercisePageState extends State<StartExercisePage> {
  Timer? countdownTimer;
  late Duration myDuration;
  //= Duration(seconds: 0);
  bool isTimerRunning = false;
  Color pageBackgroundColor = Colors.white; // Initial background color
  late int numberOfSets; // Default value
  late double numberOfReps = 0;
  late double weight = 0;

  int userHistoryModuleIdStore = 1;

  WorkoutRewards workoutRewards =
      WorkoutRewards(numberOfPoints: 0, achievements: []);

  Future<void> saveModule() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.post(
      Uri.parse('http://localhost:8080/api/selfCoach/user/saveModule'),
      body: jsonEncode({
        "parentUserHistoryWorkoutId": widget.userHistoryWorkoutId.toString(),
        "noSets": numberOfSets
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
      bool isDone, int noReps, double weight) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.put(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/addExerciseToModule/$userHistoryModuleId'),
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

  Future<WorkoutRewards> finishWorkout(int userHistoryWorkoutId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.put(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/finishWorkout/$userHistoryWorkoutId'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      var decodedJson = json.decode(response.body);
      return WorkoutRewards.fromJson(decodedJson);
    }

    if (response.statusCode != 200) {
      throw Exception(
          'Failed to finish workout. Status code: ${response.statusCode}');
    }

    // Add a return statement here
    throw Exception('Unexpected error occurred while finishing the workout.');
  }

  Future<LastEntryUserHistoryExerciseDto> getLastUserHistoryExercise(
      int workoutId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    String email = JwtUtils.extractSubject(accessToken);
    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getLastEntryUserExerciseHistory/$workoutId/$email'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      var decodedJson = json.decode(response.body);

      bool isExerciseDone = decodedJson['isExerciseDone'];
      bool isExerciseFirst = decodedJson['isFirstExercise'];
      int userHistoryExerciseId = decodedJson['userHistoryExerciseId'];
      int userHistoryModuleId = decodedJson['userHistoryModuleId'];
      LastEntryUserHistoryExerciseDto lastEntryUserHistoryExerciseDto =
          new LastEntryUserHistoryExerciseDto(
              isExerciseDone: isExerciseDone,
              isExerciseFirst: isExerciseFirst,
              userHistoryExerciseId: userHistoryExerciseId,
              userHistoryModuleId: userHistoryModuleId);
      return lastEntryUserHistoryExerciseDto;
    } else {
      print(
          'http://localhost:8080/api/selfCoach/user/getLastEntryUserExerciseHistory/$workoutId/$email');
      throw Exception("workout with id $workoutId is finished");
    }
  }

  Future<void> updateExerciseToModule(
      UpdateExerciseToModule updateExerciseToModule,
      int userHistoryExerciseId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }
    final response = await http.put(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/updateUserHistoryExercise/$userHistoryExerciseId'),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
      body: json.encode({
        'currentNoSeconds': updateExerciseToModule.currentNoSeconds,
        'noReps': updateExerciseToModule.noReps,
        'weight': updateExerciseToModule.weight,
      }),
    );

    if (response.statusCode != 200) {
      print(
          'http://localhost:8080/api/selfCoach/user/updateUserHistoryExercise/$userHistoryExerciseId');
      throw Exception(
          'Failed to update user history exercise. Status code: ${response.statusCode}');
    }
  }

  Future<void> updateModule(int noSets, int userHistoryModuleId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }
    final response = await http.put(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/updateUserHistoryModule/$userHistoryModuleId'),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
      body: json.encode({
        'noSets': noSets,
      }),
    );

    if (response.statusCode != 200) {
      throw Exception(
          'Failed to update module with id ${widget.workoutId}. Status code: ${response.statusCode}');
    }
  }

  @override
  void initState() {
    super.initState();
    numberOfSets = widget.initialNoSets;
    myDuration = Duration(seconds: widget.initialNoSeconds);
    //numberOfReps = widget.initialNoReps.toDouble();
    // weight = widget.initialWeight;
  }

  Future<void> _showSetSelectionDialog() async {
    await showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Select Number of Sets'),
          content: SetSelectionDialogContent(
            initialSelectedSets: widget.initialNoSets,
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
            initialValue: widget.initialNoReps.toDouble(),
            onValueChanged: (value) {
              setState(() {
                numberOfReps = value;
              });
            },
            labelText: 'Choose the number of reps for this exercise:',
            min: 0.0,
            max: 20.0,
            divisions: 20,
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

  void _showWeightSelectionDialog() {
    final TextEditingController _weightController =
        TextEditingController(text: widget.initialWeight.toString());

    showDialog<double>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Set Weight'),
          content: TextField(
            controller: _weightController,
            keyboardType: TextInputType.numberWithOptions(decimal: true),
            decoration: InputDecoration(
              labelText: 'Enter the weight for this exercise:',
              // If you want to add a suffix text like 'kg' or 'lbs', you can uncomment the next line
              suffixText: 'kg',
            ),
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
                // Do something with the weight value
                double? enteredWeight = double.tryParse(_weightController.text);
                if (enteredWeight != null) {
                  setState(() {
                    weight = enteredWeight;
                  });
                  // Use the entered weight value as needed
                  print('Entered weight: $enteredWeight');
                }
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

  Future<void> goBack() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    LastEntryUserHistoryExerciseDto lastEntryUserHistoryExerciseDto =
        await getLastUserHistoryExercise(widget.workoutId);

    if (lastEntryUserHistoryExerciseDto.isExerciseFirst) await saveModule();
    if (lastEntryUserHistoryExerciseDto.isExerciseDone) {
      await saveExerciseToModule(widget.userHistoryModuleId,
          myDuration.inSeconds, false, numberOfReps.toInt(), weight);
    } else {
      await updateModule(
          numberOfSets, lastEntryUserHistoryExerciseDto.userHistoryModuleId);

      UpdateExerciseToModule updateExerciseToModuleDto =
          new UpdateExerciseToModule(
              currentNoSeconds: myDuration.inSeconds,
              noReps: numberOfReps.toInt(),
              weight: weight);
      await updateExerciseToModule(updateExerciseToModuleDto,
          lastEntryUserHistoryExerciseDto.userHistoryExerciseId);
    }

    Navigator.pop(context);
  }

  void goToNextModule(bool isFirstExercise) async {
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
            userHistoryWorkoutId: widget.userHistoryWorkoutId,
            initialNoSets: 1,
            initialNoReps: 0,
            initialWeight: 0,
            initialNoSeconds: 0,
          ),
        ),
      );
    } else {
      //workout finish
      workoutRewards = await finishWorkout(widget.userHistoryWorkoutId);

      Navigator.pop(context);
      Navigator.push(
        context,
        MaterialPageRoute(
            builder: (context) => FinishWorkoutPage(
                  workoutRewards: workoutRewards,
                )),
      );
      // Navigator.push(context, MaterialPageRoute(builder: (context)=> ProfilePage()));
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
          userHistoryWorkoutId: widget.userHistoryWorkoutId,
          initialNoSets: numberOfSets,
          initialNoReps: 0,
          initialWeight: 0,
          initialNoSeconds: 0,
        ),
      ),
    );
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
            backgroundColor: isTimerRunning ? Colors.red : Colors.green,
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
    final exercise = widget.exercises[widget.exerciseIndex];

    return Card(
      margin: EdgeInsets.symmetric(vertical: 20, horizontal: 16),
      elevation: 10,
      color: Colors.grey.withOpacity(0.6), // Grey color with opacity

      child: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Exercise Details',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 20),
            _buildDetail(Icons.fitness_center, 'Name', exercise.name),
            _buildDetail(
                Icons.description, 'Description', exercise.description),
            _buildDetail(Icons.bar_chart, 'Difficulty', exercise.difficulty),
            _buildDetail(Icons.group_work, 'Targeted Muscle Group',
                exercise.muscleGroup),
            _buildDetail(
                Icons.build, 'Equipment Necessary', exercise.equipment),
            Divider(height: 30, thickness: 1),
            _buildDetail(Icons.loop, 'Number of sets left', '$numberOfSets'),
            // _buildImageSection('Start Position:', exercise.exerciseImageStartUrl),
            // _buildImageSection('End Position:', exercise.exerciseImageEndUrl),
            _buildImageSection(
                exercise.exerciseImageStartUrl, exercise.exerciseImageEndUrl),
            if (exercise.descriptionUrl.isNotEmpty) ...[
              _buildDetail(Icons.link, 'More Details', ''),
              _buildLink(exercise.descriptionUrl),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildImageSection(
      String exerciseImageStartUrl, String exerciseImageEndUrl) {
    return ExerciseImageToggle(
      startImageUrl: exerciseImageStartUrl,
      endImageUrl: exerciseImageEndUrl,
    );
  }

  Widget _buildDetail(IconData icon, String label, String value) {
    return ListTile(
      leading: Icon(icon),
      title: Text(
        label,
        style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
      ),
      subtitle: Text(
        value,
        style: TextStyle(fontSize: 16),
      ),
      contentPadding: EdgeInsets.all(0),
    );
  }

  Widget _buildLink(String url) {
    return InkWell(
      onTap: () async {
        final Uri parsedUrl = Uri.parse(url);
        if (await canLaunchUrl(parsedUrl)) {
          await launchUrl(parsedUrl);
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Could not launch $url')),
          );
        }
      },
      child: Text(
        url,
        style: TextStyle(
          fontSize: 16,
          color: Colors.blue,
          decoration: TextDecoration.underline,
        ),
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
              await saveExerciseToModule(widget.userHistoryModuleId,
                  myDuration.inSeconds, true, numberOfReps.toInt(), weight);
              goToNextModule(true);
            } else {
              if (widget.isFirstExercise) {
                await saveModule();
              }
              await saveExerciseToModule(widget.userHistoryModuleId,
                  myDuration.inSeconds, true, numberOfReps.toInt(), weight);

              goToNextExercise(false);
            }

            // if (widget.exerciseIndex == widget.exercises.length - 1) {
            //   if (noSets == 1) {
            //     workoutRewards = await finishWorkout(widget.userHistoryWorkoutId);
            //   }
            // }
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
                            onPressed: _showWeightSelectionDialog,
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
