package com.ssafy.los.backend.dto.play.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BattleRoomRegisterDto {

    private String title;

    // Todo: 여기서 객체를 받아올 수 없으니까, sheet id 또는 sheet name을 받아올 것.
//    private String sheetId;
    // Todo: 비밀방 여부 및 비밀번호
    // private boolean isPrivate;
    // private Long pwd;

}
