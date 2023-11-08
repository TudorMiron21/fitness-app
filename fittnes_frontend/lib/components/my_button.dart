import 'package:flutter/material.dart';


class MyButton extends StatelessWidget {

  Function()? onTap;
  final String buttonText;
  MyButton({
    Key? key,
    required this.buttonText,
    required this.onTap,
  });
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: EdgeInsets.all(25),
        margin: EdgeInsets.symmetric(horizontal: 25),
        decoration: BoxDecoration(
          color: Colors.black,
          borderRadius: BorderRadius.circular(8)
          ),
        child: Center(
          child: Text(
            this.buttonText,
            style: TextStyle(color: Colors.white),
    
            ),
        ),
      ),
    );
  }
}