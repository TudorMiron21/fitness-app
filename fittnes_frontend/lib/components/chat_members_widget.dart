import 'dart:convert';
import 'dart:io';

import 'package:fittnes_frontend/components/conversation_list.dart';
import 'package:fittnes_frontend/models/coach.dart';
import 'package:fittnes_frontend/pages/private_chat.dart';
import 'package:fittnes_frontend/security/jwt_utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:socket_io_client/socket_io_client.dart' as IO;

class ChatMembersWidget extends StatefulWidget {
  const ChatMembersWidget({super.key});

  @override
  State<ChatMembersWidget> createState() => _ChatMembersWidgetState();
}

class _ChatMembersWidgetState extends State<ChatMembersWidget> {
  List<Coach> followingCoaches = [];
  late IO.Socket socket;
  late String sourceEmail;

  Future<void> fetchFollowingCoaches() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/payingUser/getFollowingCoaches'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    sourceEmail = JwtUtils.extractSubject(accessToken);
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      setState(() {
        followingCoaches = data
            .map((json) => Coach(
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
      });
    } else {
      throw Exception(
          'Failed to load following coaches. Status code: ${response.statusCode}');
    }
  }

  Future<void> initializeSocket() async {
    // Get the access token from FlutterSecureStorage
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    // Initialize the socket with the access token
    socket = IO.io(
      'http://localhost:8084',
      IO.OptionBuilder()
          .setTransports(['websocket'])
          .disableAutoConnect()
          .setExtraHeaders(
              {HttpHeaders.authorizationHeader: 'Bearer $accessToken'})
          .build(),
    );
    socket.connect();

    // // Set up socket event listeners
    // socket.onConnect((_) {
    //   print('Connected to server');
    // });
  }

  @override
  void initState() {
    super.initState();
    initializeSocket();
    fetchFollowingCoaches(); // Fetch coaches when the widget initializes
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: <Widget>[
            // Any non-scrollable widgets go here, outside of Expanded
            SafeArea(
              child: Padding(
                padding: EdgeInsets.only(left: 16, right: 16, top: 10),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                    Text(
                      "Your Coaches",
                      style:
                          TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                    ),
                    Container(
                      padding:
                          EdgeInsets.only(left: 8, right: 8, top: 2, bottom: 2),
                      height: 30,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(30),
                        color: Colors.pink[50],
                      ),
                      child: Row(
                        children: <Widget>[
                          Icon(
                            Icons.add,
                            color: Colors.pink,
                            size: 20,
                          ),
                          SizedBox(
                            width: 2,
                          ),
                          Text(
                            "Add New",
                            style: TextStyle(
                                fontSize: 14, fontWeight: FontWeight.bold),
                          ),
                        ],
                      ),
                    )
                  ],
                ),
              ),
            ),
            Padding(
              padding: EdgeInsets.only(top: 16, left: 16, right: 16),
              child: TextField(
                decoration: InputDecoration(
                  hintText: "Search...",
                  hintStyle: TextStyle(color: Colors.grey.shade600),
                  prefixIcon: Icon(
                    Icons.search,
                    color: Colors.grey.shade600,
                    size: 20,
                  ),
                  filled: true,
                  fillColor: Colors.grey.shade100,
                  contentPadding: EdgeInsets.all(8),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(20),
                      borderSide: BorderSide(color: Colors.grey.shade100)),
                ),
              ),
            ),
            Expanded(
              child: Padding(
                padding: EdgeInsets.all(8.0),
                child:
                    // ListView.builder(
                    //   physics: BouncingScrollPhysics(),
                    //   itemCount: followingCoaches.length,
                    //   itemBuilder: (context, index) {
                    //     final coach = followingCoaches[index];
                    //     // Build UI for each coach
                    //     return ListTile(
                    //       leading: CircleAvatar(
                    //         backgroundImage: AssetImage(coach.profilePictureUrl),
                    //       ),
                    //       title: Text(coach.firstName + ' ' + coach.lastName),
                    //       subtitle: Text(coach.email),
                    //       onTap: () {
                    //         Navigator.push(
                    //           context,
                    //           MaterialPageRoute(
                    //             builder: (context) => PrivateChat(
                    //               socket: socket,
                    //               roomName: '',
                    //               sourceEmail: sourceEmail,
                    //               destinationEmail: coach.email,
                    //             ),
                    //           ),
                    //         );
                    //       },
                    //     );
                    //   },
                    // ),
                    ListView.builder(
                  itemCount: followingCoaches.length,
                  shrinkWrap: true,
                  padding: EdgeInsets.only(top: 16),
                  physics: NeverScrollableScrollPhysics(),
                  itemBuilder: (context, index) {
                    return ConversationList(
                      name: followingCoaches[index].firstName +
                          " " +
                          followingCoaches[index].lastName,
                      messageText: '',
                      imageUrl: followingCoaches[index].profilePictureUrl,
                      time: "",
                      isMessageRead: true,
                    );
                  },
                ),
              ),
            ),

            // Any additional widgets go here, after the Expanded
          ],
        ),
      ),
    );
  }
}
