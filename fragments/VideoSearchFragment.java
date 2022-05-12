package com.yeslabapps.fictionfocus.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.AdapterVideo;
import com.yeslabapps.fictionfocus.adapter.PostAdapter;
import com.yeslabapps.fictionfocus.adapter.SearchVideoAdapter;
import com.yeslabapps.fictionfocus.adapter.UserAdapter;
import com.yeslabapps.fictionfocus.model.FirebaseMovie;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Post;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoSearchFragment extends Fragment {

    private RecyclerView videoRv;
    LinearLayoutManager linearLayoutManager;

    private ArrayList<ModelVideo> modelVideoArrayList;

    private SearchVideoAdapter adapterVideo;
    private AutoCompleteTextView search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_search, container, false);


        videoRv = view.findViewById(R.id.recyclerViewVideo);

       /* linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);*/

        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);

        adapterVideo = new SearchVideoAdapter(getContext(),modelVideoArrayList);
        videoRv.setAdapter(adapterVideo);

        videoRv.setHasFixedSize(true);
        videoRv.setLayoutManager(layoutManager);


        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(videoRv.getContext(), DividerItemDecoration.VERTICAL);
        videoRv.addItemDecoration(dividerItemDecoration);*/

        modelVideoArrayList = new ArrayList<>();



        search_bar=view.findViewById(R.id.searchbar);



        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                readfilms();

                searchFilm(charSequence.toString());
                deleteFilms();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;

    }

    private void deleteFilms(){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Videos");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            ModelVideo post = npsnapshot.getValue(ModelVideo.class);
                            modelVideoArrayList.clear();

                        }

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        adapterVideo = new SearchVideoAdapter(getContext(),modelVideoArrayList);
        videoRv.setAdapter(adapterVideo);


    }

    private void readfilms() {

        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Videos");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            ModelVideo post = npsnapshot.getValue(ModelVideo.class);
                            modelVideoArrayList.add(post);

                        }

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        adapterVideo = new SearchVideoAdapter(getContext(), modelVideoArrayList);
        videoRv.setAdapter(adapterVideo);
    }

    private void searchFilm(String s){

        Query queryFilm= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Videos")
                .orderByChild("searchTitle").startAt(s).endAt(s+"\uf8ff");
        queryFilm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_bar.getText().length()>2){
                    modelVideoArrayList.clear();
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        ModelVideo post = npsnapshot.getValue(ModelVideo.class);
                        modelVideoArrayList.add(post);

                    }

                }
                Collections.reverse(modelVideoArrayList);
                adapterVideo.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }


}
