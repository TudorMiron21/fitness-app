import 'dart:convert';

import 'package:fittnes_frontend/models/user.dart';
import 'package:fittnes_frontend/pages/coach_profile.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class FollowNewCoaches extends StatefulWidget {
  const FollowNewCoaches({super.key});

  @override
  State<FollowNewCoaches> createState() => _FollowNewCoachesState();
}

class _FollowNewCoachesState extends State<FollowNewCoaches> {
  List<User> coaches = [];
  bool areCoachesLoading = true;
  String fetchCoachesErrorMessage = '';
  List<User> followingCoaches = [];
  @override
  void initState() {
    super.initState();
    _fetchNonFollowingCoaches();
  }

  Future<void> _fetchNonFollowingCoaches() async {
    try {
      List<User> data = await fetchNonFollowingCoaches();
      setState(() {
        coaches = data;
        areCoachesLoading = false;
      });
    } catch (error) {
      setState(() {
        fetchCoachesErrorMessage = 'Error: $error';
        areCoachesLoading = false;
      });
    }
  }

  Future<List<User>> fetchNonFollowingCoaches() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/payingUser/getNonFollowingCoaches'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);

      return data
          .map((json) => User(
                id: json['id'] ??
                    0, // Provide a default value for id if it's null
                email: json['email'] ??
                    '', // Provide a default value for email if it's null
                profilePictureUrl: json['profilePictureUrl'] as String? ??
                    "https://i.stack.imgur.com/l60Hf.png",
                firstName: json['firstName'] ??
                    '', // Provide a default value for firstName if it's null
                lastName: json['lastName'] ??
                    '', // Provide a default value for lastName if it's null
              ))
          .toList();
    } else {
      throw Exception(
          'Failed to load non following coaches. Status code: ${response.statusCode}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Follow New Coaches'),
      ),
      body: areCoachesLoading
          ? Center(child: CircularProgressIndicator())
          : fetchCoachesErrorMessage.isNotEmpty
              ? Center(child: Text(fetchCoachesErrorMessage))
              : GridView.builder(
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2, // Two items per row
                    crossAxisSpacing: 10.0,
                    mainAxisSpacing: 10.0,
                    childAspectRatio: 0.75, // Adjust as needed
                  ),
                  itemCount: coaches.length,
                  itemBuilder: (context, index) {
                    return buildCoachCard(coaches[index]);
                  },
                ),
    );
  }

  Widget buildCoachCard(User coach) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: <Widget>[
        const SizedBox(height: 20),
        InkWell(
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => CoachProfile(
                  coach: coach,
                ),
              ),
            );
          },
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: CircleAvatar(
              radius: 50,
              backgroundImage: NetworkImage(coach.profilePictureUrl),
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(bottom: 8.0),
          child: Text(
            '${coach.firstName} ${coach.lastName}',
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
        ),
        Text(
          coach.email,
          style: TextStyle(fontSize: 12),
        ),
      ],
    );
  }
}
