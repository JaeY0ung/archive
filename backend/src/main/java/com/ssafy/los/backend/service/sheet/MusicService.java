package com.ssafy.los.backend.service.sheet;

public interface MusicService {

    void saveMidFileWithSplit(String midFileName) throws IllegalArgumentException;
    
    String searchRecommendMidFile(String url, String midFileName) throws IllegalArgumentException;
}
