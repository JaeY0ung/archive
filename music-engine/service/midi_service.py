import logging
import os
import numpy as np
from io import BytesIO
from sklearn.neighbors import NearestNeighbors
from .feature_extraction import extract_features_from_data


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


class MidiService:
  def __init__(self):
    self.training_features = None
    self.training_file_names = None
    self.nn_model = None
    self.train_data_path = None

  def initialize(self, train_data_path):
    self.train_data_path = train_data_path
    self.load_training_data()
    self.train_model()

  def load_training_data(self):
    features = []
    file_names = []
    for filename in os.listdir(self.train_data_path):
      if filename.endswith('.mid'):
        file_path = os.path.join(self.train_data_path, filename)
        with open(file_path, 'rb') as f:
          midi_data = f.read()
        feature = extract_features_from_data(BytesIO(midi_data))
        features.append(feature)
        file_names.append(filename)
    self.training_features = np.array(features)
    self.training_file_names = file_names

  def train_model(self):
    self.nn_model = NearestNeighbors(n_neighbors=1, metric='euclidean')
    self.nn_model.fit(self.training_features)

  def find_similar_songs(self, file_name):
    if not self.train_data_path:
      logger.error("MIDI 서비스가 초기화되지 않았습니다.")
      raise ValueError("MIDI 서비스가 초기화되지 않았습니다.")

    file_path = os.path.join(self.train_data_path, file_name)
    if not os.path.exists(file_path):
      logger.error(f"MIDI 파일을 찾을 수 없습니다: {file_name}")
      raise FileNotFoundError(f"MIDI 파일을 찾을 수 없습니다: {file_name}")

    with open(file_path, 'rb') as midi_file:
      midi_data = midi_file.read()

    features = extract_features_from_data(BytesIO(midi_data))
    distances, indices = self.nn_model.kneighbors([features])

    similar_songs = [
      {
        "file_name": self.training_file_names[i],
        "distance": distances[0][j]
      } for j, i in enumerate(indices[0])
    ]
    return similar_songs, features.tolist()


# TRAIN_DATA_PATH = r"C:\SSAFY\Archive\file\upload-sheet\mid"
TRAIN_DATA_PATH = r"/Users/moongi/Desktop/SSAFY/Archive/file/upload-sheet/mid"
# midi_service = MidiService(TRAIN_DATA_PATH)

midi_service = MidiService()
