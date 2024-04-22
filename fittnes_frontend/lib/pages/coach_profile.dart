import 'dart:convert';

import 'package:fittnes_frontend/models/coach.dart';
import 'package:fittnes_frontend/models/coach_details.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class CoachProfile extends StatefulWidget {
  final Coach coach;
  const CoachProfile({super.key, required this.coach});

  @override
  State<CoachProfile> createState() => _CoachProfileState();
}

class _CoachProfileState extends State<CoachProfile> {
  bool isCoachFollowedByUser = false;
  late int numberOfFollowers;
  CoachDetails coachDetails = CoachDetails(
      numberOfSubscribers: 0,
      numberOfExercises: 0,
      numberOfWorkouts: 0,
      numberOfPrograms: 0,
      numberOfChallenges: 0);

  @override
  void initState() {
    super.initState();
    initIsCoachFollowedByUser();
    initCoachDetails();
  }

  Future<void> initIsCoachFollowedByUser() async {
    try {
      bool fetchedResponse =
          await checkIfCoachIsFollowedByUser(widget.coach.id);
      setState(() {
        isCoachFollowedByUser = fetchedResponse;
      });
    } catch (e) {
      print('Error fetching response: $e');
    }
  }

  Future<bool> checkIfCoachIsFollowedByUser(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    try {
      final response = await http.get(
        Uri.parse(
            'http://localhost:8080/api/selfCoach/payingUser/isCoachFollowedByUser/$coachId'),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data is bool) {
          return data;
        } else {
          throw Exception('Unexpected data type received from the server.');
        }
      } else {
        throw Exception(
            'Failed to load following status. Status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to check if coach is followed by user: $e');
    }
  }

  Future<void> initCoachDetails() async {
    try {
      CoachDetails fetchedCoachDetails = await getCoachDetails(widget.coach.id);
      setState(() {
        coachDetails = fetchedCoachDetails;
      });
    } catch (e) {
      print('Error fetching response: $e');
    }
  }

  Future<CoachDetails> getCoachDetails(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    try {
      final response = await http.get(
        Uri.parse(
            'http://localhost:8080/api/selfCoach/payingUser/getCoachDetails/$coachId'),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return CoachDetails.fromJson(data);
      } else {
        throw Exception(
            'Failed to load coach details. Status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to fetch coach details $e');
    }
  }

  Future<void> toggleFollowCoach(int coachId) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    setState(() {
      isCoachFollowedByUser = !isCoachFollowedByUser;
      if (isCoachFollowedByUser == true) {
        coachDetails.numberOfSubscribers += 1;
      } else {
        coachDetails.numberOfSubscribers -= 1;
      }
    });

    try {
      final response = await http.put(
        Uri.parse(
            'http://localhost:8080/api/selfCoach/payingUser/toggleFollowCoach/$coachId'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        print(response.body);
      } else {
        setState(() {
          isCoachFollowedByUser = !isCoachFollowedByUser;
          if (isCoachFollowedByUser == true) {
            coachDetails.numberOfSubscribers += 1;
          } else {
            coachDetails.numberOfSubscribers -= 1;
          }
        });
        throw Exception(
            'Failed to load following status. Status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to check if coach is followed by user: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Center(
          child: RichText(
            text: TextSpan(
              text: '${widget.coach.firstName} ${widget.coach.lastName}',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.black, // This should match AppBar's brightness
              ),
              children: <TextSpan>[
                TextSpan(
                  text: ' (${widget.coach.email})',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.normal,
                    color:
                        Colors.black.withOpacity(0.7), // Lighter and not bold
                  ),
                ),
              ],
            ),
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  CircleAvatar(
                    radius: 60,
                    backgroundImage:
                        NetworkImage(widget.coach.profilePictureUrl),
                    backgroundColor: Colors.transparent,
                  ),
                  SizedBox(width: 20),
                  Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Row(
                        children: <Widget>[
                          Icon(Icons.sports_gymnastics, color: Colors.black),
                          Text('Exercises: ${coachDetails.numberOfExercises}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.fitness_center, color: Colors.blue),
                          Text('Workouts: ${coachDetails.numberOfWorkouts}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.list_alt, color: Colors.green),
                          Text('Programs: ${coachDetails.numberOfPrograms}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.access_time, color: Colors.red),
                          Text('Challenges: ${coachDetails.numberOfChallenges}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                      Row(
                        children: <Widget>[
                          Icon(Icons.person, color: Colors.purple),
                          Text(
                              'Subscribers: ${coachDetails.numberOfSubscribers}',
                              style: TextStyle(fontSize: 16)),
                        ],
                      ),
                    ],
                  ),
                ],
              ),
              SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  ElevatedButton(
                    onPressed: () async {
                      await toggleFollowCoach(widget.coach.id);
                    },
                    child: Text(isCoachFollowedByUser ? 'Unfollow' : 'Follow'),
                  ),
                  SizedBox(width: 20),
                  isCoachFollowedByUser
                      ? ElevatedButton(
                          onPressed: ()  {},
                          child: const Text("Send Message"),
                        )
                      : SizedBox(),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
