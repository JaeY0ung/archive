import subprocess
import os

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

        # 변환된 파일의 경로 설정
        base_filename = os.path.splitext(os.path.basename(file_path))[0]
        wav_file = os.path.join(output_dir, f"{base_filename}.wav")

        # ffmpeg를 사용하여 변환
        subprocess.run(['ffmpeg','-y', '-i', file_path, wav_file], check=True)
        return wav_file

    def wav_to_mp3(self, wav_file, output_dir="temp"):
        base_filename = os.path.splitext(os.path.basename(wav_file))[0]
        mp3_file = os.path.join(output_dir, f"{base_filename}.mp3")

        subprocess.run(['ffmpeg', '-i', wav_file, '-y', '-f', 'mp3', '-ab',
                        f'{self.mp3_bitrate}k', '-ac', str(self.channels),
                        '-ar', str(self.sample_rate), '-vn', mp3_file], check=True)
        return mp3_file

    def webm_to_mp3(self, file_path, output_dir="temp"):
        wav_file = self.convert_to_wav(file_path, output_dir)
        mp3_file = self.convert_to_mp3(wav_file, output_dir)
        return mp3_file
    def wav_to_midi(self, wav_file, output_dir="temp"):
        base_filename = os.path.splitext(os.path.basename(wav_file))[0]
        midi_file = os.path.join(output_dir, f"{base_filename}.mid")

        cmd = [
            "docker", "exec", "omnizart_container",
            "omnizart", "music", "transcribe",
            wav_file, "-o", output_dir
        ]
        
        subprocess.run(cmd, check=True)
        return os.path.join(output_dir, f"{base_filename}.mid")
