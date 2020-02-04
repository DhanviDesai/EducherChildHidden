package com.example.childhidden.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.childhidden.MainActivity;
import com.example.childhidden.OverlayDialog;
import com.example.childhidden.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.childhidden.MainActivity.APPS;

public class ForegroundService extends Service {
    OverlayDialog dialog;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        check();
        initDialoge();
    }

    public void check(){
        String parent_key = MainActivity.parent_key;
        String child_id = MainActivity.child_id;
        if(parent_key!=null && child_id!=null){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(parent_key)
                    .child(child_id).child(APPS);
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    if(name.equals("Phone Screen")){
                        boolean value = (boolean)dataSnapshot.child("locked").getValue();
                        if(!value){
                            dialog.show();
                        }
                    }
                    if(name.equals("System UI")){
                        boolean value = (boolean)dataSnapshot.child("locked").getValue();
                        if(!value){
                            dialog.show();
                        }
                    }

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
}
