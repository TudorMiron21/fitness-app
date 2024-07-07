import 'dart:convert';
import 'dart:typed_data';
// import 'dart:ffi';
import 'dart:ui'; // For ImageFilter
import 'dart:io'; // For File
// import 'dart:ui_web';
import 'package:fittnes_frontend/models/achievement.dart';
import 'package:fittnes_frontend/models/leader_board.dart';
import 'package:fittnes_frontend/models/user.dart';
import 'package:fittnes_frontend/models/user_details.dart';
import 'package:fittnes_frontend/pages/create_workout_page.dart';
import 'package:fittnes_frontend/pages/details.dart';
import 'package:fittnes_frontend/pages/login_page.dart';
import 'package:fittnes_frontend/pages/paypal_subscription_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:image_picker/image_picker.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:http/http.dart' as http;
import '../models/user_details.dart';
import 'package:http_parser/http_parser.dart'; // Import for MediaType

class UserInformation extends StatefulWidget {
  const UserInformation({super.key});

  @override
  State<UserInformation> createState() => _UserInformationState();
}

class _UserInformationState extends State<UserInformation> {
  File? _profileImage;
  String profilePictureUrl = '';
  List<Achievement> userAchievements = [];
  LeaderBoard leaderBoardEntry = LeaderBoard(
      id: -1,
      user: User(
          id: -1,
          email: '',
          firstName: '',
          lastName: '',
          profilePictureUrl: ''));

  @override
  void initState() {
    super.initState();
    fetchUserData(); // Fetch data on initialization
    _fetchUserProfilePicture();
  }

Future<void> _fetchUserProfilePicture() async {
  await fetchUserProfilePicture();
  setState(() {
  });
}

  Future<void> fetchUserProfilePicture() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://192.168.54.182:8080/api/selfCoach/user/getProfilePicture'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );
    if (response.statusCode == 200) {
      profilePictureUrl = response.body;
    }

    if (response.statusCode == 400)
      throw Exception("User has no profile picture");
  }

  Future<void> fetchUserData() async {
    try {
      userAchievements = await getUserAchievements();
      leaderBoardEntry = await getLeaderBoardEntryForUser();
      setState(() {}); // Update the UI with fetched data
    } catch (e) {
      print(e); // Handle error
    }
  }

  Future<LeaderBoard> getLeaderBoardEntryForUser() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse(
          'http://localhost:8080/api/selfCoach/user/getLeaderBoardEntryForUser'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      dynamic jsonResponse = json.decode(response.body);
      return LeaderBoard.fromJson(jsonResponse);
    } else if (response.statusCode == 202) //accepted
    {
      throw Exception(
          'User has no Leader Board entry. Status code: ${response.statusCode}');
    } else {
      throw Exception(
          'Failed to load Leader Board entry. Status code: ${response.statusCode}');
    }
  }

  Future<List<Achievement>> getUserAchievements() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    final response = await http.get(
      Uri.parse('http://localhost:8080/api/selfCoach/user/getUserAchievements'),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> jsonResponse = json.decode(response.body);
      return jsonResponse.map<Achievement>((json) {
        return Achievement.fromJson(json);
      }).toList();
    } else {
      throw Exception(
          'Failed to load achievements. Status code: ${response.statusCode}');
    }
  }

  Future<void> uploadProfilePicture(File imageFile) async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    String? accessToken = await storage.read(key: 'accessToken');

    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('Authentication token is missing or invalid.');
    }

    var request = http.MultipartRequest(
      'PUT',
      Uri.parse(
          'http://192.168.54.182:8080/api/selfCoach/user/uploadProfilePicture'),
    );

    request.headers.addAll({
      'Authorization': 'Bearer $accessToken',
    });

    request.files.add(
      await http.MultipartFile.fromPath(
        'file',
        imageFile.path,
        contentType: MediaType('image', 'png'), // Adjust as needed
      ),
    );

    // Send request
    var response = await request.send();

    // Check response
    if (response.statusCode == 200) {
      print('Profile picture uploaded successfully');
    } else {
      print(
          'Failed to upload profile picture. Status code: ${response.statusCode}');
    }
  }

  Future<void> _pickImage() async {
    try {
      final pickedFile =
          await ImagePicker().pickImage(source: ImageSource.gallery);
      if (pickedFile != null) {
        setState(() {
          _profileImage = File(pickedFile.path);
        });
        await uploadProfilePicture(File(pickedFile.path));
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
      // appBar: AppBar(
      //     title: Text(
      //       'User Information',
      //       style: TextStyle(),
      //     ),
      //     centerTitle: true,
      //     toolbarHeight: 30,
      //     backgroundColor: Colors.blue,
      //     elevation: 4,
      //     automaticallyImplyLeading: false),
      body: Stack(
        fit: StackFit.expand, // Ensure the stack fills the screen
        children: <Widget>[
          // Blurred image background
          Container(
            decoration: BoxDecoration(
              image: DecorationImage(
                image: AssetImage(
                    'lib/images/pattern_background.png'), // Replace with your image path
                fit: BoxFit.cover,
              ),
            ),
          ),
          BackdropFilter(
            filter: ImageFilter.blur(
                sigmaX: 3.0, sigmaY: 3.0), // Adjust the blur intensity
            child: Container(
              color:
                  Colors.black.withOpacity(0.6), // Darken the background a bit
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
                  child: profilePictureUrl.isEmpty
                      ? CircleAvatar(
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
                        )
                      : CircleAvatar(
                          radius: 60,
                          backgroundColor: Colors.grey.shade200,
                          backgroundImage: NetworkImage(profilePictureUrl),
                        ),
                ),
                const SizedBox(height: 24),
                Text(
                  'Tap to change profile picture',
                  style: TextStyle(fontSize: 16, color: Colors.white),
                ),
                const SizedBox(height: 24),
                Wrap(
                  spacing: 10.0,
                  runSpacing: 10.0,
                  alignment: WrapAlignment.center,
                  children: [
                    ElevatedButton(
                      onPressed: () {
                        Navigator.of(context).push(MaterialPageRoute(
                            builder: (context) => CreateWorkoutPage()));
                      },
                      child: Text('Add Workout'),
                      style: ElevatedButton.styleFrom(
                        foregroundColor: Colors.white,
                        backgroundColor: Colors.blue, // Button background color
                      ),
                    ),
                    ElevatedButton(
                      onPressed: () async {
                        final storage = FlutterSecureStorage();
                        await storage.delete(key: 'access_token');
                        await signOut();
                        Navigator.of(context).pushAndRemoveUntil(
                          MaterialPageRoute(builder: (context) => LoginPage()),
                          (Route<dynamic> route) => false,
                        );
                      },
                      child: Text('Log out'),
                      style: ElevatedButton.styleFrom(
                        foregroundColor: Colors.white,
                        backgroundColor: Colors.blue,
                      ),
                    ),
                    // ElevatedButton(
                    //   onPressed: () {
                    //     Navigator.of(context).push(
                    //         MaterialPageRoute(builder: (context) => Details()));
                    //   },
                    //   child: Text('Details'),
                    //   style: ElevatedButton.styleFrom(
                    //     foregroundColor: Colors.white,
                    //     backgroundColor: Colors.green,
                    //   ),
                    // ),
                    ElevatedButton(
                      child: Text('Go Premium'),
                      style: ElevatedButton.styleFrom(
                        foregroundColor: Colors.white,
                        backgroundColor: Colors.green,
                      ),
                      onPressed: () async {
                        const url =
                            'http://localhost:8080/api/selfCoach/paypal/getPayPalSubscriptionButton';
                        await _launchAsInAppWebViewWithCustomHeaders(
                            Uri.parse(url));
                      },
                    ),
                  ],
                ),
                const SizedBox(height: 24),
                if (leaderBoardEntry.id != -1)
                  SizedBox(
                    width: double.infinity, // Ensures full width of the parent
                    child: Container(
                      padding: const EdgeInsets.all(16.0),
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.8),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Leaderboard Information:',
                            style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 18,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            'User: ${leaderBoardEntry!.user.firstName} ${leaderBoardEntry!.user.lastName}',
                          ),
                          Text(
                            'Total Points: ${leaderBoardEntry!.numberOfPoints.ceil()}',
                          ),
                          Text(
                            'Done Exercises: ${leaderBoardEntry!.numberOfDoneExercises}',
                          ),
                          Text(
                            'Done Workouts: ${leaderBoardEntry!.numberOfDoneWorkouts}',
                          ),
                          Text(
                            'Done Programs: ${leaderBoardEntry!.numberOfDonePrograms}',
                          ),
                        ],
                      ),
                    ),
                  ),

                const SizedBox(height: 24), // Separation between widgets

                SizedBox(
                  width: double.infinity, // Ensures full width
                  child: Container(
                    padding: const EdgeInsets.all(8.0),
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.8),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: ExpansionTile(
                      title: Text("Achievements"),
                      children: userAchievements.map((achievement) {
                        return ListTile(
                          leading: Image.network(
                              achievement.achievementPicturePath,
                              width: 40,
                              height: 40,
                              fit: BoxFit.cover),
                          title: Text(achievement.name),
                          subtitle: Text(achievement.description),
                        );
                      }).toList(),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
