    // src/stores/musicStore.js
    import { defineStore } from 'pinia';
    import { ref } from 'vue';
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
        const isLast = ref(false);
        const singleResultId = ref(0);
        const multiResultId = ref(0);
        const playMode = ref("");
        const last_measure = ref(0);
        const check_measure = ref(-1);

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

            audioPlayer.value.GainMultiplier = 6;

            timingSource.value.reset();
            timingSource.value.pause();
            timingSource.value.Settings = osmd.value.Sheet.playbackSettings;
            playbackManager.value.initialize(osmd.value.Sheet.musicPartManager);
            playbackManager.value.addListener(osmd.value.cursor);

            let lastEmittedMeasureIndex = 0;
            playbackManager.value.addListener({
                cursorPositionChanged: (currentTimestamp, data) => {
                    const currentMeasureIndex = data.CurrentMeasureIndex;
                    // if (currentMeasureIndex >= 0 && currentMeasureIndex - lastEmittedMeasureIndex >= 8) {
                    //     lastEmittedMeasureIndex = currentMeasureIndex;
                    //     triggerSplit.value++;
                    //     splitRecording();
                    // }
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
            setInterval(() => {
                if (isRecording.value) {
                    splitRecording();
                }
            }, 5000); // Calls splitRecording every 5 seconds
            mediaRecorder.value.onstop = () => {
                if (chunks.value.length > 0) {
                    const blob = new Blob(chunks.value, { type: 'audio/webm' });
                    console.log(`Blob size: ${blob.size} bytes`);
                    audioBlobs.value.push({ blob });
                    chunks.value = [];
                    if (isPlay.value == false) {
                        const lastBlob = audioBlobs.value[audioBlobs.value.length - 2] // 이전 청크 가져오기
                        const combinedBlob = new Blob([lastBlob.blob, blob], { type: 'audio/webm' }); // 결합
                        console.log("마지막 마디입니다. 앞쪽과 결합후 전송..")
                        sendToServer(combinedBlob);
                    } else {
                        console.log("5초컷 NORMAL")
                        const doubledBlob = new Blob([blob, blob], { type: 'audio/webm' }); // 자기 자신을 두 번 결합
                        console.log(doubledBlob.size);
                        sendToServer(doubledBlob); // 두 번 결합한 Blob을 서버로 전송
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
            console.log("Sending formData", formData);
            
            try {
                const res = await local.post(`/plays/${playMode.value}/${singleResultId.value}/live-score`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                console.log('채점 성공: ', res.data);
                // console.log(route.params.sheetId)
                // console.log(res.data.similarity_results)
                //     local.post(`/play/single`,{
                //         sheetId: route.params.sheetId,
                //         score: res.data.similarity_results.f1_score+res.data.similarity_results.jaccard,
                //     })
                //     .then((res)=>{
                //         console.log(res);
                //     }).catch((err)=>{
                //         console.error(err);
                //     })
                // isLast.value = res.data.isLast == 1;
                
                f1.value.push(res.data.similarity_results.f1_score);
                jaccard.value.push(res.data.similarity_results.jaccard_similarity);
                last_measure.value = res.data.last_measure;
                check_measure.value = check_measure.value + 1;
                if(last_measure.value == check_measure.value){
                    isLast.value = 1;
                }
            } catch (err) {
                console.error('채점 실패: ', err);
            }
        };        

        const splitRecording = () => {
            console.log("SPLIT")
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
                isLast.value = true;
                //splitRecording();
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
            last_measure,
            check_measure,
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
