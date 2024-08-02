import { defineStore } from 'pinia';
import axios from 'axios';

const baseURL = process.env.VUE_APP_REQUEST_URL;

export const usePlayStore = defineStore('playMode', {
    state: () => ({
        mode: null, // "single" 또는 "multi"
        onlineUsers: [], // 온라인 유저 목록을 저장할 상태 
    }),
    actions: {
        setMode(mode) {
            this.mode = mode;
        },
        async fetchOnlineUsers() {
        try {
            const response = await axios.get(`${baseURL}/users/online-users`);
            this.onlineUsers = response.data;
        } catch (error) {
            console.error('온라인 유저 목록을 불러오는 것에 실패했습니다', error);
        }
        },
        async sendInviteAlert(userId) {
        try {
            const alertDto = {
                receiver: { id: userId },
                alertType: { id: 1 }, // 대결 초대 알림 타입
                referenceId: 1, // 관련 ID, 필요에 따라 설정
                readStatus: false,
                createdAt: new Date().toISOString()
            };
            // TODO: AXIOS 관리 필요
            const response = await axios.post(`${baseURL}/alert`, alertDto);
        } catch (error) {
            console.error('초대 알림 전송 실패:', error);
        }
        }
    },
    getters: {
        getMode: (state) => state.mode,
        getOnlineUsers: (state) => state.onlineUsers,
    },
});
