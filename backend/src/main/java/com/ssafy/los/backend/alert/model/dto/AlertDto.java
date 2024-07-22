package com.ssafy.los.backend.alert.model.dto;

import com.ssafy.los.backend.alert.model.entity.AlertType;
import com.ssafy.los.backend.user.model.entity.User;
import java.sql.Timestamp;

public interface AlertDto {

    Long getId();

    User getReceiver();

    AlertType getAlertType(); // TODO : typeName 으로 가져와야 함

    Long getReferenceId();

    Boolean getReadStatus();

    Timestamp getCreatedAt();
}
