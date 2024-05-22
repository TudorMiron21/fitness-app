package tudor.work.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.FireBaseNotificationRequestDto;
import tudor.work.dto.FireBaseNotificationResponseDto;

//@Service
//@RequiredArgsConstructor
//public class FirebaseNotificationService {
//
//    private final FirebaseMessaging firebaseMessaging;
//
//
//    public FireBaseNotificationResponseDto sendNotificationByToken(FireBaseNotificationRequestDto fireBaseNotificationRequestDto) {
//        Notification notification = Notification
//                .builder()
//                .setTitle(fireBaseNotificationRequestDto.getTitle())
//                .setBody(fireBaseNotificationRequestDto.getBody())
//                .build();
//
//        Message message = Message.builder()
//                .setToken(fireBaseNotificationRequestDto.getToken())
//                .setNotification(notification)
//                .build();
//
//        try {
//            firebaseMessaging.send(message);
//            return FireBaseNotificationResponseDto
//                    .builder()
//                    .message("Notification sent")
//                    .status(200)
//                    .build();
//
//        } catch (FirebaseMessagingException e) {
//            return FireBaseNotificationResponseDto
//                    .builder()
//                    .message("Notification was not sent")
//                    .status(500)
//                    .build();
//        }
//
//    }
//}
