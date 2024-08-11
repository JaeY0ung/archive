import os
import logging
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, VotingClassifier
from sklearn.svm import SVC
from xgboost import XGBClassifier
import joblib
import pretty_midi

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class LevelPredictionService:
    def __init__(self, model_dir):
        model_path = os.path.join(model_dir, 'ensemble_clf_model.joblib')
        scaler_path = os.path.join(model_dir, 'scaler.joblib')
        self.model = joblib.load(model_path)
        self.scaler = joblib.load(scaler_path)

    def extract_features(self, midi_file):
        try:
            midi_data = pretty_midi.PrettyMIDI(midi_file)
            
            # Feature extraction logic
            chroma = midi_data.get_chroma()
            chromaticism = 1 - (np.max(np.sum(chroma, axis=1)) / np.sum(chroma))
            
            pitches = [note.pitch for instrument in midi_data.instruments for note in instrument.notes]
            pitch_mean = np.mean(pitches) if pitches else 0
            pitch_range = np.max(pitches) - np.min(pitches) if pitches else 0
            
            velocities = [note.velocity for instrument in midi_data.instruments for note in instrument.notes]
            velocity_mean = np.mean(velocities) if velocities else 0
            
            total_notes = sum(len(instrument.notes) for instrument in midi_data.instruments)
            total_time = midi_data.get_end_time()
            note_density = total_notes / total_time if total_time > 0 else 0
            
            note_lengths = [note.end - note.start for instrument in midi_data.instruments for note in instrument.notes]
            avg_note_duration = np.mean(note_lengths) if note_lengths else 0
            
            chord_complexity = np.mean([len(set(note.pitch for note in instrument.notes if note.start == start_time))
                                        for instrument in midi_data.instruments
                                        for start_time in set(note.start for note in instrument.notes)])
            
            unique_note_lengths = len(set(note_lengths))
            rhythm_complexity = unique_note_lengths / total_notes if total_notes > 0 else 0
            
            return {
                'chromaticism': chromaticism,
                'pitch_mean': pitch_mean,
                'pitch_range': pitch_range,
                'velocity_mean': velocity_mean,
                'note_density': note_density,
                'avg_note_duration': avg_note_duration,
                'chord_complexity': chord_complexity,
                'rhythm_complexity': rhythm_complexity
            }
        except Exception as e:
            logger.error(f"Error extracting features from {midi_file}: {str(e)}")
            return None

    def predict_difficulty(self, midi_file):
        features = self.extract_features(midi_file)
        if features is None:
            return None
        
        features_df = pd.DataFrame([features])
        features_scaled = self.scaler.transform(features_df)
        
        prediction = self.model.predict(features_scaled)
        probabilities = self.model.predict_proba(features_scaled)
        
        difficulty = prediction[0]
        confidence = probabilities[0][prediction[0] - 1] * 100
        
        return difficulty, confidence







# 사용 예시
if __name__ == "__main__":
    # 이 부분은 테스트 목적으로만 사용됩니다.
    PROJECT_ROOT_PATH = os.getenv("PROJECT_ROOT_PATH")
    MODEL_DIR = os.path.join(PROJECT_ROOT_PATH, "models")
    
    service = LevelPredictionService(
        model_path=os.path.join(MODEL_DIR, 'ensemble_clf_model.joblib'),
        scaler_path=os.path.join(MODEL_DIR, 'scaler.joblib')
    )
    
    # # 테스트 MIDI 파일 경로
    # test_midi_file = os.path.join(PROJECT_ROOT_PATH, "file", "upload-sheet", "mid", "test.mid")
    
    if os.path.exists(test_midi_file):
        difficulty, confidence = service.predict_difficulty(test_midi_file)
        print(f"Predicted difficulty: {difficulty}")
        print(f"Confidence: {confidence:.2f}%")
    else:
        print(f"Test file not found: {test_midi_file}")