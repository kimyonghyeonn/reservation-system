<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>QWERTY</ion-title>
        <ion-buttons slot="end">
          <ion-menu-button auto-hide="false"></ion-menu-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content id="main-content" :fullscreen="true">
      <div v-if="!accountStore.loggedIn">
        <ion-card>
          <LoginForm />
        </ion-card>
      </div>

      <div v-if="accountStore.loggedIn">
        <ion-card>
          <ion-card-header>
            <ion-list v-if="loaded">
              <ion-list-header> 회원 정보 </ion-list-header>

              <ion-item lines="full">
                <div class="profile-box">
                  <ProfileUploader
                    v-model="userInfo.profileImage"
                    @save="saveProfileImage"
                  />
                </div>

                <ion-label>
                  <h3>이름 : {{ userInfo.name }}</h3>
                  <p>이메일 : {{ userInfo.email }}</p>
                  <p>전화번호 : {{ userInfo.phoneNumber }}</p>
                  <p>자기소개 : {{ userInfo.remark }}</p>
                </ion-label>
              </ion-item>
            </ion-list>

            <ion-list v-if="!loaded">
              <ion-list-header>
                <ion-skeleton-text
                  :animated="true"
                  style="width: 80px"
                ></ion-skeleton-text>
              </ion-list-header>
              <ion-item>
                <ion-thumbnail>
                  <ion-skeleton-text :animated="true"></ion-skeleton-text>
                </ion-thumbnail>
                <ion-label>
                  <h3>
                    <ion-skeleton-text
                      :animated="true"
                      style="width: 80%"
                    ></ion-skeleton-text>
                  </h3>
                  <p>
                    <ion-skeleton-text
                      :animated="true"
                      style="width: 60%"
                    ></ion-skeleton-text>
                  </p>
                  <p>
                    <ion-skeleton-text
                      :animated="true"
                      style="width: 30%"
                    ></ion-skeleton-text>
                  </p>
                </ion-label>
              </ion-item>
            </ion-list>
          </ion-card-header>

          <div class="button-group">
            <ion-button color="secondary" fill="clear" @click="update"
              >회원 정보 수정</ion-button
            >
            <ion-button color="danger" fill="clear" @click="logout"
              >로그아웃</ion-button
            >
          </div>
        </ion-card>
      </div>

      <div v-if="showUpdateForm">
        <ion-card>
          <UpdateForm :Userinfo="userInfo" @Close="close" />
        </ion-card>
      </div>

      <div v-if="showBookingInfo">
        <ion-card>
          <BookingInfo :res="res" />
        </ion-card>
      </div>
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
  IonButton,
  IonItem,
  IonLabel,
  IonList,
  IonListHeader,
  IonSkeletonText,
  IonThumbnail,
  IonCard,
  IonCardHeader,
  alertController,
  IonButtons,
  IonMenuButton,
  onIonViewWillEnter,
} from "@ionic/vue";

import { ref, onMounted, watch } from "vue";
import UpdateForm from "../components/UpdateForm.vue";
import LoginForm from "../components/LoginForm.vue";
import BookingInfo from "../components/BookingInfo.vue";
import { useAccountStore } from "@/stores/account";
import { logout as logoutAPI } from "@/services/accountService";
import axiosInstance from "@/libs/httpRequester"; // ✅ 인터셉터 적용된 axios 인스턴스
import ProfileUploader from "@/components/ProfileUploader.vue";

// 계정 스토어
const accountStore = useAccountStore();

const showUpdateForm = ref(false);
const showBookingInfo = ref(false);
const res = ref();

const loaded = ref(false);

const userInfo = ref({
  name: "",
  email: "",
  phoneNumber: "",
  birthDay: "",
  remark: "",
  position: "",
  profileImage: "",
});
onIonViewWillEnter(async () => {
  try {
    if (accountStore.loggedIn && accountStore.user) {
      userInfo.value = { ...accountStore.user };
      loaded.value = true;
    } else {
      throw new Error("No member info");
    }
  } catch (err) {
    accountStore.logout(); // ✅ 상태 정리
    loaded.value = false;
  }
});

function close(state) {
  showUpdateForm.value = state;
}
watch(
  () => accountStore.loggedIn,
  async (val) => {
    if (val) {
      const member = await accountStore.getMyInfo();
      if (member) {
        userInfo.value = {
          id: member.id,
          name: member.name,
          email: member.email,
          phoneNumber: member.phoneNumber,
          remark: member.remark,
          position: member.position,
          profileImage: member.profileImage,
        };
        loaded.value = true;
      }
    }
  },
  { immediate: true }
);

function update() {
  showUpdateForm.value = !showUpdateForm.value;
}
async function logout() {
  showUpdateForm.value = false;
  try {
    const token = localStorage.getItem("accessToken");

    // (1) 토큰이 없으면 상태만 초기화하고 종료
    if (!token) {
      accountStore.logout();
      return;
    }

    // (2) 로그아웃 API 요청 (axios 인터셉터에 토큰 자동 포함)
    const response = await logoutAPI();

    // (3) 응답 성공 시 상태 초기화
    if (response.status === 200) {
      accountStore.logout();
    } else {
      // 서버가 실패를 응답했을 경우 (거의 없음)
      const alert = await alertController.create({
        header: "로그아웃 실패",
        message: "서버와의 연결에 실패했습니다.",
        buttons: ["확인"],
      });
      await alert.present();
    }
  } catch (error) {
    // (4) 토큰이 만료되었거나 네트워크 오류인 경우
    console.error("Logout Error:", error);

    // (5) 상태는 무조건 초기화
    accountStore.logout();

    const alert = await alertController.create({
      header: "세션 만료",
      message: "세션이 만료되어 자동 로그아웃되었습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

async function saveProfileImage(base64) {
  try {
    if (!userInfo.value.id) {
      const alert = await alertController.create({
        header: "저장 실패",
        message: "회원 정보가 없습니다. 다시 로그인해주세요.",
        buttons: ["확인"],
      });
      await alert.present();
      return;
    }

    const payload = {
      id: userInfo.value.id,
      profileImage: base64, // base64 문자열
    };

    const response = await axiosInstance.put("/account/updateImage", payload);

    if (response.status === 200) {
      const alert = await alertController.create({
        header: "저장 완료",
        message: "프로필 사진이 저장되었습니다.",
        buttons: ["확인"],
      });
      await alert.present();
    }
  } catch (error) {
    console.error("프로필 이미지 저장 실패:", error);
    const alert = await alertController.create({
      header: "저장 실패",
      message: "저장 중 오류가 발생했습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

onMounted(async () => {
  if (accountStore.loggedIn && !userInfo.value.name) {
    const member = await accountStore.getMyInfo();
    if (member) {
      userInfo.value = { ...member };
      loaded.value = true;
    }
  }
});
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-wrap: wrap;

  align-items: center;
  justify-content: space-between;
  text-align: center;

  height: 170px;
  width: 400px;

  margin: 0 auto;
}

b {
  width: 100%;
}

.ripple-parent {
  display: flex;
  align-items: center;
  justify-content: center;

  position: relative;
  overflow: hidden;

  border: 1px solid #ddd;

  user-select: none;
}

.rounded-rectangle {
  width: 100%;
  height: 50px;
  border-radius: 1px;
}

.button-group {
  display: flex;
  justify-content: space-between;
}
.user-icon {
  margin-right: 10%;
}
.profile-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 16px;
}
</style>
