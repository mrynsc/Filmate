package com.yeslabapps.fictionfocus.fragments;

import static android.content.Context.MODE_PRIVATE;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.AlternativeListAdapter;
import com.yeslabapps.fictionfocus.adapter.PostAdapter;
import com.yeslabapps.fictionfocus.adapter.SearchVideoAdapter;
import com.yeslabapps.fictionfocus.model.FilmLists;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchListFragment extends Fragment {

    private RecyclerView recyclerView;

    private ArrayList<FilmLists> filmLists;

    private AlternativeListAdapter listAdapter;
    private AutoCompleteTextView search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        listAdapter = new AlternativeListAdapter(filmLists,getContext());
        recyclerView.setAdapter(listAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        filmLists = new ArrayList<>();


        search_bar=view.findViewById(R.id.searchbar);

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                readLists();

                searchLists(charSequence.toString());
                deleteLists();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });






        return view;

    }


    private void deleteLists(){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Lists");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            FilmLists post = npsnapshot.getValue(FilmLists.class);
                            filmLists.clear();

                        }

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        listAdapter = new AlternativeListAdapter(filmLists,getContext());
        recyclerView.setAdapter(listAdapter);


    }

    private void readLists() {

        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Lists");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            FilmLists post = npsnapshot.getValue(FilmLists.class);
                            if (post.getListType().equals("Public")){
                                filmLists.add(post);

                            }

                        }

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        listAdapter = new AlternativeListAdapter(filmLists,getContext());
        recyclerView.setAdapter(listAdapter);
    }

    private void searchLists(String s){

        Query queryFilm= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Lists")
                .orderByChild("listTitleLower").startAt(s).endAt(s+"\uf8ff");
        queryFilm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_bar.getText().length()>2){
                    filmLists.clear();
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        FilmLists post = npsnapshot.getValue(FilmLists.class);

                        if (post.getListType().equals("Public")){
                            filmLists.add(post);

                        }


                    }

                }
                Collections.reverse(filmLists);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

}
