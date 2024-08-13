package com.ssafy.los.backend.service.sheet;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicServiceImpl implements MusicService {

    private final RestTemplate restTemplate;

    @Value("${cors.allowedOrigins.music-engine}")
    private String musicEngineBaseUrl;

    @Value("${cors.allowedOrigins.predict")
    private String predictBaseUrl;

    public void saveMidFileWithSplit(String midFileName) throws IllegalArgumentException {
        String url = musicEngineBaseUrl + "/sheets/mid-to-xml";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("filename", midFileName);
        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("파일 변환 실패: " + midFileName);
        }

    }

    @Override
    public String searchRecommendMidFile(String url, String midFileName)
            throws IllegalArgumentException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("filename", midFileName);
        log.info("URL :::{}", url);
        HttpEntity<Object> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, request, String.class);
    }
}
