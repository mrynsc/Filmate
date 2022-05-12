package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.ListAdapter;
import com.yeslabapps.fictionfocus.adapter.PhotoAdapter;
import com.yeslabapps.fictionfocus.model.FilmLists;
import com.yeslabapps.fictionfocus.model.Post;
import com.yeslabapps.fictionfocus.model.User;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class ListsActivity extends AppCompatActivity {

    private FloatingActionButton addList;
    private RecyclerView recyclerView;
    private ArrayList<FilmLists> filmLists;
    private FirebaseUser firebaseUser;
    private String profileId;
    private ListAdapter listAdapter;
    private TextView username;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private ProgressDialog pd;

    private String listReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        addList=findViewById(R.id.addList);

        username = findViewById(R.id.usernameLists);

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListDialog();

            }
        });

        pd = new ProgressDialog(ListsActivity.this,R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();



        Toolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView privateList = findViewById(R.id.privateList);
        privateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListsActivity.this,PrivateListsActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = ListsActivity.this.getIntent();
        listReceiver = intent.getStringExtra("profileReceiverId");

        recyclerView = findViewById(R.id.film_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListsActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        filmLists = new ArrayList<>();

        listAdapter = new ListAdapter(filmLists,ListsActivity.this);
        recyclerView.setAdapter(listAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String data =ListsActivity.this.getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        if (listReceiver.equals(firebaseUser.getUid())){
            addList.setVisibility(View.VISIBLE);
        }else{
            addList.setVisibility(View.INVISIBLE);
        }

        if (listReceiver.equals(firebaseUser.getUid())){
            privateList.setVisibility(View.VISIBLE);
        }else{
            privateList.setVisibility(View.INVISIBLE);
        }



        userinfo();

        getMyList();

    }

    private void userinfo() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users").child(listReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


                username.setText(user.getUsername() + "'s lists");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyList() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Lists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filmLists.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    FilmLists film = np.getValue(FilmLists.class);

                    if (film.getListPublisher().equals(listReceiver) && film.getListType().equals("Public")){
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



    private void showListDialog(){
        final Dialog dialog = new Dialog(ListsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_movie_details);

        EditText titleEt = dialog.findViewById(R.id.listTitleEt);
        EditText descEt = dialog.findViewById(R.id.listDescEt);

        TextView button = dialog.findViewById(R.id.createList);

        ToggleButton toggleButton = dialog.findViewById(R.id.toggleButton);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (titleEt.getText().toString().length()>1){
                    DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Lists");

                    String listId= ref.push().getKey();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("listId",listId);
                    hashMap.put("listTitle",titleEt.getText().toString().trim());
                    hashMap.put("listTitleLower",titleEt.getText().toString().toLowerCase().trim());
                    hashMap.put("listDesc",descEt.getText().toString().trim());
                    hashMap.put("listDescLower",descEt.getText().toString().toLowerCase().trim());
                    hashMap.put("listPublisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("listType",toggleButton.getText().toString());
                    ref.child(listId).setValue(hashMap);

                    //ref.child(listId).setValue(hashMap);

                    //ref.child("id").setValue(fUser.getUid());

                    dialog.dismiss();
                }
                else{
                    StyleableToast.makeText(ListsActivity.this, "Please add title!", R.style.customToast).show();
                }



            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }


}