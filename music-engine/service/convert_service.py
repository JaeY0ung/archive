from starlette.status import HTTP_200_OK
from dotenv import load_dotenv
import subprocess
from music21 import converter, meter, stream, metadata
import os
import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


dotenv_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), '.env')
load_dotenv(dotenv_path)

PROJECT_ROOT_PATH = os.getenv("PROJECT_ROOT_PATH")
MUSESCORE_ENV_PATH = os.getenv("MUSESCORE_ENV_PATH")

if MUSESCORE_ENV_PATH:
    MUSESCORE_ENV_PATH = os.getenv("MUSESCORE_ENV_PATH")
    # r이 포함된 상태로 가져왔다면 이를 제거해야 합니다
    if MUSESCORE_ENV_PATH.startswith('r"') or MUSESCORE_ENV_PATH.startswith("r'"):
        MUSESCORE_ENV_PATH = MUSESCORE_ENV_PATH[2:-1]

class ConvertService:
    def __init__(self, mp3_bitrate=192, channels=2, sample_rate=44100):
        self.mp3_bitrate = mp3_bitrate
        self.channels = channels
        self.sample_rate = sample_rate

    def check_ext(self, file_path):
        _, ext = os.path.splitext(file_path)
        return ext.lower()

    def webm_to_wav(self, file_path, output_dir="temp"):
        ext = self.check_ext(file_path)
        if ext != ".webm":
            raise ValueError("Only WebM files can be converted using this method")

        base_filename = os.path.splitext(os.path.basename(file_path))[0]
        wav_file = os.path.join(output_dir, f"{base_filename}.wav")

        subprocess.run(['ffmpeg', '-y', '-i', file_path, wav_file], check=True)
        
        with open(wav_file, 'rb') as f:
            wav_data = f.read()
        
        return wav_data

    def wav_to_mp3(self, wav_file, output_dir="temp"):
        base_filename = os.path.splitext(os.path.basename(wav_file))[0]
        mp3_file = os.path.join(output_dir, f"{base_filename}.mp3")

        subprocess.run(['ffmpeg', '-i', wav_file, '-y', '-f', 'mp3', '-ab',
                        f'{self.mp3_bitrate}k', '-ac', str(self.channels),
                        '-ar', str(self.sample_rate), '-vn', mp3_file], check=True)
        
        with open(mp3_file, 'rb') as f:
            mp3_data = f.read()
        
        return mp3_data

    def wav_to_midi(self, wav_file, output_dir="/app/shared/temp"):
        base_filename = os.path.splitext(os.path.basename(wav_file))[0]
        midi_file = os.path.join(output_dir, f"{base_filename}.mid")

        cmd = [
            "docker", "exec", "omnizart_container",
            "omnizart", "music", "transcribe",
            os.path.join("/app/shared/temp", os.path.basename(wav_file)),  # 절대 경로 사용
            "-o", "/app/shared/temp"
        ]
        subprocess.run(cmd, check=True)
        
        with open(midi_file, 'rb') as f:
            midi_data = f.read()
        
        return midi_data

    def xml_to_midi(self, musicxml_path, output_dir="temp"):
        base_filename = os.path.splitext(os.path.basename(musicxml_path))[0]
        midi_output_path = os.path.join(output_dir, f"{base_filename}.mid")
        score = converter.parse(musicxml_path)
        score.write('midi', fp=midi_output_path)
        
        with open(midi_output_path, 'rb') as f:
            midi_data = f.read()
        
        return midi_data

    def midi_to_xml(self, midi_file_path, xml_file_path):
        if not os.path.isfile(midi_file_path):
            raise FileNotFoundError(f"The MIDI file was not found: {midi_file_path}")

        # MuseScore를 사용하여 MIDI를 MusicXML로 변환
        try:
            subprocess.run(["xvfb-run", MUSESCORE_ENV_PATH, midi_file_path, "-o", xml_file_path], check=True)
            logger.info(f"Successfully converted {midi_file_path} to {xml_file_path} using MuseScore.")
        except subprocess.CalledProcessError as e:
            logger.info(f"An error occurred while converting {midi_file_path} to MusicXML: {e}")
        except Exception as e:
            logger.info(f"Unexpected error: {e}")
        
        # 변환된 MusicXML 파일 읽기
        with open(xml_file_path, 'rb') as f:
            musicxml_data = f.read()
        
        return musicxml_data
    
    def get_rounded_measures(self, midi_file_path, measures_per_section=8):
        """
        MIDI 파일을 파싱하여 전체 마디 수를 계산하고,
        전체 마디를 measures_per_section로 나눈 몫을 올림 처리한 후 1을 뺀 값을 반환합니다.

        :param midi_file_path: 분석할 MIDI 파일의 경로
        :param measures_per_section: 나눌 마디 수 (기본값은 8)
        :return: (올림된 마디 수의 몫에서 1을 뺀 값)
        """
        # 파일 경로가 올바른지 확인 (파일이어야 함)
        if not os.path.isfile(midi_file_path):
            raise ValueError(f"Provided path is not a valid file: {midi_file_path}")

        # MIDI 파일을 파싱
        midi_stream = converter.parse(midi_file_path)

        # 전체 마디 수 계산
        total_measures = len(midi_stream.parts[0].getElementsByClass(stream.Measure))

        # 전체 마디 수를 measures_per_section으로 나눈 몫을 올림 처리한 후 1을 뺀 값을 계산
        rounded_measures = (total_measures + measures_per_section - 1) // measures_per_section
        rounded_measures = total_measures / measures_per_section

        # 8로 나눈 나머지 계산
        remainder = total_measures % measures_per_section

        # 나머지가 있을 경우 올림된 마디 수 계산
        if remainder > 0:
            rounded_measures += 1

        # 1을 뺀 값 반환
        return rounded_measures - 1
