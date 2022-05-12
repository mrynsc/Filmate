package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.AdapterVideo;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Collections;

public class MoreVideoActivity extends AppCompatActivity {

    private ViewPager2 recyclerView;
    private AdapterVideo adapterVideo;
    private ArrayList<ModelVideo> videoArrayList;
    private FirebaseUser fUser;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_video);

        recyclerView=findViewById(R.id.moreVideosRv);
        videoArrayList = new ArrayList<>();
        adapterVideo = new AdapterVideo(videoArrayList,MoreVideoActivity.this);

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        loadVideosFromFirebase();

    }

    private void loadVideosFromFirebase() {
        videoArrayList = new ArrayList<>();

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Videos").limitToFirst(40).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if (snapshot.exists()){
                        ModelVideo modelVideo=ds.getValue(ModelVideo.class);

                        videoArrayList.add(modelVideo);

                        /*if (!modelVideo.getUserId().equals(fUser.getUid())){
                            videoArrayList.add(modelVideo);
                        }*/
                    }
                }
                Collections.shuffle(videoArrayList);
                adapterVideo =new AdapterVideo(videoArrayList,MoreVideoActivity.this);

                recyclerView.setAdapter(adapterVideo);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
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