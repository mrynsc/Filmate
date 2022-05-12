package com.yeslabapps.fictionfocus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.MyAdapterVideo;
import com.yeslabapps.fictionfocus.model.ModelVideo;

import java.util.ArrayList;

public class VideoDetailsFragment extends Fragment {

    private String postId;
    private ViewPager2 recyclerView;
    private MyAdapterVideo postAdapter;
    private ArrayList<ModelVideo> videoArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_details, container, false);


        postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("videopostid", "none");

        recyclerView=view.findViewById(R.id.detailsVideosRv);
        videoArrayList = new ArrayList<>();
        postAdapter = new MyAdapterVideo(videoArrayList,getContext());


        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoArrayList.clear();
                videoArrayList.add(dataSnapshot.getValue(ModelVideo.class));


                postAdapter =new MyAdapterVideo(videoArrayList,getContext());
                //set adapter to recyclerview

                recyclerView.setAdapter(postAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
