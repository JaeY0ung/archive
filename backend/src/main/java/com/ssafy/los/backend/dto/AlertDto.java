package com.ssafy.los.backend.dto;

import com.ssafy.los.backend.domain.entity.Alert;
import com.ssafy.los.backend.domain.entity.AlertType;
import com.ssafy.los.backend.domain.entity.User;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDto {

    private Long id;
    private Long receiverId; // User 객체 대신 ID를 사용
    private Long alertTypeId; // AlertType 객체 대신 ID를 사용
    private Long referenceId;
    private Boolean readStatus;
    private Timestamp createdAt;
    private Long senderId;
    private Long roomId; // db에 저장하지 않고 dto에서만 사용

    public Alert toEntity(User receiver, AlertType alertType) {
        return Alert.builder()
                .id(id)
                .receiver(receiver)
                .alertType(alertType)
                .referenceId(referenceId)
                .readStatus(readStatus)
                .createdAt(createdAt)
                .senderId(senderId)
                .build();
    }

}
