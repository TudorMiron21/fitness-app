import 'dart:convert';

import 'package:fittnes_frontend/components/bottom_nav.dart';
import 'package:fittnes_frontend/components/my_button.dart';
import 'package:fittnes_frontend/components/my_textfield.dart';
import 'package:fittnes_frontend/components/square_tile.dart';
import 'package:fittnes_frontend/pages/forgot_password_page.dart';
import 'package:fittnes_frontend/pages/home_page.dart';
import 'package:fittnes_frontend/pages/register_page.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:http/http.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class LoginPage extends StatefulWidget {
  LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final usernameController = TextEditingController();

  final passwordController = TextEditingController();

  //Sing user method
  void login(String email, password, BuildContext context) async {
    Map<String, String> headers = {
      'Content-Type': 'application/json',
    };

    try {
      Map<String, String> loginData = {
        'email': email,
        'password': password,
      };

      String loginDataJson = json.encode(loginData);

      Uri url = Uri.parse(
          'http://192.168.199.182:8080/api/v1/auth/login'); // Use Uri.http

      Response response =
          await post(url, headers: headers, body: loginDataJson);

      if (response.statusCode == 200) //ok
      {
        final storage = FlutterSecureStorage();

        var data = jsonDecode(response.body.toString());
        // print(data['access_token']);
        print('Login successfully');

        await storage.write(key: 'accessToken', value: data['access_token']);

        String? accessToken = await storage.read(key: 'accessToken');

        print(accessToken);
        // Redirect to the home page
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) =>
                NavBar(), // Replace with the actual home page widget
          ),
        );
      }
       else if (response.statusCode == 403) //FORBIDDEN
      {
          final snackBar = SnackBar(
          content: Text('wrong username/password'),
          backgroundColor: Colors.red,
        ); // You can customize the appearance

        ScaffoldMessenger.of(context).showSnackBar(snackBar);
      }
      
    } catch (e) {
      print(e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[300],
      body: SafeArea(
        child: SingleChildScrollView(
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(height: 50),
        
                //lock icon
                const Icon(Icons.lock, size: 100),
                const SizedBox(height: 50),
        
                //greetings
                Text(
                  "Wellcome Back! You have been missed.",
                  style: TextStyle(
                    color: Colors.grey[700],
                    fontSize: 16,
                  ),
                ),
        
                const SizedBox(height: 25),
        
                //username field
                MyTextField(
                  controller: usernameController,
                  hintText: 'Email',
                  obscureText: false,
                ),
                const SizedBox(height: 25),
        
                //password field
                MyTextField(
                  controller: passwordController,
                  hintText: 'Password',
                  obscureText: true,
                ),
        
                Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 25, vertical: 10),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      GestureDetector(
                        onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) =>
                                ForgotPasswordPage(), // Replace with the actual registration page widget
                          ),
                        );

                        },
                        child: Text('Forgot Password?',
                            style: TextStyle(color: Colors.blue,fontWeight:FontWeight.bold,)
                            ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 10),
                MyButton(
                    buttonText: "Sign in",
                    onTap: () {
                      login(usernameController.text.toString(),
                          passwordController.text.toString(), context);
                    }),
                const SizedBox(height: 20),
        
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 25.0),
                  child: Row(
                    children: [
                      Expanded(
                        child: Divider(
                          thickness: 0.5,
                          color: Colors.grey[400],
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 10),
                        child: Text(
                          'Or continue with:',
                          style: TextStyle(color: Colors.grey[700]),
                        ),
                      ),
                      Expanded(
                        child: Divider(
                          thickness: 0.5,
                          color: Colors.grey[400],
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 10),
        
                const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    //google button
                    SquareTile(imagePath: 'lib/images/google_logo.png'),
        
                    const SizedBox(width: 10),
                    //apple button
                    SquareTile(imagePath: 'lib/images/apple_logo.png'),
                  ],
                ),
        
                const SizedBox(
                  height: 10,
                ),
        
                //not a member. register now
                //
        
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text('not a memeber?'),
                    const SizedBox(
                      width: 4,
                    ),
                    GestureDetector(
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) =>
                                Register(), // Replace with the actual registration page widget
                          ),
                        );
                      },
                      child: const Text(
                        'Register now',
                        style: TextStyle(
                            color: Colors.blue, fontWeight: FontWeight.bold),
                      ),
                    )
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
