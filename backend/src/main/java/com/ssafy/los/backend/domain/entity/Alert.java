package com.ssafy.los.backend.domain.entity;

import com.ssafy.los.backend.dto.AlertDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    private Boolean readStatus;

    @CreatedDate
    private Timestamp createdAt;

    public AlertDto toDto() {
        return AlertDto.builder()
                .id(id)
                .receiver(receiver.getId())
                .alertType(alertType.getId())
                .referenceId(referenceId)
                .readStatus(readStatus)
                .createdAt(createdAt)
                .build();
    }

}
