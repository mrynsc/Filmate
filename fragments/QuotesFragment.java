package com.yeslabapps.fictionfocus.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.RegisterActivity;
import com.yeslabapps.fictionfocus.activities.SearchQuotesActivity;
import com.yeslabapps.fictionfocus.adapter.PostAdapter;
import com.yeslabapps.fictionfocus.model.LoadingDialog;
import com.yeslabapps.fictionfocus.model.Post;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuotesFragment extends Fragment {


    RecyclerView recyclerviewpost;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followinglist;
    private ImageView goToSearchQuotes;
    private ImageView filterImage;


    private Toolbar toolbar;
   // private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseUser firebaseUser;
    private String profileId;
    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();

        View view = inflater.inflate(R.layout.fragment_quotes, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }



        recyclerviewpost = view.findViewById(R.id.recyclerview_posts);
        goToSearchQuotes = getActivity().findViewById(R.id.searchQuotes);
        recyclerviewpost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerviewpost.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postList, true);
        recyclerviewpost.setAdapter(postAdapter);

        filterImage=getActivity().findViewById(R.id.filterImage);

        toolbar=view.findViewById(R.id.toolbarHome);


        /*swipeRefreshLayout=view.findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recyclerviewpost.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setStackFromEnd(true);
                linearLayoutManager.setReverseLayout(true);
                recyclerviewpost.setLayoutManager(linearLayoutManager);
                postList = new ArrayList<>();
                postAdapter = new PostAdapter(getContext(),postList, true);
                recyclerviewpost.setAdapter(postAdapter);

                readRandom();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

*/




        filterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(getContext(),R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"Only From Followings","Random","Movies","Series"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:checkForFriends();
                                break;
                            case 1:readRandom();
                                break;
                            case 2:readMovies();
                                break;
                            case 3:readSeries();
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



        followinglist = new ArrayList<>();

        goToSearchQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent(getContext(), SearchQuotesActivity.class));


            }
        });



        readDefault();


        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();






        return view;

    }




    private void readDefault() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Posts").limitToFirst(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        Post post = np.getValue(Post.class);

                        postList.add(post);
                        pd.dismiss();
                    }


                }
                Collections.shuffle(postList);
                pd.dismiss();
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRandom() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Posts").limitToFirst(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        Post post = np.getValue(Post.class);

                        postList.add(post);
                    }

                }
                Collections.shuffle(postList);
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMovies() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Posts").limitToFirst(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    Post post = np.getValue(Post.class);


                    if (post.getQuoteType().equals("Movie")){
                        postList.add(post);
                        pd.dismiss();
                    }

                }
                Collections.reverse(postList);
                //Collections.shuffle(postList);
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSeries() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Posts").limitToFirst(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    Post post = np.getValue(Post.class);


                    if (post.getQuoteType().equals("Series")){
                        postList.add(post);
                    }

                }
                Collections.reverse(postList);
                //Collections.shuffle(postList);
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }






    /*private void checkForRandom() {


        FirebaseDatabase.getInstance("https://quotebox-69d55-default-rtdb.europe-west1.firebasedatabase.app/")


               .getReference().child("Likes").child().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followinglist.clear();
                for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                    followinglist.add(npsnapshot.getKey());

                }
                readRandom();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void readForFriends() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                    Post post = npsnapshot.getValue(Post.class);
                    for (String id : followinglist){
                        if (post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }


                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForFriends() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followinglist.clear();
                for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                    followinglist.add(npsnapshot.getKey());

                }
                readForFriends();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}