package com.gurjeet.note_gkj_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gurjeet.note_gkj_android.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final int SPLASH_DISPLAY_LENGTH = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,
                        CategoryActivity.class);
                startActivity(intent);//moving to second activity page
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
//Reference: https://www.codegrepper.com/code-examples/java/how+to+show+splash+fragment+full+screen+on+android