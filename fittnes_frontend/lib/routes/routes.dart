import 'package:fittnes_frontend/components/bottom_nav.dart';
import 'package:fittnes_frontend/pages/discover_page.dart';
import 'package:fittnes_frontend/pages/home_page.dart';
import 'package:fittnes_frontend/pages/login_page.dart';
import 'package:fittnes_frontend/pages/profile_page.dart';
import 'package:fittnes_frontend/pages/register_page.dart';
import 'package:fittnes_frontend/pages/search_page.dart';
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
  ];

  static getNavbar() => navbar;
  static getHome() => home;
  static getDiscover() => discover;
  static getSearch() => search;
  static getProfile() => profile;
  static getLogin() => login;
  static getRegister() => register;

  static String navbar = "/";
  static String home = "/home";
  static String discover = "/discover";
  static String search = "/search";
  static String profile = "/profile";
  static String login = "/login";
  static String register = "/register";
}
