package com.ssafy.los.backend.domain.entity;

import com.ssafy.los.backend.dto.AlertDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @JoinColumn(name = "receiver_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User receiver;

    @JoinColumn(name = "alert_type_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private AlertType alertType;

//    @Column(name = "alert_type_id")
//    private Long alertTypeId;

    private Long referenceId;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean readStatus;

    @CreatedDate
    private Timestamp createdAt;

    private Long senderId;

    public AlertDto toDto() {
        return AlertDto.builder()
                .id(id)
                .receiverId(receiver.getId())
                .alertTypeId(alertType.getId())
                .referenceId(referenceId)
                .readStatus(readStatus)
                .createdAt(createdAt)
                .senderId(senderId)
                .build();
    }

}
