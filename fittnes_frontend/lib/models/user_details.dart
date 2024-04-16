import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';

class UserDetails {
  final String? firstName;
  final String? lastName;
  final String? email;

  UserDetails({this.firstName, this.lastName, this.email});
}

Future<UserDetails?> retrieveGoogleUser() async {
  final googleAccount = await GoogleSignIn().signIn();
  final googleAuth = await googleAccount?.authentication;

  final credential = GoogleAuthProvider.credential(
    accessToken: googleAuth?.accessToken,
    idToken: googleAuth?.idToken,
  );

  final userCredential =
      await FirebaseAuth.instance.signInWithCredential(credential);
  final user = userCredential.user;

  if (user != null) {
    String? firstName;
    String? lastName;
    List<String> names = user.displayName?.split(" ") ?? [];

    if (names.isNotEmpty) {
      firstName = names.first;
      lastName = names.length > 1 ? names.sublist(1).join(" ") : null;
    }

    return UserDetails(
      firstName: firstName,
      lastName: lastName,
      email: user.email,
    );
  }
  return null;
}

Future<void> signOut() async {
  await FirebaseAuth.instance.signOut();
  await GoogleSignIn().signOut();
}
