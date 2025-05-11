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
          <ion-label position="stacked">
            새 비밀번호 (8~16자리) <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input
            v-model="newPassword"
            type="password"
            placeholder="8~16자 영문/숫자 포함"
            clear-input="true"
          ></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">
            비밀번호 확인 <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input
            v-model="confirmPassword"
            type="password"
            placeholder="비밀번호 재입력"
            clear-input="true"
          ></ion-input>
        </ion-item>

        <!-- 🔥 비밀번호 복잡도 안내 -->
        <ion-item lines="none">
          <ion-label>
            <div
              :style="{ color: passwordValidations.length ? '#3880ff' : 'red' }"
            >
              ✓ 6자 이상, 16자 이하
            </div>
            <div
              :style="{
                color: passwordValidations.letterNumber ? '#3880ff' : 'red',
              }"
            >
              ✓ 영문자와 숫자 포함
            </div>
            <div
              :style="{
                color: passwordValidations.specialChar ? '#3880ff' : 'red',
              }"
            >
              ✓ 특수문자 1개 이상 포함
            </div>
          </ion-label>
        </ion-item>

        <ion-button expand="block" color="success" @click="checkMatch">
          비밀번호 일치 확인
        </ion-button>

        <ion-button expand="block" @click="updatePassword">
          비밀번호 수정
        </ion-button>
      </ion-list>
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
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import axiosInstance from "@/libs/httpRequester";
import { useAccountStore } from "@/stores/account";

const newPassword = ref("");
const confirmPassword = ref("");
const router = useRouter();
const accountStore = useAccountStore();

// 🔥 비밀번호 복잡도 체크용
const passwordValidations = ref({
  length: false,
  letterNumber: false,
  specialChar: false,
});

// 🔥 newPassword 입력 변화 감지해서 실시간 체크
watch(
  () => newPassword.value,
  (newPw) => {
    passwordValidations.value.length = newPw.length >= 6 && newPw.length <= 16;
    passwordValidations.value.letterNumber =
      /[A-Za-z]/.test(newPw) && /\d/.test(newPw);
    passwordValidations.value.specialChar = /[!@#$%^&*(),.?":{}|<>]/.test(
      newPw
    );
  }
);

// 🔥 비밀번호 일치 검사
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

// 🔥 비밀번호 수정 요청
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

  if (
    !passwordValidations.value.length ||
    !passwordValidations.value.letterNumber ||
    !passwordValidations.value.specialChar
  ) {
    const alert = await alertController.create({
      header: "비밀번호 규칙 오류",
      message:
        "비밀번호는 6~16자, 영문자+숫자 조합, 특수문자 1개 이상을 포함해야 합니다.",
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

    router.push("/library/home");
  } catch (error) {
    const alert = await alertController.create({
      header: "실패",
      message: "비밀번호 변경에 실패했습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

// 🔥 뒤로가기 버튼
function goBack(event) {
  event.preventDefault();
  localStorage.removeItem("accessToken");
  accountStore.setAccessToken(null);
  accountStore.setLoggedIn(false);
  accountStore.setUser({});
  router.back();
}
</script>

<style scoped>
/* 스타일은 필요시 추가 가능 */
</style>
