package com.ssafy.los.backend.alert.model.service;

import com.ssafy.los.backend.alert.model.dto.AlertDto;

public interface AlertService {

    String sendMessage(AlertDto alertDto);

    void saveFirebaseToken(Long userId, String firebaseToken);
}
