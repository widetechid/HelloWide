package com.wide.mobile.hwdemo.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wide.mobile.hellowide.api.Constants;
import com.wide.mobile.hellowide.api.NotificationUtil;
import com.wide.mobile.hellowide.core.utils.Logger;
import com.wide.mobile.hwdemo.MainActivity;
import com.wide.mobile.hwdemo.R;
import com.wide.mobile.hwdemo.core.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nikmatulfajar on 8/7/20
 */
public class HelloWideMessagingService extends FirebaseMessagingService {
    private static final String TAG = "HelloWide FMS";
    private static final String CHANNEL_ID = "hellowide-demo";
    private static final String CHANNEL_NAME = "hellowide";
    private static String MISSED_CALL_BODY = "Missed Call";

    public static Timer missedCallTimer;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Helper helper = new Helper(this);
        if (remoteMessage.getData() != null) {
            try{
                JSONObject data = new JSONObject(remoteMessage.getData());
                JSONObject params = new JSONObject(data.getString("params"));

                if(data.getString("action").equalsIgnoreCase(Helper.ACTION_INCOMING_CALL)){
                    Logger.d(TAG,"Receive pushNotification on "+ MainActivity.appState);
                    Logger.d(TAG,"PushNotification data: " + data.toString());
                    if(Helper.isCallObsolete(params.getLong("timestamp"))){
                        Logger.d(TAG,"IncomingCall is obsolete, display missedCall notification");
                        NotificationManager manager = NotificationUtil.getNotificationManager(this, CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                        showMissedCallNotification(this, manager, params.getString("callerName"), MISSED_CALL_BODY, Helper.createNotificationID());
                    }
                    else{
                        data.put("appState", MainActivity.appState);
                        Logger.d(TAG, "Is this device on a call? : "+ NotificationUtil.isOnCall());
                        if(NotificationUtil.isOnCall()){
                            Logger.d(TAG, "Device currently on call");
                        }
                        else{
                            if("foreground".equals(MainActivity.appState)){
                                showVideoCallNotification(this, params);
//                                callPushIntentService(this, params);
                            } else {
//                                callPushIntentService(this, params);
                                showVideoCallNotification(this, params);
                            }
                            NotificationUtil.playAudio(this);
                        }
                    }
                }
                else{
                    Logger.d(TAG,"Receiving unidentified pushNotification");
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
        Logger.d(TAG, "Refreshed token: " + s);
        saveToken(this, s);
    }

    public void saveToken(Context ctx, String s){
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("myToken",s).apply();
    }

    public static String getToken(Context ctx){
        String token = PreferenceManager.getDefaultSharedPreferences(ctx).getString("myToken", "0");
        Logger.d(TAG,"GET TOKEN "+token);
        return token;
    }

    public static void showVideoCallNotification(final Context context, JSONObject data) {
        try {
            String title = data.getString("callerName");
            String body = "Incoming Video Call";
            if(data.getString("callType").equalsIgnoreCase(Constants.CALL_TYPE_VOICE)){
                body = "Incoming Voice Call";
            }

            int notificationId = Helper.createNotificationID();
            Logger.d(TAG, "Showing PushNotification with id : "+ notificationId);

            Intent intent = new Intent(context, PushIntentService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("pushMessage", data.toString());
            intent.putExtra(PushIntentService.NOTIFICATION_ID, notificationId);
            PendingIntent pIntent = PendingIntent.getService(context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);

            //accept action
            Intent actionIntentA = new Intent(context, IncomingCallService.class);
            actionIntentA.setAction(IncomingCallService.ACTION_ACCEPT);
            actionIntentA.putExtra("appState", MainActivity.appState);
            actionIntentA.putExtra("pushMessage", data.toString());
            actionIntentA.putExtra(IncomingCallService.EXTRA_DATA, Helper.ACTION_ACCEPT_CALL);
            actionIntentA.putExtra(IncomingCallService.NOTIFICATION_ID, notificationId);
            PendingIntent pActionA = PendingIntent.getBroadcast(context,0,actionIntentA,PendingIntent.FLAG_MUTABLE);

            //reject action
            Intent actionIntentR = new Intent(context, IncomingCallService.class);
            actionIntentR.setAction(IncomingCallService.ACTION_REJECT);
            actionIntentR.putExtra("appState", MainActivity.appState);
            actionIntentR.putExtra("pushMessage", data.toString());
            actionIntentR.putExtra(IncomingCallService.EXTRA_DATA, Helper.ACTION_REJECT_CALL);
            actionIntentR.putExtra(IncomingCallService.NOTIFICATION_ID, notificationId);
            PendingIntent pActionR = PendingIntent.getBroadcast(context,0,actionIntentR,PendingIntent.FLAG_MUTABLE);

            //for add button action
            NotificationCompat.Action actionA = new NotificationCompat.Action(R.drawable.ic_call, "accept", pActionA);
            NotificationCompat.Action actionR = new NotificationCompat.Action(R.drawable.ic_call_end, "reject", pActionR);

            Notification incomingCallNotification = NotificationUtil.createNotification(context, pIntent, title, body, R.drawable.ic_cob_notification, context.getResources().getColor(R.color.backgroundColor), CHANNEL_ID, actionA, actionR);
            incomingCallNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            incomingCallNotification.defaults |= Notification.DEFAULT_SOUND;
            incomingCallNotification.defaults |= Notification.DEFAULT_VIBRATE;

            NotificationManager manager = NotificationUtil.getNotificationManager(context, CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            manager.notify(notificationId, incomingCallNotification);

            NotificationUtil.setOnCall();

            missedCallTimer = new Timer();

            TimerTask missedCallTask = new TimerTask() {
                @Override
                public void run() {
                    manager.cancel(notificationId);
                    Logger.d(TAG,"Incoming call notification with id : "+ notificationId + " is canceled due to unanswered call");
                    if(true){
                        //IncomingCallActivity.activity.finish();
                        Logger.d(TAG,"IncomingCallActivity finished");
                    }
                    HelloWideMessagingService.showMissedCallNotification(context, manager, title, MISSED_CALL_BODY, notificationId+1);
                    NotificationUtil.endIncomingNotificationProcess();
                }
            };
            missedCallTimer.schedule(missedCallTask, 60000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMissedCallNotification(Context context, NotificationManager manager, String title, String body, int notificationId){
        Intent missedIntent = new Intent(context, PushIntentService.class);
        missedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        missedIntent.putExtra("missedCallNotification", true);
        missedIntent.putExtra(PushIntentService.NOTIFICATION_ID, notificationId);
        PendingIntent missedPendingIntent = PendingIntent.getService(context, notificationId, missedIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification missedCallNotification = NotificationUtil.createNotification(context, missedPendingIntent, title, body, R.drawable.ic_cob_notification, context.getResources().getColor(R.color.backgroundColor), CHANNEL_ID);
        manager.notify(notificationId, missedCallNotification);
    }
}
