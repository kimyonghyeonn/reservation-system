<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button
            @click="goBack"
            default-href="/login"
          ></ion-back-button>
        </ion-buttons>
        <ion-title>비밀번호 변경</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-list>
        <ion-item>
          <ion-label position="stacked"
            >새 비밀번호 (8자리 이상)
            <ion-text color="danger">*</ion-text></ion-label
          >
          <ion-input
            v-model="newPassword"
            type="password"
            placeholder="8자 이상 영문/숫자 포함"
          ></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked"
            >비밀번호 확인 (8자리 이상)
            <ion-text color="danger">*</ion-text></ion-label
          >
          <ion-input
            v-model="confirmPassword"
            type="password"
            placeholder="비밀번호 재입력"
          ></ion-input>
        </ion-item>
      </ion-list>

      <ion-button expand="block" color="success" @click="checkMatch">
        비밀번호 일치 확인
      </ion-button>

      <ion-button expand="block" @click="updatePassword">
        비밀번호 수정
      </ion-button>
    </ion-content>
  </ion-page>
</template>

<script setup>
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonItem,
  IonLabel,
  IonInput,
  IonButtons,
  IonButton,
  IonBackButton,
  IonList,
  alertController,
} from "@ionic/vue";
import { ref } from "vue";
import { useRouter } from "vue-router";
import axiosInstance from "@/libs/httpRequester";
import { useAccountStore } from "@/stores/account";
import { onBeforeRouteLeave } from "vue-router";

const newPassword = ref("");
const confirmPassword = ref("");
const router = useRouter();
const accountStore = useAccountStore();

// ① 비밀번호 일치 검사
async function checkMatch() {
  if (newPassword.value === confirmPassword.value) {
    const alert = await alertController.create({
      header: "일치 확인",
      message: "비밀번호가 일치합니다.",
      buttons: ["확인"],
    });
    await alert.present();
  } else {
    const alert = await alertController.create({
      header: "불일치",
      message: "비밀번호가 일치하지 않습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

// ② 비밀번호 수정 요청
async function updatePassword() {
  if (newPassword.value !== confirmPassword.value) {
    const alert = await alertController.create({
      header: "오류",
      message: "비밀번호가 일치하지 않습니다.",
      buttons: ["확인"],
    });
    await alert.present();
    return;
  }

  if (newPassword.value.length < 8) {
    const alert = await alertController.create({
      header: "유효성 오류",
      message: "비밀번호는 8자리 이상이어야 합니다.",
      buttons: ["확인"],
    });
    await alert.present();
    return;
  }

  try {
    const response = await axiosInstance.post("/account/changePw", {
      newPassword: newPassword.value,
    });

    const alert = await alertController.create({
      header: "완료",
      message: "비밀번호가 성공적으로 변경되었습니다.",
      buttons: ["확인"],
    });
    await alert.present();

    // 로그인 페이지로 이동 (또는 홈으로)
    router.replace("/login");
  } catch (error) {
    const alert = await alertController.create({
      header: "실패",
      message: "비밀번호 변경에 실패했습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

function goBack(event) {
  // ✅ 1. accessToken 삭제
  localStorage.removeItem("accessToken");
  // ✅ 2. 상태 스토어 초기화
  accountStore.setAccessToken(null);
  accountStore.setLoggedIn(false);
  accountStore.setUser({});

  event.preventDefault();
  router.back();
}
// onBeforeRouteLeave((to, from, next) => {
//   // 로그아웃 처리
//   localStorage.removeItem("accessToken");
//   accountStore.setAccessToken(null);
//   accountStore.setLoggedIn(false);
//   accountStore.setUser(null);

//   next(); // 페이지 전환 허용
// });
</script>
