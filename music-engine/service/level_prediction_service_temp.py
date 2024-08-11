import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, VotingClassifier
from sklearn.svm import SVC
from xgboost import XGBClassifier
from sklearn.metrics import classification_report, confusion_matrix
import pretty_midi
import warnings
import joblib

# 현재 스크립트의 디렉토리를 기준으로 상대 경로 설정
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

def extract_features(midi_file):
    try:
        with warnings.catch_warnings():
            warnings.simplefilter("ignore")
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
        
        def get_chord_type(notes):
            pitches = sorted(set(note.pitch for note in notes))
            intervals = [pitches[i+1] - pitches[i] for i in range(len(pitches)-1)]
            
            if len(intervals) == 2:
                if intervals == [3, 4]: return "minor_triad"
                elif intervals == [4, 3]: return "major_triad"
                elif intervals == [3, 3]: return "diminished_triad"
                elif intervals == [4, 4]: return "augmented_triad"
            elif len(intervals) == 3:
                if intervals == [3, 3, 4]: return "diminished_seventh"
                elif intervals == [3, 4, 3]: return "minor_seventh"
                elif intervals == [4, 3, 3]: return "dominant_seventh"
                elif intervals == [4, 3, 4]: return "major_seventh"
            return "other"

        chord_counts = {"major_triad": 0, "minor_triad": 0, "diminished_triad": 0, "augmented_triad": 0,
                        "major_seventh": 0, "minor_seventh": 0, "diminished_seventh": 0, "dominant_seventh": 0, "other": 0}
        
        for instrument in midi_data.instruments:
            notes = instrument.notes
            for i in range(len(notes)):
                chord_notes = [notes[i]]
                j = i + 1
                while j < len(notes) and notes[j].start == notes[i].start:
                    chord_notes.append(notes[j])
                    j += 1
                if len(chord_notes) > 1:
                    chord_type = get_chord_type(chord_notes)
                    chord_counts[chord_type] += 1
        
        total_chords = sum(chord_counts.values())
        chord_percentages = {k: v/total_chords for k, v in chord_counts.items()} if total_chords > 0 else chord_counts
        
        simple_chords = chord_percentages['major_triad'] + chord_percentages['minor_triad']
        complex_chords = 1 - simple_chords  # 나머지 모든 화음
        
        return {
            'chromaticism': chromaticism,
            'pitch_mean': pitch_mean,
            'pitch_range': pitch_range,
            'velocity_mean': velocity_mean,
            'note_density': note_density,
            'avg_note_duration': avg_note_duration,
            'chord_complexity': chord_complexity,
            'rhythm_complexity': rhythm_complexity,
            'simple_chord_ratio': simple_chords,
            'complex_chord_ratio': complex_chords
        }
    except Exception as e:
        print(f"Error processing {midi_file}: {str(e)}")
        return None

def process_midi_files(folder_path):
    features = []
    for file in os.listdir(folder_path):
        if file.endswith('.mid') or file.endswith('.midi'):
            file_path = os.path.join(folder_path, file)
            file_features = extract_features(file_path)
            if file_features:
                features.append(file_features)
    return features

def train_model():
    print("Starting model training process...")
    
    features = []
    labels = []
    
    for level in range(1, 6):  # 1부터 5까지의 레벨
        level_folder = os.path.join(BASE_DIR, 'midi_files', str(level))
        if not os.path.exists(level_folder):
            print(f"Warning: Folder for level {level} not found.")
            continue
        
        level_features = process_midi_files(level_folder)
        features.extend(level_features)
        labels.extend([level] * len(level_features))
    
    if not features:
        print("Error: No features extracted from MIDI files. Please check the data.")
        return None, None

    print("Creating dataset...")
    df = pd.DataFrame(features)
    df['level'] = labels

    print("Removing outliers...")
    def remove_outliers(df):
        Q1 = df.quantile(0.25)
        Q3 = df.quantile(0.75)
        IQR = Q3 - Q1
        return df[~((df < (Q1 - 1.5 * IQR)) | (df > (Q3 + 1.5 * IQR))).any(axis=1)]

    df_clean = remove_outliers(df.drop('level', axis=1))
    df_clean['level'] = df.loc[df_clean.index, 'level']

    X = df_clean.drop('level', axis=1)
    y = df_clean['level']

    print("Splitting data into training and test sets...")
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    print("Scaling features...")
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)

    print("Training ensemble model...")
    rf = RandomForestClassifier(n_estimators=100, random_state=42)
    gb = GradientBoostingClassifier(n_estimators=100, random_state=42)
    svm = SVC(kernel='rbf', probability=True, random_state=42)
    xgb = XGBClassifier(n_estimators=100, random_state=42)

    ensemble_clf = VotingClassifier(
        estimators=[('rf', rf), ('gb', gb), ('svm', svm), ('xgb', xgb)],
        voting='soft'
    )

    ensemble_clf.fit(X_train_scaled, y_train)
    
    print("Evaluating model...")
    ensemble_pred = ensemble_clf.predict(X_test_scaled)
    print("Ensemble Classifier Results:")
    print(classification_report(y_test, ensemble_pred))

    print("Performing cross-validation...")
    cv_scores = cross_val_score(ensemble_clf, X_train_scaled, y_train, cv=5)
    print(f"Cross-validation scores: {cv_scores}")
    print(f"Mean CV score: {cv_scores.mean():.4f} (+/- {cv_scores.std() * 2:.4f})")

    print("Saving model and scaler...")
    joblib.dump(ensemble_clf, os.path.join(BASE_DIR, 'ensemble_clf_model.joblib'))
    joblib.dump(scaler, os.path.join(BASE_DIR, 'scaler.joblib'))
    print("Model and scaler saved successfully.")

    return ensemble_clf, scaler

def predict_difficulty(model, scaler, midi_file):
    features = extract_features(midi_file)
    if features is None:
        return None
    
    features_df = pd.DataFrame([features])
    features_scaled = scaler.transform(features_df)
    
    prediction = model.predict(features_scaled)
    probabilities = model.predict_proba(features_scaled)
    
    difficulty = prediction[0]
    confidence = probabilities[0][prediction[0] - 1] * 100  # 예측된 클래스의 확률
    
    return difficulty, confidence

def evaluate_midi_files(model, scaler, folder_path):
    results = []
    for file in os.listdir(folder_path):
        if file.endswith('.mid') or file.endswith('.midi'):
            file_path = os.path.join(folder_path, file)
            prediction = predict_difficulty(model, scaler, file_path)
            if prediction:
                difficulty, confidence = prediction
                results.append({
                    'file': file,
                    'predicted_difficulty': difficulty,
                    'confidence': confidence
                })
    return results

# 메인 실행 부분
if __name__ == "__main__":
    # 모델 훈련 또는 로드
    model_path = os.path.join(BASE_DIR, 'ensemble_clf_model.joblib')
    scaler_path = os.path.join(BASE_DIR, 'scaler.joblib')
    
    if os.path.exists(model_path) and os.path.exists(scaler_path):
        print("Loading pre-trained model and scaler...")
        model = joblib.load(model_path)
        scaler = joblib.load(scaler_path)
        print("Pre-trained model and scaler loaded successfully.")
    else:
        print("No pre-trained model found. Starting new model training...")
        model, scaler = train_model()
        if model is None or scaler is None:
            print("Error: Model training failed. Please check your training data and paths.")
            exit()
        print("New model training completed and saved.")

    # 평가 MIDI 파일 처리
    eval_folder = os.path.join(BASE_DIR, 'midi_files', 'eval')
    print(f"Evaluating MIDI files in {eval_folder}")
    evaluation_results = evaluate_midi_files(model, scaler, eval_folder)

    # 결과 출력
    print("\nEvaluation Results:")
    print("=" * 70)
    print(f"{'File Name':<30} {'Predicted Level':<20} {'Confidence':<10}")
    print("=" * 70)
    for result in evaluation_results:
        print(f"{result['file']:<30} {result['predicted_difficulty']:<20} {result['confidence']:.2f}%")
    print("=" * 70)

    # 결과 통계
    total_files = len(evaluation_results)
    level_counts = {i: sum(1 for r in evaluation_results if r['predicted_difficulty'] == i) for i in range(1, 6)}
    
    print(f"\nTotal files evaluated: {total_files}")
    for level, count in level_counts.items():
        print(f"Predicted as Level {level}: {count} ({count/total_files*100:.2f}%)")

    # 평균 신뢰도
    avg_confidence = sum(r['confidence'] for r in evaluation_results) / total_files
    print(f"Average prediction confidence: {avg_confidence:.2f}%")