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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.PostAdapter;
import com.yeslabapps.fictionfocus.model.Post;

import java.util.ArrayList;
import java.util.List;

public class FilmSearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private AutoCompleteTextView search_bar;
    private List<Post> mPosts;
    private PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_film_search, container, false);



        recyclerView = view.findViewById(R.id.recyclerview_posts);
        search_bar = view.findViewById(R.id.searchbar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPosts = new ArrayList<>();


        /*Bundle bundle =this.getArguments();
        String data = bundle.getString("key");
        search_bar.setText(data);*/





        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                readFilms();

                searchFilm(charSequence.toString());
                deleteFilms();

                if (search_bar.getText().toString().length() == 1 && search_bar.getTag().toString().equals("true"))
                {
                    search_bar.setTag("false");
                    search_bar.setText(search_bar.getText().toString().toLowerCase());
                    search_bar.setSelection(search_bar.getText().toString().length());
                }
                if(search_bar.getText().toString().length() == 0)
                {
                    search_bar.setTag("true");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        return view;




    }




    private void deleteFilms(){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            Post post = npsnapshot.getValue(Post.class);
                            mPosts.clear();

                        }

                        postAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        postAdapter = new PostAdapter(getContext(), mPosts, true);
        recyclerView.setAdapter(postAdapter);


    }

    private void readFilms() {
        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            Post post = npsnapshot.getValue(Post.class);
                            mPosts.add(post);

                        }
//                if (TextUtils.isEmpty(search_bar.getText().toString())){
//                    mUsers.clear();
//                    User user = snapshot.getValue(User.class);
//                    mUsers.add(user);
//                }
                        postAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        postAdapter = new PostAdapter(getContext(), mPosts, true);
        recyclerView.setAdapter(postAdapter);
    }
    private void searchFilm(String s){

        Query queryFilm= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Posts")
                .orderByChild("searchFilm").startAt(s).endAt(s+"\uf8ff");
        queryFilm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_bar.getText().toString().length()>2){
                    mPosts.clear();
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        Post post = npsnapshot.getValue(Post.class);
                        mPosts.add(post);

                    }
                }


                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}
