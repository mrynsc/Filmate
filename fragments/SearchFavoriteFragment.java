package com.yeslabapps.fictionfocus.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.FavoriteMovieAdapter;
import com.yeslabapps.fictionfocus.model.FavoriteMovie;

import java.util.ArrayList;
import java.util.List;

public class SearchFavoriteFragment extends Fragment {


    private RecyclerView recyclerView;
    private AutoCompleteTextView search_bar;
    private List<FavoriteMovie> mUsers;
    private FavoriteMovieAdapter userAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_favorite, container, false);


        recyclerView = view.findViewById(R.id.recyclerview_users);
        search_bar = view.findViewById(R.id.searchbar);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        mUsers = new ArrayList<>();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                readusers();
                searchuser(charSequence.toString());
                deleteUsers();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }

    private void deleteUsers(){

        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().
                child("ForProfile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            FavoriteMovie user = npsnapshot.getValue(FavoriteMovie.class);
                            mUsers.clear();

                        }
//                if (TextUtils.isEmpty(search_bar.getText().toString())){
//                    mUsers.clear();
//                    User user = snapshot.getValue(User.class);
//                    mUsers.add(user);
//                }
                        userAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        userAdapter = new FavoriteMovieAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

    }



    private void readusers() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference().child("ForProfile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            FavoriteMovie user = npsnapshot.getValue(FavoriteMovie.class);
                            mUsers.add(user);

                        }
//                if (TextUtils.isEmpty(search_bar.getText().toString())){
//                    mUsers.clear();
//                    User user = snapshot.getValue(User.class);
//                    mUsers.add(user);
//                }
                        userAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        userAdapter = new FavoriteMovieAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);
    }
    private void  searchuser(String s) {
        Query queryuser = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ForProfile")
                .orderByChild("favoriteMovieNameLower").startAt(s).endAt(s + "\uf8ff");
        queryuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_bar.getText().length()>2){
                    mUsers.clear();
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        FavoriteMovie user = npsnapshot.getValue(FavoriteMovie.class);
                        mUsers.add(user);

                    }

                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}


