import numpy as np
import pretty_midi
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from sklearn.metrics.pairwise import cosine_similarity

from service.feature_extraction import extract_features_from_data


# 노트 필터링 함수
def filter_notes(notes):
    return [note for note in notes if note[2] > 0.05]  # 길이가 0.05초보다 짧은 노트는 제거

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
def adjust_timing(notes, tolerance=0.01):
    return [(round(start / tolerance) * tolerance, pitch, round(duration / tolerance) * tolerance) for start, pitch, duration in notes]

# 피치 오프셋 적용 함수
def apply_pitch_offset(notes, offset):
    return [(start, pitch + offset, duration) for start, pitch, duration in notes]

# 노트 비교를 위한 시각화 함수
def plot_notes(notes1, notes2, matched_notes, best_shift, title1='Original Notes', title2='Result Notes', time_tolerance=0.3, pitch_tolerance=1.0):
    fig, axs = plt.subplots(2, 1, figsize=(15, 10), sharex=True, sharey=True)

    # Original notes
    times1, pitches1 = zip(*[(note[0], note[1]) for note in notes1])
    colors1 = []

    if matched_notes:
        first_matched_time = min(note[0] for note in matched_notes)
        last_matched_time = max(note[0] for note in matched_notes)
        min_pitch = min(note[1] for note in matched_notes)
        max_pitch = max(note[1] for note in matched_notes)
    else:
        first_matched_time = last_matched_time = min_pitch = max_pitch = 0

    for note in notes1:
        if note in matched_notes:
            colors1.append('red')  # Original의 빨간점 (매칭된 노트)
        elif note[0] < first_matched_time or note[0] > last_matched_time:
            colors1.append('black')  # Original의 검은점 (범위 밖의 노트)
        else:
            colors1.append('blue')  # Original의 파란점 (매칭되지 않은 노트)

    axs[0].scatter(times1, pitches1, c=colors1, edgecolors='k', label='Original Notes')

    axs[0].set_title(title1)
    axs[0].set_ylabel('Pitch')
    axs[0].legend()

    # Result notes (shifted)
    shifted_notes2 = [(start + best_shift, pitch, duration) for start, pitch, duration in notes2]
    times2, pitches2 = zip(*[(note[0], note[1]) for note in shifted_notes2])
    colors2 = []

    for note in shifted_notes2:
        if any(abs(note[0] - match[0]) < time_tolerance and abs(note[1] - match[1]) <= pitch_tolerance for match in matched_notes):
            colors2.append('green')  # Result의 초록점 (매칭된 노트)
        elif note[1] < min_pitch or note[1] > max_pitch:
            colors2.append('black')  # Result의 검은점 (범위 밖의 노트)
        else:
            colors2.append('blue')  # Result의 파란점 (매칭되지 않은 노트)

    axs[1].scatter(times2, pitches2, c=colors2, edgecolors='k', label='Result Notes')

    axs[1].set_title(title2)
    axs[1].set_xlabel('Time (s)')
    axs[1].set_ylabel('Pitch')
    axs[1].legend()

    plt.show()

# 노트 매칭 개수 계산 및 매칭된 노트 추출
def note_matching_count(notes1, notes2, tolerance, pitch_tolerance):
    matched_notes = []
    for note1 in notes1:
        for note2 in notes2:
            if abs(note1[0] - note2[0]) < tolerance and abs(note1[1] - note2[1]) <= pitch_tolerance:
                matched_notes.append(note1)
                break
    precision = len(matched_notes) / len(notes2) if len(notes2) > 0 else 0
    recall = len(matched_notes) / len(notes1) if len(notes1) > 0 else 0
    f1 = 2 * (precision * recall) / (precision + recall) if (precision + recall) > 0 else 0
    return len(matched_notes), matched_notes, precision, recall, f1

# 최적의 시간 이동 찾기 함수 (양수 및 음수 이동 고려, 음을 버리면서 이동)
def find_best_alignment(original_notes, m4a_notes, min_shift, max_shift, step, max_discard_time, time_tolerance, pitch_tolerance):
    best_match_count = 0
    best_shift = 0
    best_aligned_notes = m4a_notes
    best_matched_notes = []
    best_shifted_notes = []

    for shift in np.arange(min_shift, max_shift + step, step):
        for start_idx in range(len(m4a_notes)):
            if m4a_notes[start_idx][0] > max_discard_time:
                break
            shifted_notes_temp = [(start + shift, pitch, duration) for start, pitch, duration in m4a_notes[start_idx:]]
            adjusted_shifted_notes = adjust_timing(shifted_notes_temp)
            match_count, matched_notes, _, _, _ = note_matching_count(original_notes, adjusted_shifted_notes, time_tolerance, pitch_tolerance)
            if match_count > best_match_count:
                best_match_count = match_count
                best_shift = shift
                best_aligned_notes = adjusted_shifted_notes
                best_matched_notes = matched_notes
                best_shifted_notes = shifted_notes_temp

    return best_shift, best_aligned_notes, best_matched_notes, best_shifted_notes

def calculate_best_similarity(original_notes, aligned_notes, pitch_range, time_tolerance):
    best_jaccard_sim = 0
    best_f1 = 0
    best_pitch_offset = 0
    best_intersection = 0
    best_offset_notes = []
    best_set1 = set()
    best_set2 = set()

    for offset in range(-pitch_range, pitch_range + 1):
        offset_notes = apply_pitch_offset(aligned_notes, offset)

        # 자카드 유사도 계산
        set1 = set((round(start / time_tolerance) * time_tolerance, pitch) for start, pitch, _ in original_notes)
        set2 = set((round(start / time_tolerance) * time_tolerance, pitch) for start, pitch, _ in offset_notes)
        intersection = len(set1 & set2)

        # 합집합은 매칭된 노트 (빨간색) + 매칭되지 않은 노트 (파란색)의 수로 계산
        union = len(set1) + len([note for note in original_notes if (round(note[0] / time_tolerance) * time_tolerance, note[1]) not in set2]) + len([note for note in offset_notes if (round(note[0] / time_tolerance) * time_tolerance, note[1]) not in set1])

        print(f"Offset: {offset}, Intersection: {intersection}, Union: {union}")

        jaccard_sim = intersection / union if union != 0 else 0

        # 노트 매칭 정확도 계산
        matched_notes = 0
        for note1 in original_notes:
            for note2 in offset_notes:
                if abs(note1[0] - note2[0]) < time_tolerance and abs(note1[1] - note2[1]) <= 1.0:
                    matched_notes += 1
                    break
        precision = matched_notes / len(offset_notes) if len(offset_notes) > 0 else 0
        recall = matched_notes / len(original_notes) if len(original_notes) > 0 else 0
        f1 = 2 * (precision * recall) / (precision + recall) if (precision + recall) > 0 else 0

        # 교집합의 크기가 가장 큰 값을 선택
        if intersection > best_intersection:
            best_jaccard_sim = jaccard_sim
            best_f1 = f1
            best_pitch_offset = offset
            best_intersection = intersection
            best_offset_notes = offset_notes
            best_set1 = set1
            best_set2 = set2

    return best_jaccard_sim, best_f1, best_pitch_offset, best_offset_notes, best_set1, best_set2

def calculate_cosine_similarity(original_notes, aligned_notes):
    vec1 = np.array([note[:2] for note in original_notes]).flatten()
    vec2 = np.array([note[:2] for note in aligned_notes]).flatten()

    if len(vec1) > len(vec2):
        vec2 = np.pad(vec2, (0, len(vec1) - len(vec2)), 'constant')
    else:
        vec1 = np.pad(vec1, (0, len(vec2) - len(vec1)), 'constant')

    cosine_sim = cosine_similarity([vec1], [vec2])[0][0]
    return cosine_sim

def get_measure_duration(midi_file):
    midi_data = pretty_midi.PrettyMIDI(midi_file)
    time_signature = midi_data.time_signature_changes[0]
    tempo = midi_data.get_tempo_changes()[1][0]
    beats_per_measure = time_signature.numerator
    beat_duration = 60 / tempo
    return beats_per_measure * beat_duration

def calculate_similarity(original_file, piano_file, start_measure, end_measure):
    measure_duration = get_measure_duration(original_file)
    notes1 = load_midi_notes(original_file)
    notes2 = load_midi_notes(piano_file)
    notes1 = adjust_timing(notes1)
    notes2 = adjust_timing(notes2)

    # 시간 범위를 설정하여 해당 마디의 노트만 선택, 앞뒤로 1마디 추가
    start_time = max(0, (start_measure - 1) * measure_duration)
    end_time = (end_measure + 1) * measure_duration
    notes1_segment = [note for note in notes1 if start_time <= note[0] < end_time]

    # 앞부분을 버리고 새로운 노트로 설정
    notes1_segment = [(note[0] - start_time, note[1], note[2]) for note in notes1_segment]

    # 최적의 시간 이동 찾기
    min_shift = -5.0
    max_shift = 5.0
    step = 0.05
    max_discard_time = 5.0
    time_tolerance = 0.3
    pitch_tolerance = 1.0

    best_shift, best_aligned_notes, best_matched_notes, best_shifted_notes = find_best_alignment(
        notes1_segment, notes2, min_shift, max_shift, step, max_discard_time, time_tolerance, pitch_tolerance)

    # 원본의 가장 높은 음보다 높거나 가장 낮은 음보다 낮은 음을 필터링
    min_pitch = min(note[1] for note in notes1_segment)
    max_pitch = max(note[1] for note in notes1_segment)
    filtered_aligned_notes = [note for note in best_aligned_notes if min_pitch <= note[1] <= max_pitch]
    ignored_notes = [note for note in notes2 if note not in filtered_aligned_notes or note[1] < min_pitch or note[1] > max_pitch]

    # 매칭되지 않은 노트
    unmatched_shifted_notes = [note for note in best_shifted_notes if note not in filtered_aligned_notes]

    # 최적의 피치 오프셋 적용
    best_jaccard_sim, best_f1, best_pitch_offset, best_offset_notes, set1, set2 = calculate_best_similarity(
        [note for note in notes1_segment if min_pitch <= note[1] <= max_pitch], filtered_aligned_notes, 1, time_tolerance)

    # 가장 처음 매핑된 노트와 가장 마지막에 매핑된 노트를 기준으로 노트 필터링
    if best_matched_notes:
        first_matched_time = min(note[0] for note in best_matched_notes)
        last_matched_time = max(note[0] for note in best_matched_notes)
        valid_notes1_segment = [note for note in notes1_segment if first_matched_time <= note[0] <= last_matched_time]
        valid_best_offset_notes = [note for note in best_offset_notes if first_matched_time <= note[0] <= last_matched_time]
    else:
        valid_notes1_segment = notes1_segment
        valid_best_offset_notes = best_offset_notes

    # 유사도 계산 시 회색과 검은색 노트들을 제외
    valid_notes1_segment = [note for note in valid_notes1_segment if first_matched_time <= note[0] <= last_matched_time]
    valid_best_offset_notes = [note for note in valid_best_offset_notes if first_matched_time <= note[0] <= last_matched_time]

    # 색상별로 노트 개수 저장
    original_red_notes = best_matched_notes  # Original의 빨간점 (매칭된 노트)
    original_blue_notes = [note for note in notes1_segment if note not in best_matched_notes and first_matched_time <= note[0] <= last_matched_time]  # Original의 파란점 (매칭되지 않은 노트)
    result_green_notes = [note for note in valid_best_offset_notes if any(abs(note[0] - match[0]) < time_tolerance and abs(note[1] - match[1]) <= pitch_tolerance for match in best_matched_notes)]  # Result의 초록점 (매칭된 노트)
    result_blue_notes = [note for note in valid_best_offset_notes if note not in result_green_notes and min_pitch <= note[1] <= max_pitch]  # Result의 파란점 (매칭되지 않은 노트)

    # 코사인 유사도 계산
    final_cosine_sim = calculate_cosine_similarity(valid_notes1_segment, valid_best_offset_notes)

    # 최종 자카드 유사도 계산
    intersection = len(original_red_notes)
    union = len(original_red_notes) + len(original_blue_notes) + len(result_blue_notes)

    final_jaccard_sim = intersection / union if union != 0 else 0

    precision = len(original_red_notes)/(len(original_red_notes)+len(result_blue_notes))
    recall = len(original_red_notes)/(len(original_red_notes)+len(original_blue_notes))

    final_f1_sim = 2 * precision * recall / (precision + recall) if (precision + recall) != 0 else 0

    # 교집합과 합집합 크기 및 자카드 유사도 출력
    print(f"Intersection: {intersection}, Union: {union}, Final Jaccard Similarity: {final_jaccard_sim}")
    print(f"Final F1 Similarity : {final_f1_sim}")

    final_similarity = (final_f1_sim * 0.5) + (final_jaccard_sim * 0.5)

    return {
        'cosine_similarity': final_cosine_sim,
        'jaccard_similarity': final_jaccard_sim,
        'f1_score': final_f1_sim,
        'final_similarity': final_similarity,
        'matched_notes': best_matched_notes,  # Original의 빨간점
        'original_segment_notes': notes1_segment,  # Original의 모든 노트 (빨간점, 파란점, 검은점 포함)
        'aligned_notes': filtered_aligned_notes,  # Result의 모든 노트 (초록점, 파란점, 검은점 포함)
        'offset_notes': best_offset_notes,  # 피치 오프셋이 적용된 Result의 노트 (초록점, 파란점, 검은점 포함)
        'ignored_notes': ignored_notes,  # Result의 검은점
        'unmatched_shifted_notes': unmatched_shifted_notes,  # Result의 파란점
        'best_shift': best_shift  # 최적의 시간 이동
    }

# K-평균 클러스터링
# def perform_clustering(feature_matrix, n_clusters=3):
#     from sklearn.cluster import KMeans
#     kmeans = KMeans(n_clusters=n_clusters, random_state=0).fit(feature_matrix)
#     return kmeans.labels_, kmeans.cluster_centers_

def perform_clustering(features, n_clusters=3):
    if len(features) < n_clusters:
        return [0] * len(features), [features[0]]  # 단일 클러스터로 처리
    kmeans = KMeans(n_clusters=min(n_clusters, len(features)), random_state=42, n_init=10)
    labels = kmeans.fit_predict(features)
    return labels, kmeans.cluster_centers_


# MIDI 파일 처리 함수
def process_midi_file(file_data):
    features = extract_features_from_data(file_data)

    feature_matrix = np.array([features])
    from sklearn.decomposition import PCA
    pca = PCA(n_components=2)
    reduced_features = pca.fit_transform(feature_matrix)
    labels, centers = perform_clustering(reduced_features)

    return labels, centers



