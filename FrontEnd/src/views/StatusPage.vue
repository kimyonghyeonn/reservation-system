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
      <ion-grid :fixed="true">
        <ion-row>
          <ion-col
            v-for="room in rooms"
            :key="room.roomId"
            class="time-col head-col"
            @click="selectRoom(room)"
          >
            {{ room.roomName }}
          </ion-col>
        </ion-row>
      </ion-grid>

      <!-- ✅ Vue Cal 컴포넌트 영역 -->
      <vue-cal
        class="vuecal--blue-theme"
        style="height: 600px"
        :events="calendarEvents"
        default-view="month"
        :selected-date="selectedDate"
        :time="true"
        :disable-views="['years']"
        hide-title
        locale="ko"
        today-button
        @event-click="onEventClick"
      />

      <!-- ✅ ion-modal로 이벤트 상세 보기 -->
      <ion-modal :is-open="showDialog" @didDismiss="showDialog = false">
        <ion-content class="ion-padding">
          <h3 class="event-title">예약 상세 내역</h3>
          <p class="event-content">
            <strong>합주실 사용자:</strong><br />
            <span v-html="selectedEvent.contentFull" />
          </p>
          <ul>
            <li>
              <strong>예약 신청자:</strong>
              {{ selectedEvent.memberName }}
            </li>
            <li>
              <strong>예약 날짜:</strong> {{ formatDate(selectedEvent.start) }}
            </li>
            <li>
              <strong>시작 시간:</strong> {{ formatTime(selectedEvent.start) }}
            </li>
            <li>
              <strong>종료 시간:</strong> {{ formatTime(selectedEvent.end) }}
            </li>
          </ul>
          <ion-button expand="block" @click="showDialog = false"
            >닫기</ion-button
          >
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
  IonGrid,
  IonRow,
  IonCol,
  IonButtons,
  IonMenuButton,
  IonModal,
  IonButton,
  onIonViewWillEnter,
} from "@ionic/vue";
import { ref, onMounted } from "vue";
import axiosInstance from "@/libs/httpRequester";
import VueCal from "vue-cal";
import "vue-cal/dist/vuecal.css";
import { useRoute } from "vue-router";

const route = useRoute();
const rooms = ref([]);
const calendarEvents = ref([]);
const selectedEvent = ref({});
const showDialog = ref(false);
const selectedDate = ref(null);
const colors = ["#ff7675", "#74b9ff", "#55efc4", "#ffeaa7", "#a29bfe"];

onIonViewWillEnter(async () => {
  await getAllRooms();

  const roomIdFromQuery = route.query.room_id;
  const reserveDateFromQuery = route.query.reserve_date;

  if (reserveDateFromQuery) {
    selectedDate.value = reserveDateFromQuery;
  }

  if (roomIdFromQuery && rooms.value.length > 0) {
    const matchedRoom = rooms.value.find((r) => r.roomId == roomIdFromQuery);
    if (matchedRoom) {
      selectRoom(matchedRoom);
    }
  }
});

onMounted(async () => {
  await getAllRooms();

  const roomIdFromQuery = route.query.room_id;
  const reserveDateFromQuery = route.query.reserve_date;

  if (reserveDateFromQuery) {
    selectedDate.value = reserveDateFromQuery;
  }

  if (roomIdFromQuery && rooms.value.length > 0) {
    const matchedRoom = rooms.value.find((r) => r.roomId == roomIdFromQuery);
    if (matchedRoom) {
      selectRoom(matchedRoom);
    }
  }
});

async function getAllRooms() {
  try {
    const response = await axiosInstance.get("/room/allRooms");
    rooms.value = response.data;
    if (rooms.value.length > 0) {
      selectRoom(rooms.value[0]);
    }
  } catch (error) {
    console.error("🚨 getAllRooms 에러:", error);
  }
}

async function selectRoom(room) {
  try {
    const response = await axiosInstance.get(
      `/reservation/room/${room.roomId}`
    );
    calendarEvents.value = response.data.map((item, idx) => ({
      title: item.remark || "예약됨",
      start: `${item.reserveDate} ${String(item.startTime).padStart(
        2,
        "0"
      )}:00`,
      end: `${item.reserveDate} ${String(item.endTime).padStart(2, "0")}:00`,
      contentFull: item.remark || "",
      memberId: item.memberId,
      memberName: item.memberName,
      background: colors[idx % colors.length],
      class: `event-color-${idx % colors.length}`,
    }));
  } catch (error) {
    console.error("🚨 예약 불러오기 에러:", error);
  }
}

function onEventClick(event, e) {
  selectedEvent.value = event;

  showDialog.value = true;
  e.stopPropagation();
}

function formatTime(date) {
  if (!date) return "";
  const dt = new Date(date);
  return dt.toLocaleTimeString("ko-KR", {
    hour: "2-digit",
    minute: "2-digit",
  });
}
function formatDate(date) {
  const d = new Date(date);
  return d.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long", // ← 요일 포함
  });
}
</script>

<style scoped>
ion-col {
  background-color: #d6f0f8; /* 연한 파란색 */
  color: #000; /* 글자 검정 */
  border: 1px solid #d0d0d0; /* 연한 회색 테두리 */
  text-align: center;
  padding: 12px 8px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px 8px 0 0;
  transition: background-color 0.2s ease;
}

ion-col:hover {
  background-color: #c0e9f5; /* 호버 시 조금 더 진한 파란색 */
}

.head-col {
  background-color: #f4f4f4;
}

.time-col {
  display: flex;
  justify-content: center;
  align-items: center;
}

.event-title {
  font-size: 1.2em;
  font-weight: bold;
  margin: 4px 0 8px;
}

.event-content {
  font-style: italic;
  margin-bottom: 12px;
}
/* VueCal의 헤더 날짜 영역 텍스트를 검정색으로 */
::v-deep .vuecal__title-bar {
  color: black !important;
}
::v-deep .vuecal__title-bar .vuecal__title button {
  color: black !important;
}
/* 화살표(←, →)도 검정색으로 */
::v-deep .vuecal__arrow {
  color: black !important;
}

/* "오늘" 버튼도 검정색 */
::v-deep .vuecal__today-btn {
  color: black !important;
}
::v-deep .vuecal__heading {
  color: black !important;
}
::v-deep .vuecal__event--background {
  background-color: #00a5bc4d !important;
  color: black !important;
  color: white;
  border-radius: 6px;
  border: 1px solid rgb(255, 104, 23); /* ✅ 경계선 설정 */
}
</style>
