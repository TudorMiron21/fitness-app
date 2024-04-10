import 'package:flutter/material.dart';
import 'package:search_choices/search_choices.dart';

class Message {
  final String id;
  final String source_email;
  final String destination_email;
  final String text_content;
  final String time;

  Message({
    required this.id,
    required this.source_email,
    required this.destination_email,
    required this.text_content,
    required this.time,
  });

  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      id: json['id'] ?? '',
      source_email: json['source_email'] ?? '',
      destination_email: json['destination_email']?? '',
      text_content: json['text_content'] ?? '',
      time: json['time'] ?? '',
    );
  }
}
