<template>
  <ion-list>
    <ion-item>
      <ion-label>
        <h1>회원정보 수정</h1>
        <p style="color: red">* 는 필수입력 해야합니다.</p>
      </ion-label>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        이메일 <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input
        type="email"
        placeholder="qwerty@naver.com"
        :clear-input="true"
        v-model="form.email"
      >
      </ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        이름 <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input
        placeholder="이름을 입력하세요"
        :clear-input="true"
        v-model="form.name"
      >
      </ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        연락처 <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input
        type="tel"
        placeholder="010-1234-5678"
        maxlength="20"
        v-model="form.phoneNumber"
      ></ion-input>
    </ion-item>

    <ion-item button @click="showDateModal = true">
      <ion-label position="floating"
        >생년월일 <ion-text color="danger">*</ion-text></ion-label
      >
      <ion-input :value="formattedBirthDay" readonly />
    </ion-item>

    <ion-modal
      :is-open="showDateModal"
      @didDismiss="showDateModal = false"
      :breakpoints="[0, 0.5]"
      :initial-breakpoint="0.5"
    >
      <ion-content class="ion-padding">
        <ion-datetime
          v-model="form.birthDay"
          presentation="date"
          locale="ko-KR"
          prefer-wheel="true"
          @ionChange="onBirthDateSelected"
        ></ion-datetime>
        <ion-button expand="block" @click="showDateModal = false"
          >선택</ion-button
        >
      </ion-content>
    </ion-modal>

    <ion-item>
      <ion-label position="stacked">
        포지션 <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-select label-placement="floating" v-model="form.position">
        <ion-select-option value="BG">통기타</ion-select-option>
        <ion-select-option value="EG">일렉기타</ion-select-option>
        <ion-select-option value="BASS">베이스</ion-select-option>
        <ion-select-option value="DRUM">드럼</ion-select-option>
        <ion-select-option value="VOCAL">보컬</ion-select-option>
        <ion-select-option value="ETC">etc(멀티)</ion-select-option>
      </ion-select>
    </ion-item>

    <!-- <ion-item>
      <ion-label position="stacked">
        자기소개 <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-textarea
        label-placement="floating"
        :counter="true"
        rows="6"
        maxlength="200"
        placeholder="거주지역 : 
음악경력 :
악기보유여부 : 
좋아하는 음악 (장르) :
하고싶은 말 : "
        :clear-input="true"
        v-model="form.remark"
      ></ion-textarea>
    </ion-item> -->

    <ion-button color="primary" shape="round" expand="block" @click="btnSave">
      <div style="display: flex; align-items: center; gap: 4px">
        수정 <ion-icon :icon="checkmarkDoneOutline" />
      </div>
    </ion-button>
  </ion-list>

  <ion-loading
    :is-open="isLoading"
    message="회원정보 수정 중 입니다..."
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
  IonInput,
  IonIcon,
  alertController,
  IonDatetime,
  IonModal,
  IonTextarea,
  IonLoading,
} from "@ionic/vue";
import { ref, defineProps, computed } from "vue";
import { checkmarkDoneOutline } from "ionicons/icons";
import axiosInstance from "@/libs/httpRequester"; // ✅ axios 인스턴스 import

const props = defineProps(["Userinfo"]);

const showDateModal = ref(false);

const isLoading = ref(false);

const form = ref({
  id: props.Userinfo.id,
  email: props.Userinfo.email,
  name: props.Userinfo.name,
  phoneNumber: props.Userinfo.phoneNumber,
  remark: props.Userinfo.remark,
  position: props.Userinfo.position,
  birthDay: props.Userinfo.birthDay,
});

const formattedBirthDay = computed(() => {
  if (!form.value.birthDay) return "";
  const date = new Date(form.value.birthDay);
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
});

function onBirthDateSelected() {
  // 선택 시 이벤트 처리도 가능
  console.log("선택된 날짜:", form.value.birthDay);
}

async function chkValidate() {
  const requiredFields = [
    { key: "email", label: "이메일" },
    { key: "name", label: "이름" },
    { key: "phoneNumber", label: "전화번호" },
    { key: "birthDay", label: "생년월일" },
    { key: "position", label: "포지션" },
  ];

  for (const field of requiredFields) {
    if (!form.value[field.key] || form.value[field.key].trim() === "") {
      const alert = await alertController.create({
        header: "입력 오류",
        message: `${field.label}은(는) 필수 입력 항목입니다.`,
        buttons: ["확인"],
      });
      await alert.present();
      return false;
    }
  }
  return true;
}

async function btnSave() {
  const isValid = await chkValidate(); // 1. 유효성 검사
  if (!isValid) return;

  // 2. 확인 팝업창 표시
  const alert = await alertController.create({
    header: "회원정보 수정 확인",
    message: "수정하시겠습니까?",
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

async function Register() {
  isLoading.value = true;
  const url = "/members/update";

  try {
    const response = await axiosInstance.put(url, form.value);
    if (response.status === 200) {
      isLoading.value = false;
      const alert = await alertController.create({
        header: "수정 완료",
        message: "회원정보 수정이 완료되었습니다.",
        buttons: [
          {
            text: "확인",
            handler: () => {
              getMyInfo();
            },
          },
        ],
      });
      await alert.present();
    }
  } catch (error) {
    // 네트워크 오류 또는 서버 미응답
    const alert = await alertController.create({
      header: "서버 오류",
      message: "서버에 연결할 수 없습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}

async function getMyInfo() {
  try {
    const response = await axiosInstance.get("/account/myInfo");
    const member = response.data;

    form.value = member;
    return member;
  } catch (error) {
    return null;
  }
}
</script>

<style scoped></style>
