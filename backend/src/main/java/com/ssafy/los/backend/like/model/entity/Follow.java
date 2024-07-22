package com.ssafy.los.backend.like.model.entity;

import com.ssafy.los.backend.user.model.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;


    @JoinColumn(name = "follower_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User follower;

    @JoinColumn(name = "followed_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User followed;
}
