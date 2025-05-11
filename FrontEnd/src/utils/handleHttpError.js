import { alertController } from "@ionic/vue"; // ✅ ES Module import

export async function handleHttpError(error) {
  if (error.response) {
    const status = error.response.status;
    const message = error.response.data?.message || "서버 오류가 발생했습니다.";

    const alert = await alertController.create({
      header: `에러가 발생했습니다. (에러코드: ${status})`,
      message: "에러 내용: " + message,
      buttons: ["확인"],
    });
    await alert.present();
  } else {
    const alert = await alertController.create({
      header: "서버 오류",
      message: "서버에 연결할 수 없습니다.",
      buttons: ["확인"],
    });
    await alert.present();
  }
}
