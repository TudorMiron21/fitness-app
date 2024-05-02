import 'package:fittnes_frontend/models/leader_board.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LeaderBoardWidget extends StatefulWidget {
  final List<LeaderBoard> leaderBoardList;

  const LeaderBoardWidget({Key? key, required this.leaderBoardList})
      : super(key: key);

  @override
  State<LeaderBoardWidget> createState() => _LeaderBoardWidgetState();
}

class _LeaderBoardWidgetState extends State<LeaderBoardWidget> {
  int displayedItems = 4; // Number of initially displayed items
  final int loadMoreIncrement =
      5; // Number of items to load on "Load More" button press

  @override
  void initState() {
    super.initState();
    setState(() {
      print(widget.leaderBoardList.length);
      displayedItems = widget.leaderBoardList.length.clamp(0, 4);
    });
  }

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: displayedItems + 1, // Add 1 for "Load More" button
      itemBuilder: (context, index) {
        if (index >= displayedItems) {
          // Render "Load More" button if index exceeds the displayedItems count
          return TextButton(
            onPressed: () {
              setState(() {
                displayedItems += loadMoreIncrement;
                if (displayedItems >= widget.leaderBoardList.length)
                  displayedItems = widget.leaderBoardList.length;
              });
            },
            child: Text(
              'Load More',
              style: TextStyle(color: Colors.blue),
            ),
          );
        }

        final leaderBoard = widget.leaderBoardList[index];
        Color? backgroundColor;
        IconData? trophyIcon;
        if (index == 0) {
          backgroundColor = Colors.amber.withOpacity(0.8);
          trophyIcon = Icons.emoji_events;
        } else if (index == 1) {
          backgroundColor = Colors.grey.withOpacity(0.8);
          trophyIcon = Icons.emoji_events;
        } else if (index == 2) {
          backgroundColor = Colors.brown.withOpacity(0.8);
          trophyIcon = Icons.emoji_events;
        } else {
          backgroundColor = Colors.black.withOpacity(0.8);
        }
        return Padding(
          padding: EdgeInsets.symmetric(
              vertical: 2.0), // Add vertical spacing between list items
          child: Container(
            decoration: BoxDecoration(
              color: backgroundColor,
              borderRadius: BorderRadius.circular(10.0),
            ),
            child: ListTile(
              tileColor: Colors.transparent,
              leading: trophyIcon != null
                  ? Icon(
                      trophyIcon,
                      color: Colors.white,
                    )
                  : null,
              title: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '${index + 1}. ${leaderBoard.user.firstName} ${leaderBoard.user.lastName}',
                    style: TextStyle(
                      fontWeight:
                          index < 3 ? FontWeight.bold : FontWeight.normal,
                      color: Colors.white,
                    ),
                  ),
                  Text(
                    leaderBoard.user.email,
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 12
                    ),
                  ),
                ],
              ),
              subtitle: Text(
                'Points: ${leaderBoard.numberOfPoints}',
                style: TextStyle(
                  color: Colors.white,
                ),
              ),
              trailing: Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    'Exercises: ${leaderBoard.numberOfDoneExercises}',
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                  Text(
                    'Workouts: ${leaderBoard.numberOfDoneWorkouts}',
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                  Text(
                    'Programs: ${leaderBoard.numberOfDonePrograms}',
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
