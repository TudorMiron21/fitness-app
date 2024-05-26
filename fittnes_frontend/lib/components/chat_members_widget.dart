import 'dart:convert';
import 'dart:io';

import 'package:fittnes_frontend/components/conversation_list.dart';
import 'package:fittnes_frontend/models/user.dart';
import 'package:fittnes_frontend/models/message.dart';
import 'package:fittnes_frontend/pages/private_chat.dart';
import 'package:fittnes_frontend/security/jwt_utils.dart';
import 'package:fittnes_frontend/utils/DateFormater.dart';
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
  List<User> followingCoaches = [];
  List<Message> lastMessages = [];
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
          'https://www.fit-stack.online/api/selfCoach/payingUser/getFollowingCoaches'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    sourceEmail = JwtUtils.extractSubject(accessToken);
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      setState(() {
        followingCoaches = data
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
      // 'http://localhost:8084',
      'https://chat-service.webpubsub.azure.com',

      IO.OptionBuilder()
          .setTransports(['websocket'])
          .setPath('/clients/socketio/hubs/Hub')
          .disableAutoConnect()
          // .setExtraHeaders(
          //     {HttpHeaders.authorizationHeader: 'Bearer $accessToken'})
          .build(),
    );
    socket.connect();

    // // Set up socket event listeners
    // socket.onConnect((_) {
    //   print('Connected to server');
    // });
  }

  Future<void> getLastMessages() async {
    final List<Future> fetchTasks = [];

    // Assuming each coach has an 'email' property
    for (final coach in followingCoaches) {
      final String coachEmail = coach.email;
      final Uri url = Uri.parse(
          'https://www.fit-stack.online/chatService/getLastMessage/$sourceEmail/$coachEmail');

      fetchTasks.add(http.get(url).then((response) {
        if (response.statusCode == 200) {
          final Map<String, dynamic> data = json.decode(response.body);
          if (data['success']) {
            if (data['lastMessage'] != null) {
              final Message lastMessage = Message.fromJson(data['lastMessage']);
              return lastMessage;
            } else {
              return Message(
                  id: '',
                  source_email: '',
                  destination_email: '',
                  text_content: 'no messages yet',
                  time: '',
                  read: true);
            }
          } else {
            throw Exception('Failed to load last message');
          }
        } else {
          // Handle the case where the response status is not 200 OK
          throw Exception('Failed to load last message');
        }
      }).catchError((error) {
        // Handle any errors here
        print('Error fetching last message: $error');
      }));
    }

    final results = await Future.wait(fetchTasks);
    lastMessages = results.whereType<Message>().toList();
  }

  @override
  void initState() {
    super.initState();
    initializeSocket();
    fetchFollowingCoaches().then((_) {
      // Only after fetchFollowingCoaches completes, call getLastMessages.
      getLastMessages();
    }).catchError((error) {
      // Handle errors if fetchFollowingCoaches fails
      print('Error fetching following coaches: $error');
    });
  }

  @override
  void dispose() {
    print('socket ${socket.id} disposed');
    socket.disconnect();
    super.dispose();
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
                child: ListView.builder(
                  itemCount: followingCoaches.length,
                  shrinkWrap: true,
                  padding: EdgeInsets.only(top: 16),
                  physics: NeverScrollableScrollPhysics(),
                  itemBuilder: (context, index) {
                    return ConversationList(
                      name: followingCoaches[index].firstName +
                          " " +
                          followingCoaches[index].lastName,
                      messageText: lastMessages[index].text_content,
                      imageUrl: followingCoaches[index].profilePictureUrl,
                      time: lastMessages[index].time != ''
                          ? getFormatedDate(
                              DateTime.parse(lastMessages[index].time))
                          : '',
                      isMessageRead: lastMessages[index].read,
                      socket: socket,
                      sourceEmail: sourceEmail,
                      destinationEmail: followingCoaches[index].email,
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
