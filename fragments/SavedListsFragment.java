package com.yeslabapps.fictionfocus.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.SavedListsActivity;
import com.yeslabapps.fictionfocus.adapter.AlternativeListAdapter;
import com.yeslabapps.fictionfocus.model.FilmLists;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SavedListsFragment extends Fragment {

    private AlternativeListAdapter alternativeListAdapter;
    private ArrayList<FilmLists> filmLists;
    private RecyclerView recyclerView_Saved;
    private FirebaseUser firebaseUser;
    private String profileId;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private String profileReceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();

        View view = inflater.inflate(R.layout.fragment_saved_lists, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView_Saved = view.findViewById(R.id.savedListRecycler);
        recyclerView_Saved.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView_Saved.setLayoutManager(linearLayoutManager);

        filmLists = new ArrayList<>();

        alternativeListAdapter = new AlternativeListAdapter(filmLists,getContext());
        recyclerView_Saved.setAdapter(alternativeListAdapter);



        Intent intent = getActivity().getIntent();
        profileReceiver = intent.getStringExtra("profileReceiverId");


        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }





        getSavedLists();

        return view;


    }


    private void getSavedLists() {
        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ListSaves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Lists").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        filmLists.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            FilmLists post = snapshot1.getValue(FilmLists.class);
                            for (String id :savedId){
                                if (post.getListId().equals(id)){
                                    filmLists.add(post);
                                }
                            }
                        }
                        Collections.reverse(filmLists);
                        alternativeListAdapter.notifyDataSetChanged();
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



}


