package com.ssafy.los.backend.play.model.entity;

import com.ssafy.los.backend.common.model.entity.BaseEntity;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.user.model.entity.User;
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
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SinglePlayResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "single_result_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User user;

    @JoinColumn(name = "sheet_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Sheet sheet;

    @Column(columnDefinition = "FLOAT")
    private Float score;

    private long playTime;

    public void updatePlayTime() {
        playTime = Duration.between(this.getCreatedAt(), this.getModifiedAt()).getSeconds();
    }

}
