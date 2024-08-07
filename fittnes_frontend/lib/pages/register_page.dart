import 'dart:convert';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:fittnes_frontend/components/auth_button_tile.dart';
import 'package:fittnes_frontend/components/bottom_nav.dart';
import 'package:fittnes_frontend/components/my_button.dart';
import 'package:fittnes_frontend/components/my_textfield.dart';
import 'package:fittnes_frontend/components/square_tile.dart';
import 'package:fittnes_frontend/controller/user_controller.dart';
import 'package:fittnes_frontend/models/user_details.dart';
import 'package:fittnes_frontend/pages/details.dart';
import 'package:fittnes_frontend/pages/home_page.dart';
import 'package:fittnes_frontend/pages/login_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart';

class Register extends StatefulWidget {
  const Register({Key? key});

  @override
  State<Register> createState() => _RegisterState();
}

class _RegisterState extends State<Register> {
  final _scaffoldKey = GlobalKey<ScaffoldState>();

  final usernameController = TextEditingController();
  final passwordController = TextEditingController();
  final passwordRetypeController = TextEditingController();
  final emailController = TextEditingController();
  final firstNameController = TextEditingController();
  final lastNameController = TextEditingController();

  bool passwordsMatch = true;

  Future<void> registerWithGoogle() async {
    try {
      final UserDetails? user = await retrieveGoogleUser();
      if (user != null && mounted) {
        try {
          Map<String, String> headers = {
            'Content-Type': 'application/json',
          };
          Map<String, String> registerData = {
            'firstName':
                user.firstName ?? "", // Safe access using null-aware operator
            'lastName':
                user.lastName ?? "", // Safe access using null-aware operator
            'email': user.email ?? "", // Safe access using null-aware operator
            'role': "USER"
          };

          String loginDataJson = json.encode(registerData);

          Uri url =
              Uri.parse('https://www.fit-stack.online/api/v1/auth/registerGoogle');

          Response response =
              await post(url, headers: headers, body: loginDataJson);

          if (response.statusCode == 200) {
            final storage = FlutterSecureStorage();
            var data = jsonDecode(response.body);
            print('Login successfully');

            await storage.write(
                key: 'accessToken', value: data['access_token']);

            String? accessToken = await storage.read(key: 'accessToken');
            print(accessToken);

            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) =>
                    NavBar(), // Ensure NavBar is properly imported
              ),
            );
          } else if (response.statusCode == 400) {
            print('Failed');
            final snackBar = SnackBar(
              content: Text('Email is already in use'),
              backgroundColor: Colors.red,
            );
            ScaffoldMessenger.of(context).showSnackBar(snackBar);
          }
        } catch (e) {
          print(e.toString());
        }
      }
    } on FirebaseAuthException catch (error) {
      print(error.message);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(
        error.message ?? "Something went wrong",
      )));
    } catch (error) {
      print(error);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(
        error.toString(),
      )));
    }
  }

  Future<void> register(
      String firstName, lastName, email, password, BuildContext context) async {
    // Email validation regex
    final emailRegex = RegExp(
      r'^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$',
    );

    if (!emailRegex.hasMatch(email)) {
      final snackBar = SnackBar(
        content: Text('Email is not valid'),
        backgroundColor: Colors.red,
      );
      ScaffoldMessenger.of(context).showSnackBar(snackBar);
      return; // Return early if the email is not valid
    }
    try {
      Map<String, String> headers = {
        'Content-Type': 'application/json',
      };
      Map<String, String> registerData = {
        'firstName': firstName,
        'lastName': lastName,
        'email': email,
        'password': password,
        'role': "USER"
      };

      String loginDataJson = json.encode(registerData);

      Uri url = Uri.parse('https://www.fit-stack.online/api/v1/auth/register');

      Response response =
          await post(url, headers: headers, body: loginDataJson);

      if (response.statusCode == 200) //ok
      {
        final storage = FlutterSecureStorage();
        var data = jsonDecode(response.body.toString());
        print('Login successfully');

        await storage.write(key: 'accessToken', value: data['access_token']);

        String? accessToken = await storage.read(key: 'accessToken');

        print(accessToken);
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => Details(),
          ),
        );
      } else if (response.statusCode == 400) //bad request
      {
        print('Failed');
        final snackBar = SnackBar(
          content: Text('email is already in use'),
          backgroundColor: Colors.red,
        ); // You can customize the appearance

        ScaffoldMessenger.of(context).showSnackBar(snackBar);
      }
    } catch (e) {
      print(e.toString());
    }
  }

  void checkPasswordMatching() {
    final password = passwordController.text;
    final passwordRetype = passwordRetypeController.text;

    setState(() {
      passwordsMatch = password == passwordRetype;
    });
  }

  void _showPasswordMismatchSnackBar() {
    final snackBar = SnackBar(
      content: Text(
        'Passwords do not match',
        style: TextStyle(color: Colors.white),
      ),
      backgroundColor: Colors.red,
    );

    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,
      backgroundColor: Colors.grey[300],
      body: SafeArea(
        child: SingleChildScrollView(
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(height: 50),
                const Icon(Icons.lock, size: 100),
                const SizedBox(height: 50),
                Text(
                  "Get started with your fitness adventure",
                  style: TextStyle(
                    color: Colors.grey[700],
                    fontSize: 16,
                  ),
                ),
                const SizedBox(height: 20),
                MyTextField(
                  controller: firstNameController,
                  hintText: 'First Name',
                  obscureText: false,
                ),
                const SizedBox(height: 20),
                MyTextField(
                  controller: lastNameController,
                  hintText: 'Last Name',
                  obscureText: false,
                ),
                const SizedBox(height: 20),
                MyTextField(
                  controller: emailController,
                  hintText: 'Email',
                  obscureText: false,
                ),
                const SizedBox(height: 20),
                MyTextField(
                  controller: passwordController,
                  hintText: 'Password',
                  obscureText: true,
                ),
                const SizedBox(height: 20),
                Container(
                  child: MyTextField(
                    controller: passwordRetypeController,
                    hintText: 'Retype Password',
                    obscureText: true,
                  ),
                ),
                if (!passwordsMatch)
                  ElevatedButton(
                    style:
                        ElevatedButton.styleFrom(backgroundColor: Colors.red),
                    onPressed: () {
                      _showPasswordMismatchSnackBar();
                    },
                    child: Text(
                      'Passwords do not match',
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                const SizedBox(height: 20),
                MyButton(
                  buttonText: "Register",
                  onTap: () {
                    checkPasswordMatching();
                    if (passwordsMatch) {
                      register(
                        firstNameController.text.toString(),
                        lastNameController.text.toString(),
                        emailController.text.toString(),
                        passwordController.text.toString(),
                        context,
                      );
                    }
                  },
                ),
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
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    InkWell(
                      onTap: () async {
                        // Handle Google login
                        await registerWithGoogle();
                      },
                      child: AuthButtonTile(
                          imagePath: 'lib/images/google_logo.png'),
                    ),
 
                  ],
                ),
                const SizedBox(height: 10),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text('Do you already have an account?'),
                      SizedBox(
                        width: 4,
                      ),
                      GestureDetector(
                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) =>
                                  LoginPage(), // Replace with the actual registration page widget
                            ),
                          );
                        },
                        child: Text(
                          'Login',
                          style: TextStyle(
                            color: Colors.blue,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
