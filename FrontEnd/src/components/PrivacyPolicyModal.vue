<template>
  <ion-modal :is-open="isOpen" @didDismiss="onClose">
    <ion-content class="ion-padding">
      <h1>개인정보 처리방침</h1>

      <p>
        합주 소모임 쿼티(이하 "쿼티")는 『개인정보 보호법』 등 관련 법령을
        준수하며, 이용자의 개인정보를 보호하기 위해 아래와 같이 개인정보
        처리방침을 수립·공개합니다.
      </p>

      <h2>1. 수집하는 개인정보 항목</h2>
      <ul>
        <li>필수 항목: 이름, 이메일 주소, 연락처, 생년월일</li>
      </ul>

      <h2>2. 개인정보 수집 및 이용 목적</h2>
      <ul>
        <li>회원 가입 및 관리</li>
        <li>서비스 제공에 따른 본인 인증</li>
        <li>고객 문의 응대 및 민원 처리</li>
      </ul>

      <h2>3. 개인정보 보유 및 이용기간</h2>
      <p>
        이용자의 개인정보는 수집·이용 목적 달성 시까지 보관하며, 그 후에는 지체
        없이 파기합니다. 단, 관계 법령에 따라 보관이 필요한 경우 해당 기간 동안
        보관할 수 있습니다.
      </p>

      <h2>4. 개인정보의 제3자 제공</h2>
      <p>
        회사는 원칙적으로 이용자의 개인정보를 외부에 제공하지 않으며, 제공이
        필요한 경우 별도의 동의를 받습니다.
      </p>

      <h2>5. 개인정보의 처리 위탁</h2>
      <ul>
        <li>위탁 대상: Amazon Web Services</li>
        <li>위탁 내용: 데이터 저장 및 인프라 관리</li>
      </ul>

      <h2>6. 개인정보 파기절차 및 방법</h2>
      <p>
        개인정보는 수집·이용 목적 달성 후 내부 방침 및 관련 법령에 따라 지체
        없이 파기합니다. 전자적 파일 형태의 정보는 복구 불가능한 방법으로
        삭제합니다.
      </p>

      <h2>7. 이용자의 권리와 행사 방법</h2>
      <p>
        이용자는 언제든지 자신의 개인정보에 대한 열람, 정정, 삭제, 처리 정지를
        요청할 수 있습니다.
      </p>

      <h2>8. 개인정보 보호책임자</h2>
      <ul>
        <li>성명: 양준영</li>
        <li>연락처: qwertypremiumstudio@gmail.com</li>
        <li>성명: 조인우</li>
        <li>연락처: whdlsdn753@naver.com</li>
      </ul>

      <h2>9. 정책 변경 안내</h2>
      <p>
        본 개인정보 처리방침은 법령, 정책 또는 쿼티 내부 방침에 따라 변경될 수
        있으며, 변경 시 홈페이지를 통해 공지합니다.
      </p>

      <p style="text-align: right; font-style: italic">
        (시행일: 2025년 4월 19일)
      </p>

      <ion-button expand="block" @click="onClose">닫기</ion-button>
    </ion-content>
  </ion-modal>
</template>

<script setup>
import { IonModal, IonContent, IonButton } from "@ionic/vue";

defineProps({
  isOpen: Boolean,
});
const emit = defineEmits(["close"]);
function onClose() {
  emit("close");
}
</script>

<style scoped>
h1 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}
h2 {
  font-size: 1.1rem;
  margin-top: 1rem;
  color: #333;
}
ul {
  padding-left: 1.2em;
}
</style>
