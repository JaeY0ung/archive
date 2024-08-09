import subprocess
from music21 import converter, metadata
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
        # Parse the MIDI file
        midi_stream = converter.parse(midi_file_path)
        
        # Retrieve existing metadata from the MIDI file if available
        midi_metadata = midi_stream.metadata
        
        # If no metadata exists, create a new metadata object
        if midi_metadata is None:
            midi_metadata = metadata.Metadata()

        # Example: Setting the title and composer if not already set
        if not midi_metadata.title:
            midi_metadata.title = "Unknown Title"
        if not midi_metadata.composer:
            midi_metadata.composer = "Unknown Composer"
        
        # Assign the metadata back to the stream
        midi_stream.metadata = midi_metadata

        # Write the stream to MusicXML format
        midi_stream.write('musicxml', fp=xml_file_path)
        
        # Read the generated MusicXML file
        with open(xml_file_path, 'rb') as f:
            musicxml_data = f.read()
        
        return musicxml_data