package com.ssafy.los.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;


    @JoinColumn(name = "follower_id", referencedColumnName = "user_id")
    @ManyToOne
    private User follower; // from_user

    @JoinColumn(name = "followed_id", referencedColumnName = "user_id")
    @ManyToOne
    private User followed; // to_user

    @Builder
    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }
}
