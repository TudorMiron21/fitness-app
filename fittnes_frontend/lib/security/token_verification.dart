import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart';

class TokenChecker {
  static Future<bool> checkTokenValidity() async {
    final FlutterSecureStorage storage = FlutterSecureStorage();
    
    String? authToken = await storage.read(key: 'accessToken');
    if (authToken == null || authToken.isEmpty) {
      print("empty storage or null token");
      return false;
    }
    Uri url = Uri.parse(
        'https://www.fit-stack.online/api/v1/auth/validateToken?token=$authToken');

    try {
      final response = await get(url);

      if (response.statusCode == 200) {
        if (response.body == 'true') {
          print("token is valid");
          return true;
        } else if (response.body == 'false') {
          return false;
        }
      } else {
        print(response.body);
      }
    } catch (error) {
      // Error occurred while validating the token
      return false;
    }
    return false;
  }
}
