package com.ssafy.los.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
public class MultiPlayResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "multi_result_id")
    private Long id;

    @JoinColumn(name = "sheet_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Sheet sheet;

    @JoinColumn(name = "winner_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User winner;

    @JoinColumn(name = "loser_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User loser;

    @Column(columnDefinition = "FLOAT")
    private Float winnerScore;

    @Column(columnDefinition = "FLOAT")
    private Float loserScore;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean isDraw; // 무승부 여부: false, true

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean status; // 진행 상태: false (진행), true (완료)

    private long playTime;


    public void update(User winner, Float winnerScore, User loser, Float loserScore) {
        this.winner = winner;
        this.winnerScore = winnerScore;
        this.loser = loser;
        this.loserScore = loserScore;
    }

    public void updateDraw(boolean isDraw) {
        this.isDraw = isDraw;
    }

    public void updateStatus(boolean status) {
        this.status = status;
    }

    public void updatePlayTime() {
        playTime = Duration.between(this.getCreatedAt(), this.getModifiedAt()).getSeconds();
    }

}
