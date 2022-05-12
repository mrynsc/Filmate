package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.SearchVideoAdapter;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SavedVideosActivity extends AppCompatActivity {

    private RecyclerView videoRv;
    private LinearLayoutManager linearLayoutManager;

    //array list
    private ArrayList<ModelVideo> videoArrayList;

    //adapter
    private SearchVideoAdapter adapterVideo;
    private FirebaseUser firebaseUser;
    private String profileId;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private String profileReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_videos);

        videoRv =findViewById(R.id.videosRv);

        GridLayoutManager layoutManager=new GridLayoutManager(SavedVideosActivity.this,2);

        adapterVideo = new SearchVideoAdapter(SavedVideosActivity.this,videoArrayList);
        videoRv.setAdapter(adapterVideo);

        videoRv.setHasFixedSize(true);
        videoRv.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolbarVideo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = SavedVideosActivity.this.getIntent();
        profileReceiver =intent.getStringExtra("profileReceiverId");

        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(videoRv.getContext(), DividerItemDecoration.VERTICAL);
        videoRv.addItemDecoration(dividerItemDecoration);*/



        String data = SavedVideosActivity.this.getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        ImageView filterVideo=findViewById(R.id.filterVideo);
        filterVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(SavedVideosActivity.this,R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"A-Z","Z-A","Movies","Series"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:getSavedVideosA_Z();
                                break;
                            case 1:getSavedVideosZ_A();
                                break;
                            case 2:getSavedVideosMovies();
                                break;
                            case 3:getSavedVideosSeries();
                                break;
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog customAlertDialog = builder.create();
                customAlertDialog.show();
            }

        });

        getSavedVideos();





    }



    private void getSavedVideos() {
        videoArrayList  = new ArrayList<>();

        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoSaves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        videoArrayList.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            ModelVideo post = snapshot1.getValue(ModelVideo.class);
                            for (String id :savedId){
                                if (post.getId().equals(id)){
                                    videoArrayList.add(post);
                                }
                            }
                        }
                        Collections.reverse(videoArrayList);
                        adapterVideo =new SearchVideoAdapter(SavedVideosActivity.this,videoArrayList);
                        //set adapter to recyclerview


                        videoRv.setAdapter(adapterVideo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSavedVideosA_Z() {
        videoArrayList  =new ArrayList<>();

        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoSaves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        videoArrayList.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            ModelVideo post = snapshot1.getValue(ModelVideo.class);
                            for (String id :savedId){
                                if (post.getId().equals(id)){
                                    videoArrayList.add(post);
                                }
                            }
                        }
                        Collections.sort(videoArrayList, new Comparator<ModelVideo>() {
                            @Override
                            public int compare(ModelVideo modelVideo, ModelVideo modelVideo1) {
                                return modelVideo.getTitle().compareTo(modelVideo1.getTitle());
                            }
                        });
                        adapterVideo =new SearchVideoAdapter(SavedVideosActivity.this,videoArrayList);
                        //set adapter to recyclerview


                        videoRv.setAdapter(adapterVideo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSavedVideosZ_A() {
        videoArrayList  =new ArrayList<>();

        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoSaves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        videoArrayList.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            ModelVideo post = snapshot1.getValue(ModelVideo.class);
                            for (String id :savedId){
                                if (post.getId().equals(id)){
                                    videoArrayList.add(post);
                                }
                            }
                        }
                        Collections.sort(videoArrayList, new Comparator<ModelVideo>() {
                            @Override
                            public int compare(ModelVideo modelVideo, ModelVideo modelVideo1) {
                                return modelVideo1.getTitle().compareTo(modelVideo.getTitle());
                            }
                        });
                        adapterVideo =new SearchVideoAdapter(SavedVideosActivity.this,videoArrayList);
                        //set adapter to recyclerview


                        videoRv.setAdapter(adapterVideo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSavedVideosMovies() {
        videoArrayList  =new ArrayList<>();

        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoSaves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        videoArrayList.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            ModelVideo post = snapshot1.getValue(ModelVideo.class);
                            for (String id :savedId){
                                if (post.getId().equals(id) && post.getVideoType().equals("Movie")){
                                    videoArrayList.add(post);
                                }
                            }
                        }
                        Collections.sort(videoArrayList, new Comparator<ModelVideo>() {
                            @Override
                            public int compare(ModelVideo modelVideo, ModelVideo modelVideo1) {
                                return modelVideo.getTitle().compareTo(modelVideo1.getTitle());
                            }
                        });
                        adapterVideo =new SearchVideoAdapter(SavedVideosActivity.this,videoArrayList);
                        //set adapter to recyclerview


                        videoRv.setAdapter(adapterVideo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSavedVideosSeries() {
        videoArrayList  =new ArrayList<>();

        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoSaves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        videoArrayList.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            ModelVideo post = snapshot1.getValue(ModelVideo.class);
                            for (String id :savedId){
                                if (post.getId().equals(id) && post.getVideoType().equals("Series")){
                                    videoArrayList.add(post);
                                }
                            }
                        }
                        Collections.sort(videoArrayList, new Comparator<ModelVideo>() {
                            @Override
                            public int compare(ModelVideo modelVideo, ModelVideo modelVideo1) {
                                return modelVideo.getTitle().compareTo(modelVideo1.getTitle());
                            }
                        });
                        adapterVideo =new SearchVideoAdapter(SavedVideosActivity.this,videoArrayList);
                        //set adapter to recyclerview


                        videoRv.setAdapter(adapterVideo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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