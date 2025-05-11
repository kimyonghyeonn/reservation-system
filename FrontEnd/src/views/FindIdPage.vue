<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button @click="goBack" default-href="/"></ion-back-button>
        </ion-buttons>
        <ion-title>아이디 찾기</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-list>
        <ion-item>
          <ion-label position="stacked">
            이름 <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input v-model="name" placeholder="이름 입력"></ion-input>
        </ion-item>

        <ion-item>
          <ion-label position="stacked">
            휴대폰번호 <ion-text color="danger">*</ion-text>
          </ion-label>
          <ion-input
            v-model="phoneNumber"
            type="tel"
            placeholder="01012345678"
            maxlength="11"
          ></ion-input>
        </ion-item>
      </ion-list>

      <ion-button
        expand="block"
        :color="isFormFilled ? 'primary' : 'medium'"
        @click="findId"
        >인증 확인</ion-button
      >
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
  IonText,
  IonInput,
  IonButtons,
  IonButton,
  IonBackButton,
  IonList,
  alertController,
} from "@ionic/vue";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import axiosInstance from "@/libs/httpRequester"; // ✅ API 호출용 인스턴스

const name = ref("");
const phoneNumber = ref("");
const router = useRouter();
const isFormFilled = computed(() => {
  return name.value && phoneNumber.value;
});

async function findId() {
  if (!name.value || !phoneNumber.value) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "이름과 휴대폰번호를 모두 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return;
  }

  try {
    const response = await axiosInstance.post("/account/findId", {
      name: name.value,
      phoneNumber: phoneNumber.value,
    });

    const foundId = response.data.loginId;
    const maskedId = maskId(foundId);

    const alert = await alertController.create({
      header: "아이디 찾기 결과",
      message: `회원님의 아이디는 ${maskedId} 입니다.`,
      buttons: ["확인"],
    });
    await alert.present();
  } catch (error) {
    const alert = await alertController.create({
      header: "아이디 찾기 실패",
      message: "입력하신 정보와 일치하는 회원이 없습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

function maskId(id) {
  if (id.length <= 3) return "*".repeat(id.length);
  return id.slice(0, 3) + "*".repeat(id.length - 3);
}
function goBack(event) {
  event.preventDefault(); // 기본 이동 막기 (선택사항)
  router.back(); // 뒤로 이동
}
</script>
