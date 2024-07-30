import { defineStore } from 'pinia';

export const usePlayStore = defineStore('playMode', {
  state: () => ({
    mode: null, // "single" 또는 "multi"
  }),
  actions: {
    setMode(mode) {
      this.mode = mode;
    },
    },
    getters: {
        getMode: (state) => state.mode,
      },
});
