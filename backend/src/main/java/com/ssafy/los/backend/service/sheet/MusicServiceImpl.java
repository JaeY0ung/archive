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

    public String saveMidFileWithSplit(String midFileName) {
        String url = musicEngineBaseUrl + "/sheets/mid-to-xml";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        HttpEntity<Object> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, request, String.class);
    }
}
