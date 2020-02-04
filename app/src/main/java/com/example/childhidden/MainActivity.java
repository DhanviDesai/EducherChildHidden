package com.example.childhidden;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.childhidden.Services.ChildAppPermissions;
import com.example.childhidden.Services.ForegroundService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    public static String parent_key=null;
    public static String child_id=null;

    private BootReceiver br;
    public static final int REQUEST_CODE=11;
    private Dialog dialog;
    public static final String APPS = "apps";

    Receiver r;

    @Override
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.activity_main);
       r = new Receiver();
        IntentFilter filter = new IntentFilter("com.example.childhidden");
        this.registerReceiver(r, filter);

        if (Settings.canDrawOverlays(MainActivity.this)) {
            //finish();
        }
        else{
            checkDrawOverlayPermission();
        }

      //  initDialoge();

        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, com.example.childhidden.MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);


        //startService(new Intent(this, ForegroundService.class));


    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Here in on stop","herer");
        Log.i("Unregister","ReceiverGoneHere");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {

        // check if we already  have permission to draw over other apps
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check if received result code
        //  is equal our requested code for draw permission
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkDrawOverlayPermission();
                }
            }
        }
    }

    private void initDialoge(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.activity_lock, null);
        TextView requestUnlock,call,sos;
        requestUnlock = promptsView.findViewById(R.id.textView14);
        requestUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new OverlayDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else{
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);


    }

    public class Receiver extends BroadcastReceiver{
        private  String phoneScreen  = null;
        private String systemUI = null;
        private boolean wentThrough=false;
        @Override
        public void onReceive(Context context, Intent intent) {

            Boolean value = intent.getBooleanExtra("Status",true);
             parent_key = intent.getStringExtra("ParentId");
             child_id = intent.getStringExtra("ChildId");
            Log.i("Status",String.valueOf(value));
            Log.i("ParentId",parent_key);
            Log.i("ChildId",child_id);
            if(!wentThrough){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(parent_key)
                        .child(child_id).child(APPS);
                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        if (name.equals("Phone Screen")) {
                            phoneScreen = dataSnapshot.child("packageName").getValue().toString().replace('.', '_');
                        }
                        if (name.equals("System UI")) {
                            systemUI = dataSnapshot.child("packageName").getValue().toString().replace('.', '_');
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.i("Here", "ChildChanged");

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                wentThrough = true;

            }

            if(!value) {

                if (wentThrough) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                            .child(parent_key).child(child_id).child(APPS);
                    Log.i("PhoneScreen", phoneScreen);
                    reference1.child(phoneScreen).child("locked").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("Lock", "Done");
                        }
                    });
                    if (systemUI != null) {
                        reference1.child(systemUI).child("locked").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("Lock Screen", "Done");
                            }
                        });
                    }
                }
            }


        }
    }
}
