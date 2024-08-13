# Python 3.9 기본 이미지 사용
FROM python:3.8

# 필요한 시스템 패키지 설치
RUN apt-get update && apt-get install -y --no-install-recommends 

# pip 최신 버전으로 업그레이드
RUN pip install --upgrade pip

# Cython, numpy 설치
RUN pip install numpy==1.21.6 Cython==0.29.24

# omnizart 설치

# 작업 디렉토리 설정
WORKDIR /app

# 종속성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 소스 코드 복사
COPY . .

# 포트 노출
EXPOSE 8001

# 애플리케이션 실행 명령
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]
