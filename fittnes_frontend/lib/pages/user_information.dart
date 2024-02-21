import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

class UserInformation extends StatefulWidget {
  const UserInformation({super.key});

  @override
  State<UserInformation> createState() => _UserInformationState();
}

class _UserInformationState extends State<UserInformation> {
  File? _profileImage;

  Future<void> _pickImage() async {
    try {
      final pickedFile = await ImagePicker().pickImage(source: ImageSource.gallery);
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

@override
Widget build(BuildContext context) {
  return Container(
    alignment: Alignment.center,
    padding: const EdgeInsets.all(16.0),
    child: Padding(
      padding: const EdgeInsets.only(bottom: 100), // Add padding at the bottom
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          GestureDetector(
            onTap: _pickImage,
            child: CircleAvatar(
              radius: 60,
              backgroundColor: Colors.grey.shade200,
              backgroundImage: _profileImage != null ? FileImage(_profileImage!) : null,
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
            style: TextStyle(fontSize: 18, color: Colors.grey[600]),
          ),
        ],
      ),
    ),
  );
}

}