package io.iwa.instamojo;

import android.app.Application;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.firebase.client.Firebase;

/**
 * //TODO Add Class Description
 * Author: harshit  on 28/2/16.
 */
public class CustomApplication extends Application {
    public static Firebase firebase;

    public CustomApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://instamojo.firebaseio.com/");
        Batch.Push.setGCMSenderId("387794085578");
        Batch.Push.setManualDisplay(true);
        Batch.setConfig(new Config("DEV56D35EE63E31420760F9EE3CEA5"));

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
    }

}
