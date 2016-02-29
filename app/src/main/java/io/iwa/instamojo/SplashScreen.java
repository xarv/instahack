package io.iwa.instamojo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(CustomApplication.firebase.getAuth()!=null){
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                }
                else{
                    startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                }
                finish();

            }
        },400);

    }




}
