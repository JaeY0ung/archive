from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import os
import shutil
import logging

# ConvertService를 가져옵니다.
from service.convert_service import ConvertService

app = FastAPI(root_path="/fastapi")

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://localhost:8081", "https://arc-hive.shop"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 임시 폴더 설정
UPLOAD_DIR = "temp"
os.makedirs(UPLOAD_DIR, exist_ok=True)

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}

@app.post("/playing")
async def upload_file(file: UploadFile = File(...)):
    try:
        # 파일 저장 경로 설정
        file_location = os.path.join(UPLOAD_DIR, file.filename)
        logger.info(f"업로드된 파일을 {file_location}에 저장합니다.")

        # 파일 저장
        with open(file_location, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # 파일 변환
        convert_service = ConvertService()
        logger.info(f"파일 {file_location}을 변환합니다.")
        wav_file = convert_service.webm_to_wav(file_location, UPLOAD_DIR)

        # 원본 webm 파일 삭제
        os.remove(file_location)
        logger.info(f"원본 파일 {file_location}을 삭제했습니다.")

        return {"filename": file.filename, "wav_file": wav_file, "content_type": file.content_type}
    except Exception as e:
        logger.error(f"파일 업로드 또는 변환 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="내부 서버 오류")
