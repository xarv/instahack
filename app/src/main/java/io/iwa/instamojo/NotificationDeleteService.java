package io.iwa.instamojo;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationDeleteService extends IntentService {
    public NotificationDeleteService() {
        super("NotificationDeleteService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Record Analytics Here for Notification Deleted.
            Log.e(NotificationDeleteService.this.getClass().getName(),"Intent Recorded for Push Deleted");
        }
    }


}
