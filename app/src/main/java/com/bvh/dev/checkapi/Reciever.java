package com.bvh.dev.checkapi;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reciever extends BroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationCompat.Builder builder;

    @Override
    public void onReceive(final Context context, Intent intent) {
//        Intent intent1 = new Intent(context, MyService.class);
//        context.startService(intent1);
        setTime(context);

        RetrofitAPI.getService().getStoryByTrend().enqueue(new Callback<List<MyStory>>() {
            @Override
            public void onResponse(Call<List<MyStory>> call, Response<List<MyStory>> response) {
                int hour = Calendar.getInstance().getTime().getHours();
                int minu = Calendar.getInstance().get(Calendar.MINUTE);
                if (response.body() == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createNotification_AndroidO(context);
                        createFileLog(hour + "", minu + "", "Lỗi API__________________");
                    } else {
                        createFileLog(hour + "", minu + "", "Lỗi API__________________");
                        createNotification(context);
                    }
                    Log.e("test", "check finish");
                } else {
                    createFileLog(hour + "", minu + "", "API OK");
                    Log.e("test", "check_success: api ok");
                }
//                setTime();
            }

            @Override
            public void onFailure(Call<List<MyStory>> call, Throwable t) {
                Log.e("tets", "error check");
            }
        });
    }

    @SuppressLint("ShortAlarm")
    public void setTime(Context context) {
        Log.e("test", "restart");
        int hour = Calendar.getInstance().getTime().getHours();
        int minu = Calendar.getInstance().get(Calendar.MINUTE) + 1;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Reciever.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minu);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        Log.e("test", minu + " : " + hour);
    }

    public void createNotification_AndroidO(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        /* Notification for oreo*/
        String channelId = "channel-01";
        String channelName = "Demo";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);


            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.notification);
            builder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setCustomBigContentView(remoteViews)
                            .setDefaults(NotificationManager.IMPORTANCE_DEFAULT) // must requires VIBRATE permission
                            .setPriority(NotificationCompat.PRIORITY_HIGH);//must give priority to High, Max which will considered as heads-up notification;
            notification = builder.build();
        }
//to post your notification to the notification bar with a id. If a notification with same id already exists, it will get replaced with updated information.
        notificationManager.notify(111, notification);
    }

    public void createNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        //layout notification
        remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification);
        //build notification
        builder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomBigContentView(remoteViews)
                .setAutoCancel(true);

        notification = builder.build();
        manager.notify(111, notification);
    }

    public void createFileLog(String hours, String minu, String status) {
        Log.e("test", "create log");
        File file = new File(Environment.getExternalStorageDirectory() + "/check_api/");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "sample.txt");
//            FileOutputStream fileOutputStream = new FileOutputStream(gpxfile, true);
//            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
//            writer.append(hours + " : " + minu + " : " + status + "\n");
//            writer.close();
//            fileOutputStream.close();

            FileOutputStream f = new FileOutputStream(gpxfile, true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(hours + " : " + minu + " : " + status);
            pw.flush();
            pw.close();
            f.close();
        } catch (Exception e) {
        }
    }
}
