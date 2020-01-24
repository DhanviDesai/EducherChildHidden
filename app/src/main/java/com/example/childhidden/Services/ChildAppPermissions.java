package com.example.childhidden.Services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class ChildAppPermissions extends Service {

    PackageManager pm;
    Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Start","Here");





    }

    public void checkPermissions(){
        Runnable code = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                if(!Settings.canDrawOverlays(getApplicationContext())){
                    Intent i = new Intent();
                    i.setAction("com.example.educher_child");
                    i.putExtra("Status",Settings.canDrawOverlays(getApplicationContext()));
                    sendBroadcast(i);
                }

                handler.removeCallbacks(this);
                handler.postDelayed(this,500);
            }

        };

        handler.removeCallbacks(code);
        handler.post(code);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }



}
