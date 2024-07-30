from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from service.convert_service import ConvertService
import os
import shutil

app = FastAPI(root_path="/fastapi")

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000","http://localhost:8081","https://arc-hive:shop"],  # 특정 도메인만 허용 (예: http://localhost:3000)
    allow_credentials=True,
    allow_methods=["*"],  # 모든 HTTP 메서드 허용
    allow_headers=["*"],  # 모든 헤더 허용
)

# 임시 폴더 설정
UPLOAD_DIR = "temp"
os.makedirs(UPLOAD_DIR, exist_ok=True)

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
        
        # 파일 저장
        with open(file_location, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        
        # 파일 변환
        convert_service = ConvertService()
        wav_file = convert_service.webm_to_wav(file_location, UPLOAD_DIR)
        
        # 원본 webm 파일 삭제 함수
        os.remove(file_location)
        
        return {"filename": file.filename, "wav_file": wav_file, "content_type": file.content_type}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
