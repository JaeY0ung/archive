package com.ssafy.los.backend.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
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

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean status; // 진행 상태: false (진행), true (완료)

    private long playTime;

    public void update(User user, Float score){
        this.user = user;
        this.score = score;
    }

    public void updateStatus(boolean status) {this.status = status;}

    public void updatePlayTime() {
        playTime = Duration.between(this.getCreatedAt(), this.getModifiedAt()).getSeconds();
    }



}
