package com.ssafy.los.backend.dto.user.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter // Jackson이 JSON을 객체로 변환할 때 필요한 setter 메서드를 제공
@NoArgsConstructor // 필드가 한개로 오류 발생하여 추가
public class UserUpdateDto {

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    private String nickname;

}
