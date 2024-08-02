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
    private User receiver;
    private AlertType alertType; // TODO : typeName 으로 가져와야 함
    private Long referenceId;
    private Boolean readStatus;
    private Timestamp createdAt;

    public Alert toEntity() {
        return Alert.builder()
                .id(id)
                .receiver(receiver)
                .alertType(alertType)
                .referenceId(referenceId)
                .readStatus(readStatus)
                .createdAt(createdAt)
                .build();
    }
}
