import 'package:fittnes_frontend/controller/controller.dart';
import 'package:fittnes_frontend/pages/discover_page.dart';
import 'package:fittnes_frontend/pages/home_page.dart';
import 'package:fittnes_frontend/pages/profile_page.dart';
import 'package:fittnes_frontend/pages/search_page.dart';
import 'package:fittnes_frontend/pages/stats_page.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class NavBar extends StatefulWidget {
  const NavBar({super.key});

  @override
  State<NavBar> createState() => _NavBarState();
}

class _NavBarState extends State<NavBar> {
  final controller = Get.put(NavBarController());
  @override
  Widget build(BuildContext context) {
    return GetBuilder<NavBarController>(builder: (context) {
      return Scaffold(
        body: IndexedStack(
          index: controller.tabIndex,
          children: [
            HomePage(),
            DiscoverPage(),
            SearchPage(),
            ProfilePage(),
          ],
        ),
        bottomNavigationBar: BottomNavigationBar(
          selectedItemColor: Colors.blue,
          unselectedItemColor: Colors.blue.shade200,

            currentIndex: controller.tabIndex,
            onTap: controller.changeTabIdex,
            items: [
              const BottomNavigationBarItem(
                  icon: Icon(Icons.home), label: 'Home'),
              const BottomNavigationBarItem(
                  icon: Icon(Icons.favorite), label: 'Discover'),
              const BottomNavigationBarItem(
                  icon: Icon(Icons.search), label: 'Search'),
              const BottomNavigationBarItem(
                  icon: Icon(Icons.face), label: 'Profile'),
            ]),
      );
    });
  }
}
