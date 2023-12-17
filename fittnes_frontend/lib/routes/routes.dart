import 'package:fittnes_frontend/components/bottom_nav.dart';
import 'package:fittnes_frontend/models/exercise.dart';
import 'package:fittnes_frontend/pages/discover_page.dart';
import 'package:fittnes_frontend/pages/exercise_page.dart';
import 'package:fittnes_frontend/pages/home_page.dart';
import 'package:fittnes_frontend/pages/login_page.dart';
import 'package:fittnes_frontend/pages/profile_page.dart';
import 'package:fittnes_frontend/pages/register_page.dart';
import 'package:fittnes_frontend/pages/search_page.dart';
import 'package:fittnes_frontend/pages/start_exercise_page.dart';
import 'package:get/get.dart';

class AppPage {
  static List<GetPage> routes = [
    GetPage(
      name: navbar,
      page: () => const NavBar(),
      children: [
        GetPage(name: home, page: () => HomePage()),
        GetPage(name: discover, page: () => const DiscoverPage()),
        GetPage(name: search, page: () => const SearchPage()),
        GetPage(name: profile, page: () => const ProfilePage()),
      ],
    ),
    GetPage(name: login, page: () => LoginPage()),
    GetPage(name: register, page: () => const Register()),
  //you should add teh routes for start_exercise_page and exercise_page
  ];

  static getNavbar() => navbar;
  static getHome() => home;
  static getDiscover() => discover;
  static getSearch() => search;
  static getProfile() => profile;
  static getLogin() => login;
  static getRegister() => register;

  static GetPage getExercisePage(List<Exercise> exercises, String workoutName, int workoutId){
    return GetPage(
      name: exercisePage,
      page: () => ExercisePage(exercises: exercises, workoutName: workoutName,workoutId: workoutId,),
      transition: Transition.fadeIn, // Adjust as needed
    );
  }

  // static GetPage getStartExercisePage(List<Exercise> exercises, int exerciseIndex, String workoutName) {
  //   return GetPage(
  //     name: startExercisePage,
  //     page: () => StartExercisePage(exercises: exercises, exerciseIndex: exerciseIndex, workoutName: workoutName),
  //     transition: Transition.fadeIn, // Adjust as needed
  //   );
  // }

  static String navbar = "/";
  static String home = "/home";
  static String discover = "/discover";
  static String search = "/search";
  static String profile = "/profile";
  static String login = "/login";
  static String register = "/register";
  static String exercisePage = "/exercise_page";
  static String startExercisePage = "/start_exercise_page";
}
