package com.ssafy.los.backend.service.alert;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.los.backend.domain.entity.Alert;
import com.ssafy.los.backend.domain.entity.AlertType;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.alert.AlertRepository;
import com.ssafy.los.backend.domain.repository.alert.AlertTypeRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.AlertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final AlertTypeRepository alertTypeRepository;
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public String sendMessage(AlertDto alertDto) {
        // 알림 받을 사용자의 Firebase 토큰 값을 조회
        log.info("알림 받을 사용자ID: " + alertDto.getReceiverId());
        String userFirebaseToken = redisTemplate.opsForValue()
                .get("firebaseToken:" + alertDto.getReceiverId());

        if (userFirebaseToken == null || userFirebaseToken.isEmpty()) {
            return "알림 전송에 실패했습니다. 사용자 Firebase 토큰이 없습니다.";
        }

        // 1) 알림 유형에 따라 제목 설정
        String title;
        Long alertTypeId = alertDto.getAlertTypeId();
        log.info("alertTypeId: " + alertTypeId);
        switch (alertTypeId.intValue()) {
            case 1:
                title = "대결 초대 알림";
                break;
            case 2:
                title = "팔로우 알림";
                break;
            case 3:
                title = "악보 업로드 알람";
                break;
            case 4:
                title = "공지 알람";
                break;
            case 5:
                title = "악보 좋아요 알람";
                break;
            default:
                title = "알람 도착";
                break;
        }

        // 2) 알림 받을 사용자의 닉네임을 조회
        User receiverUser = userRepository.findById(alertDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        String receiverNickname = receiverUser.getNickname();

        // 3) 알림 보낸 사용자의 닉네임을 조회
        User senderUser = userRepository.findById(alertDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        String senderNickname = senderUser.getNickname();

        // (1), (2), (3)을 바탕으로 메시지 구성
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(senderNickname + "님으로부터 " + receiverNickname
                                + "님에게 새로운 알람이 도착했습니다.")
                        .build())
                .putData("alertTypeId", alertDto.getAlertTypeId().toString())
                .putData("referenceId", alertDto.getReferenceId().toString())
                .putData("readStatus", alertDto.getReadStatus().toString())
                .putData("roomId", alertDto.getRoomId().toString())
                .setToken(userFirebaseToken)
                .build();

        try {
            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            return "알람이 성공적으로 전송되었습니다." + response;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "알람 전송에 실패했습니다.";
        }
    }

    @Override
    public void saveFirebaseToken(Long userId, String firebaseToken) {
        redisTemplate.opsForValue().set("firebaseToken:" + userId, firebaseToken);
        log.info(
                "Firebase Token 저장 완료: userId = " + userId + " firebaseToken = " + firebaseToken);
    }

    @Override
    public String saveAlertAndSendMessage(AlertDto alertDto) {

        log.info("Received AlertDto: " + alertDto);

        // User 및 AlertType 엔티티 조회
        User receiverUser = userRepository.findById(alertDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        AlertType alertTypeEntity = alertTypeRepository.findById(alertDto.getAlertTypeId())
                .orElseThrow(() -> new IllegalArgumentException("AlertType not found"));

        // AlertDto를 Alert 엔티티로 변환
        Alert alertEntity = Alert.builder()
                .receiver(receiverUser)
                .alertType(alertTypeEntity)
                .referenceId(alertDto.getReferenceId())
                .readStatus(alertDto.getReadStatus())
                .createdAt(alertDto.getCreatedAt())
                .senderId(alertDto.getSenderId())
                .build();

        // Alert 엔티티를 데이터베이스에 저장
        Alert savedAlert = alertRepository.save(alertEntity);

        // 저장된 Alert 엔티티의 정보를 이용하여 알림 전송
        AlertDto savedAlertDto = savedAlert.toDto();
        savedAlertDto.setRoomId(alertDto.getRoomId()); // roomId를 저장된 AlertDto에 설정
        log.info("savedAlert.toDto(): " + savedAlert.toDto());
        log.info("savedAlertDto: " + savedAlertDto.toString());

        // 저장된 Alert 엔티티의 정보를 이용하여 알림 전송
        return sendMessage(savedAlertDto);
    }

}
