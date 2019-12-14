package com.bvh.dev.checkapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ImageView imgAva;

    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgAva = findViewById(R.id.img_ava);

        Glide.with(this).load("http://st.nettruyen.com/data/comics/192/tuyen-tap-truyen-ngan-cua-tac-gia-doraem-2280.jpg").error(R.drawable.ic_launcher_background).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imgAva);


        RetrofitAPI.getService().getStoryByTrend().enqueue(new Callback<List<MyStory>>() {
            @Override
            public void onResponse(Call<List<MyStory>> call, Response<List<MyStory>> response) {
                String hours = Calendar.getInstance().getTime().getHours() + "";
                String minu = Calendar.getInstance().getTime().getMinutes() + "";
                if (response.body() == null) {

                    Log.e("test", "check finish");
                    createFileLog1(hours, minu, "Api lỗi rồi???");
                } else {
                    Log.e("test", "check_success: api ok");
                    createFileLog1(hours, minu, "API OK");
                }
            }

            @Override
            public void onFailure(Call<List<MyStory>> call, Throwable t) {
                Log.e("tets", "error check");
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

    }

    @SuppressLint("ShortAlarm")
    public void setTime() {
        int hour = Calendar.getInstance().getTime().getHours();
        int minu = Calendar.getInstance().get(Calendar.MINUTE) + 1;

        Log.e("test", minu + " : " + hour);

        alarmMgr = (AlarmManager) this.getApplication().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), Reciever.class);
        alarmIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minu);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        preferences = getSharedPreferences("CHECK_API", MODE_PRIVATE);
        editor = preferences.edit();
        String checkFirst = preferences.getString("THE FIRST", "");
        if (checkFirst.equals("")) {
            editor.putString("THE FIRST", "OK");
            editor.apply();
            setTime();
        } else {
            Date currentTime = Calendar.getInstance().getTime();
            int hoursCurrent = currentTime.getHours();
            int minuCurrent = currentTime.getMinutes();
            Log.e("test", hoursCurrent + " : " + minuCurrent);
        }
//        createFileLog();
    }

    public void createNotification_AndroidO() {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        /* Notification for oreo*/
        String channelId = "channel-01";
        String channelName = "Demo";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);


            remoteViews = new RemoteViews(getPackageName(),
                    R.layout.notification);
            builder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setCustomBigContentView(remoteViews)
                            .setDefaults(NotificationManager.IMPORTANCE_DEFAULT) // must requires VIBRATE permission
                            .setPriority(NotificationCompat.PRIORITY_HIGH);//must give priority to High, Max which will considered as heads-up notification;
            notification = builder.build();
        }
//to post your notification to the notification bar with a id. If a notification with same id already exists, it will get replaced with updated information.
        notificationManager.notify(111, notification);
    }

    public void createFileLog() {
        File file = new File(Environment.getExternalStorageDirectory() + "/check_api/");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "sample.txt");
            FileWriter writer = new FileWriter(gpxfile);
        } catch (Exception e) {
        }

    }

    public void createFileLog1(String hours, String minu, String status) {
        Log.e("test", "create file log");
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

    public String readFile() {
        int i = 0;
        i++;
        File file = new File(Environment.getExternalStorageDirectory() + "/check_api/");
        if (!file.exists()) {
            file.mkdir();
        }
        File gpxfile = new File(file, "sample.txt");
        StringBuffer stringBuffer = null;
        try {
            stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(gpxfile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {

        }
        return stringBuffer.toString();

    }
}
