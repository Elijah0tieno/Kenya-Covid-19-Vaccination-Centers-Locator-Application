package org.vosystems.covidvaccinationcenterslocator.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import org.vosystems.covidvaccinationcenterslocator.Activities.NotificationsMessage;
import org.vosystems.covidvaccinationcenterslocator.R;

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String text = bundle.getString("Vaccine Reminder");
        String date = bundle.getString("date") + " " + bundle.getString("time");

        //Click on notification
        Intent intent1 = new Intent(context, NotificationsMessage.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("message", text);

        //Notification Builder
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_001");

        //set notifications properties
        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
        contentView.setTextViewText(R.id.message, text);
        contentView.setTextViewText(R.id.date, date);
        builder.setSmallIcon(R.drawable.ic_baseline_alarm_24);
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setOnlyAlertOnce(true);
        builder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
        builder.setContent(contentView);
        builder.setContentIntent(pendingIntent);

        //create notification channel after api level 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "channel_name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        Notification notification = builder.build();
        notificationManager.notify(1, notification);

    }
}
