package com.ssafy.los.backend.controller.admin;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.service.play.MultiPlayService;
import com.ssafy.los.backend.service.play.SinglePlayService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final SinglePlayService singlePlayService;
    private final MultiPlayService multiPlayService;

    @PutMapping("/update-score")
    public ResponseEntity<?> updateAllUsersScore() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            singlePlayService.refreshSingleScoreOfUser(user.getId());
            multiPlayService.refreshMultiScoreOfUser(user.getId());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
