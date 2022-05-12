package com.yeslabapps.fictionfocus.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.PostDetailsActivity;
import com.yeslabapps.fictionfocus.fragments.PostDetailsFragment;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.Post;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private Context mcontext;
    private List<Post> mpost;
    private FirebaseUser firebaseUser;

    public PhotoAdapter(Context mcontext, List<Post> mpost) {
        this.mcontext = mcontext;
        this.mpost = mpost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.photo_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Post post = mpost.get(position);

        Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.loading2).into(holder.postimage);
        holder.filmname.setText(post.getFilmName());
        holder.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, PostDetailsActivity.class);
                mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("postid", post.getPostId()).apply();
                mcontext.startActivity(intent);

                /*mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid",post.getPostId()).apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment())
                        .commit();*/

            }

        });




        holder.postimage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (post.getPublisher().equals(firebaseUser.getUid())){

                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) mcontext)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(true)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    deletePost(post);
                                    deleteNotification(post.getPostId(),post.getPublisher());
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            }).build();

                    materialDialog.show();



                   /* final AlertDialog ad =  new AlertDialog.Builder(mcontext).create();
                    ad.setTitle("Are you sure wou want to delete?");
                    ad.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
                    ad.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            deletePost(post);
                            deleteNotification(post.getPostId(),post.getPublisher());

                            ad.dismiss();


                        }

                    });

                    ad.show();*/
                }

                return true;
            }
        });


    }

    private void deleteNotification(final String postId, final String postperonid) {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notification").child(postperonid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Notification notification = snapshot1.getValue(Notification.class);
                    if (notification.getPostid().equals(postId)){
                        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference().child("Notification").child(postperonid).child(notification.getNotificationId()).removeValue();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void deletePost(Post post){
        final String deletePostId=post.getPostId();
        String deleteImageUrl=post.getImageUrl();

        StorageReference reference= FirebaseStorage.getInstance()
                .getReferenceFromUrl(deleteImageUrl);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DatabaseReference databaseReference= FirebaseDatabase
                        .getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Posts");
                databaseReference.child(deletePostId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        StyleableToast.makeText(mcontext, "Quote Deleted!", R.style.customToast).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mcontext, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


    }



    @Override
    public int getItemCount() {
        return mpost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postimage;
        public TextView filmname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filmname=itemView.findViewById(R.id.post_text);
            postimage = itemView.findViewById(R.id.post_image);
        }
    }
}
