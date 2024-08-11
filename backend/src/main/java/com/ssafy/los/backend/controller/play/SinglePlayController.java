package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.dto.play.request.SinglePlayRequestDto;
import com.ssafy.los.backend.dto.play.response.SingePlayResultProfileDto;
import com.ssafy.los.backend.service.play.SinglePlayService;
import com.ssafy.los.backend.service.sheet.SheetService;
import java.io.IOException;
import java.util.List;

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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
// TODO : play -> plays로 변환하기
@RequestMapping("/plays/single")
public class SinglePlayController {

    @Value("${fastapi.server.url}")
    private String fastapiServerUrl;
    private final SinglePlayService singlePlayService;
    private final SheetService sheetService;

    // 싱글 중간 결과 생성 후 파이썬 전송
    @PostMapping(value = "/sendFile")
    public ResponseEntity<?> sendIntermediateScoreToPython(
            @RequestPart(value = "file", required = false) MultipartFile file, @RequestPart("sheetId") Long sheetId) {
        log.info("Client로부터 중간 점수를 전송 받음: {}", file);

        String url = fastapiServerUrl+"/playing";

        Sheet sheet = sheetService.searchById(sheetId);

        String sheetName = sheet.getUuid();

        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url);

            // 멀티파트 엔티티 생성
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file.getInputStream(), ContentType.MULTIPART_FORM_DATA,
                    file.getOriginalFilename());
            builder.addTextBody("sheetName", sheetName, ContentType.TEXT_PLAIN);
            HttpEntity multipart = builder.build();

            request.setEntity(multipart);

            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                String responseString = EntityUtils.toString(response.getEntity());
                return ResponseEntity.ok(responseString);
            } else {
                return ResponseEntity.status(statusCode).body("파일 전송 실패");
            }
//            request.setHeader("Content-Type", "multipart/form-data");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send file");
        }
    }

    // 싱글 최종 결과 생성
    @PostMapping
    private ResponseEntity<?> saveSinglePlayResult(SinglePlayRequestDto singlePlayRequestDto) {
        Long singePlayResultId = singlePlayService.saveSinglePlayResult(singlePlayRequestDto);

        return new ResponseEntity<>(singePlayResultId, HttpStatus.CREATED);
    }

    // 생성날짜를 기준으로 싱글 결과를 조회한다.
    @GetMapping
    private ResponseEntity<?> getSinglePlayResult(
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {
        Page<SinglePlayResult> singlePlayResultList = singlePlayService.getSinglePlayResultList(
                pageable);

        return new ResponseEntity<>(singlePlayResultList, HttpStatus.OK);
    }

    // 특정 유저에 대해서 멀티 결과 기록들을 조회한다. (프로필 페이지)
    @GetMapping("/{user-id}")
    private ResponseEntity<?> getSinglePlayResultAllByUser(@PathVariable("user-id") Long userId) {
        List<SingePlayResultProfileDto> singlePlayResultList = singlePlayService.getSinglePlayResultListByUser(userId);

        return new ResponseEntity<>(singlePlayResultList, HttpStatus.OK);
    }

    @DeleteMapping("/{single-result-id}")
    private ResponseEntity<?> deleteSinglePlayResult(
            @PathVariable("single-result-id") Long singleResultId) {
        Long deletedSingleResultId = singlePlayService.removeSinglePlayResult(singleResultId);
        return new ResponseEntity<>(deletedSingleResultId, HttpStatus.OK);
    }


}
