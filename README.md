# Archive : 피아노 악보 공유 & 난이도 제공 & 대결 웹 서비스

구글 드라이브(다운 장소) : https://drive.google.com/drive/folders/1cM1lv-Jh-szOb0iiLOi7LWmhLsBe0fM5?usp=drive_link

## 프로젝트 세팅
1. 클론 받은 프로젝트 폴더를 C:\SSAFY로 이동
2. 이름을 Archive로 변경

<br>

## 파이썬 서버 (music-engine) 가이드
###  ```Archive\music-engine```에 저장
1. ```ffmpeg-master-latest-win64-gpl``` 폴더
2. ```TiMidity++-2.15.0``` 폴더
3. ```ffmpeg-2024-07-10-git-1a86a7a48d-full_build.7z``` 파일

### ```Archive\music-engine\soundfonts```에 저장
1. ```soundfonts.sf2``` 파일


### 시스템 환경 변수 Path에 추가
1. ```C:\SSAFY\Archive\music-engine\TiMidity++-2.15.0```
2. ```C:\SSAFY\Archive\music-engine\ffmpeg-master-latest-win64-gpl\bin```

### 터미널 입력
1. ```Archive\music-engine```에서 터미널 열기
2. ```pip install -r requirements.txt``` 입력

<br>

## Spring Boot 세팅 가이드
1. ```application.properties``` 다운 받아 ```resources``` 폴더 밑에 저장하고 수정
2. ```archive``` 이름으로 스케마(데이터베이스) 만들기 
    - ```DROP SCHEMA IF EXISTS archive;```
    - ```CREATE SCHEMA IF NOT EXISTS archive DEFAULT CHARACTER SET UTF8mb4;```
3. IntelliJ Setting : Actions on Save 설정. 
<br>

## Vue 세팅 가이드

1. ```.env``` 파일 다운 받아 .env로 이름 변경하여, ```/frontend``` 폴더 밑에 저장
2. ```Archive\frontend```에서 터미널 열기
3. ```npm install``` 입력