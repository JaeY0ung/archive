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
    private Long receiver; // User 객체 대신 ID를 사용
    private Long alertType; // AlertType 객체 대신 ID를 사용
    private Long referenceId;
    private Boolean readStatus;
    private Timestamp createdAt;

    public Alert toEntity(User receiverUser, AlertType alertTypeEntity) {
        return Alert.builder()
                .id(id)
                .receiver(receiverUser)
                .alertType(alertTypeEntity)
                .referenceId(referenceId)
                .readStatus(readStatus)
                .createdAt(createdAt)
                .build();
    }

}
