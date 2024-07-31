# 기존의 Dockerfile 내용 시작

FROM python:3.9-slim

# 시스템 패키지 업데이트 및 Docker CLI 설치
RUN apt-get update && apt-get install -y ffmpeg docker.io

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

# 기존의 Dockerfile 내용 끝
