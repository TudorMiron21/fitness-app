import 'package:fittnes_frontend/components/chat_members_widget.dart';
import 'package:fittnes_frontend/components/deny_access_chat_widget.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/security/jwt_utils.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ChatPage extends StatefulWidget {
  const ChatPage({super.key});

  @override
  State<ChatPage> createState() => _ChatPageState();
}

class _ChatPageState extends State<ChatPage> {
  late String accessToken = '';

  @override
  void initState() {
    super.initState();
    _fetchAccessToken();
  }

  Future<void> _fetchAccessToken() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    accessToken = await storage.read(key: 'accessToken') ?? '';
    if (accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return accessToken.isEmpty
        ? Center(
            child:
                CircularProgressIndicator()) // Show a loading indicator while fetching the access token
        : _buildUserBasedUI();
  }

  Widget _buildUserBasedUI() {
    final bool isPaying = JwtUtils.isPayingUser(accessToken);
    final bool isRegularUser = JwtUtils.isUser(accessToken);

    if (isPaying) {
      return ChatMembersWidget();
    } else if (isRegularUser) {
      return const DenyAccessChatWidget(
        textWidget: 'Go premium and chat with your favorite coaches',
      );
    } else {
      return const Text("No user");
    }
  }
}
