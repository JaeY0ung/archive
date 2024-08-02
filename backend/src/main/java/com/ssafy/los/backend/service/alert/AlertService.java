package com.ssafy.los.backend.service.alert;

import com.ssafy.los.backend.dto.AlertDto;

public interface AlertService {

    String sendMessage(AlertDto alertDto);

    void saveFirebaseToken(Long userId, String firebaseToken);

    String saveAlertAndSendMessage(AlertDto alertDto);
}
