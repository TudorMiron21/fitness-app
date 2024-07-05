import 'package:flutter/material.dart';
import 'package:fittnes_frontend/models/personal_record.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class PersonalRecordPage extends StatefulWidget {
  const PersonalRecordPage({super.key});

  @override
  State<PersonalRecordPage> createState() => _PersonalRecordPageState();
}

class _PersonalRecordPageState extends State<PersonalRecordPage> {
  List<PersonalRecord> records = [];

  @override
  void initState() {
    super.initState();
    loadPersonalRecords();
  }


  Future<void> loadPersonalRecords() async {
    try {
      List<PersonalRecord> fetchedRecords = await getPersonalRecordsForUser();
      setState(() {
        records = fetchedRecords;
      });
    } catch (e) {
      print('Error fetching personal records: $e');
    }
  }

  Future<List<PersonalRecord>> getPersonalRecordsForUser() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();

    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getPersonalRecordsForUser'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> jsonDataList = json.decode(response.body);
      return PersonalRecord.fromJsonList(jsonDataList);
    } else {
      throw Exception('Failed to load personal records');
    }
  }

  String getFormatedTime(int totalTimeInSeconds)
  {
        final int hours = totalTimeInSeconds ~/ 3600;
    final int minutes = (totalTimeInSeconds % 3600) ~/ 60;
    final int seconds = totalTimeInSeconds % 60;
    return
        '${hours.toString().padLeft(2, '0')}:${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
  }
  @override
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'Personal Records',
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.w500,
          ),
        ),
        centerTitle: true,
        toolbarHeight: 30,
        backgroundColor: Colors.blue,
        elevation: 4,
        automaticallyImplyLeading: false,
      ),
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: ConstrainedBox(
            constraints:
                BoxConstraints(minWidth: MediaQuery.of(context).size.width),
            child: Table(
              columnWidths: {
                0: FractionColumnWidth(0.3),
                1: FractionColumnWidth(0.14),
                2: FractionColumnWidth(0.14),
                3: FractionColumnWidth(0.14),
                4: FractionColumnWidth(0.14),
                5: FractionColumnWidth(0.14),
              },
              defaultVerticalAlignment: TableCellVerticalAlignment.middle,
              children: [
                TableRow(
                  children: [
                    Container(
                        height: 32,
                        color: Colors.grey,
                        child: Center(
                            child: Text(
                          'Exercise',
                          style: TextStyle(
                            fontWeight:
                                FontWeight.w600, // Specify the font size here
                          ),
                        ))),
                    Container(
                        height: 32,
                        color: Colors.grey,
                        child: Center(
                            child: Text(
                          'Weight',
                          style: TextStyle(
                            fontWeight:
                                FontWeight.w600, // Specify the font size here
                          ),
                        ))),
                    Container(
                        height: 32,
                        color: Colors.grey,
                        child: Center(
                            child: Text(
                          'Reps',
                          style: TextStyle(
                            fontWeight:
                                FontWeight.w600, // Specify the font size here
                          ),
                        ))),
                    Container(
                        height: 32,
                        color: Colors.grey,
                        child: Center(
                            child: Text(
                          'Time',
                          style: TextStyle(
                            fontWeight:
                                FontWeight.w600, // Specify the font size here
                          ),
                        ))),
                    Container(
                        height: 32,
                        color: Colors.grey,
                        child: Center(
                            child: Text(
                          'Volume',
                          style: TextStyle(
                            fontWeight:
                                FontWeight.w600, // Specify the font size here
                          ),
                        ))),
                    Container(
                        height: 32,
                        color: Colors.grey,
                        child: Center(
                            child: Text(
                          'Calories',
                          style: TextStyle(
                            fontWeight:
                                FontWeight.w600, // Specify the font size here
                          ),
                        ))),
                  ],
                ),
                ...records
                    .map((record) => TableRow(
                          children: [
                            Container(
                                color: Colors.blue.shade200,
                                height: 54,
                                child: Center(
                                    child: Text(
                                  record.exerciseName,
                                  style: const TextStyle(
                                    fontSize: 10,
                                    fontWeight: FontWeight
                                        .w500, // Specify the font size here
                                  ),
                                ))),
                            Container(
                                color: Colors.blue.shade100,
                                height: 54,
                                child: Center(
                                    child: Text(
                                  record.maxWeight.toString(),
                                  style: TextStyle(
                                    fontSize: 12, // Specify the font size here
                                  ),
                                ))),
                            Container(
                                color: Colors.blue.shade100,
                                height: 54,
                                child: Center(
                                    child: Text(
                                  record.maxNoReps?.toString() ?? 'N/A',
                                  style: TextStyle(
                                    fontSize: 12, // Specify the font size here
                                  ),
                                ))),
                            Container(
                                color: Colors.blue.shade100,
                                height: 54,
                                child: Center(
                                    child: Text(
                                  getFormatedTime(record.maxTime),
                                  style: TextStyle(
                                    fontSize: 12, // Specify the font size here
                                  ),
                                ))),
                            Container(
                                color: Colors.blue.shade100,
                                height: 54,
                                child: Center(
                                    child: Text(
                                  record.maxVolume?.toString() ?? 'N/A',
                                  style: TextStyle(
                                    fontSize: 12, // Specify the font size here
                                  ),
                                ))),
                            Container(
                                color: Colors.blue.shade100,
                                height: 54,
                                child: Center(
                                    child: Text(
                                  record.maxCalories?.toStringAsFixed(2) ?? 'N/A',
                                  style: TextStyle(
                                    fontSize: 12, // Specify the font size here
                                  ),
                                ))),
                          ],
                        ))
                    .toList(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget buildHeaderCell(String text) {
    return Container(
      height: 32,
      color: Colors.grey,
      child: Center(child: Text(text)),
    );
  }

  Widget buildDataCell(String text) {
    return Container(
      height: 48,
      child: Center(child: Text(text)),
    );
  }
}
