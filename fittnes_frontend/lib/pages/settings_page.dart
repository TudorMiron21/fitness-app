import 'package:curved_navigation_bar/curved_navigation_bar.dart';
import 'package:fittnes_frontend/pages/discover_page.dart';
import 'package:fittnes_frontend/pages/home_page.dart';
import 'package:fittnes_frontend/pages/profile_page.dart';
import 'package:fittnes_frontend/pages/search_page.dart';
import 'package:flutter/material.dart';

class SettingPage extends StatefulWidget {
  const SettingPage({super.key});

  @override
  State<SettingPage> createState() => _SettingPageState();
}

class _SettingPageState extends State<SettingPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      bottomNavigationBar: CurvedNavigationBar(
        animationDuration: const Duration(milliseconds: 300),
        backgroundColor: Colors.black,
        onTap: (index) {
          if (index == 0) {
            Navigator.push(
                context, MaterialPageRoute(builder: (context) => HomePage()));
          } else if (index == 1) {
            Navigator.push(context,
                MaterialPageRoute(builder: (context) => DiscoverPage()));
          } else if (index == 2) {
            Navigator.push(
                context, MaterialPageRoute(builder: (context) => SearchPage()));
          } else if (index == 3) {
            Navigator.push(context,
                MaterialPageRoute(builder: (context) => ProfilePage()));
          }
        },
        items: [
          Icon(Icons.home),
          Icon(Icons.favorite),
          Icon(Icons.settings),
          Icon(Icons.face)
        ],
      ),
    );
  }
}
