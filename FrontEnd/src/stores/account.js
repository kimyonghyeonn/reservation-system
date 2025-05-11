// 계정 스토어
import { defineStore } from "pinia";
import axiosInstance from "@/libs/httpRequester"; // ✅ axios 인스턴스 가져오기

export const useAccountStore = defineStore("account", {
  state: () => ({
    checked: false, // 사용자 로그인 체크여부
    loggedIn: false, // 사용자 로그인 여부
    accessToken: "", // 엑세스 토큰 프로퍼티, 문자열
    user: null, // 로그인한 사용자 정보
  }),
  actions: {
    setChecked(val) {
      this.checked = val;
    }, // 사용자 로그인 체크여부 수정 메소드
    setLoggedIn(val) {
      this.loggedIn = val;
    }, // 사용자 로그인 여부 수정 메소드
    setAccessToken(val) {
      this.accessToken = val;
    },
    setUser(val) {
      const cleaned = Object.fromEntries(
        Object.entries(val).filter(([_, v]) => v !== null)
      );
      this.user = cleaned;
    },
    async getMyInfo() {
      try {
        const response = await axiosInstance.get("/account/myInfo");
        const member = response.data;
        this.setUser(member); // 스토어에 저장
        return member;
      } catch (error) {
        return null;
      }
    },
    logout() {
      console.log("logout");
      this.accessToken = "";
      this.user = null;
      this.loggedIn = false;
      this.checked = false;
      localStorage.removeItem("accessToken"); // 필요시 전체 localStorage.clear() 도 가능
    },
  },

  persist: {
    key: "account-store", // localStorage에 저장될 키 이름
    storage: localStorage, // 또는 sessionStorage
    paths: ["accessToken", "user", "loggedIn", "checked"], // 저장하고 싶은 항목들
  },
});
