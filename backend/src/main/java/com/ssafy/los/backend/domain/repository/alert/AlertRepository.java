package com.ssafy.los.backend.domain.repository.alert;

import com.ssafy.los.backend.domain.entity.Alert;
import com.ssafy.los.backend.dto.AlertDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // 유저 아이디를 통해 알람들 가져오기
    List<AlertDto> findAllAlertDtoByReceiverIdAndReadStatus(Long receiverId, boolean readStatus);

}
