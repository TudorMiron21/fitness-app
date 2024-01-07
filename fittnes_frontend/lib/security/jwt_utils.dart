import 'dart:convert';
import 'dart:typed_data';

class JwtUtils {
  static String extractSubject(String token) {
    try {
      // Split the JWT token and get the payload part
      final parts = token.split('.');
      if (parts.length != 3) {
        throw FormatException('Invalid token');
      }
      final payload = _decodeBase64(parts[1]);
      final payloadMap = json.decode(payload);

      // Get the subject claim
      if (payloadMap is! Map<String, dynamic>) {
        throw FormatException('Invalid payload');
      }
      final subject = payloadMap['sub'];
      print(subject);
      return subject ?? "No subject found";
    } catch (ex) {
      // Handle errors
      print('An error occurred while processing the JWT: ${ex.toString()}');
      return "Invalid token";
    }
  }

  static String _decodeBase64(String str) {
    // Normalize the string to ensure padding exists
    String output = str.replaceAll('-', '+').replaceAll('_', '/');
    switch (output.length % 4) {
      case 0:
        break; // No pad chars in this case
      case 2:
        output += '==';
        break; // Two pad chars
      case 3:
        output += '=';
        break; // One pad char
      default:
        throw FormatException('Illegal base64url string!');
    }

    // Decode the base64 string
    var decoded = utf8.decode(base64Url.decode(output));
    return decoded;
  }
}
