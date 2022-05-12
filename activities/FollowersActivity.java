package com.yeslabapps.fictionfocus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.UserAdapter;
import com.yeslabapps.fictionfocus.model.User;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;


import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    private  String personId;
    private  String title;
    private  List<String> idlist;
    private RecyclerView recyclerView_followers;
    private UserAdapter userAdapter;
    private  List<User> mUsers;
    private ProgressDialog pd;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private String listTitle;

    private String profileId;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        Intent intent = getIntent();
        personId = intent.getStringExtra("ID");
        title = intent.getStringExtra("TITLE");
//        personId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        title = "following";
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        recyclerView_followers = findViewById(R.id.recycler_viewfollwers);
        recyclerView_followers.setHasFixedSize(true);
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, mUsers, true);
        GridLayoutManager layoutManager=new GridLayoutManager(FollowersActivity.this,2);
        recyclerView_followers.setAdapter(userAdapter);

        recyclerView_followers.setLayoutManager(layoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();




        pd = new ProgressDialog(FollowersActivity.this,R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();

        idlist = new ArrayList<>();

        switch (title) {
            case "Followers" :
                getFollowers();
                break;

            case "Followings":
                getFollowings();
                break;

            case "likes":
                getLikes();
                break;

            case "Likes":
                getVideoLikes();
                break;

            case "Liked by":
                getListLikes();
                break;

        }


    }

    private void getListLikes(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ListLikes").child(personId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getVideoLikes(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("VideoLikes").child(personId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    idlist.add(snapshot1.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Likes").child(personId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    idlist.add(snapshot1.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getFollowings() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Follow").child(personId).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    idlist.add(snapshot1.getKey());

                }
                showUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Follow").child(personId).child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    idlist.add(snapshot1.getKey());

                }
                showUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
//        mUsers.clear();
//       // for (String id :idlist)
//            FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    User user =snapshot.getValue(User.class);
//                    mUsers.add(user);
//                    Log.d("list of followers", mUsers.toString());
//                    userAdapter.notifyDataSetChanged();
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = snapshot1.getValue(User.class);
                        {

                            for (String id :idlist)
                            {
                                if (user.getUserId().equals(id)){
                                    mUsers.add(user);
                                    pd.dismiss();
                                }
                            }
                        }
                    }

                }
                pd.dismiss();
                Log.d("list of followers", mUsers.toString());
                userAdapter.notifyDataSetChanged();

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