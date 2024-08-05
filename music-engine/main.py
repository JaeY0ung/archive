import uvicorn
from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from starlette.status import HTTP_200_OK
import os
import shutil
import logging
import re
from service.convert_service import ConvertService
from service.calculate_service import calculate_similarity
from pydantic import BaseModel

PROJECT_ROOT_PATH = os.getenv('PROJECT_ROOT_PATH')

app = FastAPI(root_path="/fastapi")

# CORS 설정
origins = [
    "http://localhost:3000",
    "http://localhost:8081",
    "https://arc-hive.shop"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 임시 폴더 설정
UPLOAD_DIR = os.getenv("UPLOAD_DIR", PROJECT_ROOT_PATH)
DUMMY_OUTPUTS_DIR = "dummyOutputs"
#os.makedirs(UPLOAD_DIR, exist_ok=True)

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class FileRequest(BaseModel):
    filename: str

@app.post("/playing")
async def upload_file(file: UploadFile = File(...)):
    print("playing")
    try:
        # 파일 저장 경로 설정
        file_location = os.path.join(UPLOAD_DIR, file.filename)
        logger.info(f"업로드된 파일을 {file_location}에 저장합니다.")

        # 파일 저장
        with open(file_location, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # 파일이 실제로 저장되었는지 확인
        if not os.path.exists(file_location):
            raise FileNotFoundError(f"파일이 {file_location}에 저장되지 않았습니다.")

        # 파일 변환
        convert_service = ConvertService()
        logger.info(f"파일 {file_location}을 WAV로 변환합니다.")
        wav_data = convert_service.webm_to_wav(file_location, UPLOAD_DIR)

        # 파일명에 포함된 숫자 추출
        match = re.search(r'\d+', file.filename)
        if match:
            file_number = match.group()
            wav_file_name = f"converted_{file_number}.wav"
            midi_file_name = f"converted_{file_number}.mid"
        else:
            wav_file_name = "converted.wav"
            midi_file_name = "converted.mid"

        # WAV 데이터를 임시 파일로 저장
        wav_file_location = os.path.join(UPLOAD_DIR, wav_file_name)
        with open(wav_file_location, "wb") as wav_file:
            wav_file.write(wav_data)
        logger.info(f"WAV 파일이 {wav_file_location}에 저장되었습니다.")

        # WAV 파일을 MIDI로 변환
        logger.info(f"WAV 파일 {wav_file_location}을 MIDI로 변환합니다.")
        midi_data = convert_service.wav_to_midi(wav_file_location, UPLOAD_DIR)

        # MIDI 데이터를 임시 파일로 저장
        midi_file_location = os.path.join(UPLOAD_DIR, midi_file_name)
        with open(midi_file_location, "wb") as midi_file:
            midi_file.write(midi_data)
        logger.info(f"MIDI 파일이 {midi_file_location}에 저장되었습니다.")
        
        # 원본 webm 파일 삭제
        os.remove(file_location)
        logger.info(f"원본 파일 {file_location}을 삭제했습니다.")

        # 원본 MIDI 파일 경로 설정
        original_file_location = os.path.join("original", "original.mid")

        if not os.path.exists(original_file_location):
            raise FileNotFoundError(f"original 폴더에 {original_file_location} 파일이 존재하지 않습니다.")
        
        start_measure = max(0, int(file_number) * 8 - 1)
        end_measure = (int(file_number) + 1) * 8 + 1

        similarity_scores = calculate_similarity(original_file_location, midi_file_location, start_measure, end_measure)
        logger.info(similarity_scores)
        return {
            "filename": file.filename,
            "wav_file": wav_file_location,
            "midi_file": midi_file_location,
            "content_type": file.content_type,
            "similarity_scores": similarity_scores
        }

    except Exception as e:
        logger.error(f"파일 업로드 또는 변환 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="내부 서버 오류")

@app.post("/sheets/mid-to-xml")
async def mid2xml(file_request: FileRequest):
    filename = file_request.filename
    try:
        # 입력 파일 경로 설정
        input_mid_path = os.path.join("app/shared/upload-sheet/mid", filename)
        if not os.path.exists(input_mid_path):
            raise FileNotFoundError(f"{input_mid_path} 파일이 존재하지 않습니다.")

        # 출력 파일 경로 설정
        output_xml_path = os.path.join("app/shared/upload-sheet/musicxml", f"{os.path.splitext(filename)[0]}.musicxml")

        # MIDI 파일을 MusicXML로 변환
        ConvertService.midi_to_xml(input_mid_path, os.path.dirname(output_xml_path))

        # 결과 확인
        if not os.path.exists(output_xml_path):
            raise FileNotFoundError(f"{output_xml_path} 파일이 생성되지 않았습니다.")

        return JSONResponse(content={"message": "Conversion successful", "output_file": output_xml_path}, status_code=200)

    except Exception as e:
        logger.error(f"파일 변환 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="파일 변환 중 오류 발생")
    
# FastAPI 실행 명령어
# uvicorn main:app --reload --host 0.0.0.0 --port 8000
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True)
