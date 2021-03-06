package io.iwa.instamojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.batch.android.Batch;

import java.util.Date;

/**
 * //TODO Add Class Description
 * Author: harshit  on 29/2/16.
 */
public class BaseActivity extends AppCompatActivity{
    long startTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Batch.onStart(this);
        startTime = new Date().getTime();
    }

    @Override
    protected void onStop()
    {
        Batch.onStop(this);
        long endTime = new Date().getTime();
        Toast.makeText(getApplicationContext(),"Total Time Spent in Activity: " +
                String.valueOf(endTime-startTime)+"ms",Toast.LENGTH_LONG).show();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Batch.onDestroy(this);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Batch.onNewIntent(this, intent);

        super.onNewIntent(intent);
    }
}
