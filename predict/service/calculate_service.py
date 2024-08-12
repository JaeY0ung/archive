import numpy as np
import pretty_midi
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from service.feature_extraction import extract_features_from_data


# K-평균 클러스터링
def perform_clustering(feature_matrix, n_clusters=3):
    from sklearn.cluster import KMeans
    kmeans = KMeans(n_clusters=n_clusters, random_state=0).fit(feature_matrix)
    return kmeans.labels_, kmeans.cluster_centers_

# MIDI 파일 처리 함수
def process_midi_file(file_data):
    features = extract_features_from_data(file_data)

    feature_matrix = np.array([features])
    from sklearn.decomposition import PCA
    pca = PCA(n_components=2)
    reduced_features = pca.fit_transform(feature_matrix)
    labels, centers = perform_clustering(reduced_features)

    return labels, centers



