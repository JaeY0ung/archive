package com.ssafy.los.backend.controller.like;

import com.ssafy.los.backend.service.like.LikeSheetService;
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

    private final LikeSheetService likeSheetService;

    @PostMapping("/sheets/{sheet-id}")
    public ResponseEntity<?> likeSheetById(@PathVariable("sheet-id") Long sheetId) {
        if (likeSheetService.likeSheetById(sheetId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/sheets/{sheet-id}")
    public ResponseEntity<?> dislikeSheetById(@PathVariable("sheet-id") Long sheetId) {
        if (likeSheetService.dislikeSheetById(sheetId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
