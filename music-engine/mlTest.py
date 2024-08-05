import os
import logging
from service.calculate_service import calculate_similarity, load_midi_notes, plot_notes

# 임시 폴더 설정
UPLOAD_DIR = os.path.dirname(__file__)
UPLOAD_DIR = os.path.join(UPLOAD_DIR, "temp")
if not os.path.exists(UPLOAD_DIR):
    os.makedirs(UPLOAD_DIR)

# 원본 MIDI 파일 경로
ORIGINAL_DIR = os.path.dirname(__file__)
ORIGINAL_DIR = os.path.join(ORIGINAL_DIR, "original")
if not os.path.exists(ORIGINAL_DIR):
    os.makedirs(ORIGINAL_DIR)

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

def main():
    try:
        for file_number in range(0, 4):
            midi_file_name = f"result_{file_number}.mid"
            midi_file_location = os.path.join(UPLOAD_DIR, midi_file_name)
            logger.info(f"MIDI 파일 위치: {midi_file_location}")

            # 원본 MIDI 파일 경로
            original_midi_file = os.path.join(ORIGINAL_DIR, "original.mid")  # 실제 경로로 변경 필요
            logger.info(f"원본 MIDI 파일 위치: {original_midi_file}")

            if os.path.exists(midi_file_location) and os.path.exists(original_midi_file):
                start_measure = max(0, file_number * 8 - 1)
                end_measure = (file_number + 1) * 8 + 1

                # 유사도 계산
                similarity_results = calculate_similarity(original_midi_file, midi_file_location, start_measure, end_measure)
                logger.info({
                    "midi_file": midi_file_location,
                    "similarity_results": similarity_results
                })
                print({
                    "midi_file": midi_file_location,
                    "similarity_results": similarity_results
                })

                # 분할된 원본 및 비교할 MIDI 파일의 노트 로드
                original_segment_notes = similarity_results['original_segment_notes']
                comparison_notes = load_midi_notes(midi_file_location)
                matched_notes = similarity_results['matched_notes']
                ignored_notes = similarity_results['ignored_notes']

                # 시각화 함수 호출
                plot_notes(
                    original_segment_notes,
                    comparison_notes,
                    matched_notes,
                    ignored_notes,
                    title1=f'Segment {file_number} Notes',
                    title2=f'Result {file_number} Notes'
                )
            else:
                logger.error(f"파일을 찾을 수 없습니다: {midi_file_location} 또는 {original_midi_file}")

    except Exception as e:
        logger.error(f"파일 처리 중 오류 발생: {str(e)}", exc_info=True)

if __name__ == "__main__":
    main()
