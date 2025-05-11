<template>
  <ion-menu content-id="main-content" side="end" @ionWillOpen="onMenuOpen">
    <ion-header>
      <ion-toolbar>
        <ion-title>Menu</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding">
      <ion-list>
        <ion-item @click="changeMenuHandler('home')" router-direction="root"
          >마이 페이지</ion-item
        >
        <ion-item
          v-if="accountStore.loggedIn && accountStore.user.manager"
          @click="changeMenuHandler('userManager')"
          router-direction="root"
          >회원 관리</ion-item
        >
        <ion-item
          v-if="accountStore.loggedIn && accountStore.user.manager"
          @click="changeMenuHandler('roomManager')"
          router-direction="root"
          >합주실 관리</ion-item
        >
        <ion-item
          v-if="accountStore.loggedIn"
          @click="changeMenuHandler('booking')"
          router-direction="root"
          >합주실 예약</ion-item
        >
        <ion-item @click="changeMenuHandler('main')">예약 현황</ion-item>
        <ion-item
          v-if="accountStore.loggedIn"
          @click="changeMenuHandler('myReservation')"
          >나의 예약 관리</ion-item
        >
        <ion-item
          v-if="accountStore.loggedIn && accountStore.user.manager"
          @click="changeMenuHandler('allReservation')"
          >전체 예약 관리</ion-item
        >
        <ion-item
          button
          href="https://open.kakao.com/o/s1oXl4rh"
          target="_blank"
        >
          <ion-label>기술지원 문의</ion-label>
        </ion-item>
        <ion-item
          v-if="!accountStore.loggedIn"
          button
          @click="loginHandler"
          color="primary"
          >로그인</ion-item
        >
        <ion-item
          v-if="accountStore.loggedIn"
          button
          @click="logoutHandler"
          color="medium"
          >로그아웃</ion-item
        >
      </ion-list>
    </ion-content>
  </ion-menu>
</template>

<script setup>
import {
  IonMenu,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonList,
  IonItem,
  menuController,
} from "@ionic/vue";

import { useAccountStore } from "@/stores/account";
import { useRouter } from "vue-router";
import { login, logout } from "@/services/accountService";

const accountStore = useAccountStore();
const router = useRouter();

async function onMenuOpen() {
  console.log("accountStore.loggedIn: ", accountStore.loggedIn);
  console.log("accountStore: ", accountStore.user.manager);
}

async function changeMenuHandler(menu) {
  const url = "/library/" + menu;
  await menuController.close();
  router.push(url);
}

async function loginHandler() {
  await login();
  localStorage.removeItem("accessToken");
  window.location.replace("/library/home");
}

async function logoutHandler() {
  const response = await logout();

  if (response.status === 200) {
    localStorage.removeItem("accessToken");
    accountStore.user = null;
    accountStore.loggedIn = false;
    window.location.replace("/library/main");
  } else {
    console.warn("❗ 로그아웃 실패:", response);
    alert("서버와 연결되지 않았습니다. 다시 시도해주세요.");
  }
}
</script>
