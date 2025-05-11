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
        <ion-title>비밀번호 찾기</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-list>
        <ion-item>
          <ion-label position="stacked">
            아이디 <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input v-model="loginId" placeholder="아이디 입력"></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">
            이메일 <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input v-model="email" placeholder="이메일 입력"></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">
            이름 <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input v-model="name" placeholder="이름 입력"></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">
            전화번호 <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input
            v-model="phone"
            type="tel"
            placeholder="01012345678"
            maxlength="11"
          ></ion-input>
        </ion-item>
      </ion-list>

      <ion-button
        expand="block"
        :color="isFormFilled ? 'primary' : 'medium'"
        @click="resetPassword"
      >
        비밀번호 초기화
      </ion-button>
    </ion-content>

    <ion-loading
      :is-open="isLoading"
      message="비밀번호를 초기화 중입니다..."
      spinner="circles"
    />
  </ion-page>
</template>

<script setup>
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonButtons,
  IonBackButton,
  IonItem,
  IonLabel,
  IonText,
  IonInput,
  IonButton,
  IonList,
  IonLoading,
  alertController,
} from "@ionic/vue";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useAccountStore } from "@/stores/account";
import axiosInstance from "@/libs/httpRequester";

const router = useRouter();
const accountStore = useAccountStore();
const loginId = ref("");
const email = ref("");
const name = ref("");
const phone = ref("");
const isLoading = ref(false);

const isFormFilled = computed(() => {
  return loginId.value && email.value && name.value && phone.value;
});

function goBack(event) {
  event.preventDefault();
  router.back();
}

async function resetPassword() {
  if (!isFormFilled.value) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "모든 항목을 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return;
  }

  try {
    const response = await axiosInstance.post("/account/verifyUser", {
      loginId: loginId.value,
      email: email.value,
      name: name.value,
      phoneNumber: phone.value,
    });

    const userExists = response.data.exists;
    if (!userExists) {
      const alert = await alertController.create({
        header: "사용자 없음",
        message: "입력하신 정보와 일치하는 사용자를 찾을 수 없습니다.",
        buttons: ["확인"],
      });
      await alert.present();
      return;
    }

    const confirmAlert = await alertController.create({
      header: "비밀번호 초기화",
      message:
        "입력된 이메일 주소로 초기화된 비밀번호가 발송됩니다. 초기화 하시겠습니까?",
      buttons: [
        {
          text: "취소",
          role: "cancel",
        },
        {
          text: "확인",
          handler: async () => {
            await sendResetRequest();
          },
        },
      ],
    });
    await confirmAlert.present();
  } catch (error) {
    console.error("검증 오류:", error);
    const alert = await alertController.create({
      header: "오류",
      message: "서버 오류가 발생했습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

async function sendResetRequest() {
  try {
    isLoading.value = true;

    await axiosInstance.post("/account/resetPW", {
      loginId: loginId.value,
      email: email.value,
      name: name.value,
      phoneNumber: phone.value,
    });

    isLoading.value = false;

    const alert = await alertController.create({
      header: "비밀번호 초기화 완료",
      message: "초기화된 비밀번호가 이메일로 발송되었습니다.",
      buttons: ["확인"],
    });
    // ✅ 1. accessToken 삭제
    localStorage.removeItem("accessToken");
    // ✅ 2. 상태 스토어 초기화
    accountStore.setAccessToken(null);
    accountStore.setLoggedIn(false);
    accountStore.setUser({});

    await alert.present();
    // await alert.onDidDismiss();

    router.back();
  } catch (error) {
    isLoading.value = false;
    console.error("초기화 실패:", error);
    const alert = await alertController.create({
      header: "초기화 실패",
      message: "비밀번호 초기화에 실패했습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}
</script>
