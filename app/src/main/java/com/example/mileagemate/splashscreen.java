package com.example.mileagemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashscreen extends AppCompatActivity {
    FirebaseAuth mauth;
    FirebaseUser user;
ImageView logo;
    Animation alpha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        logo=findViewById(R.id.logo);
        mauth=FirebaseAuth.getInstance();
        alpha= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha_anim);
        logo.startAnimation(alpha);
        user=mauth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user!=null){
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    finish();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), signup.class));
                    finish();
                }
            }
        },4000);
    }
}