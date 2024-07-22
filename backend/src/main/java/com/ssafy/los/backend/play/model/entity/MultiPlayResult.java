package com.ssafy.los.backend.play.model.entity;

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
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class MultiPlayResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "multi_result_id")
    private Long id;

    @JoinColumn(name = "sheet_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Sheet sheet;

    private String status; // 진행 상태 : 0(대기), 1(진행), 2(완료)

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

    @CreatedDate
    private Timestamp createdAt;

    @CreatedDate
    @LastModifiedDate
    private Timestamp modifiedAt;
}
