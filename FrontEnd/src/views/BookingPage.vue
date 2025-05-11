<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>QWERTY</ion-title>
        <ion-buttons slot="end">
          <ion-menu-button auto-hide="false" />
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content id="main-content" :fullscreen="true">
      <div v-if="login">
        <BookingForm :room="selectedRoom" @callBack="Back" />
      </div>

      <div v-else>
        <Swiper
          :modules="[EffectCoverflow, Navigation]"
          effect="coverflow"
          grabCursor="true"
          centeredSlides="true"
          slides-per-view="auto"
          :coverflowEffect="{
            rotate: 50,
            stretch: 0,
            depth: 100,
            modifier: 1,
            slideShadows: true,
          }"
          navigation
          class="roomSwiper"
        >
          <SwiperSlide
            v-for="room in rooms"
            :key="room.roomId"
            @click="bookRoom(room.roomId)"
            class="slide"
          >
            <ion-card>
              <img
                :src="room.imageBase64"
                alt="room image"
                style="width: 100%; height: 300px; object-fit: cover"
              />
              <ion-card-header>
                <ion-card-title>{{ room.roomName }}</ion-card-title>
                <ion-card-subtitle>
                  수용인원: {{ room.capacity }}명 / 영업시간:
                  {{ room.startTime }} ~ {{ room.endTime }}시
                </ion-card-subtitle>
                <ion-card-subtitle>
                  {{ room.description }}
                </ion-card-subtitle>
              </ion-card-header>
            </ion-card>
          </SwiperSlide>
        </Swiper>
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
  IonButtons,
  IonMenuButton,
  alertController,
} from "@ionic/vue";
import { ref, reactive, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import BookingForm from "../components/BookingForm.vue";
import { useAccountStore } from "@/stores/account";
import axiosInstance from "@/libs/httpRequester";

// ✅ Swiper import
import { Swiper, SwiperSlide } from "swiper/vue";
import { Navigation, EffectCoverflow } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/effect-coverflow";

const router = useRouter();
const route = useRoute();
const accountStore = useAccountStore();

const login = ref(false);
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
  imageBase64: null,
  imageUrl: "",
});

onMounted(async () => {
  await getAllRooms();
});

watch(
  () => route.query.room_id,
  (roomId) => {
    login.value = !!roomId;
  },
  { immediate: true }
);

async function getAllRooms() {
  try {
    const response = await axiosInstance.get("/room/allRooms");
    rooms.value = response.data;
  } catch (error) {
    console.error("🚨 방 정보 조회 실패:", error);
  }
}

async function bookRoom(roomId) {
  const room = rooms.value.find((r) => r.roomId === roomId);
  if (!room) return;
  Object.assign(selectedRoom, room);

  if (!accountStore.loggedIn || !accountStore.checked) {
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
</script>

<style scoped>
.roomSwiper {
  width: 100%;
  padding-top: 20px;
  padding-bottom: 40px;
}

.slide {
  width: 90%;
  max-width: 350px;
}

.swiper-button-next,
.swiper-button-prev {
  color: #2a73a1;
  font-size: 24px;
}
</style>
