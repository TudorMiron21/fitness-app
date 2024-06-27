import 'package:fittnes_frontend/components/bottom_nav.dart';
import 'package:fittnes_frontend/pages/contacts_leader_board.dart';
import 'package:fittnes_frontend/pages/general_stats_page.dart';
import 'package:fittnes_frontend/pages/global_leader_board.dart';
import 'package:fittnes_frontend/pages/personal_records_page.dart';
import 'package:flutter/material.dart';
import 'package:fittnes_frontend/pages/last_workout_stats_page.dart';
import 'package:fittnes_frontend/pages/user_information.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({Key? key}) : super(key: key);

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage>
    with SingleTickerProviderStateMixin {
  PageController _pageController = PageController(initialPage: 0);
  int _currentPage = 0;
  List<Widget> pages = [];
  @override
  void initState() {
    super.initState();
    _currentPage = 0;
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  void _onPageChanged(int page) {
    setState(() {
      _currentPage = page;
    });
  }

  Widget _buildPageIndicator(bool isCurrentPage) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 4.0),
      child: CircleAvatar(
        radius: isCurrentPage ? 5.0 : 4.0,
        backgroundColor: isCurrentPage ? Colors.blue : Colors.grey,
      ),
    );
  }


  @override
  Widget build(BuildContext context) {

    bool showContactsLeaderBoard = false; // Example condition
      pages.add(UserInformation());
    
      pages.add(LastWorkoutStatsPage());
    
      pages.add(GeneralStatsPage());
      pages.add(PersonalRecordPage());

      pages.add(GlobalLeaderBoard());
    
    if (showContactsLeaderBoard) {
      pages.add(ContactsLeaderBoard());
    }

    return Scaffold(
      body: Column(
        children: [
          Expanded(
            child: PageView(
              controller: _pageController,
              onPageChanged: _onPageChanged,
              children: pages
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List<Widget>.generate(
                pages.length, // Replace with the actual number of pages
                (int index) => _buildPageIndicator(index == _currentPage),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
