import 'dart:ui'; // For ImageFilter
import 'dart:io'; // For File
import 'package:fittnes_frontend/pages/paypal_subscription_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:image_picker/image_picker.dart';
import 'package:url_launcher/url_launcher.dart';

class UserInformation extends StatefulWidget {
  const UserInformation({super.key});

  @override
  State<UserInformation> createState() => _UserInformationState();
}

class _UserInformationState extends State<UserInformation> {
  File? _profileImage;

  Future<void> _pickImage() async {
    try {
      final pickedFile =
          await ImagePicker().pickImage(source: ImageSource.gallery);
      if (pickedFile != null) {
        setState(() {
          _profileImage = File(pickedFile.path);
        });
      }
    } catch (e) {
      // Handle any errors that occur during image selection
      print(e); // For debugging
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Failed to pick image'),
        ),
      );
    }
  }

  Future<void> _launchAsInAppWebViewWithCustomHeaders(Uri url) async {
    final bool result = await launchUrl(
      url,
    );

    if (!result) {
      throw Exception('Could not launch $url');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
          title: Text(
            'User Information',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.w500,
            ),
          ),
          centerTitle: true,
          toolbarHeight: 30,
          backgroundColor: Colors.blue,
          elevation: 4),
      body: Stack(
        fit: StackFit.expand, // Ensure the stack fills the screen
        children: <Widget>[
          // Blurred image background
          Container(
            decoration: BoxDecoration(
              image: DecorationImage(
                image: AssetImage(
                    'lib/images/fittness_background.jpg'), // Replace with your image path
                fit: BoxFit.cover,
              ),
            ),
          ),
          BackdropFilter(
            filter: ImageFilter.blur(
                sigmaX: 3.0, sigmaY: 3.0), // Adjust the blur intensity
            child: Container(
              color:
                  Colors.black.withOpacity(0.5), // Darken the background a bit
            ),
          ),
          // Content
          SingleChildScrollView(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                GestureDetector(
                  onTap: _pickImage,
                  child: CircleAvatar(
                    radius: 60,
                    backgroundColor: Colors.grey.shade200,
                    backgroundImage: _profileImage != null
                        ? FileImage(_profileImage!)
                        : null,
                    child: _profileImage == null
                        ? const Icon(
                            Icons.camera_alt,
                            color: Colors.grey,
                            size: 50,
                          )
                        : null,
                  ),
                ),
                const SizedBox(height: 24),
                Text(
                  'Tap to change profile picture',
                  style: TextStyle(fontSize: 18, color: Colors.white),
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: () {
                    // Handle "Add Workout" button tap
                  },
                  child: Text('Add Workout'),
                  style: ElevatedButton.styleFrom(
                    foregroundColor: Colors.white,
                    backgroundColor: Colors.blue, // Text color
                  ),
                ),
                const SizedBox(height: 16),
                ElevatedButton(
                  child: Text('Go Premium'),
                  style: ElevatedButton.styleFrom(
                    foregroundColor: Colors.white,
                    backgroundColor: Colors.green,
                    // Text color
                  ),
                  onPressed: () async {
                    const url =
                        'http://192.168.199.182:8080/api/selfCoach/paypal/getPayPalSubscriptionButton';
                    await _launchAsInAppWebViewWithCustomHeaders(
                        Uri.parse(url));
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
