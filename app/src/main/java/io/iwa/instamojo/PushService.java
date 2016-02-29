package io.iwa.instamojo;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.batch.android.Batch;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class PushService extends IntentService {


    public PushService() {
        super("PushService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try
        {
            if( Batch.Push.shouldDisplayPush(this, intent) ) // Check that the push is valid
            {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                // Assuming you have a drawable named notification_icon, can otherwise be anything you want
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(intent.getStringExtra(Batch.Push.TITLE_KEY))
                        .setContentText(intent.getStringExtra(Batch.Push.ALERT_KEY));

                // Create intent
                Intent launchIntent = startSplashActivity(); // Create your own intent
                Batch.Push.appendBatchData(intent, launchIntent); // Call this method to add tracking data to your intent to track opens

                // Finish building the notification using the launchIntent
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);


                PendingIntent deleteIntent =  recordNotificationDeleted();
                builder.setDeleteIntent(deleteIntent);

                // Display your notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                // "id" is supposed to be a unique id, in order to be able to update the notification if you want.
                // If you don't care about updating it, you can simply make a random it, like below
                int id = (int) (Math.random() * Integer.MAX_VALUE);
                notificationManager.notify(id, builder.build());

                // Call Batch to keep track of that notification
                Batch.Push.onNotificationDisplayed(this, intent);
            }
        }
        finally
        {
            PushReceiver.completeWakefulIntent(intent);
        }
    }

    private PendingIntent recordNotificationDeleted() {
        Intent intent = new Intent(this,NotificationDeleteService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,Constants.NOTIFICATION_DELETED,intent,0);
        return pendingIntent;
    }

    private Intent startSplashActivity() {
        return new Intent(this,SplashScreen.class);
    }


}
