package com.yeslabapps.fictionfocus.fragments;

import static android.content.Context.MODE_PRIVATE;

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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.PhotoAdapter;
import com.yeslabapps.fictionfocus.model.FirebaseMovie;
import com.yeslabapps.fictionfocus.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SavedQuotesFragment extends Fragment {

    private PhotoAdapter photoAdapter_savedphoto;
    private List<Post> mysaved_posts;
    RecyclerView recyclerView_Saved;
    private FirebaseUser firebaseUser;
    private String profileId;

    private String profileReceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();

        View view = inflater.inflate(R.layout.fragment_saved_quotes, container, false);


        recyclerView_Saved = view.findViewById(R.id.recyclerview_saved);
        recyclerView_Saved.setHasFixedSize(true);
        recyclerView_Saved.setLayoutManager(new GridLayoutManager(getContext(),3));


        mysaved_posts = new ArrayList<>();
        photoAdapter_savedphoto = new PhotoAdapter(getContext(),mysaved_posts);
        recyclerView_Saved.setAdapter(photoAdapter_savedphoto);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        Intent intent = getActivity().getIntent();
        profileReceiver = intent.getStringExtra("profileReceiverId");
        ImageView filterWatched=view.findViewById(R.id.filterWatched);

        filterWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder =new AlertDialog.Builder(getContext(),R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"A-Z", "Z-A","Movies","Series"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:getsavedpostsA_Z();
                                break;

                            case 1:getsavedpostsZ_A();
                                break;

                            case 2: getsavedpostsMovie();
                                break;

                            case 3:getsavedpostsSeries();
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



        getsavedpostsRecently();

        return view;

    }


    private void getsavedpostsRecently() {
        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Saves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mysaved_posts.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            Post post = snapshot1.getValue(Post.class);
                            for (String id :savedId){
                                if (post.getPostId().equals(id)){
                                    mysaved_posts.add(post);
                                }
                            }
                        }
                        Collections.reverse(mysaved_posts);
                        photoAdapter_savedphoto.notifyDataSetChanged();
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


    private void getsavedpostsA_Z() {
        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Saves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mysaved_posts.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            Post post = snapshot1.getValue(Post.class);
                            for (String id :savedId){
                                if (post.getPostId().equals(id)){
                                    mysaved_posts.add(post);
                                }
                            }
                        }
                        Collections.sort(mysaved_posts, new Comparator<Post>() {
                            @Override
                            public int compare(Post post, Post post1) {
                                return post.getFilmName().compareTo(post1.getFilmName());
                            }
                        });
                        photoAdapter_savedphoto.notifyDataSetChanged();
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

    private void getsavedpostsZ_A() {
        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Saves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mysaved_posts.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            Post post = snapshot1.getValue(Post.class);
                            for (String id :savedId){
                                if (post.getPostId().equals(id)){
                                    mysaved_posts.add(post);
                                }
                            }
                        }
                        Collections.sort(mysaved_posts, new Comparator<Post>() {
                            @Override
                            public int compare(Post post, Post post1) {
                                return post1.getFilmName().compareTo(post.getFilmName());
                            }
                        });
                        photoAdapter_savedphoto.notifyDataSetChanged();
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

    private void getsavedpostsMovie() {
        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Saves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mysaved_posts.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            Post post = snapshot1.getValue(Post.class);
                            for (String id :savedId){
                                if (post.getPostId().equals(id) && post.getQuoteType().equals("Movie")){
                                    mysaved_posts.add(post);
                                }
                            }
                        }
                        Collections.sort(mysaved_posts, new Comparator<Post>() {
                            @Override
                            public int compare(Post post, Post post1) {
                                return post.getFilmName().compareTo(post1.getFilmName());
                            }
                        });
                        photoAdapter_savedphoto.notifyDataSetChanged();
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

    private void getsavedpostsSeries() {
        final List<String > savedId = new ArrayList<>();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Saves").child(profileReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np : snapshot.getChildren()){
                    savedId.add(np.getKey());

                }
                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mysaved_posts.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren())
                        {
                            Post post = snapshot1.getValue(Post.class);
                            for (String id :savedId){
                                if (post.getPostId().equals(id) && post.getQuoteType().equals("Series")){
                                    mysaved_posts.add(post);
                                }
                            }
                        }
                        Collections.sort(mysaved_posts, new Comparator<Post>() {
                            @Override
                            public int compare(Post post, Post post1) {
                                return post.getFilmName().compareTo(post1.getFilmName());
                            }
                        });
                        photoAdapter_savedphoto.notifyDataSetChanged();
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
