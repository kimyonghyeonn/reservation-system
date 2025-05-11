<template>
  <ion-list>
    <ion-item>
      <ion-label>
        <h1>회원 가입</h1>
        <p style="color: red">* 는 필수입력 해야합니다.</p>
      </ion-label>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        이메일 (이메일 형식대로 입력) <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input
        type="email"
        ref="emailRef"
        placeholder="qwerty@naver.com"
        :clear-input="true"
        v-model="form.email"
        maxlength="100"
      >
      </ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        이름 (한글) <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input
        ref="nameRef"
        maxlength="5"
        placeholder="홍길동"
        :clear-input="true"
        v-model="form.name"
      >
      </ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        연락처 (숫자) <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input
        type="tel"
        placeholder="01012345678"
        maxlength="11"
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
        ID (영문) <ion-text color="danger">*</ion-text>
      </ion-label>

      <div style="display: flex; align-items: center; width: 100%; gap: 8px">
        <ion-input
          v-model="form.loginId"
          ref="loginIdRef"
          type="text"
          placeholder="아이디를 입력하세요"
          clear-input="true"
          maxlength="50"
          style="flex: 1"
          autocomplete="off"
          autocapitalize="off"
          spellcheck="false"
        ></ion-input>

        <ion-button size="small" @click="isDuplicated">
          ID 중복체크
        </ion-button>
      </div>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        비밀번호
        <ion-text color="danger">*</ion-text>
      </ion-label>
      <ion-input
        ref="loginPwRef"
        maxlength="50"
        type="password"
        :clear-input="true"
        v-model="form.loginPw"
      >
      </ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="stacked">
        비밀번호 확인
        <ion-text color="danger">*</ion-text>
      </ion-label>
      <div style="display: flex; align-items: center; width: 100%">
        <ion-input
          ref="loginPwConfirmRef"
          style="flex: 1"
          v-model="form.loginPwConfirm"
          type="password"
          maxlength="50"
          :clear-input="true"
          autocomplete="off"
          autocapitalize="off"
          spellcheck="false"
          placeholder="비밀번호를 다시 입력하세요"
        ></ion-input>
        <ion-button
          size="small"
          @click="checkSamePassword"
          style="margin-left: 8px"
        >
          비밀번호 체크
        </ion-button>
      </div>
    </ion-item>

    <ion-item lines="none">
      <ion-label>
        <div :style="{ color: passwordValidations.length ? '#3880ff' : 'red' }">
          ✓ 6자 이상, 16자 이하
        </div>
        <div
          :style="{
            color: passwordValidations.letterNumber ? '#3880ff' : 'red',
          }"
        >
          ✓ 영문자와 숫자 포함
        </div>
        <div
          :style="{
            color: passwordValidations.specialChar ? '#3880ff' : 'red',
          }"
        >
          ✓ 특수문자 1개 이상 포함
        </div>
      </ion-label>
    </ion-item>

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
        <ion-select-option value="ETC">etc(미정)</ion-select-option>
      </ion-select>
    </ion-item>

    <PrivacyAgreement v-model="agreedPrivacy" />

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

    <div style="display: flex; justify-content: space-around; gap: 8px">
      <ion-button
        id="signup"
        fill="clear"
        color="success"
        shape="round"
        expand="block"
        @click="btnSave"
      >
        <div
          style="display: flex; align-items: center; gap: 4px; color: #3880ff"
        >
          동료되기
          <ion-icon :icon="checkmarkDoneOutline" />
        </div>
      </ion-button>

      <ion-button
        fill="clear"
        color="medium"
        shape="round"
        expand="block"
        @click="goLogin"
      >
        <div style="display: flex; align-items: center; gap: 4px">
          이미 동료입니다.
          <ion-icon :icon="arrowForwardOutline" />
        </div>
      </ion-button>
    </div>
  </ion-list>
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
} from "@ionic/vue";
import { ref, defineProps, watch, defineEmits, computed } from "vue";
import { checkmarkDoneOutline, arrowForwardOutline } from "ionicons/icons";
import { useRouter } from "vue-router";
import axiosInstance from "@/libs/httpRequester"; // ✅ axios 인스턴스 import
import PrivacyAgreement from "@/components/PrivacyAgreement.vue";

const router = useRouter();
const props = defineProps(["isRegister"]);
const emit = defineEmits(["goBackToLogin"]);
const emailRef = ref(null);
const nameRef = ref(null);
const loginIdRef = ref(null);
const loginPwRef = ref(null);
const loginPwConfirmRef = ref(null);
const agreedPrivacy = ref(false);
const form = ref({
  email: "",
  name: "",
  loginId: "",
  loginPw: "",
  loginPwConfirm: "",
  phoneNumber: "",
  remark: "",
  position: "",
  manager: "",
  birthDay: "", // ✅ 초기값: YYYY-MM-DD,
  infoAgree: false, // 개인정보 동의 여부
});
const passwordValidations = ref({
  length: false,
  letterNumber: false,
  specialChar: false,
});

async function chkValidate() {
  const requiredFields = [
    { key: "email", label: "이메일" },
    { key: "name", label: "이름" },
    { key: "birthDay", label: "생년월일" },
    { key: "loginId", label: "ID" },
    { key: "loginPw", label: "비밀번호" },
    { key: "loginPwConfirm", label: "비밀번호 확인" },
    { key: "phoneNumber", label: "연락처" },
    { key: "position", label: "포지션" },
  ];

  // 1. 필수값 입력 확인
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

  // 2. 이메일 형식 검사
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(form.value.email)) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "올바른 이메일 형식을 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  // 3. 비밀번호 복잡도 검사
  // const pw = form.value.loginPw;
  // const pwRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?":{}|<>]).{8,}$/;

  // if (!pwRegex.test(pw)) {
  //   const alert = await alertController.create({
  //     header: "비밀번호 규칙 오류",
  //     message:
  //       "비밀번호는 8자리 이상이며, 최소 1개의 대문자와 1개의 특수문자를 포함해야 합니다.",
  //     buttons: ["확인"],
  //   });
  //   await alert.present();
  //   return false;
  // }
  // ✅ 3. 비밀번호 길이 검사 (8자리 이상)
  const pw = form.value.loginPw;
  if (pw && pw.length < 8) {
    const alert = await alertController.create({
      header: "비밀번호 오류",
      message: "비밀번호는 8자리 이상이어야 합니다.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  // 비밀번호 일치 확인
  if (form.value.loginPw !== form.value.loginPwConfirm) {
    const alert = await alertController.create({
      header: "비밀번호 확인",
      message: "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  const koreanRegex = /^[가-힣]+$/;
  if (!koreanRegex.test(form.value.name)) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "이름은 한글만 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  if (!agreedPrivacy.value) {
    const alert = await alertController.create({
      header: "동의 필요",
      message: "개인정보 처리방침에 동의해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  return true;
}

async function Register() {
  form.value.infoAgree = true;
  const url = "/account/join"; // ⚠️ axiosInstance의 baseURL이 '/api'이므로 '/account/join'만 작성

  try {
    const response = await axiosInstance.post(url, form.value);
    if (response.status === 200) {
      const alert = await alertController.create({
        header: "회원가입 성공",
        message: "회원가입이 완료되었습니다.",
        buttons: [
          {
            text: "확인",
            handler: () => {
              emit("goBackToLogin", false); // 상태만 변경
            },
          },
        ],
      });
      await alert.present();
    }

    // 회원가입 완료 후 로그인 화면으로 전환
    emit("goBackToLogin", !props.isRegister);
  } catch (error) {
    if (error.response) {
      const status = error.response.status;

      if (status === 400) {
        const alert = await alertController.create({
          header: "입력 오류",
          message: "이메일, 아이디, 비밀번호는 필수 입력 항목입니다.",
          buttons: ["확인"],
        });
        await alert.present();
      } else if (status === 409) {
        const alert = await alertController.create({
          header: "중복 오류",
          message: "이미 사용 중인 아이디입니다.",
          buttons: ["확인"],
        });
        await alert.present();
      } else {
        const alert = await alertController.create({
          header: "오류",
          message: "회원가입 중 오류가 발생했습니다.",
          buttons: ["확인"],
        });
        await alert.present();
      }
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

async function btnSave() {
  // 1. 중복 체크 먼저 수행

  const isDupl = await isDuplicatedForBtnSave();

  // 2. 중복일 경우 중단
  if (!isDupl) {
    return;
  }

  // 3. 유효성 검사
  const isValid = await chkValidate();
  if (!isValid) return;

  // 2. 확인 팝업창 표시
  const alert = await alertController.create({
    header: "회원가입 확인",
    message: "함께하시겠습니까?",
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

function goLogin() {
  emit("goBackToLogin", !props.isRegister);
}

import axios from "axios"; // 기본 axios import 추가

async function isDuplicated() {
  const loginId = form.value.loginId;

  if (!loginId) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "아이디를 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return;
  }

  const url = `/api/account/isDupl/${loginId}`; // 전체 경로 명시 (axiosInstance 미사용 시 필요)

  try {
    const response = await axios.get(url); // ✅ axiosInstance → axios
    const result = response.data;

    const alert = await alertController.create({
      header: "중복 확인 결과",
      message: result
        ? "사용 가능한 아이디입니다."
        : "이미 사용 중인 아이디입니다.",
      buttons: ["확인"],
    });

    await alert.present();

    return result;
  } catch (error) {
    const alert = await alertController.create({
      header: "오류",
      message: "중복 확인 중 오류가 발생했습니다.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }
}

async function isDuplicatedForBtnSave() {
  const loginId = form.value.loginId;

  // 1. 아이디 입력 여부 확인
  if (!loginId || loginId.trim() === "") {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "아이디를 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }

  // 2. 중복 검사
  const url = `/api/account/isDupl/${loginId}`;

  try {
    const response = await axios.get(url);
    const isAvailable = response.data; // ✅ true: 사용 가능, false: 중복

    if (!isAvailable) {
      const alert = await alertController.create({
        header: "중복 오류",
        message: "이미 사용 중인 아이디입니다.",
        buttons: ["확인"],
      });
      await alert.present();
      return false;
    }

    // 사용 가능한 아이디인 경우: 아무 알림 없이 통과
    return true;
  } catch (error) {
    const alert = await alertController.create({
      header: "오류",
      message: "중복 확인 중 오류가 발생했습니다.",
      buttons: ["확인"],
    });
    await alert.present();
    return false;
  }
}

async function checkSamePassword() {
  const loginPw = form.value.loginPw;
  const loginPwConfirm = form.value.loginPwConfirm;
  if (!loginPwConfirm) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "비밀번호 확인란을 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return; // 아래 로직 실행하지 않도록 중단
  } else if (!loginPw) {
    const alert = await alertController.create({
      header: "입력 오류",
      message: "비밀번호를 입력해주세요.",
      buttons: ["확인"],
    });
    await alert.present();
    return; // 아래 로직 실행하지 않도록 중단
  }
  const same = form.value.loginPw === form.value.loginPwConfirm;

  const alert = await alertController.create({
    header: "비밀번호 확인 결과",
    message: same
      ? "비밀번호가 일치합니다."
      : "비밀번호가 일치하지 않습니다. 비밀번호를 확인해주세요.",
    buttons: ["확인"],
  });

  await alert.present();
}
const showDateModal = ref(false);

const formattedBirthDay = computed(() => {
  if (!form.value.birthDay) return "";
  const date = new Date(form.value.birthDay);
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
});

function onBirthDateSelected() {
  // 선택 시 이벤트 처리도 가능
  console.log("선택된 날짜:", form.value.birthDay);
  if (navigator.vibrate) {
    navigator.vibrate(100); // 진동 길이 (밀리초)
  }
}

watch(
  () => form.value.loginPw,
  (newPw) => {
    passwordValidations.value.length = newPw.length >= 6 && newPw.length <= 16;
    passwordValidations.value.letterNumber =
      /[A-Za-z]/.test(newPw) && /\d/.test(newPw);
    passwordValidations.value.specialChar = /[!@#$%^&*(),.?":{}|<>]/.test(
      newPw
    );
  }
);
</script>

<style scoped></style>
