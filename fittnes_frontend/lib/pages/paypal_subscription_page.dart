import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';

class PayPalSubscriptionPage extends StatefulWidget {
  const PayPalSubscriptionPage({super.key});

  @override
  State<PayPalSubscriptionPage> createState() => _PayPalSubscriptionPageState();
}

class _PayPalSubscriptionPageState extends State<PayPalSubscriptionPage> {
  // Helper function to load HTML file from assets
  Future<String> loadLocalHTML() async {
    return await rootBundle.loadString('lib/assets/html/paypal.html');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('PayPal Subscription'),
      ),
      body: FutureBuilder<String>(
        future: loadLocalHTML(),
        builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
          if (snapshot.hasData) {
            return WebView(
              initialUrl: Uri.dataFromString(snapshot.data!,
                      mimeType: 'text/html',
                      encoding: Encoding.getByName('utf-8'))
                  .toString(),
              javascriptMode: JavascriptMode.unrestricted,
              onWebViewCreated: (WebViewController controller) {
                controller.clearCache();
              },
            );
          } else if (snapshot.hasError) {
            return Center(child: Text('Error loading HTML'));
          } else {
            return Center(child: CircularProgressIndicator());
          }
        },
      ),
    );
  }
}
