import os
import fnmatch  # name matching
import subprocess  # to handle subprocesses
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
import mido
import numpy as np
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
import os
from collections import Counter

PROJECT_PATH = os.path.abspath(os.getcwd())
original_mid_directory = os.path.join(PROJECT_PATH, 'res', 'original', 'mid')
original_convert_to_mp3_directory = os.path.join(PROJECT_PATH, 'res',
                                                 'original', 'mp3')
TIMIDITY_PATH = os.path.join(PROJECT_PATH, 'TiMidity++-2.15.0', "timidity")

app = FastAPI()


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
  tonality = tonality_mapping.get(best_key, -1)  # Use -1 for unknown tonality

  chromatic_notes = sum(note_counts[note] for note in range(12) if
                        note not in key_notes[best_key])
  chromaticism = chromatic_notes / len(notes)

  return tonality, chromaticism


# MIDI 파일에서 특징 추출
def extract_features(midi_file_path):
  mid = mido.MidiFile(midi_file_path)

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
    return np.zeros(12)  # 노트가 없으면 0 벡터 반환

  pitch_mean = np.mean(notes[:, 0])  # 음 높이 평균
  pitch_range = np.max(notes[:, 0]) - np.min(notes[:, 0])  # 음 높이 범위
  volume_mean = np.mean(notes[:, 1])  # 볼륨 평균
  volume_range = np.ptp(notes[:, 1])  # 볼륨 범위
  note_density = len(notes) / (total_time if total_time > 0 else 1)  # 노트 밀도
  avg_note_duration = np.mean(np.diff(notes[:, 2]))  # 평균 노트 길이
  num_instruments = len(instruments)  # 악기 수

  # tonality 및 chromaticism 계산
  tonality, chromaticism = calculate_tonality_chromaticism(notes[:, 0])

  return np.array(
      [pitch_mean, pitch_range, volume_mean, volume_range, note_density,
       avg_note_duration, num_instruments, tonality, chromaticism, total_time])


# K-평균 클러스터링
def perform_clustering(feature_matrix, n_clusters=3):
  kmeans = KMeans(n_clusters=n_clusters, random_state=0).fit(feature_matrix)
  return kmeans.labels_, kmeans.cluster_centers_


def perform_clustering(features, n_clusters=3):
  if len(features) < n_clusters:
    return [0] * len(features), [features[0]]  # 단일 클러스터로 처리
  kmeans = KMeans(n_clusters=min(n_clusters, len(features)), random_state=42)
  labels = kmeans.fit_predict(features)
  return labels, kmeans.cluster_centers_

# predict
