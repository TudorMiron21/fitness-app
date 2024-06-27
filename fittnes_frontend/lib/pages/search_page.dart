import 'dart:convert';

import 'package:fittnes_frontend/components/filter_results_widget.dart';
import 'package:fittnes_frontend/components/program_tile.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/models/filter_search.dart';
import 'package:fittnes_frontend/models/program.dart';
import 'package:fittnes_frontend/models/workout.dart';
import 'package:fittnes_frontend/pages/exercise_page.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  RangeValues difficultyRange = RangeValues(1.0, 3.0);
  String searchValue = "";
  FilterSearch filterSearch =
      FilterSearch(name: "", minDifficultyLevel: 1.0, maxDifficultyLevel: 3.0);

  List<Workout> filteredWorkouts = [];
  bool areWorkoutsLoading = true;
  String fetchWorkoutsErrorMeassage = '';

  List<Program> filteredPrograms = [];
  bool areProgramsLoading = true;
  String fetchProgramsErrorMeassage = '';

  Future<void> _fetchFilteredWorkouts() async {
    try {
      List<Workout> data = await getFilteredWorkouts();
      setState(() {
        filteredWorkouts = data;
        areWorkoutsLoading = false;
      });
    } catch (error) {
      setState(() {
        fetchWorkoutsErrorMeassage = 'Error: $error';
        areWorkoutsLoading = false;
      });
    }
  }

  Future<void> _fetchFilteredPrograms() async {
    try {
      List<Program> data = await getFilteredPrograms();
      setState(() {
        filteredPrograms = data;
        areProgramsLoading = false;
      });
    } catch (error) {
      setState(() {
        fetchProgramsErrorMeassage = 'Error: $error';
        areProgramsLoading = false;
      });
    }
  }

  Future<List<Workout>> getFilteredWorkouts() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }
    print(Uri.parse(
        'http://localhost:8080/api/selfCoach/user/getFilteredWorkouts?${makeRequestParamStringFromSearchFilter(filterSearch)}'));
    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getFilteredWorkouts?${makeRequestParamStringFromSearchFilter(filterSearch)}'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      return data.map((json) {
        List<Exercise> exerciseList = (json['exercises'] as List)
            .map((exerciseJson) => Exercise(
                  id: exerciseJson['id'],
                  name: exerciseJson['name'],
                  description: exerciseJson['description'] ?? "",
                  descriptionUrl: exerciseJson['descriptionUrl'] ?? "",
                  exerciseImageStartUrl:
                      exerciseJson['exerciseImageStartUrl'] ?? "",
                  exerciseImageEndUrl:
                      exerciseJson['exerciseImageEndUrl'] ?? "",
                  exerciseVideoUrl: exerciseJson['exerciseVideoUrl'] ?? "",
                  difficulty:
                      exerciseJson['difficulty']['dificultyLevel'] ?? 0.0,
                  category: exerciseJson['category']['name'] ?? "",
                  exerciseExclusive: exerciseJson['exerciseExclusive'] ?? false,
                  equipment: exerciseJson['equipment']['name'] ?? "",
                  muscleGroup: exerciseJson['muscleGroup']['name'] ?? "",
                  rating: exerciseJson['rating'] ?? 0.0,
                  hasNoReps: exerciseJson['hasNoReps'] ?? false,
                  hasWeight: exerciseJson['hasWeight'] ?? false,
                ))
            .toList() as List<Exercise>;

        return Workout(
            id: json['id'],
            name: json['name'],
            description: json['description'],
            difficultyLevel: json['difficultyLevel'],
            coverPhotoUrl: json['coverPhotoUrl'] ?? "",
            exercises: exerciseList,
            likedByUser: json['likedByUser']);
      }).toList();
    } else {
      throw Exception(
          'Failed to load workouts. Status code: ${response.statusCode}');
    }
  }

  Future<List<Program>> getFilteredPrograms() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getFilteredPrograms?${makeRequestParamStringFromSearchFilter(filterSearch)}'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> jsonResponse = json.decode(response.body);
      return jsonResponse.map((programJson) {
        Map<int, Workout> indexedWorkouts = {};
        var workoutList = programJson['workoutProgramSet'] as List? ??
            []; // Handling potential null
        for (var workoutJson in workoutList) {
          var exercisesList = workoutJson['workout']['exercises'] as List? ??
              []; // Handling potential null
          var workout = Workout(
            id: workoutJson['workout']['id'],
            name: workoutJson['workout']['name'] ?? "",
            description: workoutJson['workout']['description'] ?? "",
            coverPhotoUrl: workoutJson['workout']['coverPhotoUrl'] ?? '',
            difficultyLevel: workoutJson['workout']['difficultyLevel'] ?? 0.0,
            exercises: exercisesList
                .map((exerciseJson) => Exercise(
                      id: exerciseJson['id'],
                      name: exerciseJson['name'],
                      description: exerciseJson['description'] ?? "",
                      descriptionUrl: exerciseJson['descriptionUrl'] ?? "",
                      exerciseImageStartUrl:
                          exerciseJson['exerciseImageStartUrl'] ?? "",
                      exerciseImageEndUrl:
                          exerciseJson['exerciseImageEndUrl'] ?? "",
                      exerciseVideoUrl: exerciseJson['exerciseVideoUrl'] ?? "",
                      difficulty:
                          exerciseJson['difficulty']['dificultyLevel'] ?? 0.0,
                      category: exerciseJson['category']['name'] ?? "",
                      exerciseExclusive:
                          exerciseJson['exerciseExclusive'] ?? false,
                      equipment: exerciseJson['equipment']['name'] ?? "",
                      muscleGroup: exerciseJson['muscleGroup']['name'] ?? "",
                      rating: exerciseJson['rating'] ?? 0.0,
                      hasNoReps: exerciseJson['hasNoReps'] ?? false,
                      hasWeight: exerciseJson['hasWeight'] ?? false,
                    ))
                .toList(),
            likedByUser: workoutJson['likedByUser'] ?? false,
          );
          indexedWorkouts[workoutJson['workoutIndex']] = workout;
        }

        return Program(
          id: programJson['id'],
          name: programJson['name'] ?? "",
          description: programJson['description'] ?? "",
          durationInDays: programJson['durationInDays'] ?? 0,
          coverPhotoUrl: programJson['coverPhotoUrl'] ?? "",
          indexedWorkouts: indexedWorkouts,
        );
      }).toList();
    } else {
      throw Exception(
          'Failed to load programs. Status code: ${response.statusCode}');
    }
  }

  Future<void> likeWorkout(Workout workout) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.put(
      Uri.parse('http://localhost:8080/api/selfCoach/user/likeWorkout/' +
          workout.id.toString()),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) //OK
    {
      setState(() {
        workout.likedByUser = true;
      });
    } else {
      throw Exception(
          'Failed to like workout. Status code: ${response.statusCode}');
    }
  }

  Future<void> unlikeWorkout(Workout workout) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    // TODO: Implement the API call to unlike the workout
    // Use the workout.id to identify the workout to unlike

    final response = await http.delete(
      Uri.parse('http://localhost:8080/api/selfCoach/user/unlikeWorkout/' +
          workout.id.toString()),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) //OK
    {
      setState(() {
        workout.likedByUser = false;
      });
    } else {
      throw Exception(
          'Failed to like workout. Status code: ${response.statusCode}');
    }
  }

  String makeRequestParamStringFromSearchFilter(FilterSearch filterSearch) {
    // Convert the properties of the FilterSearch object into query parameters
    String nameParam = 'name=${Uri.encodeQueryComponent(filterSearch.name)}';
    String minDifficultyParam =
        'minDifficultyLevel=${filterSearch.minDifficultyLevel}';
    String maxDifficultyParam =
        'maxDifficultyLevel=${filterSearch.maxDifficultyLevel}';

    // Concatenate the query parameters with '&' separator
    String queryParams = '$nameParam&$minDifficultyParam&$maxDifficultyParam';

    return queryParams;
  }

  dynamic onSearchChanged(String newText) async {
    setState(() {
      searchValue = newText;
      filterSearch.name = newText;
      areWorkoutsLoading = true;
      areProgramsLoading = true;
      print(searchValue);
    });
    await _fetchFilteredWorkouts();
    await _fetchFilteredPrograms();
  }

  void onRangeChanged(RangeValues newRange) async {
    setState(() {
      difficultyRange = newRange;
      filterSearch.minDifficultyLevel = newRange.start;
      filterSearch.maxDifficultyLevel = newRange.end;
      areWorkoutsLoading = true;
      areProgramsLoading = true;
      print(difficultyRange);
    });

    await _fetchFilteredWorkouts();
    await _fetchFilteredPrograms();
  }

  @override
  Widget build(BuildContext context) {
    // return FilterResultsWidget(
    //     onSearchChanged: onSearchChanged, onRangeChanged: onRangeChanged);

    return Scaffold(
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(
            180.0), // Set the AppBar height to fit the filter widget
        child: AppBar(
          automaticallyImplyLeading: false,
          flexibleSpace: SafeArea(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                FilterResultsWidget(
                    onSearchChanged: onSearchChanged,
                    onRangeChanged: onRangeChanged),
              ],
            ),
          ),
        ),
      ),
      body: Column(
        children: [
          Visibility(
            visible: filteredWorkouts.isNotEmpty,
            child: Expanded(
              flex: 1,
              child: areWorkoutsLoading
                  ? Center(child: CircularProgressIndicator())
                  : fetchWorkoutsErrorMeassage.isNotEmpty
                      ? Center(child: Text(fetchWorkoutsErrorMeassage))
                      : PageView.builder(
                          itemCount: filteredWorkouts.length,
                          itemBuilder: (context, index) {
                            Workout workout = filteredWorkouts[index];
                            return buildWorkoutCard(workout);
                          },
                        ),
            ),
          ),
          Visibility(
            visible: filteredPrograms.isNotEmpty,
            child: Expanded(
              // height: constraints.maxHeight,
              flex: 1,
              child: areProgramsLoading
                  ? Center(child: CircularProgressIndicator())
                  : fetchProgramsErrorMeassage.isNotEmpty
                      ? Center(child: Text(fetchProgramsErrorMeassage))
                      : buildProgramPageView(),
            ),
          ),
        ],
      ),
    );
  }

  Widget buildPageBackground(String imgPath, String text) {
    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: 25.0,
        vertical: 10,
      ),
      decoration: BoxDecoration(
        image: DecorationImage(
          image: AssetImage(imgPath), // Set your background image here
          fit: BoxFit.cover,
          colorFilter: ColorFilter.mode(
            Colors.black
                .withOpacity(0.6), // Adjust the opacity for a darker filter
            BlendMode.darken,
          ),
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            text,
            style: TextStyle(
              color: Colors.white,
              fontSize: 18, // Adjust the font size as needed
              fontWeight: FontWeight.bold,
            ),
          ),
          SizedBox(width: 10), // Adjust the spacing between text and arrow
          Icon(
            Icons.arrow_forward,
            color: Colors.white,
          ),
        ],
      ),
    );
  }

  Widget buildProgramPageView() {
    return PageView.builder(
      itemCount: filteredPrograms.length,
      itemBuilder: (context, index) {
        Program program = filteredPrograms[index];
        return ProgramTile(program: program);
      },
    );
  }

  Widget buildWorkoutCard(Workout workout) {
    // Calculate the difficulty level representation
    int maxDifficulty = 3; // Adjust the maximum difficulty level
    double fractionalPart =
        workout.difficultyLevel - workout.difficultyLevel.floor();
    List<Widget> difficultyCircles = List.generate(
      maxDifficulty,
      (index) {
        Color circleColor;
        if (index < workout.difficultyLevel.floor()) {
          circleColor = Colors.blue;
        } else if (index == workout.difficultyLevel.floor()) {
          circleColor =
              Color.lerp(Colors.blue, Colors.blue.shade200, fractionalPart)!;
        } else {
          circleColor = Colors.grey;
        }
        return Container(
          width: 14,
          height: 14,
          margin: EdgeInsets.only(left: 4),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: circleColor.withOpacity(1), // Adjust opacity
          ),
        );
      },
    );
    return Card(
      elevation: 5,
      margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Container(
        decoration: BoxDecoration(
          image: DecorationImage(
            image: NetworkImage(workout.coverPhotoUrl),
            fit: BoxFit.cover,
            colorFilter: ColorFilter.mode(
              Colors.black
                  .withOpacity(0.65), // Adjust opacity of the background image
              BlendMode.darken,
            ),
          ),
        ),
        child: ListTile(
          contentPadding: EdgeInsets.all(16),
          title: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                workout.name,
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: Colors.white, // Adjust text color
                ),
              ),
              SizedBox(height: 8),
              Text(
                workout.description,
                style: TextStyle(
                  color: Colors.white, // Adjust text color
                ),
              ),
              SizedBox(height: 8),
              Text(
                'Exercises: ${workout.exercises.length}',
                style: TextStyle(
                  color: Colors.white, // Adjust text color
                ),
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  // Difficulty Circles
                  ...difficultyCircles,
                  SizedBox(
                      width:
                          8), // Adjust spacing between circles and difficulty level
                  // Difficulty Level
                  Text(
                    getDifficultyText(workout.difficultyLevel),
                    style: TextStyle(
                      color: Colors.white, // Adjust text color
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ],
          ),
          trailing: GestureDetector(
            onTap: () {
              if (workout.likedByUser) {
                unlikeWorkout(workout);
              } else {
                likeWorkout(workout);
              }
            },
            child: Icon(
              workout.likedByUser ? Icons.favorite : Icons.favorite_border,
              color: workout.likedByUser ? Colors.red : Colors.white,
            ),
          ),
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => ExercisePage(
                  exercises: workout.exercises,
                  workoutName: workout.name,
                  workoutId: workout.id,
                  programId: -1,
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  String getDifficultyText(double difficultyLevel) {
    if (difficultyLevel >= 0 && difficultyLevel < 1) {
      return 'Beginner';
    } else if (difficultyLevel >= 1 && difficultyLevel < 2) {
      return 'Intermediate';
    } else if (difficultyLevel >= 2 && difficultyLevel < 3) {
      return 'Expert';
    } else {
      return 'Unknown';
    }
  }
}
