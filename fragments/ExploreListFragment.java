package com.yeslabapps.fictionfocus.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.ListDetailsActivity;
import com.yeslabapps.fictionfocus.activities.SearchListActivity;
import com.yeslabapps.fictionfocus.adapter.AlternativeListAdapter;
import com.yeslabapps.fictionfocus.adapter.ListAdapter;
import com.yeslabapps.fictionfocus.model.FilmLists;
import com.yeslabapps.fictionfocus.model.ListMovie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<FilmLists> filmLists;
    private FirebaseUser firebaseUser;
    private String profileId;
    private AlternativeListAdapter listAdapter;

    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore_list, container, false);


        recyclerView = view.findViewById(R.id.film_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        filmLists = new ArrayList<>();

        listAdapter = new AlternativeListAdapter(filmLists,getContext());
        recyclerView.setAdapter(listAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        ImageView searchList = view.findViewById(R.id.searchListBtn);
        searchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchListActivity.class));
            }
        });

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();

        getList();


        return view;
    }



    private void getList() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Lists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filmLists.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    FilmLists film = np.getValue(FilmLists.class);

                    if (!film.getListPublisher().equals(firebaseUser.getUid()) && film.getListType().equals("Public")){
                        filmLists.add(film);
                        pd.dismiss();
                    }

                }
                Collections.reverse(filmLists);
                pd.dismiss();
                listAdapter.notifyDataSetChanged();
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
