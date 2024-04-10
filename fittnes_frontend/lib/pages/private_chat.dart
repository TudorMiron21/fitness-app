import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:fittnes_frontend/models/message.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:socket_io_client/socket_io_client.dart' as IO;

class PrivateChat extends StatefulWidget {
  const PrivateChat(
      {required this.socket,
      required this.roomName,
      required this.sourceEmail,
      required this.destinationEmail,
      super.key});

  final IO.Socket socket;
  final String roomName;
  final String sourceEmail;
  final String destinationEmail;

  @override
  State<PrivateChat> createState() => _PrivateChatState();
}

class _PrivateChatState extends State<PrivateChat> {
  List<Message> messageList = [];

  Future<void> fetchMessages() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');
    if (accessToken == null || accessToken.isEmpty) {
      // Handle the case where the authToken is missing or empty
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8084/chatService/getMessages/${widget.sourceEmail}/${widget.destinationEmail}'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      // Parse the response body
      Map<String, dynamic> responseBody = json.decode(response.body);
      List<dynamic> data = responseBody['messages']; // Update the messageList
      setState(() {
        messageList = data.map((json) => Message.fromJson(json)).toList();
      });
    } else {
      throw Exception(
          'Failed to fetch messages. Status code: ${response.statusCode}');
    }
  }

  @override
  void initState() {
    super.initState();
    fetchMessages(); // Fetch messages when the widget initializes
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Private Chat'),
        backgroundColor: Colors.blue,
        toolbarHeight: 40,
      ),
      body: ListView.builder(
        itemCount: messageList.length,
        itemBuilder: (context, index) {
          final message = messageList[index];
          return ListTile(
            title: Text(message.text_content),
            subtitle: Text(message.time),
            // Customize ListTile according to your message model
          );
        },
      ),
    );
  }
}
