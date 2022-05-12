package com.yeslabapps.fictionfocus.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.MoreVideoActivity;
import com.yeslabapps.fictionfocus.activities.MyVideosActivity;
import com.yeslabapps.fictionfocus.activities.SearchQuotesActivity;
import com.yeslabapps.fictionfocus.activities.SearchVideoActivity;
import com.yeslabapps.fictionfocus.adapter.AdapterVideo;
import com.yeslabapps.fictionfocus.adapter.AlternativeVideoAdapter;
import com.yeslabapps.fictionfocus.adapter.SearchVideoAdapter;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Token;
import com.yeslabapps.fictionfocus.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class VideosFragment extends Fragment {

    private RecyclerView videoRv;


    private ArrayList<ModelVideo> videoArrayList;


    //private AdapterVideo adapterVideo;
    private Toolbar toolbar;

    private ProgressDialog pd;
    private ImageView searchVideo;

    private FirebaseUser fuser;

    private SearchVideoAdapter adapterVideo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);


        fuser = FirebaseAuth.getInstance().getCurrentUser();

        videoRv = view.findViewById(R.id.videosRv);

/*        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);*/

        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);

        adapterVideo = new SearchVideoAdapter(getContext(),videoArrayList);
        videoRv.setAdapter(adapterVideo);

        videoRv.setHasFixedSize(true);
        videoRv.setLayoutManager(layoutManager);


        toolbar=view.findViewById(R.id.toolbarVideo);



        ImageView moreVideo = view.findViewById(R.id.moreVideo);
        moreVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MoreVideoActivity.class);
                startActivity(intent);
            }
        });

        searchVideo = view.findViewById(R.id.searchVideo);
        searchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(getContext().getApplicationContext(), SearchVideoActivity.class));

            }
        });

        ImageView filterVideo=view.findViewById(R.id.filterVideo);
        filterVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(getContext(),R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"Movies","Series"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0: loadMoviesFromFirebase();
                                break;
                            case 1:loadSeriesFromFirebase();
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

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();


        loadVideosFromFirebase();



        return view;

    }


    private void loadVideosFromFirebase() {

        videoArrayList  =new ArrayList<>();

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Videos").limitToFirst(40).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if (snapshot.exists()){
                        ModelVideo modelVideo=ds.getValue(ModelVideo.class);




                        if (modelVideo.getVideoType().equals("Movie") || modelVideo.getVideoType().equals("Series")){
                            videoArrayList.add(modelVideo);
                            pd.dismiss();
                        }
                    }
                }

                pd.dismiss();
                Collections.shuffle(videoArrayList);
                adapterVideo =new SearchVideoAdapter(getContext(),videoArrayList);

                videoRv.setAdapter(adapterVideo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadMoviesFromFirebase() {
        videoArrayList  =new ArrayList<>();

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Videos").limitToFirst(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                videoArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){

                    ModelVideo modelVideo=ds.getValue(ModelVideo.class);

                    if (modelVideo.getVideoType().equals("Movie")){
                        videoArrayList.add(modelVideo);
                    }

                }
                Collections.shuffle(videoArrayList);
                //setup adapter
                adapterVideo =new SearchVideoAdapter(getContext(),videoArrayList);
                //set adapter to recyclerview


                videoRv.setAdapter(adapterVideo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void loadSeriesFromFirebase() {
        videoArrayList  =new ArrayList<>();

        //db reference
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Videos").limitToFirst(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                //clear list before adding data into it
                videoArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVideo modelVideo=ds.getValue(ModelVideo.class);


                    if (modelVideo.getVideoType().equals("Series")){
                        videoArrayList.add(modelVideo);
                    }

                    // add model/ data into list
                }

                Collections.shuffle(videoArrayList);
                //setup adapter
                adapterVideo =new SearchVideoAdapter(getContext(),videoArrayList);
                //set adapter to recyclerview

                videoRv.setAdapter(adapterVideo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }


}

