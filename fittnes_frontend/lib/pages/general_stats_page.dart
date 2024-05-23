import 'dart:convert';
import 'dart:math';
import 'dart:ui';

import 'package:fittnes_frontend/models/WorkoutStatistics.dart';
import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class VolumeLineChart extends StatelessWidget {
  final List<WorkoutStatistics> workoutStatistics;
  final String graphTitle;
  final List<FlSpot> spots;

  VolumeLineChart(this.workoutStatistics, this.graphTitle, this.spots);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      mainAxisSize: MainAxisSize.min,
      children: [
        // Graph title
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Text(
            graphTitle,
            style: Theme.of(context)
                .textTheme
                .headline6
                ?.copyWith(color: Colors.white),
            textAlign: TextAlign.center,
          ),
        ),
        // Chart
        AspectRatio(
          aspectRatio: 2,
          child: LineChart(
            LineChartData(
              lineBarsData: [
                LineChartBarData(
                  spots: spots,
                  isCurved: true,
                ),
              ],
              titlesData: FlTitlesData(
                // Hide titles on the top side
                topTitles: AxisTitles(
                  sideTitles: SideTitles(showTitles: false),
                ),
                // Hide titles on the right side
                rightTitles: AxisTitles(
                  sideTitles: SideTitles(showTitles: false),
                ),
                // Show titles on the left side
                leftTitles: AxisTitles(
                  sideTitles: SideTitles(
                    showTitles: true,
                    getTitlesWidget: (value, meta) {
                      return Padding(
                        padding: const EdgeInsets.only(
                            right: 8.0), // Adjust the padding as needed
                        child: Text(
                          '${value.toInt()}',
                          style: TextStyle(
                            color: Colors.grey,
                            fontSize: 12, // Adjust the font size as needed
                          ),
                        ),
                      );
                    },
                    reservedSize: 40,
                  ),
                ),
                bottomTitles: AxisTitles(
                  sideTitles: SideTitles(
                    showTitles: false,
                  ),
                ),
              ),
              gridData: FlGridData(show: false),
              borderData: FlBorderData(show: true),
            ),
          ),
        ),
      ],
    );
  }
}

class GeneralStatsPage extends StatefulWidget {
  @override
  _GeneralStatsPageState createState() => _GeneralStatsPageState();
}

class _GeneralStatsPageState extends State<GeneralStatsPage> {
  List<WorkoutStatistics> workoutStatistics = [];
  bool isLoading = true;

  Future<void> getWorkoutStatistics() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();

    String? accessToken = await storage.read(key: 'accessToken');
    try {
      if (accessToken == null || accessToken.isEmpty) {
        throw Exception('Authentication token is missing or invalid.');
      }

      final response = await http.get(
        Uri.parse(
            'https://fit-stack.online/api/selfCoach/user/getGeneralWorkoutInformation/10'),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        var jsonResponse = json.decode(response.body);

        if (jsonResponse is List) {
          workoutStatistics.clear();

          workoutStatistics = jsonResponse.map((jsonItem) {
            return WorkoutStatistics.fromJson(jsonItem);
          }).toList();
        }
      } else {
        throw Exception('Failed to load workout statistics');
      }
    } catch (e) {
      print('Error fetching workout statistics: $e');
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
    getWorkoutStatistics();
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return Scaffold(
        appBar: AppBar(
          automaticallyImplyLeading:false,
            title: Text(
              'General Workout Statistics',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.w500,
              ),
            ),
            centerTitle: true,
            toolbarHeight: 30,
            backgroundColor: Colors.blue,
            elevation: 4,
            ),
        body: const Center(
          child: CircularProgressIndicator(),
        ),
      );
    }
    return Scaffold(
      backgroundColor: Colors.black.withOpacity(0.8),
      appBar: AppBar(
          title: Text(
            'General Workout Statistics',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.w500,
            ),
          ),
          centerTitle: true,
          toolbarHeight: 30,
          backgroundColor: Colors.blue,
          elevation: 4),
      body: Stack(
        fit: StackFit.expand,
        children: [
          Image.asset(
            'lib/images/background2.jpg',
            fit: BoxFit.cover,
          ),
          // This widget applies a filter to the background image
          BackdropFilter(
            filter: ImageFilter.blur(
                sigmaX: 3.0, sigmaY: 3.0), // Adjust the blur intensity
            child: Container(
              color:
                  Colors.black.withOpacity(0.2), // Darken the background a bit
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: ListView(
              // Use ListView.builder if you have a dynamic list of charts
              children: [
                workoutStatistics.isNotEmpty
                    ? VolumeLineChart(
                        workoutStatistics,
                        "Volume Chart",
                        workoutStatistics.asMap().entries.map((entry) {
                          return FlSpot(
                              entry.key.toDouble(), entry.value.totalVolume);
                        }).toList())
                    : Center(child: Text('No data available for Chart 1.')),
                SizedBox(
                  height: 50,
                ),
                workoutStatistics.isNotEmpty
                    ? VolumeLineChart(
                        workoutStatistics,
                        "Calories Chart",
                        workoutStatistics.asMap().entries.map((entry) {
                          return FlSpot(entry.key.toDouble(),
                              entry.value.totalCaloriesBurned);
                        }).toList())
                    : Center(child: Text('No data available for Chart 2.')),
                SizedBox(
                  height: 50,
                ),
                workoutStatistics.isNotEmpty
                    ? VolumeLineChart(
                        workoutStatistics,
                        "Time Chart",
                        workoutStatistics.asMap().entries.map((entry) {
                          return FlSpot(entry.key.toDouble(),
                              entry.value.totalTime.toDouble());
                        }).toList())
                    : Center(child: Text('No data available for Chart 3.')),
                SizedBox(
                  height: 50,
                ),
                _buildPieChartSection(context, "Category Percentages",
                    calculateAverageCategoryPercentages(workoutStatistics)),
                _buildPieChartSection(context, "Muscle Group Percentages",
                    calculateAverageMuscleGroupPercentages(workoutStatistics)),
                _buildPieChartSection(context, "Difficulty Percentages",
                    calculateDifficultyPercentages(workoutStatistics))
              ],
            ),
          ), // Add your content here (e.g., ListView, Column)
        ],
      ),
    );
  }

  Map<String, double> calculateAverageCategoryPercentages(
      List<WorkoutStatistics> workoutStatisticsList) {
    Map<String, double> totalPercentageMap = {};
    Map<String, int> categoryCountMap = {};

    // Aggregate the percentages for each category across all WorkoutStatistics objects
    for (var ws in workoutStatisticsList) {
      ws.categoryPercentage.forEach((category, percent) {
        // If the category already exists in the map, add the percent to it; otherwise, initialize it
        totalPercentageMap[category] =
            (totalPercentageMap[category] ?? 0) + percent;
        // Count the occurrences of each category
        categoryCountMap[category] = (categoryCountMap[category] ?? 0) + 1;
      });
    }

    // Calculate the average percentages for each category
    Map<String, double> averagePercentageMap = {};
    totalPercentageMap.forEach((category, totalPercent) {
      // Get the count for the current category
      int count = categoryCountMap[category] ?? 1; // Prevent division by zero
      // Calculate the average percentage
      averagePercentageMap[category] = totalPercent / count;
    });

    return averagePercentageMap;
  }

  Map<String, double> calculateAverageMuscleGroupPercentages(
      List<WorkoutStatistics> workoutStatisticsList) {
    Map<String, double> totalPercentageMap = {};
    Map<String, int> muscleGroupCountMap = {};

    // Aggregate the percentages for each category across all WorkoutStatistics objects
    for (var ws in workoutStatisticsList) {
      ws.muscleGroupPercentage.forEach((muscleGroup, percent) {
        // If the category already exists in the map, add the percent to it; otherwise, initialize it
        totalPercentageMap[muscleGroup] =
            (totalPercentageMap[muscleGroup] ?? 0) + percent;
        // Count the occurrences of each category
        muscleGroupCountMap[muscleGroup] =
            (muscleGroupCountMap[muscleGroup] ?? 0) + 1;
      });
    }

    // Calculate the average percentages for each category
    Map<String, double> averagePercentageMap = {};
    totalPercentageMap.forEach((muscleGroup, totalPercent) {
      // Get the count for the current category
      int count =
          muscleGroupCountMap[muscleGroup] ?? 1; // Prevent division by zero
      // Calculate the average percentage
      averagePercentageMap[muscleGroup] = totalPercent / count;
    });

    return averagePercentageMap;
  }

  Map<String, double> calculateDifficultyPercentages(
      List<WorkoutStatistics> workoutStatisticsList) {
    Map<String, double> totalPercentageMap = {};
    Map<String, int> difficultyCountMap = {};

    // Aggregate the percentages for each category across all WorkoutStatistics objects
    for (var ws in workoutStatisticsList) {
      ws.difficultyPercentage.forEach((difficulty, percent) {
        // If the category already exists in the map, add the percent to it; otherwise, initialize it
        totalPercentageMap[difficulty] =
            (totalPercentageMap[difficulty] ?? 0) + percent;
        // Count the occurrences of each category
        difficultyCountMap[difficulty] =
            (difficultyCountMap[difficulty] ?? 0) + 1;
      });
    }

    // Calculate the average percentages for each category
    Map<String, double> averagePercentageMap = {};
    totalPercentageMap.forEach((difficulty, totalPercent) {
      // Get the count for the current category
      int count =
          difficultyCountMap[difficulty] ?? 1; // Prevent division by zero
      // Calculate the average percentage
      averagePercentageMap[difficulty] = totalPercent / count;
    });

    return averagePercentageMap;
  }

  Widget _buildPieChartSection(
      BuildContext context, String title, Map<String, double> percentages) {
    List<PieChartSectionData> sections = percentages.entries.map((entry) {
      return PieChartSectionData(
        color: _getRandomColor(),
        value: entry.value *
            100, // Convert the decimal percentage to a full percentage
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
