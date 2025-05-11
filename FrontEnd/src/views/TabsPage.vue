<template>
  <ion-page>
    <!-- 헤더는 각 페이지 내에서 정의하므로 TabsPage에는 생략 -->

    <!-- 콘텐츠 영역 -->
    <ion-content>
      <ion-router-outlet animated="false"></ion-router-outlet>
    </ion-content>

    <!-- 하단 탭 바 -->
    <ion-tab-bar slot="bottom">
      <ion-tab-button tab="main" href="/library/main">
        <ion-icon :icon="barChartOutline" />
        <ion-label>예약 현황</ion-label>
      </ion-tab-button>

      <ion-tab-button tab="booking" href="/library/booking">
        <ion-icon :icon="bookOutline" />
        <ion-label>합주실 예약</ion-label>
      </ion-tab-button>

      <ion-tab-button tab="home" href="/library/home">
        <ion-icon :icon="homeOutline" />
        <ion-label>마이 페이지</ion-label>
      </ion-tab-button>
    </ion-tab-bar>
  </ion-page>
</template>

<script setup>
import {
  IonPage,
  IonContent,
  IonRouterOutlet,
  IonTabBar,
  IonTabButton,
  IonLabel,
  IonIcon,
} from "@ionic/vue";
import { barChartOutline, bookOutline, homeOutline } from "ionicons/icons";
</script>
