    // src/stores/musicStore.js
    import { defineStore } from 'pinia';
    import { ref, watch } from 'vue';
    import { OpenSheetMusicDisplay, PlaybackManager, BasicAudioPlayer, LinearTimingSource } from '@/assets/js/opensheetmusicdisplay.min.js';
    import { getMusicXmlById } from '@/api/sheet';
    import { localAxios } from "@/util/http-common";
    import { useRoute } from 'vue-router';

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
        const route = useRoute();
        const singleResultId = ref(0);
        const multiResultId = ref(0);
        const playMode = ref("");
        const isLast = ref(false);
        const isLastTrigger = ref(false);
        const sendRequests = ref(0);
        const receivedResponse = ref(0);
        let recordingInterval = null;

        watch(
            () => [sendRequests.value, receivedResponse.value, isLastTrigger.value],
            () => {
                if (isLastTrigger.value && sendRequests.value === receivedResponse.value) {
                    isLast.value = true;
                    isLastTrigger.value = false;
                }
            }
        ,{deep:true});

        const initializeOsmd = (container) => {
            osmd.value = new OpenSheetMusicDisplay(container);
            osmd.value.setOptions({
                pageBackgroundColor: 'white',
                drawPartNames: false,
            });
            isLast.value = false;
            isLastTrigger.value = false;
            sendRequests.value = 0;;
            receivedResponse.value = 0;
            
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

            audioPlayer.value.GainMultiplier = 6;

            timingSource.value.reset();
            timingSource.value.pause();
            timingSource.value.Settings = osmd.value.Sheet.playbackSettings;
            playbackManager.value.initialize(osmd.value.Sheet.musicPartManager);
            playbackManager.value.addListener(osmd.value.cursor);

            let lastEmittedMeasureIndex = 0;
            playbackManager.value.addListener({
                cursorPositionChanged: (currentTimestamp, data) => {
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
            isLast.value = false;
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
            f1.value = [];  // 녹음 시작 시점에 배열을 초기화
            jaccard.value = [];  // 녹음 시작 시점에 배열을 초기화
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
            recordingInterval = setInterval(() => {
                if (isRecording.value) {
                    splitRecording();
                }
            }, 10000); 
            mediaRecorder.value.onstop = () => {
                if (chunks.value.length > 0) {
                    const blob = new Blob(chunks.value, { type: 'audio/webm' });
                    audioBlobs.value.push({ blob });
                    chunks.value = [];
                    if (isPlay.value == true) {
                        sendToServer(blob);
                        // const doubledBlob = new Blob([blob, blob], { type: 'audio/webm' }); // 자기 자신을 두 번 결합
                        // console.log(doubledBlob.size);
                        // sendToServer(doubledBlob); // 두 번 결합한 Blob을 서버로 전송
                    }
                }
        
                if (isRecording.value) {
                    mediaRecorder.value.start();
                }
            };
        
            mediaRecorder.value.start();
            isRecording.value = true;
        };

        const sendToServer = async (blob) => {
            const formData = new FormData();
            formData.append('file', blob, `chunk_${audioBlobs.value.length - 1}.webm`);
            const sheetIdBlob = new Blob([route.params.sheetId], { type: 'application/json' });
            formData.append('sheetId', sheetIdBlob);
            sendRequests.value= sendRequests.value + 1;
            try {
                const res = await local.post(`/plays/${playMode.value}/${singleResultId.value}/live-score`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                f1.value.push(res.data.similarity_results.f1_score);
                jaccard.value.push(res.data.similarity_results.jaccard_similarity);
                receivedResponse.value = receivedResponse.value + 1;
            } catch (err) {
                receivedResponse.value = receivedResponse.value + 1;
            }
        };        

        const splitRecording = () => {
            if (isRecording.value && mediaRecorder.value.state === 'recording') {
                mediaRecorder.value.stop();
            }
        };

        const stopRecording = () => {
            stopMusic();
            isRecording.value = false;
            isPlay.value=false;
            if (mediaRecorder.value && mediaRecorder.value.state === 'recording') {
                mediaRecorder.value.stop();
                isLastTrigger.value = true;
            }
            if (recordingInterval) {
                clearInterval(recordingInterval);
                recordingInterval = null;
            }
            
        };

        const cleanup = () => {
            if (osmd.value) {
                osmd.value.clear();
                osmd.value = null;
            }
            if (playbackManager.value) {
                // Playback을 확실히 정지
                playbackManager.value.pause(); // 혹은 stop()
                playbackManager.value.Dispose(); // PlaybackManager 객체 해제
            }
            if (timingSource.value) {
                // TimingSource를 확실히 정지
                timingSource.value.pause();
                timingSource.value.reset();
            }
            if (audioPlayer.value) {
                // AudioPlayer 정지 및 해제
                audioPlayer.value.close(); // stop 메서드가 있는 경우
            }
            if (mediaRecorder.value) {
                // MediaRecorder 정지 및 해제
                if (mediaRecorder.value.state === 'recording') {
                    mediaRecorder.value.stop();
                }
            }
            isPlay.value = false;
            isLast.value = false;
            isLastTrigger.value = false;
            sendRequests.value = 0;;
            receivedResponse.value = 0;
            isRecording.value= false;
            mediaRecorder.value = null;
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
            isLast,
            singleResultId,
            playMode,
            multiResultId,
            initializeOsmd,
            loadAndSetupOsmd,
            setVolume,
            startMusic,
            pauseMusic,
            stopMusic,
            startRecording,
            splitRecording,
            stopRecording,
            cleanup
        };
    });
