package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.MultiPlayResult;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.play.SinglePlayResultRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.play.request.SinglePlayRequestDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultProfileDto;
import com.ssafy.los.backend.dto.play.response.SingePlayResultProfileDto;
import com.ssafy.los.backend.exception.user.UserNotFoundException;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ssafy.los.backend.domain.entity.QUser.user;

@Service
@RequiredArgsConstructor
public class SinglePlayServiceImpl implements SinglePlayService {

    private final SinglePlayResultRepository singlePlayResultRepository;
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final FileUploadUtil fileUploadUtil;

    // 싱글 결과 생성
    @Override
    public Long saveSinglePlayResult(SinglePlayRequestDto singlePlayRequestDto) {
        User loginUser = authService.getLoginUser();
        Sheet sheetInfo = sheetRepository.findById(singlePlayRequestDto.getSheetId())
                .orElseThrow(() -> new RuntimeException("Sheet not found"));

        // singlePlayResult에 대한 결과 저장
        SinglePlayResult singlePlayResult = SinglePlayResult.builder()
                .user(loginUser)
                .sheet(sheetInfo)
                .score(singlePlayRequestDto.getScore())
                .build();

        // 플레이 종료시간 저장
        singlePlayResult.updatePlayTime();

        return singlePlayResultRepository.save(singlePlayResult).getId(); // single result 결과 저장
    }

    // 싱글 결과 목록 조회(유저, 악보 기준)
    @Override
    public Page<SinglePlayResult> getSinglePlayResultList(Pageable pageable) {
        Page<SinglePlayResult> page = singlePlayResultRepository.findAll(pageable);
        return page;
    }

    // 싱글 결과 목록 조회 (프로필 페이지)
    @Override
    public List<SingePlayResultProfileDto> getSinglePlayResultListByUser(Long userId) {
        User findUser = userRepository.findUserByIdAndDeletedAtNull(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다: " + userId));

        return singlePlayResultRepository.findByUser(findUser).stream()
                .map(result -> {
                    SingePlayResultProfileDto dto = SingePlayResultProfileDto.builder()
                            .nickname(result.getUser().getNickname())
                            .userImgName(result.getUser().getUserImg())
                            .sheetTitle(result.getSheet().getTitle())
                            .songComposer(result.getSheet().getSong().getComposer())
                            .songImgName(result.getSheet().getSong().getImgName())
                            .uploaderNickname(result.getSheet().getUploader().getNickname())
                            .level(result.getSheet().getLevel())
                            .score(result.getScore())
                            .playTime(result.getPlayTime())
                            .build();
                    dto.loadUserImg(fileUploadUtil);
                    dto.loadSongImg(fileUploadUtil);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 싱글 결과 삭제 : admin
    @Override
    public Long removeSinglePlayResult(Long singlePlayResultId) {
        singlePlayResultRepository.deleteById(singlePlayResultId);

        return singlePlayResultId;
    }
}
