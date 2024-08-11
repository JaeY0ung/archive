# Python 3.9-slim 이미지 사용
FROM python:3.9

# 시스템 패키지 업데이트 및 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    ffmpeg \
    software-properties-common \
    apt-transport-https \
    ca-certificates \
    docker.io

# MuseScore PPA 추가 및 설치
RUN add-apt-repository ppa:mscore-ubuntu/mscore-stable -y && \
    apt-get update && \
    apt-get install -y musescore

# 작업 디렉토리 설정
WORKDIR /app

# 종속성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 소스 코드 복사
COPY . .

# 포트 노출
EXPOSE 8000

# 애플리케이션 실행 명령
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
