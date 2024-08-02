package com.ssafy.los.backend.service.alert;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ssafy.los.backend.domain.entity.Alert;
import com.ssafy.los.backend.domain.repository.alert.AlertRepository;
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
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public String sendMessage(AlertDto alertDto) {
        // 알림 받을 사용자의 Firebase 토큰 값을 조회
        log.info("알림 받을 사용자ID: " + alertDto.getReceiver().getId());
        String userFirebaseToken = redisTemplate.opsForValue()
                .get("firebaseToken:" + alertDto.getReceiver().getId());

        if (userFirebaseToken == null || userFirebaseToken.isEmpty()) {
            return "알림 전송에 실패했습니다. 사용자 Firebase 토큰이 없습니다.";
        }

        // 알림 유형에 따라 제목 설정
        String title;
        Long alertTypeId = alertDto.getAlertType().getId();
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

        // 메시지 구성
        Message message = Message.builder()
                .putData("title", title)
                .putData("content", alertDto.getReceiver().getNickname() + " 님에게 새로운 알람이 도착했습니다.")
                .putData("alertTypeId", alertDto.getAlertType().toString())
                .putData("referenceId", "0")
                .putData("readStatus", alertDto.getReadStatus().toString())
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
        // AlertDto를 Alert 엔티티로 변환
        Alert alertEntity = alertDto.toEntity();

        // Alert 엔티티를 데이터베이스에 저장
        Alert savedAlert = alertRepository.save(alertEntity);
        log.info("savedAlert.toDto(): " + savedAlert.toDto());

        // 저장된 Alert 엔티티의 정보를 이용하여 알림 전송
        return sendMessage(savedAlert.toDto());
    }

}
