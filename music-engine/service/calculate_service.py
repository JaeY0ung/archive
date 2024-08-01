import os
import numpy as np
import pretty_midi
from sklearn.metrics.pairwise import cosine_similarity

# 노트 필터링 함수
def filter_notes(notes):
    filtered_notes = []
    for note in notes:
        if note[2] > 0.05:  # 길이가 0.05초보다 짧은 노트는 제거
            filtered_notes.append(note)
    return filtered_notes

# MIDI 파일 로드 및 노트 이벤트 추출 함수
def load_midi_notes(file_path):
    midi_data = pretty_midi.PrettyMIDI(file_path)
    notes = []
    for instrument in midi_data.instruments:
        if not instrument.is_drum:
            for note in instrument.notes:
                notes.append((note.start, note.pitch, note.end - note.start))
    return filter_notes(sorted(notes))

# 타이밍 오차 보정 함수
def adjust_timing(notes, tolerance=0.1):
    adjusted_notes = []
    for start, pitch, duration in notes:
        adjusted_start = round(start / tolerance) * tolerance
        adjusted_duration = round(duration / tolerance) * tolerance
        adjusted_notes.append((adjusted_start, pitch, adjusted_duration))
    return adjusted_notes

# Jaccard 유사도 계산 함수
def jaccard_similarity(notes1, notes2):
    set1 = set((start, pitch) for start, pitch, _ in notes1)
    set2 = set((start, pitch) for start, pitch, _ in notes2)
    intersection = len(set1 & set2)
    union = len(set1 | set2)
    return intersection / union if union != 0 else 0

# 노트 매칭 정확도 계산 함수
def note_matching_accuracy(notes1, notes2, tolerance=0.1, pitch_tolerance=0.5):
    matched_notes = 0
    for note1 in notes1:
        for note2 in notes2:
            if abs(note1[0] - note2[0]) < tolerance and abs(note1[1] - note2[1]) <= pitch_tolerance:
                matched_notes += 1
                break
    precision = matched_notes / len(notes2) if len(notes2) > 0 else 0
    recall = matched_notes / len(notes1) if len(notes1) > 0 else 0
    f1 = 2 * (precision * recall) / (precision + recall) if (precision + recall) > 0 else 0
    return precision, recall, f1

# MIDI 유사도 계산 함수
def calculate_similarity(original_file, piano_file):
    notes1 = load_midi_notes(original_file)
    notes2 = load_midi_notes(piano_file)
    notes1 = adjust_timing(notes1)
    notes2 = adjust_timing(notes2)
    vec1 = np.array(notes1).flatten()
    vec2 = np.array(notes2).flatten()

    if len(vec1) > len(vec2):
        vec2 = np.pad(vec2, (0, len(vec1) - len(vec2)), 'constant')
    else:
        vec1 = np.pad(vec1, (0, len(vec2) - len(vec1)), 'constant')

    cosine_sim = cosine_similarity([vec1], [vec2])[0][0]
    jaccard_sim = jaccard_similarity(notes1, notes2)
    precision, recall, f1 = note_matching_accuracy(notes1, notes2)
    final_similarity = (cosine_sim * 0.3)  + (jaccard_sim * 0.2) + (f1 * 0.5)

    return {
        'cosine_similarity': cosine_sim,
        'jaccard_similarity': jaccard_sim,
        'f1_score': f1,
        'final_similarity': final_similarity
    }
