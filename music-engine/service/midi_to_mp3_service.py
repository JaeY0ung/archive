import fnmatch  # name matching
import os       # file listing
import subprocess  # to handle subprocesses

PROJECT_PATH = os.path.abspath(os.getcwd())
original_mid_directory = os.path.join(PROJECT_PATH, 'res', 'original', 'mid')
original_convert_to_mp3_directory = os.path.join(PROJECT_PATH, 'res', 'original', 'mp3')
TIMIDITY_PATH = os.path.join(PROJECT_PATH, 'TiMidity++-2.15.0', "timidity")

class MidiToMp3Service:
    def __init__(self):
        # CONFIGURATION
        # self.do_ffmpeg_convert = True  # Uses FFmpeg to convert WAV files to MP3. Requires ffmpeg.exe in the script folder or PATH
        self.do_wav_cleanup = True  # Deletes WAV files after conversion to MP3
        self.sample_rate = 44100  # Sample rate used for WAV/MP3
        self.channels = 2  # Audio channels (1 = mono, 2 = stereo)
        self.mp3_bitrate = 128  # Bitrate to save MP3 with in kbps (CBR)

    def convert_midi_to_mp3(self):
        # Make a list of .mid files in the specified directory
        matches = []
        for root, dirNames, filenames in os.walk(original_mid_directory):
            for filename in fnmatch.filter(filenames, '*.mid'):
                matches.append(os.path.join(root, filename))

        # Convert each MIDI file to WAV and then to MP3
        for song in matches:
            # Create a filename with a .wav extension
            file_name = os.path.splitext(os.path.basename(song))[0]
            wav_file = os.path.join(os.path.dirname(song), file_name + '.wav')
            mp3_file = os.path.join(os.path.dirname(song), '..', 'mp3', file_name + '.mp3')

            # Convert MIDI to WAV using timidity with specified configuration file
            subprocess.run([TIMIDITY_PATH, song, '-Ow', '-o', wav_file], check=True)

            # Convert WAV to MP3 using ffmpeg
            # if not self.do_ffmpeg_convert:
            #     pass

            subprocess.run(['ffmpeg', '-i', wav_file, '-y', '-f', 'mp3', '-ab',
                            f'{self.mp3_bitrate}k', '-ac', str(self.channels),
                            '-ar', str(self.sample_rate), '-vn', mp3_file], check=True)

            # Delete the WAV file if desired
            if self.do_wav_cleanup:
                os.remove(wav_file)
        print("Conversion complete.")


if __name__ == '__main__':
    midi_to_mp3_service = MidiToMp3Service()
    midi_to_mp3_service.convert_midi_to_mp3()