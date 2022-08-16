package com.wide.mobile.hwdemo.core;

import android.content.Context;

import com.wide.mobile.hellowide.api.Constants;
import com.wide.mobile.hellowide.api.HelloWide;
import com.wide.mobile.hellowide.core.utils.Logger;
import com.wide.mobile.hwdemo.firebase.HelloWideMessagingService;
import com.wide.mobile.hwdemo.firebase.PushIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    public static final String TAG = "Helper";
    private static Context context;

    public static final String ACTION_INCOMING_CALL = "INCOMING_CALL";
    public static final String ACTION_ACCEPT_CALL = "ACCEPT_CALL";
    public static final String ACTION_REJECT_CALL = "REJECT_CALL";

    public Helper(Context context) {
        this.context = context;
    }

    public String getDeviceToken(){
        return HelloWideMessagingService.getToken(context);
    }

    public static int createNotificationID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(now));
        return id;
    }

    public static boolean isCallObsolete(long callTime){
        long diffInMills = Math.abs(System.currentTimeMillis() - callTime);

        Logger.d(TAG, "Different on callTime with received time : "+diffInMills+" millisecond");

        if(diffInMills > 60000){
            return true;
        }
        return false;
    }

    public static void stopMissedCallTimer(){
        if(HelloWideMessagingService.missedCallTimer !=null){
            HelloWideMessagingService.missedCallTimer.cancel();
        }
    }

    public static void checkPushIntentService(){
        Logger.d(TAG, "Check pushIntentService content");
        if(PushIntentService.pushMessage != null){
            try{
                JSONObject pushContent = new JSONObject(PushIntentService.pushMessage);
                if(pushContent.has("action")){
                    String action = pushContent.getString("action");
                    if(action.equalsIgnoreCase(ACTION_ACCEPT_CALL)){
                        Logger.d(TAG, "Receiving pushIntentService from videoCallNotification accepted action");
                        HelloWide helloWide = HelloWide.getInstance(context);
                        helloWide.incomingCall(pushContent.getString("callType"), pushContent.getString("roomName"), pushContent.getString("callerName"));
                        PushIntentService.pushMessage = null;
                    }
                    else if(action.equalsIgnoreCase(ACTION_INCOMING_CALL)){
                        Logger.d(TAG, "Receiving pushIntentService from videoCallNotification pressed");
                        HelloWide helloWide = HelloWide.getInstance(context);
                        helloWide.incomingCall(pushContent.getString("callType"), pushContent.getString("roomName"), pushContent.getString("callerName"));
                        PushIntentService.pushMessage = null;
                    }
                }
            }
            catch (JSONException e){
                System.out.println(e);
            }
        }
        else{
            Logger.d(TAG, "PushIntentService is empty, no active calling notification");
        }
    }
}
