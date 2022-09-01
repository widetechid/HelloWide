package com.wide.mobile.hwdemo.firebase;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wide.mobile.hellowide.api.HWCallCompact;

import org.json.JSONException;
import org.json.JSONObject;

public class MyMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            try{
                JSONObject data = new JSONObject(remoteMessage.getData());

                if(data.getString("action").equalsIgnoreCase(HWCallCompact.ACTION_INCOMING_CALL)){
                    HWCallCompact.processIncomingCall(this, data);
                }
                else{
                    Log.d(TAG,"Receiving unidentified pushNotification");
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);
        saveToken(this, s);
    }

    public void saveToken(Context ctx, String s){
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("myToken",s).apply();
    }

    public static String getToken(Context ctx){
        String token = PreferenceManager.getDefaultSharedPreferences(ctx).getString("myToken", "0");
        Log.d(TAG,"GET TOKEN "+token);
        return token;
    }
}
