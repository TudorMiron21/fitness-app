import 'package:fittnes_frontend/models/workout_rewards.dart';
import 'package:fittnes_frontend/pages/last_workout_stats_page.dart';
import 'package:fittnes_frontend/pages/profile_page.dart';
import 'package:fittnes_frontend/routes/routes.dart';
import 'package:flutter/material.dart';
import 'package:confetti/confetti.dart';
import 'package:flutter/widgets.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';

class FinishWorkoutPage extends StatefulWidget {
  final WorkoutRewards workoutRewards;

  const FinishWorkoutPage({Key? key, required this.workoutRewards})
      : super(key: key);

  @override
  _FinishWorkoutPageState createState() => _FinishWorkoutPageState();
}

class _FinishWorkoutPageState extends State<FinishWorkoutPage>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _animation;
  late ConfettiController _confettiController;

  @override
  void initState() {
    super.initState();
    _confettiController =
        ConfettiController(duration: const Duration(seconds: 5));
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 3),
    );
    _animation = CurvedAnimation(
      parent: _animationController,
      curve: Curves.linear,
    );
    _animationController.forward();
    _confettiController.play();
  }

  @override
  void dispose() {
    _confettiController.dispose();
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Colors.blue, // Set the background color to blue
        child: Stack(
          children: [
            // Confetti animation
            Align(
              alignment: Alignment.bottomCenter,
              child: ConfettiWidget(
                confettiController: _confettiController,
                blastDirectionality: BlastDirectionality.explosive,
                particleDrag: 0.05,
                emissionFrequency: 0.05,
                numberOfParticles: 20,
                gravity: 0.05,
                shouldLoop: false,
                colors: const [
                  Colors.green,
                  Colors.blue,
                  Colors.pink,
                  Colors.orange,
                ],
              ),
            ),
            Align(
              alignment: Alignment.center,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    'Congratulations!',
                    style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 16),
                  Text(
                    'You earned ${widget.workoutRewards.numberOfPoints.ceil()} points!',
                    style: TextStyle(fontSize: 18),
                  ),
                  SizedBox(height: 16),
                  Text(
                    'Achievements:',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 8),
                  ListView.builder(
                    shrinkWrap: true,
                    itemCount: widget.workoutRewards.achievements.length,
                    itemBuilder: (context, index) {
                      final achievement =
                          widget.workoutRewards.achievements[index];
                      return Container(
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        margin:
                            EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        child: ListTile(
                          leading: ClipOval(
                            child: Image.network(
                              achievement.achievementPicturePath,
                              width: 48,
                              height: 48,
                              fit: BoxFit.cover,
                            ),
                          ),
                          title: Text(achievement.name),
                          subtitle: Text(achievement.description),
                          trailing:
                              Text('${achievement.numberOfPoints} points'),
                        ),
                      );
                    },
                  ),
                  ElevatedButton(
                    onPressed: () {
                      Get.toNamed('${AppPage.getNavbar()}');
                    },
                    child: Text('Home Page'),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
