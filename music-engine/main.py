from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from service.midi_to_mp3_service import MidiToMp3Service
import os
import shutil

app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"],  # 특정 도메인만 허용 (예: http://localhost:3000)
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


@app.get("/convert/{filename}")
async def say_hello(filename: str):
    midi_to_mp3_service = MidiToMp3Service()
    midi_to_mp3_service.convert_midi_to_mp3()
    return {"message": f"{filename} 생성 완료"}

@app.post("/playing")
async def upload_file(file: UploadFile = File(...)):
    try:
        # 파일 저장 경로 설정
        file_location = os.path.join(UPLOAD_DIR, file.filename)
        
        # 파일 저장
        with open(file_location, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        
        return {"filename": file.filename, "content_type": file.content_type}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
