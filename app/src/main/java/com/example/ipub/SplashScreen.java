package com.example.ipub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView splashImage = findViewById(R.id.splashimg);
        TextView ipub = findViewById(R.id.ipubText);

        Animation bottom_animation = AnimationUtils.loadAnimation(this , R.anim.bottom_animation);
        Animation top_animation = AnimationUtils.loadAnimation(this , R.anim.top_animation);

        ipub.setAnimation(bottom_animation);
        splashImage.setAnimation(top_animation);


        splashImage.animate().setDuration(3500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //move to new activity in 2 seconds
                startActivity(new Intent(SplashScreen.this, WelcomeSlides.class));
                SplashScreen.this.finish();
            }
        }, 3500);


    }

}