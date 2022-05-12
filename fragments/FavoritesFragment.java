package com.yeslabapps.fictionfocus.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.SearchActivity;
import com.yeslabapps.fictionfocus.adapter.FavoritesAdapter;
import com.yeslabapps.fictionfocus.model.FirebaseMovie;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FavoritesFragment extends Fragment {

    private FavoritesAdapter movieAdapter;
    private ArrayList<FirebaseMovie> movieArrayList;
    private RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private String profileId;
    private TextView usersFavorites;

    private String profileReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        FloatingActionButton addFavorites=view.findViewById(R.id.addFavorites);

        ImageView filterWatched=view.findViewById(R.id.filterWatched);

        usersFavorites=view.findViewById(R.id.usersFavorites);

        recyclerView = view.findViewById(R.id.movie_list);
        movieAdapter = new FavoritesAdapter(getContext(), movieArrayList);
        recyclerView.setAdapter(movieAdapter);
        movieArrayList=new ArrayList<>();
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);

        recyclerView.setLayoutManager(layoutManager);


        Intent intent = getActivity().getIntent();
        profileReceiver = intent.getStringExtra("profileReceiverId");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        if (profileReceiver.equals(firebaseUser.getUid())){
            addFavorites.setVisibility(View.VISIBLE);
        }else {
            addFavorites.setVisibility(View.INVISIBLE);
        }


        readNewest();

        addFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("ID",profileId);
                intent.putExtra("TITLE","Favorites");
                startActivity(intent);
            }
        });

        filterWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(getContext(),R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"Recently added", "First added","A-Z"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:readNewest();
                                break;

                            case 1:readOldest();
                                break;

                            case 2:readA_Z();
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

        userinfo();


        return view;

    }

    private void userinfo() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                usersFavorites.setText(user.getUsername() + "'s favorites");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readNewest() {
        movieArrayList=new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference().child("Favorites").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    FirebaseMovie post = np.getValue(FirebaseMovie.class);

                    if (post.getUserId().equals(profileReceiver)){
                        movieArrayList.add(post);

                    }
                }
                Collections.reverse(movieArrayList);
                movieAdapter=new FavoritesAdapter(getContext(),movieArrayList);
                recyclerView.setAdapter(movieAdapter);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readOldest() {
        movieArrayList=new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference().child("Favorites").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    FirebaseMovie post = np.getValue(FirebaseMovie.class);

                    if (post.getUserId().equals(profileReceiver)){
                        movieArrayList.add(post);

                    }
                }
                movieAdapter=new FavoritesAdapter(getContext(),movieArrayList);
                recyclerView.setAdapter(movieAdapter);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readA_Z() {
        movieArrayList=new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference().child("Favorites").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    FirebaseMovie post = np.getValue(FirebaseMovie.class);

                    if (post.getUserId().equals(profileReceiver)){
                        movieArrayList.add(post);

                    }
                }

                Collections.sort(movieArrayList, new Comparator<FirebaseMovie>() {
                    @Override
                    public int compare(FirebaseMovie firebaseMovie, FirebaseMovie movie) {
                        return firebaseMovie.getMovieName().compareTo(movie.getMovieName());
                    }
                });
                movieAdapter=new FavoritesAdapter(getContext(),movieArrayList);
                recyclerView.setAdapter(movieAdapter);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}