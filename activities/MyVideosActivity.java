package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.AlternativeVideoAdapter;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Post;
import com.yeslabapps.fictionfocus.model.User;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyVideosActivity extends AppCompatActivity {

    private RecyclerView videoRv;
    private FirebaseUser firebaseUser;
    private String profileId;

    //array list
    private ArrayList<ModelVideo> videoArrayList;

    //adapter
    private AlternativeVideoAdapter adapterVideo;

    private TextView userNameText;

    private ProgressDialog pd;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private String profileReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_videos);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        videoRv=findViewById(R.id.videosRv);

        userNameText=findViewById(R.id.usersVideos);

        Toolbar toolbar = findViewById(R.id.toolbarVideo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(MyVideosActivity.this,R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        GridLayoutManager layoutManager=new GridLayoutManager(MyVideosActivity.this,2);

        adapterVideo = new AlternativeVideoAdapter(MyVideosActivity.this,videoArrayList);
        videoRv.setAdapter(adapterVideo);

        videoRv.setHasFixedSize(true);
        videoRv.setLayoutManager(layoutManager);

        Intent intent = MyVideosActivity.this.getIntent();
        profileReceiver = intent.getStringExtra("profileReceiverId");



       /* linearLayoutManager=new LinearLayoutManager(MyVideosActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);*/



        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(videoRv.getContext(), DividerItemDecoration.VERTICAL);
        videoRv.addItemDecoration(dividerItemDecoration);*/


        //fucntion call, load vibes

        String data = MyVideosActivity.this.getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        ImageView filterVideo = findViewById(R.id.filterVideo);
        filterVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(MyVideosActivity.this,R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"A-Z","Z-A","Movies","Series"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:getVideosA_Z();
                                break;
                            case 1:getVideosZ_A();
                                break;
                            case 2:getMovies();
                                break;
                            case 3:getSeries();
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


        loadVideosFromFirebase();

        userinfo();

    }

    private void getVideosA_Z(){
        videoArrayList  =new ArrayList<>();

        //db reference
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding data into it
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVideo modelVideo=ds.getValue(ModelVideo.class);

                    if (modelVideo.getUserId().equals(profileReceiver)){
                        videoArrayList.add(modelVideo);

                    }


                    // add model/ data into list
                }
                Collections.sort(videoArrayList, new Comparator<ModelVideo>() {
                    @Override
                    public int compare(ModelVideo modelVideo, ModelVideo modelVideo1) {
                        return modelVideo.getTitle().compareTo(modelVideo1.getTitle());
                    }
                });
                //setup adapter
                adapterVideo = new AlternativeVideoAdapter(MyVideosActivity.this,videoArrayList);
                //set adapter to recyclerview

                videoRv.setAdapter(adapterVideo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getVideosZ_A(){
        videoArrayList  =new ArrayList<>();

        //db reference
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding data into it
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVideo modelVideo=ds.getValue(ModelVideo.class);

                    if (modelVideo.getUserId().equals(profileReceiver)){
                        videoArrayList.add(modelVideo);

                    }


                    // add model/ data into list
                }
                Collections.sort(videoArrayList, new Comparator<ModelVideo>() {
                    @Override
                    public int compare(ModelVideo post, ModelVideo post1) {
                        return post1.getTitle().compareTo(post.getTitle());
                    }
                });
                //setup adapter
                adapterVideo = new AlternativeVideoAdapter(MyVideosActivity.this,videoArrayList);
                //set adapter to recyclerview

                videoRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMovies(){
        videoArrayList  =new ArrayList<>();

        //db reference
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding data into it
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVideo modelVideo=ds.getValue(ModelVideo.class);

                    if (modelVideo.getUserId().equals(profileReceiver) && modelVideo.getVideoType().equals("Movie")){
                        videoArrayList.add(modelVideo);

                    }


                    // add model/ data into list
                }
                Collections.reverse(videoArrayList);
                //setup adapter
                adapterVideo = new AlternativeVideoAdapter(MyVideosActivity.this,videoArrayList);
                //set adapter to recyclerview

                videoRv.setAdapter(adapterVideo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSeries(){
        videoArrayList  =new ArrayList<>();

        //db reference
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding data into it
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVideo modelVideo=ds.getValue(ModelVideo.class);

                    if (modelVideo.getUserId().equals(profileReceiver) && modelVideo.getVideoType().equals("Series")){
                        videoArrayList.add(modelVideo);

                    }


                    // add model/ data into list
                }
                Collections.reverse(videoArrayList);
                //setup adapter
                adapterVideo = new AlternativeVideoAdapter(MyVideosActivity.this,videoArrayList);
                //set adapter to recyclerview

                videoRv.setAdapter(adapterVideo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userinfo() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


                userNameText.setText(user.getUsername() + "'s videos");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadVideosFromFirebase() {
        videoArrayList  =new ArrayList<>();

        //db reference
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding data into it
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVideo modelVideo=ds.getValue(ModelVideo.class);

                    if (modelVideo.getUserId().equals(profileReceiver)){
                        videoArrayList.add(modelVideo);
                        pd.dismiss();
                    }


                    // add model/ data into list
                }
                Collections.reverse(videoArrayList);
                pd.dismiss();
                //setup adapter
                adapterVideo = new AlternativeVideoAdapter(MyVideosActivity.this,videoArrayList);
                //set adapter to recyclerview

                videoRv.setAdapter(adapterVideo);
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