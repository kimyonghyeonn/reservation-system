<template>
  <ion-list v-if="!isRegister">
    <ion-item>
      <ion-label>
        <h1 style="text-align: center">로그인</h1>
      </ion-label>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        ID <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input type="text" :clear-input="true" v-model="form.loginId">
      </ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        PW <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input type="password" v-model="form.loginPw"> </ion-input>
    </ion-item>

    <div style="display: flex; justify-content: space-around">
      <ion-button
        id="signup"
        fill="clear"
        color="success"
        shape="round"
        expand="block"
        @click="btnLogin"
      >
        <div
          style="display: flex; align-items: center; gap: 4px color: #3880ff"
        >
          로그인
          <ion-icon :icon="checkmarkDoneOutline" />
        </div>
      </ion-button>

      <ion-button
        fill="clear"
        color="medium"
        shape="round"
        expand="block"
        @click="goRegisterForm"
      >
        <div style="display: flex; align-items: center; gap: 4px">
          아직 동료가 아니신가요?
          <ion-icon :icon="arrowForwardOutline" />
        </div>
      </ion-button>
    </div>

    <div class="helper-links">
      <ion-text color="primary" @click="goToFindId">아이디 찾기</ion-text>
      <ion-text color="primary" @click="goToFindPw">비밀번호 찾기</ion-text>
    </div>
  </ion-list>

  <div v-if="isRegister">
    <RegisterForm :isRegister="isRegister" @goBackToLogin="goBackToLogin" />
  </div>
  <!-- <ion-loading
    :is-open="isLoading"
    message="로그인 중 입니다..."
    spinner="circles"
  /> -->
</template>

<script setup>
import {
  IonItem,
  IonButton,
  IonList,
  IonLabel,
  IonText,
  IonInput,
  IonIcon,
  alertController,
  IonLoading,
} from "@ionic/vue";
import { ref } from "vue";

import { arrowForwardOutline, checkmarkDoneOutline } from "ionicons/icons";
import { useAccountStore } from "@/stores/account";
import { useRouter } from "vue-router";
import axiosInstance from "@/libs/httpRequester"; // ✅ axios 인스턴스 import

import RegisterForm from "./RegisterForm.vue";
const router = useRouter();
const isLoading = ref(false);
const emit = defineEmits(["haveLogin"]);
// 계정 스토어
const accountStore = useAccountStore();

const form = ref({
  loginId: "",
  loginPw: "",
});
const isRegister = ref(false);

async function btnLogin() {
  try {
    isLoading.value = true;
    const response = await axiosInstance.post("/account/login", form.value);
    const token = response.data;
    isLoading.value = false;
    // 로그인 성공 시 처리
    emit("haveLogin", true);
    localStorage.setItem("accessToken", JSON.stringify(token));
    accountStore.setAccessToken(token);
    accountStore.setLoggedIn(true);
    accountStore.setChecked(true);
    const member = await getMyInfo(); // ✅ getMyInfo() 사용
    console.log("member: ", member);
    if (member) {
      accountStore.setUser(member); // ✅ user 정보 Pinia에 저장
    }

    if (member.resetPw === true) {
      const alert = await alertController.create({
        header: "비밀번호 변경 요청",
        message:
          "비밀번호가 초기화 되었습니다. 비밀번호를 변경 후 사용해주세요.",
        buttons: [
          {
            text: "확인",
            handler: () => {
              router.push("/changePw"); // ✅ 비밀번호 변경 페이지로 이동
            },
          },
        ],
      });
      await alert.present();
      return; // 비밀번호 변경 페이지로 이동 후 종료
    }

    router.push("/library/booking");
  } catch (error) {
    const alert = await alertController.create({
      header: "로그인 실패",
      message: "아이디 또는 비밀번호가 잘못되었습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  } finally {
    isLoading.value = false;
  }
}

async function getMyInfo() {
  try {
    const response = await axiosInstance.get("/account/myInfo");
    const member = response.data;
    return member;
  } catch (error) {
    return null;
  }
}

function goBackToLogin(value) {
  isRegister.value = value;
}
function goRegisterForm() {
  isRegister.value = true;
}

function goToFindId() {
  router.push("/findId");
}
function goToFindPw() {
  router.push("/findPw");
}
</script>

<style scoped>
.helper-links {
  margin-top: 8px;
  padding: 0 12px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  cursor: pointer;
}
</style>
