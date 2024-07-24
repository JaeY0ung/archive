package com.ssafy.los.backend.sheet.model.dto.response;

import com.ssafy.los.backend.song.model.entity.Song;
import com.ssafy.los.backend.user.model.entity.User;
import java.sql.Timestamp;

public interface SheetResponseDto {

    Long getId();

    String getTitle();

    Song getSong();

    User getUploader();

    String getFileName();

    Integer getPrice();

    Integer getLevel(); // 1,2,3,4,5,6,7,8,9

    Integer getStatus(); // "waiting: 0, accepted: 1, rejected: 2"

    Integer getPoint();

    Integer getViewCount();

    Timestamp getCreatedAt();

}
