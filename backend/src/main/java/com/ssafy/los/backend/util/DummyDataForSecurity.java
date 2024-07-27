package com.ssafy.los.backend.util;

import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class DummyDataForSecurity implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<User> user = userRepository.findByEmail("wngud@naver.com");
        if (user.isEmpty()) {
            createDummyUser("wngud@naver.com", "1234");
        }

    }

    private void createDummyUser(String email, String password) {
        User user = User.builder()
                .email(email)
                .pwdHash(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);
    }
}
