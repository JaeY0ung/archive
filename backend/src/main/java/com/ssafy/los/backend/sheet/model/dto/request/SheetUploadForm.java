package com.ssafy.los.backend.sheet.model.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
public class SheetUploadForm {

    private List<MultipartFile> files;

    private String title;

    private Long songId;

    private Integer price;

    private Integer level; // 1,2,3,4,5,6,7,8,9

    private Integer point;
}
