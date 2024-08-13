import uvicorn
from fastapi import FastAPI, File, UploadFile, HTTPException, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from starlette.status import HTTP_200_OK
import os
import logging
from service.calculate_service import process_midi_file
from pydantic import BaseModel
from dotenv import load_dotenv

# from service.level_prediction_service import LevelPredictionService
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

app = FastAPI(root_path="/predict")

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

dotenv_path = os.path.join(os.path.dirname(__file__), '.env')
load_dotenv(dotenv_path)

# 임시 폴더 설정
# TODO: UPLOAD DIR, DUMMY OUTPUTS_DIR 추후 삭제
PROJECT_ROOT_PATH = os.getenv("PROJECT_ROOT_PATH")
UPLOAD_DIR = os.getenv("UPLOAD_DIR", PROJECT_ROOT_PATH)
DUMMY_OUTPUTS_DIR = "dummyOutputs"
PC = os.getenv("PC")

class FileRequest(BaseModel):
    filename: str


CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_DIR = os.path.join(CURRENT_DIR,"model")
TRAIN_DATA_DIR = os.path.join(PROJECT_ROOT_PATH, "file", "train-midi")
# level_prediction_service = LevelPredictionService(MODEL_DIR, TRAIN_DATA_DIR)

class MidiRequest(BaseModel):
    filename: str

class FileRequest(BaseModel):
    filename: str


@app.post("/process-midi")
async def process_midi(request: MidiRequest):
    try:
        # TRAIN_DATA_PATH를 설정하고 MidiService 초기화
        train_data_path = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid", file_request.filename)
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
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")

# @app.post("/predict-difficulty")
# async def predict_difficulty(file_request: FileRequest):
#     try:
#         midi_file_path = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid", file_request.filename)
#         if not os.path.exists(midi_file_path):
#             raise FileNotFoundError(f"{midi_file_path} 파일이 존재하지 않습니다.")

#         difficulty, confidence = level_prediction_service.predict_difficulty(midi_file_path)

#         return JSONResponse(content={
#             "filename": file_request.filename,
#             "predicted_difficulty": int(difficulty),
#             "prediction_confidence": float(confidence)
#         }, status_code=200)

#     except FileNotFoundError as e:
#         logger.error(f"MIDI 파일을 찾을 수 없음: {str(e)}", exc_info=True)
#         raise HTTPException(status_code=404, detail=str(e))
#     except Exception as e:
#         logger.error(f"난이도 예측 중 오류 발생: {str(e)}", exc_info=True)
#         raise HTTPException(status_code=500, detail=f"내부 서버 오류: {str(e)}")


# FastAPI 실행 명령어
# uvicorn main:app --reload --host localhost --port 8000
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True)
