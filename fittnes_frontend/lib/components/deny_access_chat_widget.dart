import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class DenyAccessChatWidget extends StatelessWidget {
  const DenyAccessChatWidget({required this.textWidget, Key? key})
      : super(key: key);

  final String textWidget;

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
    return Container(
      decoration: BoxDecoration(
        image: DecorationImage(
          image: AssetImage(
              'lib/images/fittness_background.jpg'), // Replace with your image path
          fit: BoxFit.cover,
        ),
      ),
      child: Stack(
        alignment: Alignment.center,
        children: [
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                textWidget,
                style: TextStyle(color: Colors.white, fontSize: 24),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                child: Text('Go Premium'),
                style: ElevatedButton.styleFrom(
                  foregroundColor: Colors.white,
                  backgroundColor: Colors.green,
                  // Text color
                ),
                onPressed: () async {
                  const url =
                      'https://fit-stack.online/api/selfCoach/paypal/getPayPalSubscriptionButton';
                  await _launchAsInAppWebViewWithCustomHeaders(Uri.parse(url));
                },
              ),
            ],
          ),
        ],
      ),
    );
  }
}
