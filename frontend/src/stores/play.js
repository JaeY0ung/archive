import { defineStore } from 'pinia';
import axios from 'axios';
import { localAxios } from "@/util/http-common";
const local = localAxios();

const baseURL = process.env.VUE_APP_REQUEST_URL;

export const usePlayStore = defineStore('playMode', {
  state: () => ({
    mode: null, // "single" 또는 "multi"
    onlineUsers: [], // 온라인 유저 목록을 저장할 상태
    showModal: false, // 모달 창 열림/닫힘 상태
    roomTitle: '', // 방 제목
    rooms: [], // 방 목록
    currentRoomUsers: [], // 현재 방에 있는 유저 목록
  }),
  actions: {
    setMode(mode) {
      this.mode = mode;
    },
    setShowModal(show) {
      this.showModal = show;
    },
    setRoomTitle(title) {
      this.roomTitle = title;
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
          referenceId: 123, // 관련 ID, 필요에 따라 설정
          readStatus: false,
          createdAt: new Date().toISOString()
        };
        console.log('Sending alertDto:', alertDto); // 디버깅을 위한 로그
        const response = await axios.post(`${baseURL}/alert`, alertDto);
        console.log('초대 알림 전송 성공:', response.data);
      } catch (error) {
        console.error('초대 알림 전송 실패:', error);
      }
    },
    async createRoom() {
      try {
        const battleRoomRegisterDto = { 
          title: this.roomTitle
        };
        const response = await local.post(`${baseURL}/battle-rooms`, battleRoomRegisterDto);
        // 프론트엔드에서 초기값 설정
        this.rooms.push({
          id: response.data.info.id,
          title: response.data.info.title,
          capacity: 2, // 초기 capacity 설정
          status: '게임 준비 중', // 초기 status 설정
          occupancy: 1 // 초기 occupancy 설정
        });
        this.setShowModal(false);
        console.log('방 생성 성공:', response.data);
      } catch (error) {
        console.error('방 생성 실패:', error);
      }
    },
    async fetchAllRooms() {
      try {
        const response = await local.get(`${baseURL}/battle-rooms`);
        // 방 정보를 프론트엔드에서 처리하여 초기값 설정
        this.rooms = response.data.map(room => ({
          id: room.info.id,
          title: room.info.title,
          capacity: 2, // 모든 방의 capacity를 2로 설정
          status: '게임 준비 중', // 기본값으로 '게임 준비 중' 설정
          occupancy: room.occupancy || 1 // 기본 occupancy는 1로 설정
        }));
        console.log('방 목록 가져오기 성공:', this.rooms);
      } catch (error) {
        console.error('방 목록 가져오기 실패:', error);
      }
    },
    async enterRoom(roomId) {
      try {
        const response = await local.get(`${baseURL}/battle-rooms/${roomId}`);
        console.log('입장 성공:', response.data);
        this.setMode('multi'); // 멀티 모드로 설정
      } catch (error) {
        console.error('방 입장 실패:', error);
      }
    }
    // delete axios
  },
  getters: {
    getMode: (state) => state.mode,
    getOnlineUsers: (state) => state.onlineUsers,
    getShowModal: (state) => state.showModal,
    getRoomTitle: (state) => state.roomTitle,
    getRooms: (state) => state.rooms,
  },
});
