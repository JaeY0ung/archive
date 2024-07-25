package com.ssafy.los.backend.user.model.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UserUpdateDto {

    private List<MultipartFile> files;
    private String nickname;

}
