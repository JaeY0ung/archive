package com.ssafy.los.backend.user.model.dto.request;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UserUpdateDto {

    private List<MultipartFile> files;

    @Size(min = 2, max = 20)
    private String nickname;

}
