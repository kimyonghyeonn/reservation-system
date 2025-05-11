<template>
  <ion-app>
    <!-- 메뉴 정의 -->

    <Sidebar />

    <!-- 실제 화면 표시 영역 -->
    <ion-router-outlet id="main-content" />
  </ion-app>
</template>

<script setup>
import { IonApp, IonRouterOutlet, alertController } from "@ionic/vue";
import { useAccountStore } from "@/stores/account";
import { useRoute, useRouter } from "vue-router";
import { watch } from "vue";
import { check } from "@/services/accountService"; // ① 계정 서비스 메소드 호출
import axiosInstance from "@/libs/httpRequester";
import { onMounted } from "vue";

import Sidebar from "@/components/Sidebar.vue"; // ✅ 컴포넌트 import

const accountStore = useAccountStore();
const router = useRouter();
const route = useRoute();

const restoreLogin = async () => {
  const token = localStorage.getItem("accessToken")?.replaceAll('"', "");
  const accountStore = useAccountStore();

  if (token) {
    accountStore.setAccessToken(token);

    try {
      const res = await axiosInstance.get("/account/check");
      if (res.status === 200) {
        accountStore.setLoggedIn(true);
        accountStore.setChecked(true);

        // ✅ 사용자 정보가 없을 때만 복원
        if (!accountStore.user) {
          const member = await accountStore.getMyInfo();
          if (member) {
            accountStore.setUser(member);
          }
        }
        return;
      }
    } catch (e) {
      console.warn("❗ 로그인 복원 실패:", e);
    }
  }
  if (!token && !accountStore.loggedIn) {
    accountStore.setLoggedIn(false);
    accountStore.setChecked(false);
  }
};

const checkAccount = async () => {
  // ② 로그인 여부 체크, 이 값으로 계정 스토어 객체 수정
  const res = await check();

  if (res.status === 200) {
    accountStore.setChecked(true);
    accountStore.setLoggedIn(res.data === true);
  } else {
    accountStore.setChecked(false);
  }
};

(async function onCreated() {
  await checkAccount();
});

onMounted(async () => {
  await restoreLogin();
  const savedTheme = localStorage.getItem("theme");
  if (savedTheme === "dark") {
    document.body.classList.add("dark");
  } else {
    document.body.classList.remove("dark");
  }
});

watch(
  () => route.path,
  async () => {
    if (route.path === "/library/booking" && accountStore.loggedIn === false) {
      const alert = await alertController.create({
        header: "알림",
        message: "로그인을 먼저 진행해주세요.",
        buttons: [
          {
            text: "확인",
            handler: () => {
              router.replace("/library/home");
            },
          },
        ],
      });

      await alert.present();
    }
  }
);
</script>

<style></style>
