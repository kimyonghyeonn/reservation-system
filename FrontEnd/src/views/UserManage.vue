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
      <ion-card>
        <ion-card-header>
          <ion-card-title>회원 목록</ion-card-title>
        </ion-card-header>
        <ion-grid>
          <ion-row>
            <ion-col size="3"><strong>이름</strong></ion-col>
            <ion-col size="4"><strong>이메일</strong></ion-col>
            <ion-col size="2"><strong>권한</strong></ion-col>
            <ion-col size="3"><strong>포지션</strong></ion-col>
          </ion-row>

          <ion-row v-for="member in members" :key="member.id">
            <ion-col size="3">
              <span class="clickable-name" @click="openMemberDetail(member)">
                {{ member.name }}
              </span></ion-col
            >
            <ion-col class="email-col" size="4">{{ member.email }}</ion-col>
            <ion-col size="2">{{ member.isManager }}</ion-col>
            <ion-col size="2">{{ member.positionNm }}</ion-col>
          </ion-row>
        </ion-grid>
      </ion-card>

      <ion-modal :is-open="showDetailModal" @did-dismiss="closeDetailModal">
        <ion-header>
          <ion-toolbar color="primary">
            <ion-title>회원 상세 정보</ion-title>
            <ion-buttons slot="end">
              <ion-button
                v-if="selectedMember.manager !== true"
                shape="round"
                class="delete-btn"
                @click="btnDelete"
                >삭제</ion-button
              >
              <ion-button
                shape="round"
                class="close-btn"
                @click="closeDetailModal"
                >닫기</ion-button
              >
            </ion-buttons>
          </ion-toolbar>
        </ion-header>

        <ion-content class="ion-padding">
          <div class="profile-box">
            <ProfileViewer
              :image="selectedMember.profileImage"
              :member-id="selectedMember.id"
            />
          </div>
          <ion-grid>
            <ion-row>
              <ion-col size="4"><strong>이름</strong></ion-col>
              <ion-col size="8">{{ selectedMember.name }}</ion-col>
            </ion-row>
            <ion-row>
              <ion-col size="4"><strong>권한</strong></ion-col>
              <ion-col size="8">{{ selectedMember.isManager }}</ion-col>
            </ion-row>
            <ion-row>
              <ion-col size="4"><strong>이메일</strong></ion-col>
              <ion-col size="8">{{ selectedMember.email }}</ion-col>
            </ion-row>
            <ion-row>
              <ion-col size="4"><strong>전화번호</strong></ion-col>
              <ion-col size="8">{{ selectedMember.phoneNumber }}</ion-col>
            </ion-row>
            <ion-row>
              <ion-col size="4"><strong>포지션</strong></ion-col>
              <ion-col size="8">{{ selectedMember.positionNm }}</ion-col>
            </ion-row>
            <ion-row>
              <ion-col size="4"><strong>가입일</strong></ion-col>
              <ion-col size="8">{{ selectedMember.createDt }}</ion-col>
            </ion-row>
          </ion-grid>
        </ion-content>
      </ion-modal>
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
  alertController,
  IonButtons,
  IonGrid,
  IonRow,
  IonCol,
  IonMenuButton,
  IonModal,
} from "@ionic/vue";

import { ref, onMounted } from "vue";
import { useAccountStore } from "@/stores/account";
import axiosInstance from "@/libs/httpRequester"; // ✅ 인터셉터 적용된 axios 인스턴스
import ProfileViewer from "@/components/ProfileViewer.vue";

// 계정 스토어
const accountStore = useAccountStore();
const members = ref([]);
const showDetailModal = ref(false);
const selectedMember = ref({});

async function getAllMembers() {
  try {
    const response = await axiosInstance.get("/members/allMembers");
    members.value = response.data;

    return members.value;
  } catch (error) {
    return null;
  }
}

async function btnDelete() {
  const alert = await alertController.create({
    header: "회원 삭제",
    message:
      "회원 삭제시 예약테이블의 예약내역도 삭제됩니다. 진행하시겠습니까?",
    buttons: [
      {
        text: "취소",
        role: "cancel", // 아무 동작 안 함
      },
      {
        text: "확인",
        handler: async () => {
          // 확인 누르면 Register 실행
          await deleteMember();
        },
      },
    ],
  });
  await alert.present(); // 팝업 띄우기
}

async function deleteMember() {
  const url = `/members/delete/${selectedMember.value.id}`; // ⚠️ id를 path에 포함

  try {
    const response = await axiosInstance.delete(url); // GET처럼 단순 호출

    if (response.status === 200) {
      const alert = await alertController.create({
        header: "회원 삭제 완료",
        message: "회원이 삭제되었습니다.",
        buttons: [
          {
            text: "확인",
            handler: async () => {
              await getAllMembers(); // ✅ 목록 새로고침
              closeDetailModal(); // ✅ 모달 닫기
            },
          },
        ],
      });
      await alert.present();
    }
  } catch (error) {
    console.log(error);
  }
}

onMounted(async () => {
  console.log("onMounted");
  getAllMembers();
});

async function openMemberDetail(member) {
  selectedMember.value = member;
  showDetailModal.value = true;

  // ✅ 활동 로그 전송
  try {
    await axiosInstance.post("/log/user", {
      action: "VIEW_MEMBER_DETAIL",
      target: `member#${member.id}`,
      result: "SUCCESS",
      httpMethod: "GET",
      statusCode: 200, // 임의 성공 코드
    });
  } catch (error) {
    console.error("🚨 사용자 활동 로그 기록 실패:", error);
  }
}

function closeDetailModal() {
  showDetailModal.value = false;
}
</script>

<style scoped>
ion-grid {
  padding: 0 10px;
}

ion-row {
  border-bottom: 1px solid #e0e0e0;
  min-height: 48px;
  align-items: center; /* 세로 중앙 정렬 */
  text-align: center; /* 가로 중앙 정렬 */
  background-color: #fff;
  transition: background-color 0.2s;
}

ion-row:first-child {
  background-color: #f7f9fc;
  font-weight: bold;
  text-align: center;
  border-top: 1px solid #ccc;
  border-bottom: 2px solid #dcdcdc;
}

ion-row:not(:first-child):hover {
  background-color: #f1faff; /* hover 효과로 산뜻한 느낌 */
}

ion-col {
  font-size: 14px;
  padding: 6px 4px;
  display: flex;
  justify-content: center; /* 내용 가운데 정렬 */
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.clickable-name {
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
}
.clickable-name:hover {
  color: #0056b3;
}
.close-btn {
  background-color: lightgray;
}

.delete-btn {
  background-color: lightcoral;
}

.email-col {
  white-space: normal;
  overflow: hidden;
  text-overflow: unset;
  justify-content: flex-start; /* 좌측 정렬 */
}
.profile-box {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}
</style>
