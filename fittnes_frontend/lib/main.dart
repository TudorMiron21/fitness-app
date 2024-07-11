import 'package:firebase_core/firebase_core.dart';
import 'package:fittnes_frontend/firebase_options.dart';
import 'package:fittnes_frontend/routes/routes.dart';
import 'package:fittnes_frontend/security/token_verification.dart';
import 'package:flutter/material.dart';
import 'pages/login_page.dart';
import 'package:get/get.dart';
import 'dart:io';

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
class MyHttpOverrides extends HttpOverrides {
  @override
  HttpClient createHttpClient(SecurityContext? context) {
    return super.createHttpClient(context)
      ..badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
  }
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  // Check token validity
  bool isTokenValid =
      await TokenChecker.checkTokenValidity(); // Implement this function

  // Determine the initial route based on token validity
  //String initialRoute = isTokenValid ? AppPage.getNavbar() : AppPage.getLogin();
  // String initialRoute = AppPage.getLogin();
  String initialRoute = isTokenValid ? AppPage.getNavbar() : AppPage.getLogin();

  HttpOverrides.global = MyHttpOverrides();

  runApp(GetMaterialApp(
    debugShowCheckedModeBanner: false,
    initialRoute: initialRoute,
    getPages: AppPage.routes,
  ));
}
