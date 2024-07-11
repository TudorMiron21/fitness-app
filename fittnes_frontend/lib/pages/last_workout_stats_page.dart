// import 'dart:convert';
import 'dart:convert';
import 'dart:math';
import 'dart:ui';
import 'package:fittnes_frontend/models/WorkoutStatistics.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class LastWorkoutStatsPage extends StatefulWidget {
  const LastWorkoutStatsPage({super.key});

  @override
  State<LastWorkoutStatsPage> createState() => _LastWorkoutStatsPageState();
}

class _LastWorkoutStatsPageState extends State<LastWorkoutStatsPage> {
  WorkoutStatistics? workoutStatistics;
  bool isLoading = true;

  Future<void> getLastWorkoutStats() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    try {
      if (accessToken == null || accessToken.isEmpty) {
        throw Exception('Authentication token is missing or invalid.');
      }

      final response = await http.get(
        Uri.parse(
            'https://www.fit-stack.online/api/selfCoach/user/getLastWorkoutStatistics'),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        workoutStatistics = WorkoutStatistics.fromJson(data);
      } else if (response.statusCode == 404) {
        // Handle the case where no workout statistics are found
        workoutStatistics = null;
      } else {
        throw Exception('Failed to load workout statistics');
      }
    } catch (e) {
      print('Error fetching workout statistics: $e');
      workoutStatistics = null;
    } finally {
      if (mounted) {
        setState(() {
          isLoading = false;
        });
      }
    }
  }

  @override
  void initState() {
    super.initState();
    getLastWorkoutStats();
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return Scaffold(
        appBar: AppBar(
            title: Text(
              'Last Workout Statistics',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.w500,
              ),
            ),
            centerTitle: true,
            toolbarHeight: 30,
            backgroundColor: Colors.blue,
            elevation: 4,
            automaticallyImplyLeading: false),
        body: const Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    if (workoutStatistics == null) {
      return Scaffold(
        appBar: AppBar(
            title: Text(
              'Last Workout Statistics',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.w500,
              ),
            ),
            centerTitle: true,
            toolbarHeight: 30,
            backgroundColor: Colors.blue,
            elevation: 4,
            automaticallyImplyLeading: false),
        body: Center(
          child: Text(
            'No last workout statistics available',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
        ),
      );
    }

    final double totalVolume = workoutStatistics!.totalVolume;
    final String totalCaloriesBurned = workoutStatistics!.totalCaloriesBurned.toStringAsFixed(2);
    final int totalTimeInSeconds = workoutStatistics!.totalTime;

    // Convert total time in seconds to hours:minutes:seconds format
    final int hours = totalTimeInSeconds ~/ 3600;
    final int minutes = (totalTimeInSeconds % 3600) ~/ 60;
    final int seconds = totalTimeInSeconds % 60;
    final String formattedTime =
        '${hours.toString().padLeft(2, '0')}:${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';

    return Scaffold(
      body: Stack(
        fit: StackFit.expand,
        children: <Widget>[
          Container(
            decoration: BoxDecoration(
              image: DecorationImage(
                image: AssetImage(
                    'lib/images/pattern_background.png'), // Replace with your asset image path
                fit: BoxFit.cover, // This will fill the background of the SingleChildScrollView
              ),
            ),
          ),
          BackdropFilter(
            filter: ImageFilter.blur(
                sigmaX: 3.0, sigmaY: 3.0), // Adjust the blur intensity
            child: Container(
              color: Colors.black.withOpacity(0.6), // Darken the background a bit
            ),
          ),
          SingleChildScrollView(
            padding: const EdgeInsets.all(8.0),
            child: Center(
              child: Wrap(
                alignment: WrapAlignment.center,
                spacing: 8.0, // space between cards horizontally
                runSpacing: 8.0, // space between cards vertically
                children: <Widget>[
                  _buildStatisticCard(context, 'Total Volume', '$totalVolume'),
                  _buildStatisticCard(context, 'Calories Burned',
                      '$totalCaloriesBurned kcal'),
                  _buildStatisticCard(context, 'Total Time', formattedTime),
                  _buildPieChartSection(
                    context,
                    'Category Percentage',
                    workoutStatistics!.categoryPercentage,
                  ),
                  _buildPieChartSection(
                    context,
                    'Muscle Group Percentage',
                    workoutStatistics!.muscleGroupPercentage,
                  ),
                  _buildPieChartSection(
                    context,
                    'Difficulty Percentage',
                    workoutStatistics!.difficultyPercentage,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatisticCard(BuildContext context, String title, String value) {
    return Card(
      elevation: 4,
      color: Colors.white.withOpacity(0.9),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Container(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisSize: MainAxisSize.min, // To make the card wrap content
          children: <Widget>[
            Text(title, style: Theme.of(context).textTheme.subtitle1),
            const SizedBox(height: 8),
            Text(
              value,
              style: Theme.of(context).textTheme.headline6?.copyWith(
                    color: Colors.red.shade300, // This sets the text color to red
                  ),
            ),
          ],
        ),
      ),
    );
  }

  // Builds a section of the pie chart
  Widget _buildPieChartSection(
      BuildContext context, String title, Map<String, double> percentages) {
    List<PieChartSectionData> sections = percentages.entries.map((entry) {
      return PieChartSectionData(
        color: _getRandomColor(),
        value: entry.value * 100, // Convert the decimal percentage to a full percentage
        title: '${(entry.value * 100).toStringAsFixed(1)}% ${entry.key}',
        radius: 50,
        titleStyle: TextStyle(
            fontSize: 12, fontWeight: FontWeight.bold, color: Colors.black87),
        titlePositionPercentageOffset: 0.55,
      );
    }).toList();

    return Card(
      elevation: 4,
      color: Colors.white.withOpacity(0.9),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Container(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisSize: MainAxisSize.min, // To make the card wrap content
          children: <Widget>[
            Text(
              title,
              style: Theme.of(context).textTheme.subtitle1,
            ),
            const SizedBox(height: 8),
            SizedBox(
              height: 200, // Adjust the size to fit your layout
              child: PieChart(
                PieChartData(
                  borderData: FlBorderData(show: false),
                  sectionsSpace: 0,
                  centerSpaceRadius: 40,
                  sections: sections,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Color _getRandomColor() {
    return Color((Random().nextDouble() * 0xFFFFFF).toInt()).withOpacity(0.5);
  }
}
