<template>
  <ion-item lines="none">
    <ion-checkbox
      :checked="modelValue"
      @ionChange="(e) => emit('update:modelValue', e.detail.checked)"
      slot="start"
      aria-label="개인정보 동의"
    ></ion-checkbox>
    <ion-label>
      <strong>[필수]</strong> 개인정보 처리방침에 동의합니다.
      <span
        @click="showModal = true"
        style="color: #3880ff; text-decoration: underline; cursor: pointer"
      >
        내용보기
      </span>
    </ion-label>
  </ion-item>

  <PrivacyPolicyModal :is-open="showModal" @close="showModal = false" />
</template>

<script setup>
import { ref } from "vue";
import { IonItem, IonCheckbox, IonLabel } from "@ionic/vue";
import PrivacyPolicyModal from "./PrivacyPolicyModal.vue";

defineProps({
  modelValue: Boolean,
});
const emit = defineEmits(["update:modelValue"]);
const showModal = ref(false);
</script>
