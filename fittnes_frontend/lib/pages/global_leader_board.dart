import 'dart:convert';

import 'package:fittnes_frontend/components/leader_board_widget.dart';
import 'package:fittnes_frontend/models/leader_board.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class GlobalLeaderBoard extends StatefulWidget {
  const GlobalLeaderBoard({super.key});

  @override
  State<GlobalLeaderBoard> createState() => _GlobalLeaderBoardState();
}

class _GlobalLeaderBoardState extends State<GlobalLeaderBoard> {
  List<LeaderBoard> leaderBoardEntries = [];
  @override
  void initState() {
    super.initState();
    loadLeaderBoardEntries();
  }

  Future<void> loadLeaderBoardEntries() async {
    try {
      List<LeaderBoard> fetchedLeaderBoardEntries =
          await getLeaderBoardEntries();
      setState(() {
        leaderBoardEntries = fetchedLeaderBoardEntries;
      });
    } catch (e) {
      print('Error fetching personal records: $e');
    }
  }

  Future<List<LeaderBoard>> getLeaderBoardEntries() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();

    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getLeaderBoardEntries'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> jsonDataList = json.decode(response.body);
      return LeaderBoard.fromJsonList(jsonDataList);
    } else {
      throw Exception('Failed to load personal records');
    }
  }

  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Global Leaderboard'),
      ),
      body: FutureBuilder<List<LeaderBoard>>(
        future: getLeaderBoardEntries(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(
              child: CircularProgressIndicator(),
            );
          } else if (snapshot.hasData) {
            List<LeaderBoard> leaderBoardEntries = snapshot.data!;
            return LeaderBoardWidget(leaderBoardList: leaderBoardEntries);
          } else if (snapshot.hasError) {
            return Center(
              child: Text('Error loading leader board entries'),
            );
          } else {
            return Center(
              child: Text('No data available'),
            );
          }
        },
      ),
    );
  }
}
