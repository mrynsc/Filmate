package com.yeslabapps.fictionfocus.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.SearchUserActivity;
import com.yeslabapps.fictionfocus.adapter.UserAdapter;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PeopleFragment extends Fragment {

    private RecyclerView recyclerviewpost;
    private UserAdapter userAdapter;
    private List<User> userList;

    private FirebaseUser firebaseUser;

    private String profileId;
    private ProgressDialog pd;

    private String myCountry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        ImageView searchButton= view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchUserActivity.class);
                startActivity(intent);
            }
        });

        ImageView filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(getContext(),R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"Random","Movie Lovers","Series Lovers","From My Country"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:readDefault();
                                break;

                            case 1:readPreferMovie();
                                break;

                            case 2:readPreferSeries();
                                break;
                            case 3:readMyCountry();
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

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }





        recyclerviewpost = view.findViewById(R.id.recyclerview_users);
        recyclerviewpost.setHasFixedSize(true);
        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerviewpost.setLayoutManager(linearLayoutManager);*/
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),userList, true);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);
        recyclerviewpost.setAdapter(userAdapter);


        recyclerviewpost.setLayoutManager(layoutManager);



        readDefault();

        userInfo();

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();


        return view;
    }


    private void userInfo(){

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    myCountry = user.getCountry();

                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void readDefault() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = np.getValue(User.class);
                        if (!user.getUserId().equals(firebaseUser.getUid()) && !user.getUsername().equals("testuser")) {
                            userList.add(user);
                            pd.dismiss();

                        }
                    }
                }
                pd.dismiss();
                Collections.shuffle(userList);
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readPreferMovie() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    User user = np.getValue(User.class);

                    if (user.getPrefer().equals("Movie") && !user.getUserId().equals(firebaseUser.getUid())){
                        userList.add(user);
                    }

                }
                Collections.reverse(userList);
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPreferSeries() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    User user = np.getValue(User.class);

                    if (user.getPrefer().equals("Series") && !user.getUserId().equals(firebaseUser.getUid())){
                        userList.add(user);
                    }

                }
                Collections.reverse(userList);
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMyCountry() {

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    User user = np.getValue(User.class);

                    if (user.getCountry().equals(myCountry) && !user.getUserId().equals(firebaseUser.getUid())
                            && !user.getUsername().equals("testuser")){
                        userList.add(user);
                    }

                }
                Collections.reverse(userList);
                userAdapter.notifyDataSetChanged();

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


