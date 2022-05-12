package com.yeslabapps.fictionfocus.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.ChatActivity;
import com.yeslabapps.fictionfocus.model.Token;

import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser=sp.getString("Current_USERID","None");

        String notificationtype=remoteMessage.getData().get("notificationType");

        if (notificationtype.equals("ChatNotification")){

            String sent=remoteMessage.getData().get("sent");
            String user=remoteMessage.getData().get("user");
            FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
            if (fUser!=null&&sent.equals(fUser.getUid())){
                if (!savedCurrentUser.equals(user)){
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        sendOAndAboveNotification(remoteMessage);
                    }else {
                        sentNormalNotification(remoteMessage);
                    }
                }
            }
        }

        /*if (notificationtype.equals("FollowNotification")){
            String sent = remoteMessage.getData().get("sent");
            String user = remoteMessage.getData().get("user");
            FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser!=null && sent.equals(firebaseUser.getUid())){
                if (!savedCurrentUser.equals(user)){
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        sendOreoFollow(remoteMessage);
                    }else{
                        sendFollowNotification(remoteMessage);
                    }
                }
            }
        }
*/



    }


   /* private void sendFollowNotification(RemoteMessage remoteMessage){
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("receiverId",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);


        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (i>0){
            j=1;
        }
        notificationManager.notify(j,builder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOreoFollow(RemoteMessage remoteMessage){
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("receiverId",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification notification2=new OreoNotification(this);
        Notification.Builder builder=notification2.getONotifications(title,body,pIntent,defSoundUri,icon);

        int j=0;
        if (i>0){
            j=1;
        }
        notification2.getManager().notify(j,builder.build());
    }*/


    private void sentNormalNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("receiverId",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);


        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (i>0){
            j=1;
        }
        notificationManager.notify(j,builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("receiverId",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification notification1=new OreoNotification(this);
        Notification.Builder builder=notification1.getONotifications(title,body,pIntent,defSoundUri,icon);

        int j=0;
        if (i>0){
            j=1;
        }
        notification1.getManager().notify(j,builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Tokens");
        Token token=new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);
    }

}