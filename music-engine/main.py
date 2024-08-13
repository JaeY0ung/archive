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
import subprocess
import requests

# from service.level_prediction_service import LevelPredictionService
# from service.midi_service import midi_service

dotenv_path = os.path.join(os.path.dirname(__file__), '.env')
load_dotenv(dotenv_path)
PROJECT_ROOT_PATH = os.getenv("PROJECT_ROOT_PATH")
UPLOAD_DIR = os.getenv("UPLOAD_DIR", PROJECT_ROOT_PATH)
PC = os.getenv("PC")
# 로깅 설정
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("app.log"),
        logging.StreamHandler()
    ]
)
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

class FileRequest(BaseModel):
    filename: str


convert_service = ConvertService()

@app.post("/playing/single")
async def upload_file(file: UploadFile = File(...), uuid: str = Form(...), singleResultId: str = Form(...)):
    logger.info("single_result_id" + singleResultId)
    try:
        # 파일 저장 경로 설정
        file_location = os.path.join(UPLOAD_DIR, file.filename)

        # 임시 폴더가 존재하는지 확인하고 없으면 생성
        if not os.path.exists(UPLOAD_DIR):
            os.makedirs(UPLOAD_DIR)

        # 파일 저장
        with open(file_location, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # 파일이 실제로 저장되었는지 확인
        if not os.path.exists(file_location):
            raise FileNotFoundError(f"파일이 {file_location}에 저장되지 않았습니다.")

        # 파일명에 포함된 숫자 추출
        match = re.search(r'\d+', file.filename)
        if match:
            file_number = match.group()
            wav_file_name = f"converted_{file_number}.wav"
            midi_file_name = f"converted_{file_number}.mid"
        else:
            wav_file_name = "converted.wav"
            midi_file_name = "converted.mid"
            file_number = ""

        if PC == "LOCAL":
            file_extension = os.path.splitext(file.filename)[1].lower()
            if file_extension == ".webm":
                # webm 파일을 wav로 변환
                wav_file_name = f"converted_{file_number}.wav"
                wav_file_location = os.path.join(UPLOAD_DIR, wav_file_name)
                conversion_command = [
                    "ffmpeg", "-y", "-i", file_location, wav_file_location
                ]
                subprocess.run(conversion_command, check=True)

                if not os.path.exists(wav_file_location):
                    raise FileNotFoundError(f"변환된 파일이 {wav_file_location}에 생성되지 않았습니다.")

                logger.info(f"변환된 WAV 파일이 {wav_file_location}에 저장되었습니다.")

            elif file_extension == ".wav":
                # 업로드된 파일이 wav인 경우
                wav_file_name = f"converted_{file_number}.wav"
                wav_file_location = os.path.join(UPLOAD_DIR, wav_file_name)
                shutil.move(file_location, wav_file_location)
            else:
                raise HTTPException(status_code=400, detail="지원되지 않는 파일 형식입니다. webm 또는 wav 파일만 지원됩니다.")

            # wav 파일을 midi로 변환
            midi_file_name = f"result_{file_number}.mid"
            midi_file_location = os.path.join(UPLOAD_DIR, midi_file_name)

            docker_command = [
                "docker", "run", "-v",
                f"{os.path.abspath(UPLOAD_DIR)}:/app/data",
                "mctlab/omnizart:latest",
                "bash", "-c",
                f"omnizart music transcribe /app/data/{wav_file_name} -o /app/data/{midi_file_name}"
            ]

            logger.info(f"Docker 명령어 실행: {' '.join(docker_command)}")
            subprocess.run(docker_command, check=True)

            # 변환된 파일 경로 확인 및 이름 확인
            if not os.path.exists(midi_file_location):
                raise FileNotFoundError(f"MIDI 파일이 {midi_file_location}에 생성되지 않았습니다.")
        else:
            # 파일 변환
            wav_data = convert_service.webm_to_wav(file_location, UPLOAD_DIR)

            gpu_server_url = "http://222.107.238.124:9234/convert_wav_to_midi/"
            files = {'file': (wav_file_name, wav_data, 'audio/wav')}
            try:
                response = requests.post(gpu_server_url, files=files)
                response.raise_for_status()  # HTTP 오류 발생 시 예외를 던짐

                # GPU 서버로부터 변환된 MIDI 파일 데이터를 수신
                midi_data = response.content

                # MIDI 데이터를 임시 파일로 저장
                midi_file_location = os.path.join(UPLOAD_DIR, midi_file_name)
                with open(midi_file_location, "wb") as midi_file:
                    midi_file.write(midi_data)

            except requests.exceptions.RequestException as e:
                logger.error(f"GPU 서버와의 통신 중 오류 발생: {str(e)}")
                raise HTTPException(status_code=500, detail="GPU 서버와의 통신 중 오류 발생")

            # # WAV 데이터를 임시 파일로 저장
            # wav_file_location = os.path.join(UPLOAD_DIR, wav_file_name)
            # with open(wav_file_location, "wb") as wav_file:
            #     wav_file.write(wav_data)

            # # WAV 파일을 MIDI로 변환
            # midi_data = convert_service.wav_to_midi(wav_file_location, UPLOAD_DIR)

            # # MIDI 데이터를 임시 파일로 저장
            # midi_file_location = os.path.join(UPLOAD_DIR, midi_file_name)
            # with open(midi_file_location, "wb") as midi_file:
            #     midi_file.write(midi_data)

            # 원본 webm 파일 삭제
            os.remove(file_location)


        # 원본 MIDI 파일 경로 설정
        original_file_location = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid", uuid + ".mid")

        if not os.path.exists(original_file_location):
            raise FileNotFoundError(f"original 폴더에 {original_file_location} 파일이 존재하지 않습니다.")

        start_measure = max(0, int(file_number) * 8 - 1)
        end_measure = (int(file_number) + 1) * 8 + 1
        last_measure = convert_service.get_rounded_measures(original_file_location,8);
        similarity_results = calculate_similarity(original_file_location, midi_file_location, start_measure, end_measure)
        is_last = 0
        if last_measure == int(file_number):
            is_last = 1

        return {
            "filename": file.filename,
            "wav_file": wav_file_location,
            "midi_file": midi_file_location,
            "content_type": file.content_type,
            "similarity_results": similarity_results,
            "isLast": is_last,
            "file_number": file_number,
            "last_measure": last_measure,
        }

    except subprocess.CalledProcessError as e:
        logger.error(f"Docker 명령어 실행 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Docker 명령어 실행 중 오류 발생")

    except Exception as e:
        logger.error(f"파일 업로드 또는 변환 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="내부 서버 오류")


@app.post("/sheets/mid-to-xml")
async def mid2xml(file_request: FileRequest):
    filename = file_request.filename
    try:
        # 입력 파일 경로 설정
        input_mid_path = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid", filename)
        if not os.path.exists(input_mid_path):
            raise FileNotFoundError(f"{input_mid_path} 파일이 존재하지 않습니다.")

        # 출력 파일 경로 설정
        output_xml_path = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "musicxml",
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


# level_prediction_service = LevelPredictionService(MODEL_DIR, TRAIN_DATA_DIR)

class MidiRequest(BaseModel):
    filename: str


# @app.post("/process-midi")
# async def process_midi(request: MidiRequest):
#     try:
#         # TRAIN_DATA_PATH를 설정하고 MidiService 초기화
#         train_data_path = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid")
#         midi_service.initialize(train_data_path)
#         logger.info("MIDI 서비스 초기화 완료")

#         similar_songs, input_features = midi_service.find_similar_songs(
#             request.filename)

#         return JSONResponse(content={
#             "similar_songs": similar_songs,
#             "input_features": input_features
#         })
#     except FileNotFoundError as e:
#         logger.error(f"Error processing MIDI file: {str(e)}", exc_info=True)
#         raise HTTPException(status_code=404, detail=str(e))
#     except Exception as e:
#         logger.error(f"Error processing MIDI file: {str(e)}", exc_info=True)
#         raise HTTPException(status_code=500,
#                             detail=f"Internal server error: {str(e)}")

class FileRequest(BaseModel):
    filename: str

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
if __name__ == "__main__":
    # uvicorn main:app --reload --host localhost --port 8000
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True)
