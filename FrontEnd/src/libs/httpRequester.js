import axios from "axios";
import { useAccountStore } from "@/stores/account";
import { handleHttpError } from "@/utils/handleHttpError";
import router from "@/router"; // ✅ 직접 라우터 import
import { alertController } from "@ionic/vue";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // ✅ 여기가 핵심
  withCredentials: true, // ✅ 리프레시 토큰 쿠키 자동 전송
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 5000,
});

async function showSessionExpiredAlert() {
  const alert = await alertController.create({
    header: "에러가 발생했습니다2. (에러코드: 500)",
    message: "에러 내용: 세션이 만료되었습니다. 다시 로그인해주세요.",
    buttons: [
      {
        text: "확인",
        handler: () => {
          router.push("/login"); // ✅ 확인 누르면 로그인 페이지로 이동
        },
      },
    ],
  });

  await alert.present();
}

// ✅ 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken")?.replaceAll('"', "");

    // ✅ 로그인, 회원가입 요청에는 Authorization 헤더 제거
    const noAuthUrls = ["/account/login", "/account/join", "/account/token"];

    const isNoAuthUrl = noAuthUrls.some((url) => config.url?.includes(url));

    if (!isNoAuthUrl && token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ 응답 인터셉터
axiosInstance.interceptors.response.use(
  (response) => {
    // ✅ 응답 헤더에 새로운 엑세스토큰이 있다면 교체
    const newAccessToken = response.headers["x-new-access-token"];
    console.log("response.headers : ", response.headers);
    if (newAccessToken) {
      localStorage.setItem("accessToken", JSON.stringify(newAccessToken));
      const accountStore = useAccountStore();
      accountStore.setAccessToken(newAccessToken);
    }

    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // ✅ 401이고 재시도하지 않은 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // ✅ 클라이언트가 직접 리프레시 요청을 날리는 구조일 경우 여기에 구현
      // 지금은 백엔드에서 자동 재발급하므로, 별도 token 요청 없이 종료
      const message = error.response.data?.message || "";

      if (
        message.includes("리프레시 토큰이 유효하지 않습니다") ||
        message.includes("세션이 만료")
      ) {
        // ✅ 사용자 강제 로그아웃 처리
        const accountStore = useAccountStore();
        accountStore.logout(); // ← accessToken, 유저 정보 clear
        await showSessionExpiredAlert();
        return;
      }

      return Promise.reject(error); // 그 외 401
    } else if (error.response?.status === 500 && !originalRequest._retry) {
      // 내부서버 에러 발생시
      const message = error.response.data?.message || "";
      console.log("message : ", message);
      if (
        message.includes("리프레시 토큰이 유효하지 않습니다") ||
        message.includes("세션이 만료")
      ) {
        // ✅ 사용자 강제 로그아웃 처리
        const accountStore = useAccountStore();
        accountStore.logout(); // ← accessToken, 유저 정보 clear
        await showSessionExpiredAlert();
        return;
      } else {
        await handleHttpError(error); // 👈 공통 에러 처리 호출
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
