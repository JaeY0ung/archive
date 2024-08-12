import os
import logging
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, VotingClassifier
from sklearn.svm import SVC
from xgboost import XGBClassifier
from sklearn.metrics import classification_report
import joblib
import pretty_midi
import warnings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class LevelPredictionService:
    def __init__(self, model_dir, midi_data_dir):
        self.model_dir = model_dir
        self.midi_data_dir = midi_data_dir
        self.model_path = os.path.join(model_dir, 'ensemble_clf_model.joblib')
        self.scaler_path = os.path.join(model_dir, 'scaler.joblib')
        
        if os.path.exists(self.model_path) and os.path.exists(self.scaler_path):
            logger.info("Loading pre-trained model and scaler...")
            self.model = joblib.load(self.model_path)
            self.scaler = joblib.load(self.scaler_path)
        else:
            logger.info("No pre-trained model found. Training new model...")
            self.train_model()

    def extract_features(self, midi_file):
        try:
            midi_data = pretty_midi.PrettyMIDI(midi_file)
            
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

    def process_midi_files(self, folder_path):
        features = []
        for file in os.listdir(folder_path):
            if file.endswith('.mid') or file.endswith('.midi'):
                file_path = os.path.join(folder_path, file)
                file_features = self.extract_features(file_path)
                if file_features:
                    features.append(file_features)
        return features

    def train_model(self):
        logger.info("Starting model training process...")
        
        features = []
        labels = []
        
        for level in range(1, 6):  # 1부터 5까지의 레벨
            level_folder = os.path.join(self.midi_data_dir, str(level))
            if not os.path.exists(level_folder):
                logger.warning(f"Folder for level {level} not found.")
                continue
            
            level_features = self.process_midi_files(level_folder)
            features.extend(level_features)
            labels.extend([level] * len(level_features))
        
        if not features:
            logger.error("No features extracted from MIDI files. Please check the data.")
            return

        logger.info("Creating dataset...")
        df = pd.DataFrame(features)
        df['level'] = labels

        logger.info("Removing outliers...")
        df_clean = self.remove_outliers(df.drop('level', axis=1))
        df_clean['level'] = df.loc[df_clean.index, 'level']

        X = df_clean.drop('level', axis=1)
        y = df_clean['level']

        logger.info("Splitting data into training and test sets...")
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

        logger.info("Scaling features...")
        self.scaler = StandardScaler()
        X_train_scaled = self.scaler.fit_transform(X_train)
        X_test_scaled = self.scaler.transform(X_test)

        logger.info("Training ensemble model...")
        rf = RandomForestClassifier(n_estimators=100, random_state=42)
        gb = GradientBoostingClassifier(n_estimators=100, random_state=42)
        svm = SVC(kernel='rbf', probability=True, random_state=42)
        xgb = XGBClassifier(n_estimators=100, random_state=42)

        self.model = VotingClassifier(
            estimators=[('rf', rf), ('gb', gb), ('svm', svm), ('xgb', xgb)],
            voting='soft'
        )

        self.model.fit(X_train_scaled, y_train)
        
        logger.info("Evaluating model...")
        ensemble_pred = self.model.predict(X_test_scaled)
        logger.info("Ensemble Classifier Results:\n" + classification_report(y_test, ensemble_pred))

        logger.info("Performing cross-validation...")
        cv_scores = cross_val_score(self.model, X_train_scaled, y_train, cv=5)
        logger.info(f"Cross-validation scores: {cv_scores}")
        logger.info(f"Mean CV score: {cv_scores.mean():.4f} (+/- {cv_scores.std() * 2:.4f})")

        logger.info("Saving model and scaler...")
        joblib.dump(self.model, self.model_path)
        joblib.dump(self.scaler, self.scaler_path)
        logger.info("Model and scaler saved successfully.")

    @staticmethod
    def remove_outliers(df):
        Q1 = df.quantile(0.25)
        Q3 = df.quantile(0.75)
        IQR = Q3 - Q1
        return df[~((df < (Q1 - 1.5 * IQR)) | (df > (Q3 + 1.5 * IQR))).any(axis=1)]

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