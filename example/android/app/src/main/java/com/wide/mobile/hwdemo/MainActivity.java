package com.wide.mobile.hwdemo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wide.mobile.hellowide.api.HWConstants;
import com.wide.mobile.hellowide.api.HWCallCompact;
import com.wide.mobile.hellowide.api.HWPermissionManager;
import com.wide.mobile.hellowide.api.HelloWide;
import com.wide.mobile.hellowide.core.utils.HWPermissionCallback;
import com.wide.mobile.hellowide.core.utils.Logger;
import com.wide.mobile.hwdemo.firebase.MyMessagingService;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private Context context;
    private ImageView callButton;
    private ImageView videoCallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        this.context = this;

        initActivity();
        Logger.d("DEVICE_TOKEN", MyMessagingService.getToken(this));

        HWPermissionManager hwpm = new HWPermissionManager(this);
        hwpm.requestPermission( new HWPermissionCallback() {
            @Override
            public void onCompleted(int response) {
                Logger.d("PERMISSION TEST", String.valueOf(response));
                if(response == HWPermissionManager.HW_OVERLAY_DENIED){
                    finish();
                }
                else if(response == HWPermissionManager.HW_OVERLAY_GRANTED){
                    Toast.makeText(context, "Permission Granted",Toast.LENGTH_SHORT).show();
                }
            }
        });
        HWCallCompact.checkCallService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HWCallCompact.notifyAppOnBackground();
        Logger.d(TAG, "Application on background");
    }

    @Override
    protected void onResume() {
        super.onResume();
        HWCallCompact.notifyAppOnForeground();
        Logger.d(TAG, "Application on foreground");
    }

    private void initActivity(){
        setContentView(R.layout.main_activity);
        callButton = findViewById(R.id.call_button);
        videoCallButton = findViewById(R.id.video_call_button);

        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelloWide helloWide = HelloWide.getInstance(context);
                helloWide.outgoingCall(HWConstants.CALL_TYPE_VOICE, "Call Center");
            }
        });
        videoCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelloWide helloWide = HelloWide.getInstance(context);
                helloWide.outgoingCall(HWConstants.CALL_TYPE_VIDEO, "Call Center");
            }
        });
    }
}