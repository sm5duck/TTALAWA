package ensharp.ttalawa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;

/**
 * Created by mh on 2016-08-01.
 */
public class RestTimeTask extends AsyncTask<Void, Void, Void> {

    public NotificationCompat.Builder notificationBuilder;
    public NotificationManager notificationManager;
    public Intent intent;
    public PendingIntent contentIntent;
    public final Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.mContext.getResources(), android.R.drawable.ic_menu_gallery); // 아이콘 ic_menu_gallery를 띄워준다.;
    public CountDownTimer timer;
    private String charging;
    SharedPreferences pref;

    protected void onPreExecute() {

        intent = new Intent(MainActivity.mContext, MainActivity.class);
        contentIntent = PendingIntent.getActivity(MainActivity.mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        charging = "과금 0 원";
        pref = new SharedPreferences(MainActivity.mContext);
        pref.putValue("state", "Rent", "state");
        //큰 아이콘
        notificationManager = (NotificationManager) MainActivity.mContext.getSystemService(MainActivity.mContext.NOTIFICATION_SERVICE);


        timer = new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                if ((millisUntilFinished < 1800000) && (millisUntilFinished > 1709000)) {
                    notification(notificationManager, notificationBuilder, "30분 남았습니다", charging);
                } else if ((millisUntilFinished < 1200000) && (millisUntilFinished > 1109000)) {
                    notification(notificationManager, notificationBuilder, "20분 남았습니다", charging);
                } else if ((millisUntilFinished < 600000) && (millisUntilFinished > 509000)) {
                    MainActivity.mainTxtView.setTextColor(Color.parseColor("#FF0000"));
                    notification(notificationManager, notificationBuilder, "10분 남았습니다", charging);
                } else if ((millisUntilFinished < 300000) && (millisUntilFinished > 209000)) {
                    MainActivity.mainTxtView.setTextColor(Color.parseColor("#FF0000"));
                    notification(notificationManager, notificationBuilder, "5분 남았습니다", charging);
                }
                MainActivity.mainTxtView.setText("반납까지 " + millisUntilFinished / 1000 / 60 % 60 + "분");
                MainActivity.overChargingView.setText(charging);
            }

            public void onFinish() {
                if(pref.getValue("state", "nonRent", "state").equals("Rent")){
                    pref.putValue("state", "firstOver", "state");
                    charging = "과금 1000원";
                    timeOver();
                } else{
                    initData();
                }
            }
        }.start();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(pref.getValue("state", "Rent", "state").equals("nonRent")){
            timer.cancel(); // 타이머 중지
            initData();
        }
        return null;
    }

    protected void onPostExecute(Void result) {

    }

    public void timeOver() {
        timer = new CountDownTimer(1800000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if ((millisUntilFinished < 1800000) && (millisUntilFinished > 1799000)){
                    notification(notificationManager, notificationBuilder, "30분 남았습니다", charging);
                } else if ((millisUntilFinished < 1200000) && (millisUntilFinished > 1109000)) {
                    notification(notificationManager, notificationBuilder, "20분 남았습니다", charging);
                } else if ((millisUntilFinished < 600000) && (millisUntilFinished > 509000)) {
                    notification(notificationManager, notificationBuilder, "10분 남았습니다", charging);
                } else if ((millisUntilFinished < 300000) && (millisUntilFinished > 209000)) {
                    notification(notificationManager, notificationBuilder, "5분 남았습니다", charging);
                }
                MainActivity.mainTxtView.setText("반납까지 " + millisUntilFinished / 1000 / 60 % 60 + "분");
                MainActivity.overChargingView.setText(charging);
                if(pref.getValue("state", "nonRent", "state").equals("nonRent")){
                    timer.cancel();
                    initData();
                }
            }

            @Override
            public void onFinish() {
                switch (pref.getValue("state", "nonRent", "state")){
                    case "firstOver": // 첫번째 과금
                        timer.cancel();
                        pref.putValue("state", "secondOver", "state");
                        charging = "과금 2000원";
                        timeOver();
                        break;
                    case "secondOver": // 두번째 과금
                        timer.cancel();
                        pref.putValue("state", "thirdOver", "state");
                        charging = "과금 3000원";
                        timeOver();
                        break;
                    case "thirdOver": // 세번째 과금
                        timer.cancel();
                        charging = "분실됨";
                        break;
                    default:
                        initData();
                        break;
                }
            }
        }.start();
    }

    public void initData() {
        MainActivity.mainTxtView.setTextColor(Color.parseColor("#000000"));
        MainActivity.mainTxtView.setText("따릉이를 대여하세요");
        MainActivity.overChargingView.setText("");
        pref.putValue("state", "nonRent", "state");
    }
    // 노티피케이션
    public void notification(NotificationManager pNotificationManager,
                             NotificationCompat.Builder pNotificationBuilder, String restTime, String charging){
        pNotificationBuilder = new NotificationCompat.Builder(MainActivity.mContext);
        pNotificationBuilder
                .setLargeIcon(bitmap) // 이미지 띄워주기
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle(restTime)
                .setContentText(charging) // 텍스트 띄우기
                .setTicker(restTime) // 상태 바에 뜨는 문구
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        pNotificationManager.notify(1, pNotificationBuilder.build());
    }
}