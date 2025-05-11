<template>
  <div class="profile-uploader">
    <!-- 🔍 프로필 이미지 (클릭 시 모달 열림) -->
    <img
      :src="previewImage || defaultImage"
      class="profile-image"
      @click="showModal = true"
    />

    <!-- 📤 업로드 버튼 -->
    <ion-button size="small" @click="triggerFileInput"
      >프로필 사진 업로드</ion-button
    >
    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      @change="onFileChange"
      hidden
    />

    <!-- 🔍 이미지 확대 모달 -->
    <ion-modal :is-open="showModal" @didDismiss="showModal = false">
      <ion-content class="ion-padding">
        <img
          :src="previewImage || defaultImage"
          class="modal-image"
          @click="showModal = false"
        />

        <ion-button expand="block" @click="showModal = false">닫기</ion-button>
      </ion-content>
    </ion-modal>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { IonButton, IonModal, IonContent } from "@ionic/vue";

const props = defineProps({
  modelValue: String,
});
const emit = defineEmits(["update:modelValue", "save"]); // ✅ save 이벤트 추가

const fileInput = ref(null);
const previewImage = ref(props.modelValue || "");
const showModal = ref(false);
const defaultImage = "/no-image.jpg";

watch(
  () => props.modelValue,
  (val) => {
    previewImage.value = val;
  }
);

function triggerFileInput() {
  fileInput.value.click();
}

function onFileChange(e) {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = () => {
      const base64 = reader.result;
      previewImage.value = base64;
      emit("update:modelValue", base64); // v-model 연결
      emit("save", base64); // ✅ 업로드 직후 저장 요청
    };
    reader.readAsDataURL(file);
  }
}
</script>

<style scoped>
.profile-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 16px; /* 오른쪽 여백 추가 */
}

.profile-image {
  width: 60px; /* 기존보다 축소 */
  height: 60px;
  object-fit: cover;
  border-radius: 50%;
  border: 1px solid #ccc;
  cursor: pointer;
  margin-bottom: 6px; /* 이미지와 버튼 사이 간격 */
}

.modal-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
  object-fit: contain;
  margin-bottom: 12px;
}
</style>
