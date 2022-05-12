package com.yeslabapps.fictionfocus.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.AlternativeListActivity;
import com.yeslabapps.fictionfocus.activities.CommentActivity;
import com.yeslabapps.fictionfocus.activities.ListDetailsActivity;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.activities.VideoDetailsActivity;
import com.yeslabapps.fictionfocus.model.FilmLists;
import com.yeslabapps.fictionfocus.model.ListMovie;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;


public class AlternativeListAdapter extends  RecyclerView.Adapter<AlternativeListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<FilmLists> filmLists;
    private FirebaseUser firebaseUser;
    private String profileId;

    public AlternativeListAdapter( ArrayList<FilmLists> filmLists,Context context) {
        this.filmLists = filmLists;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = holder.itemView.getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        final FilmLists lists = filmLists.get(position);


        holder.title.setText(lists.getListTitle());
        holder.desc.setText(lists.getListDesc());

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("PublisherId",lists.getListPublisher());
                intent.putExtra("receiverId", lists.getListPublisher());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AlternativeListActivity.class);
                intent.putExtra("barTitle",lists.getListTitle());
                intent.putExtra("barDesc",lists.getListDesc());
                //intent.putExtra("listPublisher",lists.getListPublisher());


                context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("listId", lists.getListId()).apply();
                context.startActivity(intent);
            }
        });

        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (lists.getListPublisher().equals(firebaseUser.getUid())) {

                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(true)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference().child("Lists").child(lists.getListId())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();

                                            }
                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            }).build();

                    materialDialog.show();


                }
                return true;
            }
        });*/

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").child(lists.getListPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageUrl().equals("default")){
                    holder.userImage.setImageResource(R.drawable.unkown_person_24);
                }else{
                    Picasso.get().load(user.getImageUrl()).into(holder.userImage);
                }

                holder.username.setText(user.getUsername());





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return null!=filmLists?filmLists.size():0;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView desc;
        private TextView username;
        private CircleImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.titleList);
            desc =itemView.findViewById(R.id.descList);
            userImage=itemView.findViewById(R.id.listProfileItem);
            username=itemView.findViewById(R.id.usernameListItem);

        }



    }




}

