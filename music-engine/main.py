import uvicorn
from fastapi import FastAPI, File, UploadFile, HTTPException, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from starlette.status import HTTP_200_OK
import os
import shutil
import logging
import re
from service.convert_service import ConvertService
from service.calculate_service import calculate_similarity
from service.calculate_service import process_midi_file
from pydantic import BaseModel
from dotenv import load_dotenv

from service.level_prediction_service import LevelPredictionService
from service.midi_service import midi_service

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# CORS 설정
origins = [
    "http://localhost:3000",
    "http://localhost:8081",
    "https://arc-hive.shop"
]

app = FastAPI(root_path="/fastapi")

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

dotenv_path = os.path.join(os.path.dirname(__file__), '..env')
load_dotenv(dotenv_path)

# 임시 폴더 설정
# TODO: UPLOAD DIR, DUMMY OUTPUTS_DIR 추후 삭제
PROJECT_ROOT_PATH = os.getenv("PROJECT_ROOT_PATH")
UPLOAD_DIR = os.getenv("UPLOAD_DIR", PROJECT_ROOT_PATH)
DUMMY_OUTPUTS_DIR = "dummyOutputs"


# os.makedirs(UPLOAD_DIR, exist_ok=True)

class FileRequest(BaseModel):
    filename: str


convert_service = ConvertService()


@app.post("/playing")
async def upload_file(file: UploadFile = File(...), sheetName: str = Form(...)):
    try:
        # 파일 저장 경로 설정
        file_location = os.path.join(UPLOAD_DIR, file.filename)
        logger.info(sheetName);
        logger.info(f"업로드된 파일을 {file_location}에 저장합니다.")

        # 파일 저장
        with open(file_location, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # 파일이 실제로 저장되었는지 확인
        if not os.path.exists(file_location):
            raise FileNotFoundError(f"파일이 {file_location}에 저장되지 않았습니다.")

        # 파일 변환
        # convert_service = ConvertService()
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
        original_file_location = os.path.join(PROJECT_ROOT_PATH, "upload-sheet", "mid", sheetName+".mid");
        # original_file_location = os.path.join("original", "original.mid")

        if not os.path.exists(original_file_location):
            raise FileNotFoundError(f"original 폴더에 {original_file_location} 파일이 존재하지 않습니다.")

        start_measure = max(0, int(file_number) * 8 - 1)
        end_measure = (int(file_number) + 1) * 8 + 1
        logger.info(original_file_location)
        last_measure = convert_service.get_rounded_measures(original_file_location,8);
        similarity_results = calculate_similarity(original_file_location, midi_file_location, start_measure, end_measure)
        logger.info(similarity_results)
        isLast = 0
        if last_measure == file_number:
            isLast = 1
        return {
            "filename": file.filename,
            "wav_file": wav_file_location,
            "midi_file": midi_file_location,
            "content_type": file.content_type,
            "similarity_results": similarity_results,
            "isLast" : isLast,
        }

    except Exception as e:
        logger.error(f"파일 업로드 또는 변환 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="내부 서버 오류")


@app.post("/sheets/mid-to-xml")
async def mid2xml(file_request: FileRequest):
    filename = file_request.filename
    try:
        # 입력 파일 경로 설정
        input_mid_path = os.path.join(PROJECT_ROOT_PATH, "upload-sheet/mid", filename)
        if not os.path.exists(input_mid_path):
            raise FileNotFoundError(f"{input_mid_path} 파일이 존재하지 않습니다.")

        # 출력 파일 경로 설정
        output_xml_path = os.path.join(PROJECT_ROOT_PATH, "upload-sheet/musicxml",
                                       f"{os.path.splitext(filename)[0]}.musicxml")
        # logger.info("output_xml_path: " + output_xml_path)
        # MIDI 파일을 MusicXML로 변환
        convert_service.midi_to_xml(input_mid_path, output_xml_path)

        # 결과 확인
        if not os.path.exists(output_xml_path):
            raise FileNotFoundError(f"{output_xml_path} 파일이 생성되지 않았습니다.")

        return JSONResponse(content={"message": "Conversion successful", "output_file": output_xml_path},
                            status_code=200)

    except Exception as e:
        logger.error(f"파일 변환 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="파일 변환 중 오류 발생")

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_DIR = os.path.join(CURRENT_DIR,"model")
TRAIN_DATA_DIR = os.path.join(PROJECT_ROOT_PATH, "file", "train-midi")
level_prediction_service = LevelPredictionService(MODEL_DIR, TRAIN_DATA_DIR)

class MidiRequest(BaseModel):
    filename: str


@app.post("/process-midi")
async def process_midi(request: MidiRequest):
    try:
        # TRAIN_DATA_PATH를 설정하고 MidiService 초기화
        train_data_path = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid")
        midi_service.initialize(train_data_path)
        logger.info("MIDI 서비스 초기화 완료")

        similar_songs, input_features = midi_service.find_similar_songs(
            request.filename)

        return JSONResponse(content={
            "similar_songs": similar_songs,
            "input_features": input_features
        })
    except FileNotFoundError as e:
        logger.error(f"Error processing MIDI file: {str(e)}", exc_info=True)
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        logger.error(f"Error processing MIDI file: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500,
                            detail=f"Internal server error: {str(e)}")

class FileRequest(BaseModel):
    filename: str

@app.post("/predict-difficulty")
async def predict_difficulty(file_request: FileRequest):
    try:
        midi_file_path = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid", file_request.filename)
        if not os.path.exists(midi_file_path):
            raise FileNotFoundError(f"{midi_file_path} 파일이 존재하지 않습니다.")

        difficulty, confidence = level_prediction_service.predict_difficulty(midi_file_path)

        return JSONResponse(content={
            "filename": file_request.filename,
            "predicted_difficulty": int(difficulty),
            "prediction_confidence": float(confidence)
        }, status_code=200)

    except FileNotFoundError as e:
        logger.error(f"MIDI 파일을 찾을 수 없음: {str(e)}", exc_info=True)
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        logger.error(f"난이도 예측 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"내부 서버 오류: {str(e)}")


# FastAPI 실행 명령어
# uvicorn main:app --reload --host localhost --port 8000
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True)
