package com.yeslabapps.fictionfocus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.yeslabapps.fictionfocus.activities.LoginActivity;
import com.yeslabapps.fictionfocus.activities.RegisterActivity;
import com.yeslabapps.fictionfocus.activities.StartActivity;

public class MainActivity extends AppCompatActivity {

    ImageView animateimage;
    Button btnregister;
    Button btnlogin;
    LinearLayout linearLayout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animateimage =findViewById(R.id.iconimage);
        btnregister =findViewById(R.id.registerbtn);
        btnlogin =findViewById(R.id.loginbtn);
        linearLayout = findViewById(R.id.linear_layout);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null ){
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }

        linearLayout.animate().alpha(0f).setDuration(10);
        TranslateAnimation animation = new TranslateAnimation(0,0,0,-1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new myanimationlisterner());
        animateimage.setAnimation(animation);


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private  class  myanimationlisterner implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            animateimage.clearAnimation();animateimage.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}