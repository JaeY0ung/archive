package com.ssafy.los.backend.like.controller;

import com.ssafy.los.backend.like.model.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;


    @PostMapping("/{user-id}/{sheet-id}")
    public ResponseEntity<?> addLike(@PathVariable("{user-id}") Long userId, @PathVariable("sheet-id") Long sheetId) {
        likeService.addLike(userId, sheetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{user-id}/{sheet-id}")
    public ResponseEntity<?> deleteLike(@PathVariable("{user-id}") Long userId, @PathVariable("sheet-id") Long sheetId) {
        likeService.deleteLike(userId, sheetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
