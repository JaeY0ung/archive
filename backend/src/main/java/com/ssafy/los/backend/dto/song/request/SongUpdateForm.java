package com.ssafy.los.backend.dto.song.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongUpdateForm {

    private MultipartFile file;

    private String title;

    private String composer;

    private Long genreId;
}
