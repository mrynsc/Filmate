package com.yeslabapps.fictionfocus.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.FriendChatAdapter;
import com.yeslabapps.fictionfocus.model.Chatlist;
import com.yeslabapps.fictionfocus.model.OnItemClick;
import com.yeslabapps.fictionfocus.model.Token;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private FriendChatAdapter userAdapter;
    private List<User> mUsers;
    FrameLayout frameLayout;
    TextView es_descp, es_title;


    FirebaseUser fuser;
    DatabaseReference reference;

    private List<Chatlist> usersList;
    static OnItemClick onItemClick;
    private ProgressDialog pd;

    public static ChatsFragment newInstance(OnItemClick click) {

        onItemClick = click;
        Bundle args = new Bundle();

        ChatsFragment fragment = new ChatsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        frameLayout = view.findViewById(R.id.es_layout);
        es_descp = view.findViewById(R.id.es_descp);
        es_title = view.findViewById(R.id.es_title);







        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        usersList.add(chatlist);


                        if(usersList.size()==0){
                            frameLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            frameLayout.setVisibility(View.GONE);
                        }
                    }



                }


                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //updateToken(String.valueOf(FirebaseMessaging.getInstance().getToken()));

        ImageView filterFriend=view.findViewById(R.id.filterFriend);
        filterFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs = {"A-Z","Z-A"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:chatListA_Z();
                                break;
                            case 1:chatListZ_A();
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

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();




        return view;
    }





    /*private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }*/

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        User user = snapshot.getValue(User.class);
                        for (Chatlist chatlist : usersList){

                            if (user!= null && user.getUserId()!=null && chatlist!=null && chatlist.getId()!= null &&
                                    user.getUserId().equals(chatlist.getId())){
                                mUsers.add(user);
                                pd.dismiss();
                            }

                        }

                    }

                }
                pd.dismiss();
                //Collections.reverse(mUsers);
                userAdapter = new FriendChatAdapter(getContext(), onItemClick,mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void chatListA_Z() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList){
                        if (user!= null && user.getUserId()!=null && chatlist!=null && chatlist.getId()!= null &&
                                user.getUserId().equals(chatlist.getId())){
                            mUsers.add(user);

                        }

                    }

                }
                Collections.sort(mUsers, new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                        return user.getUsername().compareTo(t1.getUsername());
                    }
                });
                userAdapter = new FriendChatAdapter(getContext(), onItemClick,mUsers, true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chatListZ_A() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList){
                        if (user!= null && user.getUserId()!=null && chatlist!=null && chatlist.getId()!= null &&
                                user.getUserId().equals(chatlist.getId())){
                            mUsers.add(user);

                        }

                    }


                }
                Collections.sort(mUsers, new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                        return t1.getUsername().compareTo(user.getUsername());
                    }
                });
                userAdapter = new FriendChatAdapter(getContext(), onItemClick,mUsers, true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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