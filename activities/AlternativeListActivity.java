package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.AlternativeListDetailsAdapter;
import com.yeslabapps.fictionfocus.adapter.ListDetailsAdapter;
import com.yeslabapps.fictionfocus.model.ListMovie;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Collections;

public class AlternativeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ListMovie> listMovieArrayList;
    private FirebaseUser firebaseUser;
    private String profileId;
    private AlternativeListDetailsAdapter listAdapter;
    private String listId;
    private TextView barText;
    private String barTitle;
    private String barDesc;

    private TextView counterText;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private ImageView likeList;
    private TextView likedByTv;

    private TextView descText;

    private ProgressDialog pd;

    //private String listPub;

    private ImageView saveList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_list);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        barText=findViewById(R.id.listBarText);
        counterText=findViewById(R.id.listCounterText);

        descText =findViewById(R.id.listDescriptionText);

        likeList=findViewById(R.id.likeListBtn);
        likedByTv=findViewById(R.id.listLikedText);

        saveList = findViewById(R.id.saveListBtn);

        listId = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("listId", "none");

        Intent intent = AlternativeListActivity.this.getIntent();
        barTitle = intent.getStringExtra("barTitle");
        barDesc = intent.getStringExtra("barDesc");
       // listPub = intent.getStringExtra("listPublisher");


        barText.setText(barTitle);
        descText.setText(barDesc);

        Toolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pd = new ProgressDialog(AlternativeListActivity.this,R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();



        String data = AlternativeListActivity.this.getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }


        recyclerView = findViewById(R.id.film_recycler);
        listAdapter = new AlternativeListDetailsAdapter(listMovieArrayList, AlternativeListActivity.this);
        recyclerView.setAdapter(listAdapter);
        GridLayoutManager layoutManager=new GridLayoutManager(AlternativeListActivity.this,3);

        listMovieArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);


        saveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveList.getTag().equals("save")) {
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("ListSaves").child(firebaseUser.getUid()).child(listId)
                            .setValue(true);

                }else{
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("ListSaves").child(firebaseUser.getUid()).child(listId)
                            .removeValue();

                }
            }
        });


        likedByTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlternativeListActivity.this, FollowersActivity.class);
                intent.putExtra("ID",listId);
                intent.putExtra("TITLE","Liked by");
                startActivity(intent);

            }
        });


        likeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likeList.getTag().equals("Like")){
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference().child("ListLikes").child(listId).child(firebaseUser.getUid()).setValue(true);

                }else{
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference().child("ListLikes").child(listId).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        countListFilms();
        getListFilms();
        isLiked(listId,likeList);
        countLikes(listId,likedByTv);
        isSaved(listId,saveList);


    }

    private void isSaved(final String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ListSaves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.saved_24);

                    imageView.setTag("saved");


                }else
                {
                    imageView.setImageResource(R.drawable.save_24);
                    imageView.setTag("save");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void countLikes(String postId, final TextView textView){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ListLikes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                textView.setText(snapshot.getChildrenCount()+" likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void isLiked(String listId, final ImageView imageView){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ListLikes").child(listId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.liked_24);
                    imageView.setTag("Liked");

                }else{
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    imageView.setTag("Like");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getListFilms(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ListFilms").child(listId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMovieArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    ListMovie film = np.getValue(ListMovie.class);
                    listMovieArrayList.add(film);
                    pd.dismiss();

                }
                Collections.reverse(listMovieArrayList);
                pd.dismiss();
                listAdapter=new AlternativeListDetailsAdapter(listMovieArrayList,AlternativeListActivity.this);
                recyclerView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countListFilms(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ListFilms").child(listId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int counter = 0;
                        for (DataSnapshot np : snapshot.getChildren()) {
                            ListMovie value = np.getValue(ListMovie.class);


                            counter++;



                        }
                        counterText.setText(String.valueOf(counter + " films"));

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