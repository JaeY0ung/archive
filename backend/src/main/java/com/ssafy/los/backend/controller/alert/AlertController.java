package com.ssafy.los.backend.controller.alert;

import com.ssafy.los.backend.dto.AlertDto;
import com.ssafy.los.backend.dto.FirebaseTokenRequestDto;
import com.ssafy.los.backend.service.alert.AlertService;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {

    private final AlertService alertService;
    private static final Logger LOGGER = Logger.getLogger(AlertController.class.getName());

    @PostMapping("")
    public ResponseEntity<String> sendAlert(@RequestBody AlertDto alertDto) {
        String response = alertService.saveAlertAndSendMessage(alertDto);
        LOGGER.info("알람이 전송되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/save-firebaseToken")
    public ResponseEntity<String> saveFirebaseToken(@RequestBody FirebaseTokenRequestDto request) {
        alertService.saveFirebaseToken(request.getUserId(), request.getFirebaseToken());
        LOGGER.info("Firebase Token이 저장되었습니다. UserID: " + request.getUserId() + ", Token: "
                + request.getFirebaseToken());
        return new ResponseEntity<>("Firebase Token 저장 완료", HttpStatus.OK);
    }

    // TODO: 악보 좋아요 알림
    // TODO: 팔로우 알림
    // TODO: 공지사항 알림
    // TODO: 업로드 악보 승인 알림
    // TODO: 대결 신청 알림
}
