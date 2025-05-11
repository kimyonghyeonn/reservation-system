// 사용자 정보 관리 페이지 // 관리자 권한 있을시 접근 가능

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
      <ion-card>
        <ion-card-header>
          <div class="card-header-row">
            <ion-card-title>합주실 목록 </ion-card-title>
            <ion-button @click="openDetailPopup()">합주실 등록</ion-button>
          </div>
        </ion-card-header>
        <ion-grid>
          <ion-row>
            <ion-col size="3"><strong>합주실</strong></ion-col>
            <ion-col size="4"><strong>연락처</strong></ion-col>
            <ion-col size="2"><strong>영업시간</strong></ion-col>
            <ion-col size="3"><strong>등록일</strong></ion-col>
          </ion-row>

          <ion-row v-for="room in rooms" :key="room.roomId">
            <ion-col size="3">
              <span class="clickable-name" @click="openMemberDetail(room)">
                {{ room.roomName }}
              </span></ion-col
            >
            <ion-col size="4">{{ room.phoneNumber }}</ion-col>
            <ion-col size="2">{{ room.operTime }}</ion-col>
            <ion-col size="3">{{ room.createDt }}</ion-col>
          </ion-row>
        </ion-grid>
      </ion-card>

      <ion-modal
        ref="detailModalRef"
        :is-open="showDetailModal"
        @did-dismiss="closeDetailModal"
      >
        <ion-header>
          <ion-toolbar color="primary">
            <ion-title>합주실 상세 정보</ion-title>
            <ion-buttons slot="end">
              <ion-button shape="round" class="save-btn" @click="btnSave()"
                >저장</ion-button
              >
              <ion-button
                v-if="selectedRoom.roomId !== ''"
                shape="round"
                class="delete-btn"
                @click="btnDelete"
                >삭제</ion-button
              >
              <ion-button
                shape="round"
                class="close-btn"
                @click="closeDetailModal"
                >닫기</ion-button
              >
            </ion-buttons>
          </ion-toolbar>
        </ion-header>

        <ion-content class="ion-padding">
          <ion-grid>
            <ion-item>
              <ion-label position="stacked">
                합주실 이름 <ion-text color="danger">*</ion-text>
              </ion-label>
              <ion-input
                type="text"
                placeholder="예) 영등포 합주실"
                :clear-input="true"
                v-model="selectedRoom.roomName"
                maxlength="100"
              >
              </ion-input>
            </ion-item>
            <ion-item>
              <ion-label position="stacked">
                합주실 주소 <ion-text color="danger">*</ion-text>
              </ion-label>
              <div style="display: flex; width: 100%; gap: 8px">
                <ion-input
                  placeholder="주소를 검색하세요"
                  style="flex: 1"
                  v-model="selectedRoom.roomAddr"
                  readonly
                />
                <ion-button size="small" @click="openPostcodePopup"
                  >주소 검색</ion-button
                >
              </div>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">
                상세 주소 <ion-text color="danger">*</ion-text>
              </ion-label>
              <ion-input
                type="text"
                placeholder="상세 주소를 입력하세요."
                :clear-input="true"
                v-model="selectedRoom.addrDetail"
                maxlength="100"
              >
              </ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">
                합주실 담당자 연락처 <ion-text color="danger">*</ion-text>
              </ion-label>
              <ion-input
                type="tel"
                placeholder="010-1234-5678"
                maxlength="20"
                v-model="selectedRoom.phoneNumber"
              ></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">
                수용 가능 인원 <ion-text color="danger">*</ion-text>
              </ion-label>
              <ion-input
                type="number"
                placeholder="최대 수용 가능 인원"
                maxlength="20"
                v-model="selectedRoom.capacity"
              ></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">
                운영 시작 시간 <ion-text color="danger">*</ion-text>
              </ion-label>
              <ion-input
                type="number"
                placeholder="24시 기준으로 입력하세요. 예) 오후1시 => 13"
                maxlength="20"
                v-model="selectedRoom.startTime"
              ></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked">
                운영 종료 시간 <ion-text color="danger">*</ion-text>
              </ion-label>
              <ion-input
                type="number"
                placeholder="24시 기준으로 입력하세요. 예) 오후12시 => 24"
                maxlength="20"
                v-model="selectedRoom.endTime"
              ></ion-input>
            </ion-item>

            <ion-item>
              <ion-label position="stacked"> 합주실 안내문 </ion-label>
              <ion-textarea
                label-placement="floating"
                :counter="true"
                rows="6"
                maxlength="200"
                placeholder="간략한 안내 내용을 입력해주세요."
                :clear-input="true"
                v-model="selectedRoom.description"
              ></ion-textarea>
            </ion-item>

            <ion-item>
              <ion-label position="stacked"> 합주실 이미지 </ion-label>
              <div style="display: flex; align-items: center; gap: 12px">
                <img
                  v-if="selectedRoom.imageUrl"
                  :src="selectedRoom.imageUrl"
                  alt="썸네일"
                  style="
                    width: 80px;
                    height: 80px;
                    object-fit: cover;
                    border-radius: 8px;
                  "
                  @click="previewImageModal(selectedRoom.imageUrl)"
                />

                <ion-button size="small" @click="triggerFileInput"
                  >이미지 업로드</ion-button
                >
                <input
                  type="file"
                  accept="image/*"
                  ref="fileInput"
                  style="display: none"
                  @change="handleFileChange"
                />
              </div>
            </ion-item>
          </ion-grid>
        </ion-content>
      </ion-modal>
    </ion-content>
    <ion-modal :is-open="showImageModal" @did-dismiss="closeImageModal">
      <ion-header>
        <ion-toolbar>
          <ion-title>이미지 미리보기</ion-title>
          <ion-buttons slot="end">
            <ion-button @click="closeImageModal">닫기</ion-button>
          </ion-buttons>
        </ion-toolbar>
      </ion-header>
      <ion-content class="ion-padding">
        <img
          :src="previewImage"
          alt="확대 이미지"
          style="width: 100%; height: auto; border-radius: 8px"
        />
      </ion-content>
    </ion-modal>
  </ion-page>
</template>

<script setup>
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonButton,
  alertController,
  IonButtons,
  IonGrid,
  IonRow,
  IonCol,
  IonMenuButton,
  IonModal,
  IonInput,
  IonTextarea,
} from "@ionic/vue";

import { ref, onMounted, reactive } from "vue";
import { useAccountStore } from "@/stores/account";
import axiosInstance from "@/libs/httpRequester"; // ✅ 인터셉터 적용된 axios 인스턴스

// 계정 스토어

const accountStore = useAccountStore();
const rooms = ref([]);
const showDetailModal = ref(false);
const fileInput = ref(null);
const showImageModal = ref(false);
const previewImage = ref(""); // base64 or url
const detailModalRef = ref(null);
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

async function getAllRooms() {
  try {
    const response = await axiosInstance.get("/room/allRooms");
    rooms.value = response.data;

    return rooms.value;
  } catch (error) {
    return null;
  }
}

function triggerFileInput() {
  fileInput.value?.click();
}
async function handleFileChange(event) {
  const file = event.target.files[0];
  if (!file) return;
  selectedRoom.imageFile = file;
  selectedRoom.imageUrl = URL.createObjectURL(file); // 미리보기용

  try {
    const base64 = await fileToBase64(file); // base64 변환
    selectedRoom.imageBase64 = base64; // ⬅️ 여기에 저장
    console.log("Base64 저장 완료:", base64.slice(0, 100)); // 일부만 출력
  } catch (e) {
    console.error("base64 변환 실패", e);
  }
}
function fileToBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file); // base64 포맷으로 읽기
    reader.onload = () => resolve(reader.result);
    reader.onerror = (error) => reject(error);
  });
}

function previewImageModal(imageSrc) {
  previewImage.value = imageSrc;
  showImageModal.value = true;
}
function closeImageModal() {
  showImageModal.value = false;
}

async function btnDelete() {
  const alert = await alertController.create({
    header: "합주실 삭제",
    message:
      "합주실 삭제시 예약테이블의 예약내역도 삭제됩니다. 진행하시겠습니까?",
    buttons: [
      {
        text: "취소",
        role: "cancel", // 아무 동작 안 함
      },
      {
        text: "확인",
        handler: async () => {
          // 확인 누르면 Register 실행
          await deleteRoom();
        },
      },
    ],
  });
  await alert.present(); // 팝업 띄우기
}

async function deleteRoom() {
  const url = `/room/delete/${selectedRoom.roomId}`; // ⚠️ id를 path에 포함

  try {
    const response = await axiosInstance.delete(url); // GET처럼 단순 호출

    if (response.status === 200) {
      const alert = await alertController.create({
        header: "합주실 삭제 완료",
        message: "합주실이 삭제되었습니다.",
        buttons: [
          {
            text: "확인",
            handler: async () => {
              await getAllRooms(); // ✅ 목록 새로고침
              closeDetailModal(); // ✅ 모달 닫기
            },
          },
        ],
      });
      await alert.present();
    }
  } catch (error) {
    console.log(error);
  }
}

onMounted(async () => {
  getAllRooms();
});

function openMemberDetail(room) {
  Object.assign(selectedRoom, room); // ✅ reactive 구조 유지
  if (room.imageBase64) {
    selectedRoom.imageUrl = room.imageBase64;
  }
  console.log("selectedRoom: ", selectedRoom);
  console.log(" selectedRoom.imageUrl: ", selectedRoom.imageUrl);
  showDetailModal.value = true;
}

function openDetailPopup() {
  Object.assign(selectedRoom, {
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

  showDetailModal.value = true;
}

async function closeDetailModal() {
  showDetailModal.value = false;
  if (detailModalRef.value) {
    await detailModalRef.value.$el.dismiss();
  }
}
function openPostcodePopup() {
  new window.daum.Postcode({
    oncomplete: function (data) {
      let fullAddr = data.address;
      let extraAddr = "";

      if (data.addressType === "R") {
        if (data.bname !== "") {
          extraAddr += data.bname;
        }
        if (data.buildingName !== "") {
          extraAddr +=
            extraAddr !== "" ? `, ${data.buildingName}` : data.buildingName;
        }
        if (extraAddr !== "") {
          fullAddr += ` (${extraAddr})`;
        }
      }

      selectedRoom.roomAddr = fullAddr;
    },
  }).open();
}

async function chkValidate() {
  console.log("selectedRoom: ", selectedRoom);
  const requiredFields = [
    { key: "roomName", label: "합주실 이름" },
    { key: "roomAddr", label: "합주실 주소" },
    { key: "addrDetail", label: "상세 주소" },
    { key: "phoneNumber", label: "담당자 연락처" },
    { key: "startTime", label: "운영 시작 시간" },
    { key: "endTime", label: "운영 종료 시간" },
  ];

  for (const field of requiredFields) {
    const val = selectedRoom[field.key];
    if (!val || String(val).trim() === "") {
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
  let headerMsg = "합주실 저장 확인";
  let bodyMsg = "합주실을 등록하시겠습니까?";
  if (selectedRoom.roomId && selectedRoom.roomId !== "") {
    headerMsg = "합주실 수정 확인";
    bodyMsg = "합주실 정보를 수정하시겠습니까?";
  }
  if (!isValid) return;

  // 2. 확인 팝업창 표시
  const alert = await alertController.create({
    header: headerMsg,
    message: bodyMsg,
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
  let headerMsg = "합주실 등록 성공";
  let bodyMsg = "합주실 등록이 완료되었습니다.";

  let url = "/room/create"; // ⚠️ axiosInstance의 baseURL이 '/api'이므로 '/account/join'만 작성
  if (selectedRoom.roomId && selectedRoom.roomId !== "") {
    url = "/room/update"; // ⚠️ axiosInstance의 baseURL이 '/api'이므로 '/account/join'만 작성
    headerMsg = "합주실 수정 성공";
    bodyMsg = "합주실 수정이 완료되었습니다.";
  }

  selectedRoom.capacity = Number(selectedRoom.capacity);
  selectedRoom.startTime = Number(selectedRoom.startTime);
  selectedRoom.endTime = Number(selectedRoom.endTime);

  try {
    let response = null;
    if (selectedRoom.roomId && selectedRoom.roomId !== "") {
      response = await axiosInstance.put(url, selectedRoom);
    } else {
      response = await axiosInstance.post(url, selectedRoom);
    }
    if (response.status === 200) {
      const alert = await alertController.create({
        header: headerMsg,
        message: bodyMsg,
        buttons: [
          {
            text: "확인",
            handler: async () => {
              await getAllRooms(); // ✅ 목록 새로고침
              closeDetailModal(); // ✅ 모달 닫기
            },
          },
        ],
      });
      await alert.present();
    }
  } catch (error) {
    console.log("error");
  }
}
</script>

<style scoped>
ion-grid {
  padding: 0 10px;
}

ion-row {
  border-bottom: 1px solid #e0e0e0;
  min-height: 48px;
  align-items: center; /* 세로 중앙 정렬 */
  text-align: center; /* 가로 중앙 정렬 */
  background-color: #fff;
  transition: background-color 0.2s;
}

ion-row:first-child {
  background-color: #f7f9fc;
  font-weight: bold;
  text-align: center;
  border-top: 1px solid #ccc;
  border-bottom: 2px solid #dcdcdc;
}

ion-row:not(:first-child):hover {
  background-color: #f1faff; /* hover 효과로 산뜻한 느낌 */
}

ion-col {
  font-size: 14px;
  padding: 6px 4px;
  display: flex;
  justify-content: center; /* 내용 가운데 정렬 */
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.clickable-name {
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
}
.clickable-name:hover {
  color: #0056b3;
}
.close-btn {
  background-color: lightgray;
}

.save-btn {
  background-color: lightgreen;
}
.delete-btn {
  background-color: lightcoral;
}
.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
