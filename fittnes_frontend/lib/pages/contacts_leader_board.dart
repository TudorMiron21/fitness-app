import 'dart:convert';

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

  @override
  void initState() {
    super.initState();
    getGoogleContacts();
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

    final response = await http.get(
        Uri.parse(
            'https://people.googleapis.com/v1/otherContacts?readMask=names,emailAddresses'),
        headers: authHeader);
    if (response.statusCode == 200) {
      print(response.body);
      setState(() {
        contactsEmail = getEmailAdressesFromJson(response.body);
      });
    } else {
      print(response.statusCode);
    }
  }

  List<String> getEmailAdressesFromJson(String jsonString) {
    List<String> emails = [];
    var json = jsonDecode(jsonString);
    // Access the email addresses
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

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: contactsEmail.length,
      itemBuilder: (context, index) {
        return ListTile(
          title: Text(contactsEmail[index]),
        );
      },
    );
  }
}
