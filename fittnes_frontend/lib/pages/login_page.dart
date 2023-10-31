
import 'package:fittnes_frontend/components/my_textfield.dart';
import 'package:flutter/material.dart';

class LoginPage extends StatelessWidget {
const LoginPage ({super.key});

@override
Widget build(BuildContext context) {
  return Scaffold(
    backgroundColor: Colors.grey[300],
    body: SafeArea( 
      child: Center(
      child:Column(
        children:[

         const SizedBox(height: 50),

        //lock icon
         const Icon(
            Icons.lock,
            size: 100
          ),   
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
        const MyTextField(),
        const SizedBox(height: 25),

        //password field
        const MyTextField()
      ],
    ),
    ),
    ),
  );

}
}