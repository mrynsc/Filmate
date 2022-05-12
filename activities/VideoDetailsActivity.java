package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.AdapterVideo;
import com.yeslabapps.fictionfocus.adapter.MyAdapterVideo;
import com.yeslabapps.fictionfocus.adapter.SearchVideoAdapter;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Collections;

public class VideoDetailsActivity extends AppCompatActivity {

    private String videoId;
    private ViewPager2 recyclerView;
    private AdapterVideo adapterVideo;
    private ArrayList<ModelVideo> videoArrayList;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        //Intent intent = new Intent();
        videoId = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("videoId", "none");

       // videoId = intent.getStringExtra("videoId");

        Toolbar toolbar = findViewById(R.id.toolbarVideoDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.detailsVideosRv);
        videoArrayList = new ArrayList<>();
        adapterVideo = new AdapterVideo(videoArrayList,VideoDetailsActivity.this);

        /*Intent intent = getIntent();
        int positionTouched = intent.getExtras().getInt("position");


        recyclerView.post(() -> recyclerView.setCurrentItem(positionTouched,false));*/

        loadSelectedVideo();

    }

    private void loadSelectedVideo(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos").child(videoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoArrayList.clear();
                videoArrayList.add(dataSnapshot.getValue(ModelVideo.class));


                adapterVideo =new AdapterVideo(videoArrayList,VideoDetailsActivity.this);
                //set adapter to recyclerview

                recyclerView.setAdapter(adapterVideo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*private void loadVideosFromFirebase() {
        videoArrayList  =new ArrayList<>();

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if (snapshot.exists()){
                        ModelVideo modelVideo=ds.getValue(ModelVideo.class);


                        if (modelVideo.getVideoType().equals("Movie") || modelVideo.getVideoType().equals("Series")){
                            videoArrayList.add(modelVideo);
                        }
                    }
                }
                adapterVideo =new AdapterVideo(videoArrayList,VideoDetailsActivity.this);

                recyclerView.setAdapter(adapterVideo);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }*/
    @Override
    protected void onStart() {
        IntentFilter intentFilter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}