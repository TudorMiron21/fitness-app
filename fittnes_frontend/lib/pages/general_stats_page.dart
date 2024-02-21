import 'dart:convert';

import 'package:fittnes_frontend/models/WorkoutStatistics.dart';
import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class VolumeLineChart extends StatelessWidget {
  final List<WorkoutStatistics> workoutStatistics;

  VolumeLineChart(this.workoutStatistics);

  @override
  Widget build(BuildContext context) {
    List<FlSpot> spots = workoutStatistics.asMap().entries.map((entry) {
      return FlSpot(entry.key.toDouble(), entry.value.totalVolume);
    }).toList();

    return AspectRatio(
      aspectRatio: 2 ,
      child: LineChart(
        LineChartData(
          lineBarsData: [
            LineChartBarData(
              spots: spots,
              isCurved: true 
            )
          ]
        ),
      ),
      
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
            'http://localhost:8080/api/selfCoach/user/getGeneralWorkoutInformation/10'),
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
        elevation: 4
    
      ),
        body: const Center(
          child: CircularProgressIndicator(),
        ),
      );
    }
    return Scaffold(
      appBar: AppBar(
        title: Text('Workout Volume Statistics'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: workoutStatistics.isNotEmpty
            ? VolumeLineChart(workoutStatistics)
            : Center(child: Text('No data available.')),
      ),
    );
  }
}