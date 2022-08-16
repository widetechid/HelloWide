package com.wide.mobile.hwdemo.firebase;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wide.mobile.hellowide.api.HelloWide;
import com.wide.mobile.hellowide.api.NotificationUtil;
import com.wide.mobile.hellowide.core.utils.Logger;
import com.wide.mobile.hwdemo.BuildConfig;
import com.wide.mobile.hwdemo.MainActivity;
import com.wide.mobile.hwdemo.core.Helper;

import org.json.JSONObject;

/**
 * Created by nikmatulfajar on 8/7/20
 */
public class IncomingCallService extends BroadcastReceiver {
    public static final String TAG = "WireMeet PushAction";
    public static final String ACTION_ACCEPT = "com.wirecard.action.ACCEPT";
    public static final String ACTION_REJECT = "com.wirecard.action.REJECT";
    public static final String EXTRA_DATA = "extraData";

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static String pushMessageCall;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        String data = intent.getStringExtra(EXTRA_DATA);

        Helper helper = new Helper(context);

        pushMessageCall =  intent.getStringExtra("pushMessage");
        try{
            JSONObject params = new JSONObject(pushMessageCall);
            params.put("action", data);
            params.put("appState", MainActivity.appState);

            if(Helper.ACTION_ACCEPT_CALL.equals(data)){
                Logger.d(TAG, "push accept pressed on "+ MainActivity.appState);
                Logger.d(TAG,params.toString());

                Helper.stopMissedCallTimer();
                NotificationUtil.endIncomingNotificationProcess();

                PushIntentService.pushMessage = params.toString(); // add content to pushIntentService so it can be processed on activity onCreate()

                if("firstLaunch".equals(MainActivity.appState)){
                    intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    context.startActivity(intent);
                } else {
                    helper.checkPushIntentService();
                }
            }
            else {
                Logger.d(TAG, "push reject pressed on "+ MainActivity.appState);
                Logger.d(TAG, params.toString());

                Helper.stopMissedCallTimer();
                NotificationUtil.endIncomingNotificationProcess();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
