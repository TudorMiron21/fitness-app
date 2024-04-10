import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:fittnes_frontend/models/message.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:socket_io_client/socket_io_client.dart' as IO;
import 'package:fittnes_frontend/utils/DateFormater.dart';

class PrivateChat extends StatefulWidget {
  const PrivateChat(
      {required this.socket,
      required this.roomName,
      required this.sourceEmail,
      required this.destinationEmail,
      required this.profilePictureUrl,
      required this.name,
      super.key});

  final IO.Socket socket;
  final String roomName;
  final String sourceEmail;
  final String destinationEmail;

  final String profilePictureUrl;
  final String name;

  @override
  State<PrivateChat> createState() => _PrivateChatState();
}

class _PrivateChatState extends State<PrivateChat> {
  List<Message> messageList = [];
  final TextEditingController _textController = TextEditingController();

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
        messageList.sort((a, b) => a.time.compareTo(b.time));
      });
    } else {
      throw Exception(
          'Failed to fetch messages. Status code: ${response.statusCode}');
    }
  }

  void connectToPrivateRoom() {
    widget.socket.emit('join_private_chat', widget.roomName);
  }

  void sendTextMessage() {
    DateTime currentTimeStamp = DateTime.now();
    Map<String, dynamic> messageData = {
      'room': widget.roomName,
      'source_email': widget.sourceEmail,
      'destination_email': widget.destinationEmail,
      'text_content': _textController.text,
      'timeStamp': currentTimeStamp.toIso8601String(),
    };

    widget.socket.emit("send_message", messageData);
    setState(() {
      messageList.add(Message(
          id: '',
          source_email: widget.sourceEmail,
          destination_email: widget.destinationEmail,
          text_content: _textController.text,
          time: getFormatedDate(currentTimeStamp),
          read: true));
    });

    _textController.clear();
  }

  void receiveTextMessage() {
    widget.socket.on(
      "receive_message",
      (data) {
        // Make sure that 'data' is a Map before trying to access its keys
        if (data is Map) {
          setState(() {
            messageList.add(Message(
              id: '',
              source_email: widget.destinationEmail,
              destination_email: widget.sourceEmail,
              // Use bracket notation to access values from the map
              text_content: data['text_content'],
              // Assuming you want to store the time as a String
              time: data['timeStamp'],
              read: true,
            ));
          });
        } else {
          // If data is not a Map, handle the case appropriately
          print('Error: received data is not a Map');
        }

        // For debugging purposes, print the data to the console
        print(data);
      },
    );
  }

  @override
  void initState() {
    super.initState();
    connectToPrivateRoom();
    fetchMessages(); // Fetch messages when the widget initializes
    receiveTextMessage();
  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(
        elevation: 0,
        automaticallyImplyLeading: false,
        backgroundColor: Colors.white,
        flexibleSpace: SafeArea(
          child: Container(
            padding: EdgeInsets.only(right: 16),
            child: Row(
              children: <Widget>[
                IconButton(
                  onPressed: () {
                    Navigator.pop(context);
                  },
                  icon: Icon(
                    Icons.arrow_back,
                    color: Colors.black,
                  ),
                ),
                SizedBox(
                  width: 2,
                ),
                CircleAvatar(
                  backgroundImage: NetworkImage(widget.profilePictureUrl),
                  maxRadius: 20,
                ),
                SizedBox(
                  width: 12,
                ),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                      Text(
                        widget.name,
                        style: TextStyle(
                            fontSize: 16, fontWeight: FontWeight.w600),
                      ),
                      SizedBox(
                        height: 6,
                      ),
                      Text(
                        "Online",
                        style: TextStyle(
                            color: Colors.grey.shade600, fontSize: 13),
                      ),
                    ],
                  ),
                ),
                Icon(
                  Icons.settings,
                  color: Colors.black54,
                ),
              ],
            ),
          ),
        ),
      ),
      body: Stack(
        children: <Widget>[
          ListView.builder(
            itemCount: messageList.length,
            shrinkWrap: true,
            padding: EdgeInsets.only(top: 10, bottom: 60),
            // Remove the physics property, or set it to AlwaysScrollableScrollPhysics()
            itemBuilder: (context, index) {
              return Container(
                padding:
                    EdgeInsets.only(left: 14, right: 14, top: 10, bottom: 10),
                child: Align(
                  alignment:
                      (messageList[index].source_email == widget.sourceEmail
                          ? Alignment.topLeft
                          : Alignment.topRight),
                  child: Container(
                    
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(15),
                      color: (messageList[index].destination_email ==
                              widget.destinationEmail
                          ? Colors.grey.shade200
                          : Colors.blue[200]),
                    ),
                    padding: EdgeInsets.all(10),
                    child: Text(
                      messageList[index].text_content,
                      style: TextStyle(fontSize: 15),
                    ),
                  ),
                ),
              );
            },
          ),
          Align(
            alignment: Alignment.bottomLeft,
            child: Container(
              padding: EdgeInsets.only(left: 10, bottom: 10, top: 10),
              height: 60,
              width: double.infinity,
              color: Colors.white,
              child: Row(
                children: <Widget>[
                  GestureDetector(
                    onTap: () {},
                    child: Container(
                      height: 30,
                      width: 30,
                      decoration: BoxDecoration(
                        color: Colors.lightBlue,
                        borderRadius: BorderRadius.circular(30),
                      ),
                      child: Icon(
                        Icons.add,
                        color: Colors.white,
                        size: 20,
                      ),
                    ),
                  ),
                  SizedBox(
                    width: 15,
                  ),
                  Expanded(
                    child: TextField(
                      controller: _textController,
                      decoration: InputDecoration(
                          hintText: "Write message...",
                          hintStyle: TextStyle(color: Colors.black54),
                          border: InputBorder.none),
                    ),
                  ),
                  SizedBox(
                    width: 15,
                  ),
                  FloatingActionButton(
                    onPressed: sendTextMessage,
                    child: Icon(
                      Icons.send,
                      color: Colors.white,
                      size: 18,
                    ),
                    backgroundColor: Colors.blue,
                    elevation: 0,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
