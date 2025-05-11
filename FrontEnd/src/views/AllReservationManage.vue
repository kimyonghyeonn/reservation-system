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
          <ion-card-title>전체 예약 목록</ion-card-title>
        </ion-card-header>
        <ion-grid>
          <ion-row>
            <ion-col size="3"><strong>예약자명</strong></ion-col>
            <ion-col size="4"><strong>합주실 사용자</strong></ion-col>
            <ion-col size="5"><strong>사용날짜/시간</strong></ion-col>
          </ion-row>

          <ion-row v-for="rev in myRevList" :key="rev.reserveId">
            <ion-col size="3">
              <span class="clickable-name" @click="openMemberDetail(rev)">
                {{ rev.memberName }}
              </span>
            </ion-col>
            <ion-col class="text-right" size="4">{{ rev.remark }}</ion-col>
            <ion-col class="text-right" size="5">{{ rev.operTime }}</ion-col>
          </ion-row>
        </ion-grid>
        <ion-button
          expand="block"
          color="danger"
          shape="round"
          @click="btnDeleteAll"
          style="margin: 12px"
        >
          전체 예약 삭제
        </ion-button>
      </ion-card>

      <!-- 예약 수정 모달 -->
      <ion-modal :is-open="showDetailModal" @did-dismiss="closeDetailModal">
        <ion-header>
          <ion-toolbar color="primary">
            <ion-title>예약 수정</ion-title>
            <ion-buttons slot="end">
              <ion-button
                shape="round"
                class="close-btn"
                @click="closeDetailModal"
              >
                닫기
              </ion-button>
            </ion-buttons>
          </ion-toolbar>
        </ion-header>

        <ion-content class="ion-padding">
          <ion-list>
            <ion-item>
              <ion-label position="stacked">
                사용자 리스트 <ion-text color="danger">*</ion-text>
              </ion-label>
              <ion-input
                type="text"
                placeholder="김용현, 양준영 ..."
                v-model="selectedReserv.remark"
              ></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked"
                >사용일 <ion-text color="danger">*</ion-text></ion-label
              >
              <ion-datetime-button
                datetime="edit-datetime"
              ></ion-datetime-button>
            </ion-item>

            <ion-modal :keep-contents-mounted="true">
              <ion-datetime
                id="edit-datetime"
                presentation="date"
                v-model="selectedReserv.reserveDate"
              ></ion-datetime>
            </ion-modal>

            <ion-item>
              <ion-label position="stacked"
                >예약 시작 시간 <ion-text color="danger">*</ion-text></ion-label
              >
              <ion-select
                v-model="selectedReserv.startTime"
                interface="popover"
              >
                <ion-select-option v-for="hour in 24" :key="hour" :value="hour">
                  {{ hour.toString().padStart(2, "0") }}시
                </ion-select-option>
              </ion-select>
            </ion-item>

            <ion-item>
              <ion-label position="stacked"
                >예약 종료 시간 <ion-text color="danger">*</ion-text></ion-label
              >
              <ion-select v-model="selectedReserv.endTime" interface="popover">
                <ion-select-option v-for="hour in 24" :key="hour" :value="hour">
                  {{ hour.toString().padStart(2, "0") }}시
                </ion-select-option>
              </ion-select>
            </ion-item>

            <ion-button shape="round" expand="block" @click="btnUpdate"
              >수정하기</ion-button
            >
            <ion-button
              color="danger"
              shape="round"
              expand="block"
              @click="btnDelete"
              >삭제하기</ion-button
            >
          </ion-list>
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
  IonList,
  IonItem,
  IonSelect,
  IonSelectOption,
  IonLabel,
  IonText,
  IonDatetime,
  IonDatetimeButton,
  IonInput,
  onIonViewWillEnter,
} from "@ionic/vue";

import { ref, onMounted } from "vue";
import { useAccountStore } from "@/stores/account";
import axiosInstance from "@/libs/httpRequester";

const accountStore = useAccountStore();
const myRevList = ref([]);
const showDetailModal = ref(false);
const selectedReserv = ref({});

async function getAllMyReservation() {
  try {
    const response = await axiosInstance.get(`/reservation/allReservations`);
    myRevList.value = response.data;
    return myRevList.value;
  } catch (error) {
    console.error("🚨 getAllMyReservation() 에러:", error);
    return null;
  }
}

async function btnDelete() {
  const alert = await alertController.create({
    header: "예약 삭제",
    message: "이 예약을 삭제하시겠습니까?",
    buttons: [
      {
        text: "취소",
        role: "cancel",
      },
      {
        text: "확인",
        handler: async () => {
          try {
            const reserveId = selectedReserv.value.reserveId;
            const memberId = selectedReserv.value.memberId;

            const url = `/reservation/delete/${reserveId}/${memberId}`;
            const response = await axiosInstance.delete(url);

            if (response.status === 200) {
              const confirmAlert = await alertController.create({
                header: "삭제 완료",
                message: "예약이 성공적으로 삭제되었습니다.",
                buttons: [
                  {
                    text: "확인",
                    handler: async () => {
                      closeDetailModal();
                      await getAllMyReservation(accountStore.user?.id); // 리스트 갱신
                    },
                  },
                ],
              });
              await confirmAlert.present();
            }
          } catch (error) {
            console.error("🚨 예약 삭제 에러:", error);
            const failAlert = await alertController.create({
              header: "삭제 실패",
              message: "예약 삭제 중 오류가 발생했습니다.",
              buttons: ["확인"],
            });
            await failAlert.present();
          }
        },
      },
    ],
  });

  await alert.present();
}

async function btnUpdate() {
  const isValid = await chkValidate();
  if (!isValid) return;

  const alert = await alertController.create({
    header: "예약 수정",
    message: "예약 내용을 수정하시겠습니까?",
    buttons: [
      {
        text: "취소",
        role: "cancel",
      },
      {
        text: "확인",
        handler: async () => {
          try {
            const url = "/reservation/update";
            const response = await axiosInstance.put(url, selectedReserv.value);

            // 중복 예약 시 409 Conflict 반환
            if (response.status === 200 && response.data === 409) {
              const conflict = await alertController.create({
                header: "예약 실패",
                message:
                  "해당 시간에 이미 예약이 존재합니다. 다른 시간을 선택해주세요.",
                buttons: ["확인"],
              });
              await conflict.present();
              return;
            }

            // 성공 처리
            const success = await alertController.create({
              header: "수정 완료",
              message: "예약 정보가 수정되었습니다.",
              buttons: [
                {
                  text: "확인",
                  handler: async () => {
                    closeDetailModal();
                    await getAllMyReservation(accountStore.user?.id);
                  },
                },
              ],
            });
            await success.present();
          } catch (error) {
            console.error("🚨 예약 수정 실패:", error);
            const fail = await alertController.create({
              header: "오류 발생",
              message: "예약 수정 중 오류가 발생했습니다.",
              buttons: ["확인"],
            });
            await fail.present();
          }
        },
      },
    ],
  });
  await alert.present();
}

async function chkValidate() {
  if (
    !selectedReserv.value.remark ||
    !selectedReserv.value.reserveDate ||
    !selectedReserv.value.startTime ||
    !selectedReserv.value.endTime
  ) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: `모든 필수 항목을 입력해주세요.`,
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }
  if (selectedReserv.value.endTime <= selectedReserv.value.startTime) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: `예약 종료 시간은 예약 시작 시간보다 늦어야 합니다.`,
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }
  return true;
}

async function btnDeleteAll() {
  const alert = await alertController.create({
    header: "전체 예약 삭제",
    message: "정말 모든 예약을 삭제하시겠습니까?",
    buttons: [
      {
        text: "취소",
        role: "cancel",
      },
      {
        text: "삭제",
        handler: async () => {
          try {
            const memberId = accountStore.user?.id;
            const url = `/reservation/deleteByMemberId/${memberId}`;
            const response = await axiosInstance.delete(url);

            if (response.status === 200) {
              const success = await alertController.create({
                header: "삭제 완료",
                message: "모든 예약이 삭제되었습니다.",
                buttons: ["확인"],
              });
              await success.present();
              await getAllMyReservation(memberId); // 목록 갱신
            }
          } catch (error) {
            console.error("🚨 전체 삭제 에러:", error);
            const fail = await alertController.create({
              header: "삭제 실패",
              message: "전체 예약 삭제 중 오류가 발생했습니다.",
              buttons: ["확인"],
            });
            await fail.present();
          }
        },
      },
    ],
  });

  await alert.present();
}

function openMemberDetail(reservation) {
  selectedReserv.value = {
    ...reservation,
    startTime: Number(reservation.startTime),
    endTime: Number(reservation.endTime),
  };
  showDetailModal.value = true;
}

function closeDetailModal() {
  showDetailModal.value = false;
}

onMounted(async () => {
  if (accountStore.user?.id) {
    getAllMyReservation(accountStore.user?.id);
  }
});
onIonViewWillEnter(async () => {
  if (accountStore.user?.id) {
    await getAllMyReservation(accountStore.user?.id);
  }
});
</script>

<style scoped>
ion-grid {
  padding: 0 10px;
}

ion-row {
  border-bottom: 1px solid #e0e0e0;
  min-height: 48px;
  align-items: center;
  text-align: center;
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
  background-color: #f1faff;
}

ion-col {
  font-size: 14px;
  padding: 6px 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  white-space: normal;
  word-break: break-word;
  text-align: center;
  text-wrap: wrap;
  min-height: 48px;
}

.text-right {
  justify-content: flex-start !important;
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
</style>
