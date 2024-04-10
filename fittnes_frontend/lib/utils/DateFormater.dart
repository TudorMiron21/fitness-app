import 'package:intl/intl.dart';

String getFormatedDate(DateTime messageDate) {
  DateTime now = DateTime.now();
  DateTime today = DateTime(now.year, now.month, now.day);
  DateTime yesterday = today.subtract(Duration(days: 1));
  DateTime messageDay =
      DateTime(messageDate.year, messageDate.month, messageDate.day);

  if (messageDay == today) {
    return 'Today';
  } else if (messageDay == yesterday) {
    return 'Yesterday';
  } else {
    return DateFormat('MMM d').format(messageDate);
  }
}
