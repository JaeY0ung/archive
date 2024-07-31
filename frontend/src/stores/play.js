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
  },
  getters: {
    getMode: (state) => state.mode,
    getOnlineUsers: (state) => state.onlineUsers,
  },
});
