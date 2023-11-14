import 'package:fittnes_frontend/routes/routes.dart';
import 'package:fittnes_frontend/security/token_verification.dart';
import 'package:flutter/material.dart';
import 'pages/login_page.dart';
import 'package:get/get.dart';
// void main() {
//   runApp(const MyApp());
// }

// class MyApp extends StatelessWidget {
//   const MyApp({super.key});

//   @override
//   Widget build(BuildContext buildContext)
//   {
//     return MaterialApp(
//       debugShowCheckedModeBanner: false,
//       home: LoginPage()
//     );
//   }
// }

// void main() {

//   runApp(GetMaterialApp(
//     debugShowCheckedModeBanner: false,
//     initialRoute: AppPage.getNavbar(),
//     getPages: AppPage.routes,
//   ));
// }


void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Check token validity
  bool isTokenValid = await TokenChecker.checkTokenValidity(); // Implement this function

  // Determine the initial route based on token validity
  String initialRoute = isTokenValid ? AppPage.getNavbar() : AppPage.getLogin();

  runApp(GetMaterialApp(
    debugShowCheckedModeBanner: false,
    initialRoute: initialRoute,
    getPages: AppPage.routes,
  ));
}