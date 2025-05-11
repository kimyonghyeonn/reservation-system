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
      <div v-if="login">
        <BookingForm :room="selectedRoom" @callBack="Back" />
      </div>
      <div v-if="!login">
        <ion-card
          v-for="room in rooms"
          :key="room.roomId"
          @click="bookRoom(room.roomId)"
        >
          <img
            alt="A"
            :src="room.imageBase64"
            style="width: 100%; height: 400px"
          />
          <ion-card-header>
            <ion-card-title>{{ room.roomName }}</ion-card-title>
            <ion-card-subtitle
              >수용인원 : {{ room.capacity }} 명 / 영업시간 :
              {{ room.startTime }} ~ {{ room.endTime }} 시</ion-card-subtitle
            >
            <ion-card-subtitle
              >수용인원 : {{ room.capacity }} 명 / 영업시간 :
              {{ room.startTime }} ~ {{ room.endTime }} 시</ion-card-subtitle
            >
            <ion-card-subtitle
              >물품목록 : {{ room.description }}
            </ion-card-subtitle>
          </ion-card-header>
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
  IonCard,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  alertController,
} from "@ionic/vue";
import { ref, watch, onMounted, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";
import BookingForm from "../components/BookingForm.vue";
import { useAccountStore } from "@/stores/account";
import axiosInstance from "@/libs/httpRequester"; // ✅ 인터셉터 적용된 axios 인스턴스

const router = useRouter();
const route = useRoute();
const login = ref(false);
const accountStore = useAccountStore();
const rooms = ref([]);
const selectedRoom = reactive({
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

onMounted(async () => {
  getAllRooms();
});

async function bookRoom(roomId) {
  const room = rooms.value.find((r) => r.roomId === roomId);
  if (!room) return;

  Object.assign(selectedRoom, room); // ✅ 선택한 방의 정보 복사
  if (accountStore.loggedIn == false || accountStore.checked == false) {
    const alert = await alertController.create({
      header: "알림",
      message: "로그인을 먼저 진행해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    router.push("/library/home");
  } else {
    login.value = true;
    router.push({ path: "/library/booking", query: { room_id: roomId } });
  }
}

function Back() {
  login.value = false;
  router.push({ path: "/library/booking" });
}

async function getAllRooms() {
  try {
    const response = await axiosInstance.get("/room/allRooms");
    rooms.value = response.data;
    console.log("rooms: ", rooms);
    return rooms.value;
  } catch (error) {
    return null;
  }
}

watch(
  () => route.query.room_id,
  (roomId) => {
    if (roomId) {
      login.value = true;
    } else {
      login.value = false;
    }
    console.log("login.value :  ", login.value);
  },
  { immediate: true }
);
</script>

<style scoped>
ion-card-title {
  --color: #2a73a1;
}

ion-card {
  --background: #e3e3e3;
}
</style>
