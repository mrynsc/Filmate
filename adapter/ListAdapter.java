package com.yeslabapps.fictionfocus.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.CommentActivity;
import com.yeslabapps.fictionfocus.activities.ListDetailsActivity;
import com.yeslabapps.fictionfocus.activities.ListsActivity;
import com.yeslabapps.fictionfocus.activities.VideoDetailsActivity;
import com.yeslabapps.fictionfocus.model.FilmLists;
import com.yeslabapps.fictionfocus.model.ListMovie;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;


public class ListAdapter extends  RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<FilmLists> filmLists;
    private FirebaseUser firebaseUser;
    private String profileId;

    public ListAdapter( ArrayList<FilmLists> filmLists,Context context) {
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ListDetailsActivity.class);
                intent.putExtra("barTitle",lists.getListTitle());
                intent.putExtra("barDesc",lists.getListDesc());
                intent.putExtra("profileReceiverId",lists.getListPublisher());

                //intent.putExtra("listPublisher",lists.getListPublisher());
                intent.putExtra("listPrivate",lists.getListType());
                context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("listId", lists.getListId()).apply();
                context.startActivity(intent);


            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (lists.getListPublisher().equals(firebaseUser.getUid())) {

                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Edit/Delete")
                            .setMessage("You can edit or delete the list.")
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
                            }).setNegativeButton("Edit", R.drawable.ic_baseline_create_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    showListDialog(holder,lists.getListId());
                                    dialogInterface.dismiss();
                                }
                            }).build();

                    materialDialog.show();


                }
                    return true;
            }
        });

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


        /*final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_movie_details);

        EditText titleEt = dialog.findViewById(R.id.listTitleEt);
        EditText descEt = dialog.findViewById(R.id.listDescEt);

        TextView button = dialog.findViewById(R.id.createList);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("listTitle",titleEt.getText().toString().trim());
                hashMap.put("listDesc",descEt.getText().toString().trim());

                FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Lists").child(lists.getListId()).updateChildren(hashMap);
                //ref.child(listId).setValue(hashMap);

                //ref.child("id").setValue(fUser.getUid());

                dialog.dismiss();




            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);*/




    }






    @Override
    public int getItemCount() {
        return filmLists.size();
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

    private void showListDialog(ViewHolder viewHolder,String id){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_movie_details);

        EditText titleEt = dialog.findViewById(R.id.listTitleEt);
        EditText descEt = dialog.findViewById(R.id.listDescEt);


        TextView button = dialog.findViewById(R.id.createList);

        ToggleButton toggleButton = dialog.findViewById(R.id.toggleButton);

        titleEt.setText(viewHolder.title.getText().toString());
        descEt.setText(viewHolder.desc.getText().toString());



        button.setText("Save");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (titleEt.getText().toString().length()>1){
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("listTitle",titleEt.getText().toString().trim());
                    hashMap.put("listDesc",descEt.getText().toString().trim());
                    hashMap.put("listTitleLower",titleEt.getText().toString().toLowerCase().trim());
                    hashMap.put("listDescLower",descEt.getText().toString().toLowerCase().trim());
                    hashMap.put("listType",toggleButton.getText().toString());


                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference().child("Lists").child(id).updateChildren(hashMap);
                    //ref.child(listId).setValue(hashMap);

                    //ref.child("id").setValue(fUser.getUid());


                    dialog.dismiss();


                }




            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }



}
