import axios from "@/libs/httpRequester"; // ✅ 인터셉터 포함된 axios 인스턴스 사용

// ✅ 회원가입
export const join = (args) => {
  return axios.post("/account/join", args).catch((e) => e.response);
};

// ✅ 로그인 (POST 방식)
export const login = (args) => {
  return axios.post("/account/login", args).catch((e) => e.response);
};

// ✅ 로그인 여부 확인
export const check = () => {
  return axios
    .get("/account/check", {
      headers: {
        "Cache-Control": "no-cache", // 캐시 무효화
      },
    })
    .catch((e) => e.response);
};

// ✅ 로그아웃
export const logout = () => {
  return axios.get("/account/logout").catch((e) => e.response);
};

// ✅ 내 정보 가져오기
export const getMyInfo = () => {
  return axios.get("/account/myInfo").catch((e) => e.response);
};
