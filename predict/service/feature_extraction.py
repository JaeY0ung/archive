import mido
import numpy as np
from collections import Counter
from io import BytesIO

# MIDI 파일에서 tonality 및 chromaticism 계산
def calculate_tonality_chromaticism(notes):
  key_notes = {
    'C': [0, 2, 4, 5, 7, 9, 11],
    'C#': [1, 3, 5, 6, 8, 10, 0],
    'D': [2, 4, 6, 7, 9, 11, 1],
    'D#': [3, 5, 7, 8, 10, 0, 2],
    'E': [4, 6, 8, 9, 11, 1, 3],
    'F': [5, 7, 9, 10, 0, 2, 4],
    'F#': [6, 8, 10, 11, 1, 3, 5],
    'G': [7, 9, 11, 0, 2, 4, 6],
    'G#': [8, 10, 0, 1, 3, 5, 7],
    'A': [9, 11, 1, 2, 4, 6, 8],
    'A#': [10, 0, 2, 3, 5, 7, 9],
    'B': [11, 1, 3, 4, 6, 8, 10]
  }

  notes = notes % 12
  note_counts = Counter(notes)
  best_key = None
  max_intersection = 0

  for key, key_note_values in key_notes.items():
    intersection = sum(note_counts[note] for note in key_note_values)
    if intersection > max_intersection:
      max_intersection = intersection
      best_key = key

  tonality_mapping = {key: i for i, key in enumerate(key_notes.keys())}
  tonality = tonality_mapping.get(best_key, -1) # Use -1 for unknown tonality

  chromatic_notes = sum(note_counts[note] for note in range(12) if note not in key_notes[best_key])
  chromaticism = chromatic_notes / len(notes)

  return tonality, chromaticism

def extract_features_from_data(file_data):
  if isinstance(file_data, BytesIO):
    mid = mido.MidiFile(file=file_data)
  elif isinstance(file_data, bytes):
    mid = mido.MidiFile(file=BytesIO(file_data))
  else:
    raise ValueError("Unsupported data type. Expected BytesIO or bytes.")

  notes = []
  instruments = set()
  total_time = 0

  for track in mid.tracks:
    current_time = 0
    for msg in track:
      if msg.type in ['note_on', 'note_off']:
        current_time += msg.time
        if msg.type == 'note_on' and msg.velocity > 0:
          notes.append((msg.note, msg.velocity, current_time))
          instruments.add(msg.channel)

  notes = np.array(notes)

  if notes.size == 0:
    return np.zeros(10)  # 노트가 없으면 0 벡터 반환

  pitch_mean = np.mean(notes[:, 0])
  pitch_range = np.max(notes[:, 0]) - np.min(notes[:, 0])
  volume_mean = np.mean(notes[:, 1])
  volume_range = np.ptp(notes[:, 1])
  note_density = len(notes) / (total_time if total_time > 0 else 1)
  avg_note_duration = np.mean(np.diff(notes[:, 2])) if len(notes) > 1 else 0
  num_instruments = len(instruments)

  # tonality 및 chromaticism 계산
  tonality, chromaticism = calculate_tonality_chromaticism(notes[:, 0])

  return np.array([pitch_mean, pitch_range, volume_mean, volume_range, note_density, avg_note_duration, num_instruments, tonality, chromaticism, total_time])
