# Python 3.9 기본 이미지 사용
FROM python:3.9

# 시스템 패키지 업데이트 및 필수 패키지 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    software-properties-common \
    apt-transport-https \
    ca-certificates \
    gnupg \
    musescore \
    xvfb \
    llvm-14 llvm-14-dev build-essential && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# LLVM_CONFIG 환경 변수 설정
ENV LLVM_CONFIG=/usr/bin/llvm-config-14

# pip 최신 버전으로 업그레이드
RUN pip install --upgrade pip

# 호환 가능한 llvmlite 버전 설치
RUN pip install llvmlite==0.40.0

# Cython, numpy, setuptools 설치
RUN pip install Cython numpy==1.21.6 setuptools==59.5.0

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
