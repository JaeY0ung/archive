package com.ssafy.los.backend.alert.controller;

import com.ssafy.los.backend.alert.model.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    
    // TODO: 악보 좋아요 알림
    // TODO: 팔로우 알림
    // TODO: 공지사항 알림
    // TODO: 업로드 악보 승인 알림
    // TODO: 대결 신청 알림
}
