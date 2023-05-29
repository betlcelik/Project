package com.example.roommateapplication.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.roommateapplication.UserProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            saveTokenToDatabase(user.getUid(), token);
        }
        // Here you can handle the new token as per your requirement
        // For example, you can save it to a local database or send it to your server
        // for further processing.
        // You can also associate the token with the user's account, etc.
    }

    private void saveTokenToDatabase(String userId, String token) {
        // Implement your logic to save the token to your database or server
        // For example, you can use Firebase Realtime Database or Firestore

        // Here's a sample code to save the token to the Firebase Realtime Database
        // Assuming you have a "Users" node with user tokens
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseRef.child(userId).setValue(token1);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_USERID","None");

        String sent = message.getData().get("sent");
        String user = message.getData().get("user");
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();

        if(fUser != null && sent.equals(fUser.getUid())){

            if(!savedCurrentUser.equals(user)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    sendAboveNoification(message);
                }
                else{
                    sendNormalNotification(message);
                }
            }
        }
    }

    private void sendNormalNotification(RemoteMessage message) {

        String user = message.getData().get("user");
        String icon= message.getData().get("icon");
        String title = message.getData().get("title");
        String body= message.getData().get("body");

        RemoteMessage.Notification notification= message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[//D]",""));
        Intent intent= new Intent(this, UserProfileFragment.class);
        Bundle bundle= new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this ,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j= 0;
        if(i > 0){
            j=i;
        }
        notificationManager.notify(j,builder.build());


    }

    private void sendAboveNoification(RemoteMessage message) {

        String user = message.getData().get("user");
        String icon= message.getData().get("icon");
        String title = message.getData().get("title");
        String body= message.getData().get("body");

        RemoteMessage.Notification notification= message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[//D]",""));
        Intent intent= new Intent(this, UserProfileFragment.class);
        Bundle bundle= new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this ,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         AboveNotification notification1= new AboveNotification(this);
        Notification.Builder builder=notification1.getNotifications(title,body,pendingIntent,defSoundUri,icon);



        int j= 0;
        if(i > 0){
            j=i;
        }


    }
}
