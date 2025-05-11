<template>
  <ion-list>
    <ion-item>
      <ion-label>
        <h1>합주실 예약하기</h1>
        <p style="color: red">* 는 필수입력 해야합니다.</p>
      </ion-label>
    </ion-item>

    <ion-item>
      <ion-label position="stacked"
        >사용자 리스트 <ion-text color="danger">*</ion-text></ion-label
      >
      <ion-input
        type="text"
        placeholder="김용현, 양준영, 조인우 ..."
        :clear-input="true"
        v-model="reservation.remark"
      ></ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="stacked"
        >사용일 <ion-text color="danger">*</ion-text></ion-label
      >
      <ion-datetime-button class="datetime-button" datetime="datetime">
      </ion-datetime-button>
    </ion-item>
    <ion-modal :keep-contents-mounted="true" ref="dateModal">
      <ion-datetime
        id="datetime"
        presentation="date"
        v-model="reservation.reserveDate"
        @ionChange="onDateSelected"
      >
      </ion-datetime>
    </ion-modal>

    <ion-item>
      <ion-label position="stacked"
        >예약 시작 시간 <ion-text color="danger">*</ion-text></ion-label
      >
      <ion-select v-model="reservation.startTime" interface="popover">
        <ion-select-option v-for="hour in 24" :key="hour" :value="hour">
          {{ hour.toString().padStart(2, "0") }}시
        </ion-select-option>
      </ion-select>
    </ion-item>

    <ion-item>
      <ion-label position="stacked"
        >예약 종료 시간 <ion-text color="danger">*</ion-text></ion-label
      >
      <ion-select v-model="reservation.endTime" interface="popover">
        <ion-select-option v-for="hour in 24" :key="hour" :value="hour">
          {{ hour.toString().padStart(2, "0") }}시
        </ion-select-option>
      </ion-select>
    </ion-item>

    <ion-button shape="round" expand="block" @click="btnSave"
      >예약하기<ion-icon :icon="checkmarkDoneOutline"></ion-icon
    ></ion-button>
    <ion-button
      v-if="accountStore.loggedIn && accountStore.user.manager"
      color="success"
      shape="round"
      expand="block"
      @click="bulkReserve"
    >
      이번달 일괄 예약
    </ion-button>

    <ion-button color="medium" shape="round" expand="block" @click="goBack"
      >뒤로가기<ion-icon :icon="returnDownBackOutline"></ion-icon
    ></ion-button>
  </ion-list>

  <ion-loading
    :is-open="isLoading"
    message="예약 중 입니다..."
    spinner="circles"
  />
</template>

<script setup>
import {
  IonItem,
  IonButton,
  IonList,
  IonSelect,
  IonSelectOption,
  IonLabel,
  IonText,
  IonDatetime,
  IonModal,
  IonDatetimeButton,
  IonInput,
  alertController,
  IonLoading,
} from "@ionic/vue";
import { ref, onMounted, watch } from "vue";
import { checkmarkDoneOutline, returnDownBackOutline } from "ionicons/icons";
import { useRoute, useRouter } from "vue-router";
import { useAccountStore } from "@/stores/account";
import axiosInstance from "@/libs/httpRequester"; // ✅ axios 인스턴스 import
import { format, addDays, getDay, getDaysInMonth, parseISO } from "date-fns";

const props = defineProps({
  room: {
    type: Object,
    required: true,
  },
});
const isLoading = ref(false);
const router = useRouter();
const route = useRoute();
const emit = defineEmits(["callBack"]);
const accountStore = useAccountStore();
const today = new Date();
const yyyy = today.getFullYear();
const mm = String(today.getMonth() + 1).padStart(2, "0"); // 월은 0부터 시작
const dd = String(today.getDate()).padStart(2, "0");
const selectedRoom = ref({
  roomId: "",
  roomName: "",
  roomAddr: "",
  addrDetail: "",
  phoneNumber: "",
  description: "",
  created: "",
  startTime: "",
  endTime: "",
  capacity: 0,
  imageBase64: null, // ✅ 추가: 파일 객체
  imageUrl: "", // ✅ 추가: 미리보기 URL
});

const reservation = ref({
  memberId: "",
  remark: "",
  roomId: "",
  reserveDate: `${yyyy}-${mm}-${dd}`,
  startTime: "",
  endTime: "",
  status: "",
});
const dateModal = ref();

watch(
  () => props.room,
  (room) => {
    if (room && room.roomId) {
      selectedRoom.value = room;
      reservation.value.roomId = room.roomId;
    }
  },
  { immediate: true }
);
async function bulkReserve() {
  const isValid = await chkValidate();
  if (!isValid) return;

  const baseDate = parseISO(reservation.value.reserveDate); // 선택일
  const baseDay = getDay(baseDate); // 요일 (0: 일요일 ~ 6: 토요일)
  const year = baseDate.getFullYear();
  const month = baseDate.getMonth(); // 0-based
  const today = new Date();
  const daysInMonth = getDaysInMonth(baseDate);

  // ① 선택일 포함해서 매주 같은 요일 날짜 리스트 만들기
  const sameWeekdays = [];
  for (let d = baseDate.getDate(); d <= daysInMonth; d++) {
    const target = new Date(year, month, d);
    if (target >= today && getDay(target) === baseDay) {
      sameWeekdays.push(target);
    }
  }

  const dayNames = [
    "일요일",
    "월요일",
    "화요일",
    "수요일",
    "목요일",
    "금요일",
    "토요일",
  ];
  const readableMonth = month + 1;
  const weekdayName = dayNames[baseDay];

  // ② 사용자 확인창
  const confirm = await alertController.create({
    header: "일괄 예약 확인",
    message: `${year}년 ${readableMonth}월의 남은 매주 ${weekdayName}에 예약을 생성하시겠습니까? (예약 건수: ${sameWeekdays.length}건)`,
    buttons: [
      {
        text: "취소",
        role: "cancel",
      },
      {
        text: "확인",
        handler: async () => {
          isLoading.value = true;

          const memberId = accountStore.user?.id;
          const validList = [];
          const failList = [];

          // ③ 중복 체크
          for (const date of sameWeekdays) {
            const formattedDate = format(date, "yyyy-MM-dd");
            try {
              const res = await axiosInstance.get(
                "/reservation/check-duplicate",
                {
                  params: {
                    roomId: reservation.value.roomId,
                    reserveDate: formattedDate,
                    startTime: reservation.value.startTime,
                    endTime: reservation.value.endTime,
                  },
                }
              );

              if (res.data === true) {
                failList.push(`${formattedDate} (중복됨)`);
              } else {
                validList.push(formattedDate);
              }
            } catch (error) {
              failList.push(`${formattedDate} (중복 확인 실패)`);
            }
          }

          const successList = [];

          // ④ 중복 아닌 날짜만 등록 시도
          for (const date of validList) {
            const payload = {
              ...reservation.value,
              memberId,
              reserveDate: date,
            };

            try {
              const res = await axiosInstance.post(
                "/reservation/create",
                payload
              );
              if (res.status === 200 && res.data !== 409) {
                successList.push(date);
              } else {
                failList.push(`${date} (예약 실패)`);
              }
            } catch (error) {
              failList.push(`${date} (요청 실패)`);
            }
          }

          isLoading.value = false;

          // ⑤ 사용자 알림
          const summary = `✅ 성공: ${successList.length}건\n${successList.join(
            ", "
          )}\n\n❌ 실패: ${failList.length}건\n${failList.join(", ")}`;

          const alert = await alertController.create({
            header: "일괄 예약 결과",
            message: summary,
            buttons: ["확인"],
          });
          await alert.present();
        },
      },
    ],
  });

  await confirm.present();
}

function onDateSelected() {
  if (dateModal.value) {
    dateModal.value.$el.dismiss(); // 모달 닫기
  }
}

onMounted(async () => {
  if (!props.room?.roomId && route.query.room_id) {
    const response = await axiosInstance.get(`/room/${route.query.room_id}`);
    selectedRoom.value = response.data;
    reservation.value.roomId = response.data.roomId;
  } else {
    selectedRoom.value = props.room;
    reservation.value.roomId = props.room?.roomId;
  }
});

function goBack() {
  const goBack = ref(false);
  emit("callBack", goBack.value);
}
async function chkValidate() {
  if (!reservation.value.remark) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: `사용자 리스트는 필수 입력 항목입니다.`,
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  if (!reservation.value.reserveDate) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: `사용일은 필수 입력 항목입니다.`,
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  if (!reservation.value.startTime) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: `예약 시작 시간은 필수 입력 항목입니다.`,
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  if (!reservation.value.endTime) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: `예약 종료 시간은 필수 입력 항목입니다.`,
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  if (reservation.value.endTime <= reservation.value.startTime) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: `예약 종료 시간은 예약 시작 시간과 같거나 빠를 수 없습니다.`,
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  return true;
}

async function Register() {
  if (!reservation.value.roomId) {
    const alert = await alertController.create({
      header: "예약 오류",
      message: "방 정보가 유효하지 않습니다. 다시 시도해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return;
  } else {
    reservation.value.memberId = accountStore.user?.id;
    const url = "/reservation/create"; // ⚠️ axiosInstance의 baseURL이 '/api'이므로 '/account/join'만 작성

    try {
      const response = await axiosInstance.post(url, reservation.value);
      isLoading.value = true;
      if (response.data === 409 && response.status === 200) {
        isLoading.value = false;
        const alert = await alertController.create({
          header: "예약 실패",
          message:
            "해당 날짜, 시간에 예약된 사용자가 있습니다. 예약현황을 확인해주세요.",
          buttons: ["확인"],
        });
        await alert.present();
        return;
      } else if (response.status === 200) {
        isLoading.value = false;
        const alert = await alertController.create({
          header: "예약 성공",
          message: "예약이 완료되었습니다. 예약현황에서 확인 가능합니다.",
          buttons: [
            {
              text: "확인",
              handler: () => {
                router.push({
                  path: "/library/main",
                  query: {
                    room_id: reservation.value.roomId,
                    reserve_date: reservation.value.reserveDate,
                  },
                });
              },
            },
          ],
        });
        await alert.present();
      }
      isLoading.value = false;
      // 회원가입 완료 후 로그인 화면으로 전환
      emit("goBackToLogin", !props.isRegister);
    } catch (error) {
      if (error.response) {
        const status = error.response.status;
        console.log("status: ", status);
      } else {
        // 네트워크 오류 또는 서버 미응답
        const alert = await alertController.create({
          header: "서버 오류",
          message: "서버에 연결할 수 없습니다.",
          buttons: ["확인"],
        });
        await alert.present();
      }
    }
  }
}
async function btnSave() {
  const isValid = await chkValidate(); // 1. 유효성 검사
  if (!isValid) return;

  // 2. 확인 팝업창 표시
  const alert = await alertController.create({
    header: "예약 신청",
    message: "예약 신청하시겠습니까?",
    buttons: [
      {
        text: "취소",
        role: "cancel", // 아무 동작 안 함
      },
      {
        text: "확인",
        handler: async () => {
          // 확인 누르면 Register 실행
          await Register();
        },
      },
    ],
  });
  await alert.present(); // 팝업 띄우기
}
</script>

<style scoped>
.datetime-button {
  margin-top: 2%;
  margin-bottom: 1%;
}
</style>
