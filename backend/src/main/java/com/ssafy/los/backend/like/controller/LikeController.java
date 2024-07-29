package com.ssafy.los.backend.like.controller;

import com.ssafy.los.backend.like.model.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/sheets/{sheet-id}")
    public ResponseEntity<?> likeSheetById(@PathVariable("sheet-id") Long sheetId) {
        likeService.likeSheetById(sheetId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/sheets/{sheet-id}")
    public ResponseEntity<?> dislikeSheetById(@PathVariable("sheet-id") Long sheetId) {
        likeService.dislikeSheetById(sheetId);
        return ResponseEntity.ok().build();
    }
}
