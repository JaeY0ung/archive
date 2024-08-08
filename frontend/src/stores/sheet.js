// src/stores/musicStore.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';
import { OpenSheetMusicDisplay, PlaybackManager, BasicAudioPlayer, LinearTimingSource } from '@/assets/js/opensheetmusicdisplay.min.js';
import { getMusicXmlById } from '@/api/sheet';
import { localAxios } from "@/util/http-common";

export const useMusicStore = defineStore('music', () => {
    const local = localAxios();
    const osmd = ref(null);
    const playbackManager = ref(null);
    const audioPlayer = ref(new BasicAudioPlayer());
    const timingSource = ref(new LinearTimingSource());
    const isPlay = ref(false);
    const isRecording = ref(false);
    const mediaRecorder = ref(null);
    const audioBlobs = ref([]);
    const chunks = ref([]);
    const triggerSplit = ref(0);
    const volume = ref(50);
    const f1 = ref([]);
    const jaccard = ref([]);

    const initializeOsmd = (container) => {
        osmd.value = new OpenSheetMusicDisplay(container);
        osmd.value.setOptions({
            pageBackgroundColor: 'white',
            drawPartNames: false,
        });
    };

    const loadMusicXML = async (sheetId) => {
        let xml;
        await getMusicXmlById(sheetId, ({ data }) => {
            xml = data;
        });
        return xml;
    };

    const setupPlaybackManager = () => {
        playbackManager.value = new PlaybackManager(timingSource.value, undefined, audioPlayer.value, undefined);
        playbackManager.value.DoPlayback = true;
        playbackManager.value.DoPreCount = false;
        playbackManager.value.PreCountMeasures = 1;
        osmd.value.FollowCursor = true;

        timingSource.value.reset();
        timingSource.value.pause();
        timingSource.value.Settings = osmd.value.Sheet.playbackSettings;
        playbackManager.value.initialize(osmd.value.Sheet.musicPartManager);
        playbackManager.value.addListener(osmd.value.cursor);

        let lastEmittedMeasureIndex = 0;
        playbackManager.value.addListener({
            cursorPositionChanged: (currentTimestamp, data) => {
                const currentMeasureIndex = data.CurrentMeasureIndex;
                if (currentMeasureIndex >= 0 && currentMeasureIndex - lastEmittedMeasureIndex >= 8) {
                    lastEmittedMeasureIndex = currentMeasureIndex;
                    triggerSplit.value++;
                    splitRecording();
                }
            },
            resetOccurred: (data) => {
                lastEmittedMeasureIndex = 0;
            },
            soundLoaded: (instrumentId, name) => {},
            allSoundsLoaded: () => {},
            pauseOccurred: () => {
                isPlay.value = false;
            },
            selectionEndReached: () => {
                stopRecording();
            },
            notesPlaybackEventOccurred: () => {},
        });
        playbackManager.value.reset();
        osmd.value.PlaybackManager = playbackManager.value;
    };

    const loadAndSetupOsmd = async (container, sheetId) => {
        initializeOsmd(container);
        const xml = await loadMusicXML(sheetId);
        await osmd.value.load(xml);
        osmd.value.render();
        setupPlaybackManager();
    };

    const setVolume = (vol) => {
        volume.value = vol;
        if (playbackManager.value) {
            const instrumentIds = Array.from(playbackManager.value.instrumentIdMapping.keys());
            instrumentIds.forEach((instrumentId) => {
                playbackManager.value.volumeChanged(instrumentId, volume.value);
            });
        }
    };

    const startMusic = () => {
        isPlay.value = true;
        playbackManager.value.play();
        timingSource.value.start();
    };

    const pauseMusic = () => {
        isPlay.value = false;
        playbackManager.value.pause();
        timingSource.value.pause();
    };

    const stopMusic = () => {
        pauseMusic();
        playbackManager.value.reset();
        timingSource.value.reset();
    };

    const startRecording = async () => {
        startMusic();
        audioBlobs.value = [];
        const stream = await navigator.mediaDevices.getUserMedia({
            audio: {
                sampleRate: 44100,
                channelCount: 2,
                noiseSuppression: false,
                echoCancellation: false,
                autoGainControl: false,
            },
        });

        mediaRecorder.value = new MediaRecorder(stream);

        mediaRecorder.value.ondataavailable = (event) => {
            if (event.data.size > 0) {
                chunks.value.push(event.data);
            }
        };

        mediaRecorder.value.onstop = async () => {
            if (chunks.value.length > 0) {
                const formData = new FormData();
                const blob = new Blob(chunks.value, { type: 'audio/webm' });
                formData.append('file', blob, `chunk_${audioBlobs.value.length}.webm`);
                const sheetIdBlob = new Blob([3], { type: 'application/json' });
                formData.append('sheetId', sheetIdBlob);
                console.log("formData", formData);
                for (let [key, value] of formData.entries()) {
                    console.log(key, value);
                }
                try {
                    const res = await local.post('/play/single/sendFile', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                        },
                    });
                    console.log('파일 업로드 성공: ', res.data);
                    f1.value.push(res.data.similarity_results.f1_score);
                    jaccard.value.push(res.data.similarity_results.jaccard_similarity);
                } catch (err) {
                    console.error('파일 업로드 실패: ', err);
                }
                audioBlobs.value.push({ blob });
                chunks.value = [];
            }

            if (isRecording.value) {
                mediaRecorder.value.start();
            }
        };

        mediaRecorder.value.start();
        isRecording.value = true;
    };

    const splitRecording = () => {
        if (isRecording.value && mediaRecorder.value.state === 'recording') {
            mediaRecorder.value.stop();
        }
    };

    const stopRecording = () => {
        isRecording.value = false;
        if (mediaRecorder.value && mediaRecorder.value.state === 'recording') {
            mediaRecorder.value.stop();
        }
        isPlay.value=false;
    };

    return {
        osmd,
        playbackManager,
        audioPlayer,
        timingSource,
        isPlay,
        isRecording,
        mediaRecorder,
        audioBlobs,
        chunks,
        triggerSplit,
        volume,
        f1,
        jaccard,
        initializeOsmd,
        loadAndSetupOsmd,
        setVolume,
        startMusic,
        pauseMusic,
        stopMusic,
        startRecording,
        splitRecording,
        stopRecording,
    };
});
