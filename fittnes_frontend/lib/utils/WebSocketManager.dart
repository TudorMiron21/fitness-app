import 'dart:async';
import 'package:web_socket_channel/io.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

typedef MessageHandler = Function(dynamic message);

class WebSocketManager {
  static final WebSocketManager _instance = WebSocketManager._internal();
  WebSocketChannel? _channel;
  final _messageQueue = StreamController<dynamic>();
  final List<MessageHandler> _listeners = [];

  factory WebSocketManager() {
    return _instance;
  }

  WebSocketManager._internal();

  void connect(String uri) {
    _channel = IOWebSocketChannel.connect(Uri.parse(uri));
    _channel!.stream.listen((data) {
      // Check if there are listeners available
      if (_listeners.isNotEmpty) {
        for (var listener in _listeners) {
          listener(data);
        }
      } else {
        // If no listeners are available, add data to the queue
        _messageQueue.add(data);
      }
    });
  }

  void addListener(MessageHandler listener) {
    _listeners.add(listener);

    // Drain the queue and send messages to the new listener
    _messageQueue.stream.listen(listener);
  }

  void removeListener(MessageHandler listener) {
    _listeners.remove(listener);
  }

  void dispose() {
    if (_channel != null) {
      _channel!.sink.close();
      _channel = null;
    }
    _listeners.clear();
    _messageQueue.close();
  }
}