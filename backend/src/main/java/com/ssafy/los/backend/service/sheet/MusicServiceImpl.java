package com.ssafy.los.backend.service.sheet;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final RestTemplate restTemplate;

    @Value("${cors.allowedOrigins.music-engine}")
    private String musicEngineBaseUrl;

    public String saveMidFileWithSplit(String midFileName) {
        String url = musicEngineBaseUrl + "/sheets/mid-to-xml";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(midFileName, headers);

        return restTemplate.postForObject(url, request, String.class);
    }
}
