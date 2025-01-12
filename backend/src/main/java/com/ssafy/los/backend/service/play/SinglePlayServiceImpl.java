package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.play.SinglePlayResultRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.play.request.SingleResultAfterDto;
import com.ssafy.los.backend.dto.play.request.SingleResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.SingePlayResultProfileDto;
import com.ssafy.los.backend.exception.user.UserNotFoundException;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.SheetService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SinglePlayServiceImpl implements SinglePlayService {

    @Value("${fastapi.server.url}")
    private String fastApiServerUrl;

    private final SinglePlayResultRepository singlePlayResultRepository;
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final SheetService sheetService;
    private final FileUploadUtil fileUploadUtil;

    // 게임이 시작되었을 때, 싱글 결과 생성
    @Override
    public Long saveSinglePlayResult(SingleResultBeforeDto singleResultBeforeDto) {
        Sheet playSheet = sheetRepository.findById(singleResultBeforeDto.getSheetId())
                .orElseThrow(() -> new RuntimeException("sheet not found"));
        User loginUser = authService.getLoginUser();
        SinglePlayResult singlePlayResult = SinglePlayResult.builder()
                .sheet(playSheet)
                .user(loginUser)
                .build();

        return singlePlayResultRepository.save(singlePlayResult).getId();
    }

    // 게임이 종료되었을 때, 결과 테이블 가져오기
    @Override
    public Long completeSinglePlayResult(Long singleResultId,
            SingleResultAfterDto singleResultAfterDto) {
        User loginUser = authService.getLoginUser();

        SinglePlayResult singlePlayResult = singlePlayResultRepository.findById(singleResultId)
                .orElseThrow(() -> new RuntimeException("Single Play Result Not Found"));
        log.info("업데이트 과정 singlePlayResult : {}", singlePlayResult.toString());
        log.info("업데이트 과정 status : {}", singlePlayResult.isStatus());
//        if (!singlePlayResult.isStatus()) {
//        log.info("조건문 체크");
        User user = userRepository.findUserByIdAndDeletedAtNull(
                        singleResultAfterDto.getUserId())
                .orElseThrow(() -> new RuntimeException("user not found"));

        Float score = singleResultAfterDto.getScore();

        singlePlayResult.update(user, score);

        // 플레이 종료시간 저장
        singlePlayResult.updatePlayTime();
        singlePlayResult.updateStatus(true);

        // 명시적으로 저장하여 변경 사항 반영
        singlePlayResultRepository.save(singlePlayResult);
        refreshSingleScoreOfUser(singlePlayResult.getUser().getId());
        return singleResultId;
    }

    // 싱글 결과 목록 조회(유저, 악보 기준)
    @Override
    public Page<SinglePlayResult> getSinglePlayResultList(Pageable pageable) {
        return singlePlayResultRepository.findAll(pageable);
    }

    // 싱글 결과 목록 조회 (프로필 페이지)
    @Override
    public List<SingePlayResultProfileDto> getSinglePlayResultListByUser(Long userId) {
        User findUser = userRepository.findUserByIdAndDeletedAtNull(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다: " + userId));

        return singlePlayResultRepository.findByUser(findUser).stream()
                .map(result -> {
                    SingePlayResultProfileDto.SingePlayResultProfileDtoBuilder builder = SingePlayResultProfileDto.builder();

                    if (result.getUser() != null) {
                        builder.nickname(result.getUser().getNickname())
                                .userImgName(result.getUser().getUserImg());
                    }

                    if (result.getSheet() != null) {
                        builder.sheetId(result.getSheet().getId())
                                .sheetTitle(result.getSheet().getTitle())
                                .level(result.getSheet().getLevel());

                        if (result.getSheet().getSong() != null) {
                            builder.songComposer(result.getSheet().getSong().getComposer())
                                    .songImgName(result.getSheet().getSong().getImgName());
                        }

                        if (result.getSheet().getUploader() != null) {
                            builder.uploaderNickname(result.getSheet().getUploader().getNickname());
                        } else {
                            builder.uploaderNickname("");
                        }
                    }

                    // score와 playTime에 대한 null 체크 추가
                    if (result.getScore() != null) {
                        builder.score(result.getScore());
                    }
                    if (result.getPlayTime() >= 0) {  // TODO : 0 이하의 값은 무시
                        builder.playTime(result.getPlayTime());
                    }

                    SingePlayResultProfileDto dto = builder.build();

                    try {
                        dto.loadUserImg(fileUploadUtil);
                    } catch (Exception e) {
                        log.warn("Failed to load user image for user: {}", result.getUser().getId());
                    }

                    try {
                        dto.loadSongImg(fileUploadUtil);
                    } catch (Exception e) {
                        log.warn("Failed to load song image for sheet: {}", result.getSheet().getId());
                    }

                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 싱글 결과 삭제 : admin
    @Override
    public Long removeSinglePlayResult(Long singlePlayResultId) {
        singlePlayResultRepository.deleteById(singlePlayResultId);

        return singlePlayResultId;
    }

    @Override
    public String getLiveScore(MultipartFile file, Long sheetId, Long singleResultId)
            throws IllegalArgumentException {

        try {
            String userId = authService.getLoginUser().getId().toString();
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(fastApiServerUrl + "/playing/single");

            HttpEntity multipart = MultipartEntityBuilder.create()
                    .addBinaryBody("file", file.getInputStream(),
                            ContentType.MULTIPART_FORM_DATA,
                            file.getOriginalFilename())
                    .addTextBody("uuid", sheetService.searchById(sheetId).getUuid(),
                            ContentType.TEXT_PLAIN)
                    .addTextBody("singleResultId", singleResultId.toString(),
                            ContentType.TEXT_PLAIN)
                    .addTextBody("userId", userId,
                            ContentType.TEXT_PLAIN)
                    .build();

            request.setEntity(multipart);

            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                throw new IllegalArgumentException("[파일 계산 실패]");
            }
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new IllegalArgumentException("[파일 계산 실패] " + e.getMessage());
        }
    }

    @Override
    public void refreshSingleScoreOfUser(Long userId) throws IllegalArgumentException {
        Double avgLevel = singlePlayResultRepository.calculateAvgOfPassedSinglePlayResultByUserId(
                userId);
        User user = userRepository.findById(userId).orElseThrow();
        if (avgLevel != null) {
            user.setRefreshSingleScore((int) (avgLevel * 1000));
        } else {
            user.setRefreshSingleScore(0);
        }
    }
}
