package com.yeslabapps.fictionfocus.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.CommentsAdapter;
import com.yeslabapps.fictionfocus.model.Comments;
import com.yeslabapps.fictionfocus.model.User;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText addcomment;
    private CircleImageView imageprofile;
    private ImageView post;

    private String postId;
    private String authorId;
    FirebaseUser firebaseUser;
    private RecyclerView recyclerView_comments;
    private CommentsAdapter commentsAdapter;
    private List<Comments> commentsList;

    private Comments comments;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView_comments = findViewById(R.id.recyclerview_comment);
        recyclerView_comments.setHasFixedSize(true);
        recyclerView_comments.setLayoutManager( new LinearLayoutManager(this));
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this,commentsList);
        recyclerView_comments.setAdapter(commentsAdapter);

        addcomment = findViewById(R.id.addcomment);
        imageprofile = findViewById(R.id.profile_image);
        post = findViewById(R.id.post);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getuserimage();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().trim().length()>0){
                    putcomment();
                }

            }
        });
        getcomment();



    }





    private void addNotification( String postId, String publisher,String postpersonid) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Notification");
        String notificationId= ref.push().getKey();
        String timeStamp=""+System.currentTimeMillis();

        HashMap<String,Object> map = new HashMap<>();
        map.put("userid",publisher);//who is liking the image postperson is person ehiose image is liked
        map.put("text","commented on your post");
        map.put("timeStamp","" + timeStamp);

        map.put("postid",postId);
        map.put("isPost",true);
        map.put("NotificationId",notificationId);
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Notification").child(postpersonid).child(notificationId).setValue(map);

    }


    private void getcomment() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                    Comments comment =npsnapshot.getValue(Comments.class);
                    commentsList.add(comment);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putcomment() {
        HashMap<String,Object> map = new HashMap<>();
        DatabaseReference ref= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Comments").child(postId);
        String commentid = ref.push().getKey();
        map.put("comment",addcomment.getText().toString());
        map.put("publisher",firebaseUser.getUid());
        map.put("PostId",postId);
        map.put("CommentId",commentid);
        ref.child(commentid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    addNotification(postId,firebaseUser.getUid(),authorId);
                    addcomment.setText("");
                }else{
                    Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private  void  getuserimage(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageUrl().equals("default")){
                    imageprofile.setImageResource(R.drawable.unkown_person_24);
                }else{
                    Picasso.get().load(user.getImageUrl()).into(imageprofile);}


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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