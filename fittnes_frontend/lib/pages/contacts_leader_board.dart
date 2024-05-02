import 'dart:convert';

import 'package:fittnes_frontend/components/leader_board_widget.dart';
import 'package:fittnes_frontend/models/leader_board.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class ContactsLeaderBoard extends StatefulWidget {
  const ContactsLeaderBoard({super.key});

  @override
  State<ContactsLeaderBoard> createState() => _ContactsLeaderBoardState();
}

class _ContactsLeaderBoardState extends State<ContactsLeaderBoard> {
  List<String> contactsEmail = [];
  String requestParamEmails = "";
  List<LeaderBoard> leaderBoardEntries = [];

  @override
  void initState() {
    super.initState();
    fetchData();
  }

  Future<void> fetchData() async {
    await getGoogleContacts();
    await loadContactsLeaderBoardEntries();
  }

  Future<void> loadContactsLeaderBoardEntries() async {
    try {
      List<LeaderBoard> fetchedLeaderBoardEntries =
          await getContactsLeaderBoardEntries();
      setState(() {
        leaderBoardEntries = fetchedLeaderBoardEntries;
      });
    } catch (e) {
      print('Error fetching personal records: $e');
    }
  }

  Future<List<LeaderBoard>> getContactsLeaderBoardEntries() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();

    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final url = Uri.parse(
      'http://192.168.54.182:8080/api/selfCoach/user/getContactsLeaderBoard?email=${Uri.encodeComponent(requestParamEmails)}',
    );

    print(url);

    final response = await http.get(
      url,
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

  Future<void> getGoogleContacts() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? authHeaderJson = await storage.read(key: 'authHeader');

    if (authHeaderJson == null || authHeaderJson.isEmpty) {
      throw Exception('Authentication header is missing.');
    }

    Map<String, dynamic> authHeaderMap = jsonDecode(authHeaderJson);
    Map<String, String> authHeader = authHeaderMap.map(
      (key, value) => MapEntry(key, value.toString()),
    );

    final responseContacts = await http.get(
        Uri.parse(
            'https://people.googleapis.com/v1/people/me/connections?personFields=emailAddresses'),
        headers: authHeader);
    final responseOther = await http.get(
        Uri.parse(
            'https://people.googleapis.com/v1/otherContacts?readMask=emailAddresses'),
        headers: authHeader);

    if (responseOther.statusCode == 200) {
      // print(responseOther.body);
      setState(() {
        contactsEmail
            .addAll(getOtherContactsEmailAdressesFromJson(responseOther.body));
        updateRequestParamEmails();
      });
    } else {
      print(responseOther.statusCode);
    }
    if (responseContacts.statusCode == 200) {
      print(responseContacts.body);
      setState(() {
        contactsEmail
            .addAll(getContactsEmailAddressesFromJson(responseContacts.body));
        updateRequestParamEmails();
      });
    } else {
      print(responseContacts.statusCode);
    }
  }

  void updateRequestParamEmails() {
    requestParamEmails = contactsEmail.join(',');
  }

  List<String> getOtherContactsEmailAdressesFromJson(String jsonString) {
    List<String> emails = [];
    var json = jsonDecode(jsonString);

    var otherContacts = json['otherContacts'];

    if (otherContacts != null && otherContacts is List) {
      for (var contact in otherContacts) {
        var emailAddresses = contact['emailAddresses'];
        if (emailAddresses != null && emailAddresses is List) {
          for (var address in emailAddresses) {
            var value = address['value'];
            emails.add(value);
          }
        }
      }
    }
    return emails;
  }

  List<String> getContactsEmailAddressesFromJson(String jsonString) {
    List<String> emails = [];
    var json = jsonDecode(jsonString);
    var connections = json['connections'];
    if (connections != null && connections is List) {
      for (var connection in connections) {
        var emailAddresses = connection['emailAddresses'];
        if (emailAddresses != null && emailAddresses is List) {
          for (var address in emailAddresses) {
            var value = address['value'];
            emails.add(value);
          }
        }
      }
    }
    return emails;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Google Contacts Leaderboard'),
      ),
      body: FutureBuilder<List<LeaderBoard>>(
        future: getContactsLeaderBoardEntries(),
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
