import 'package:fittnes_frontend/models/workout.dart';
import 'package:flutter/material.dart';
  import 'dart:convert';
import 'package:http/http.dart' as http;
class HomePage extends StatefulWidget {
  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final usernameController = TextEditingController();

  final passwordController = TextEditingController();



Future<List<Workout>> fetchWorkouts() async {
  final response = await http.get(Uri.parse('your_api_endpoint_here'));

  if (response.statusCode == 200) {
    final List<dynamic> data = json.decode(response.body);

    // Map the API response to a list of Workout objects
    List<Workout> workouts = data.map((json) {
      return Workout(
        name: json['name'],
        description: json['description'],
        // Add more properties as needed
      );
    }).toList();

    return workouts;
  } else {
    throw Exception('Failed to load workouts');
  }
}

  @override
  Widget build(BuildContext context) {
    // Your home page content here
    return Scaffold(
      appBar: AppBar(
        title: Text('Home Page'),
      ),
      body: Center(
        child: Text('Welcome to the Home Page!'),
      ),
    );
  }
}
