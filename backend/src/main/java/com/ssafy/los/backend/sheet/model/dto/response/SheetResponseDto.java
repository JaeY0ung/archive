package com.ssafy.los.backend.sheet.model.dto.response;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SheetResponseDto {

    private Long id;

    private String title;

    private String songTitle;

    private String songComposer;

    private String uploaderNickname;

    private String fileName;

    private String songImg;

    private Integer price;

    private Integer level; // 1,2,3,4,5,6,7,8,9

    private Integer status; // "waiting: 0, accepted: 1, rejected: 2"

    private Integer point;

    private Integer viewCount;

    private Timestamp createdAt;

}
