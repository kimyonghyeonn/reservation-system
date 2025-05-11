import { createRouter, createWebHistory } from "@ionic/vue-router";
import TabsPage from "../views/TabsPage.vue";

const routes = [
  {
    path: "/",
    redirect: "/library/main",
  },
  {
    path: "/library/",
    component: TabsPage,
    children: [
      {
        path: "",
        redirect: "/library/main",
      },
      {
        path: "main",
        component: () => import("@/views/StatusPage.vue"),
      },
      {
        path: "booking",
        component: () => import("@/views/BookingPage.vue"),
      },
      {
        path: "home",
        component: () => import("@/views/HomePage.vue"),
      },
      {
        path: "/library/userManager",
        component: () => import("@/views/UserManage.vue"),
      },
      {
        path: "/library/roomManager",
        component: () => import("@/views/RoomManage.vue"),
      },
      {
        path: "/library/myReservation",
        component: () => import("@/views/ReservationManage.vue"),
      },
      {
        path: "/library/allReservation",
        component: () => import("@/views/AllReservationManage.vue"),
      },
    ],
  },
  {
    path: "/login",
    component: () => import("@/components/LoginForm.vue"),
  },
  {
    path: "/register",
    component: () => import("@/components/RegisterForm.vue"),
  },
  {
    path: "/findId",
    component: () => import("@/views/FindIdPage.vue"),
  },
  {
    path: "/findPw",
    component: () => import("@/views/FindPwPage.vue"),
  },
  {
    path: "/changePw",
    component: () => import("@/views/ChangePwPage.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
