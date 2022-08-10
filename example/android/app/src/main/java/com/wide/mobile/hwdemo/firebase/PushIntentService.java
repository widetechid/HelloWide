package com.wide.mobile.hwdemo.firebase;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;

import com.wide.mobile.hellowide.core.utils.Logger;
import com.wide.mobile.hwdemo.BuildConfig;
import com.wide.mobile.hwdemo.MainActivity;
import com.wide.mobile.hwdemo.core.Helper;

import org.json.JSONException;
import org.json.JSONObject;

public class PushIntentService extends IntentService {
    public static final String TAG = "HelloWide PushIntent";

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    public PushIntentService() {
        super("PushIntentService");
    }

    public PushIntentService(String name){
        super(name);
    }

    public static String pushMessage;

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra(NOTIFICATION_ID, 0));

        String pushContent;

        if(intent.hasExtra("missedCallNotification")){
            Logger.d(TAG, "Press missedCallNotification on "+ MainActivity.appState);
        }
        else {
            try {
                Logger.d(TAG, "Press videoCallNotification on " + MainActivity.appState);
                pushContent = intent.getStringExtra("pushMessage");
                JSONObject params = new JSONObject(pushContent);
                params.put("action", Helper.ACTION_INCOMING_CALL);
                params.put("appState", MainActivity.appState);

                // add content to pushIntentService so it can be processed on MainActivity onCreate()
                PushIntentService.pushMessage = params.toString();
            } catch (JSONException e) {

            }
        }
        Helper helper = new Helper(this);

        if("firstLaunch".equals(MainActivity.appState)){
            Logger.d(TAG, "Waking-up the app");
            intent = getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
        }
        else {
            helper.checkPushIntentService();
        }
    }
}
