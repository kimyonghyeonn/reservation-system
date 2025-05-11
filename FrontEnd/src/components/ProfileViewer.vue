<template>
  <div class="profile-viewer">
    <img
      :src="image || defaultImage"
      class="profile-image"
      @click="handleImageClick"
    />

    <ion-modal :is-open="showModal" @didDismiss="showModal = false">
      <ion-content class="ion-padding">
        <img :src="image || defaultImage" class="modal-image" />
        <ion-button expand="block" @click="showModal = false">닫기</ion-button>
      </ion-content>
    </ion-modal>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { IonModal, IonContent, IonButton } from "@ionic/vue";
import axiosInstance from "@/libs/httpRequester";

const props = defineProps({
  image: String,
  memberId: [String, Number],
});
const defaultImage = "/no-image.jpg";

const showModal = ref(false);

const handleImageClick = async () => {
  showModal.value = true;

  try {
    await axiosInstance.post("/log/user", {
      action: "VIEW_PROFILE_IMAGE",
      target: `profile-image#${props.memberId || "UNKNOWN"}`,
      result: "SUCCESS",
      httpMethod: "GET",
      statusCode: 200,
    });
  } catch (error) {
    console.error("🚨 프로필 이미지 활동 로그 실패:", error);
  }
};
</script>

<style scoped>
.profile-viewer {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}
.profile-image {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #ccc;
  cursor: pointer;
}
.modal-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
  object-fit: contain;
  margin-bottom: 12px;
}
</style>
