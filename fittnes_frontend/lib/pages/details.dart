import 'package:fittnes_frontend/components/bottom_nav.dart';
import 'package:fittnes_frontend/pages/home_page.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class Details extends StatefulWidget {
  const Details({super.key});

  @override
  State<Details> createState() => _DetailsState();
}

class _DetailsState extends State<Details> {
  Future<void> uploadDetails() async {
    if (_formKey.currentState?.validate() ?? false) {
      final FlutterSecureStorage storage = FlutterSecureStorage();
      String? accessToken = await storage.read(key: 'accessToken');

      if (accessToken == null || accessToken.isEmpty) {
        // Handle the case where the authToken is missing or empty
        throw Exception('Authentication token is missing or invalid.');
      }

      String gender = _selectedGender!;
      int age = int.parse(_ageController.text);
      int height = int.parse(_heightController.text);
      double currentWeight = double.parse(_currentWeightController.text);
      double goalWeight = double.parse(_goalWeightController.text);

      String url =
          'https://www.fit-stack.online/api/selfCoach/user/uploadUserDetails/$gender/$age/$height/$currentWeight/$goalWeight';

      try {
        final response = await http.put(
          Uri.parse(url),
          headers: <String, String>{
            'Content-Type': 'application/json; charset=UTF-8',
            'Authorization': 'Bearer $accessToken',
          },
        );

        if (response.statusCode == 200) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Details uploaded successfully!')),
          );
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Failed to upload details.')),
          );
        }
      } catch (error) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('An error occurred: $error')),
        );
      }
    }
  }

  final _formKey = GlobalKey<FormState>();
  final _genderOptions = ['Male', 'Female'];
  String? _selectedGender;
  final _ageController = TextEditingController();
  final _heightController = TextEditingController();
  final _currentWeightController = TextEditingController();
  final _goalWeightController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Enter Your Details'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              DropdownButtonFormField<String>(
                value: _selectedGender,
                decoration: InputDecoration(
                  labelText: 'Gender',
                  border: OutlineInputBorder(),
                ),
                items: _genderOptions.map((String gender) {
                  return DropdownMenuItem<String>(
                    value: gender,
                    child: Text(gender),
                  );
                }).toList(),
                onChanged: (String? newValue) {
                  setState(() {
                    _selectedGender = newValue;
                  });
                },
                validator: (value) =>
                    value == null ? 'Please select a gender' : null,
              ),
              SizedBox(height: 16.0),
              TextFormField(
                controller: _ageController,
                decoration: InputDecoration(
                  labelText: 'Age',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter your age';
                  }
                  if (int.tryParse(value) == null || int.parse(value) <= 0) {
                    return 'Please enter a valid age';
                  }
                  return null;
                },
              ),
              SizedBox(height: 16.0),
              TextFormField(
                controller: _heightController,
                decoration: InputDecoration(
                  labelText: 'Height (cm)',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter your height';
                  }
                  if (double.tryParse(value) == null ||
                      double.parse(value) <= 0) {
                    return 'Please enter a valid height';
                  }
                  return null;
                },
              ),
              SizedBox(height: 16.0),
              TextFormField(
                controller: _currentWeightController,
                decoration: InputDecoration(
                  labelText: 'Current Weight (kg)',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter your current weight';
                  }
                  if (double.tryParse(value) == null ||
                      double.parse(value) <= 0) {
                    return 'Please enter a valid weight';
                  }
                  return null;
                },
              ),
              SizedBox(height: 16.0),
              TextFormField(
                controller: _goalWeightController,
                decoration: InputDecoration(
                  labelText: 'Goal Weight (kg)',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter your goal weight';
                  }
                  if (double.tryParse(value) == null ||
                      double.parse(value) <= 0) {
                    return 'Please enter a valid weight';
                  }
                  return null;
                },
              ),
              SizedBox(height: 32.0),
              ElevatedButton(
                onPressed: () async {
                  if (_formKey.currentState?.validate() ?? false) {
                    await uploadDetails();
                    print('Gender: $_selectedGender');
                    print('Age: ${_ageController.text}');
                    print('Height: ${_heightController.text}');
                    print('Current Weight: ${_currentWeightController.text}');
                    print('Goal Weight: ${_goalWeightController.text}');
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) =>
                            NavBar(), // Ensure NavBar is properly imported
                      ),
                    );
                  }
                },
                child: Text('Submit'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _ageController.dispose();
    _heightController.dispose();
    _currentWeightController.dispose();
    _goalWeightController.dispose();
    super.dispose();
  }
}
