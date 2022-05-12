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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.activities.VideoDetailsActivity;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;

public class AlternativeVideoAdapter extends RecyclerView.Adapter<AlternativeVideoAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ModelVideo> modelVideoArrayList;
    private FirebaseUser firebaseUser;

    public AlternativeVideoAdapter(Context mContext, ArrayList<ModelVideo> modelVideoArrayList) {
        this.mContext = mContext;
        this.modelVideoArrayList = modelVideoArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_search_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final ModelVideo modelVideo = modelVideoArrayList.get(position);

        holder.searchName.setText(modelVideo.getTitle());
        holder.searchDesc.setText(modelVideo.getVideoDesc());

        holder.mThumbnail.setImageResource(R.drawable.person_24);

        String thumbnail = modelVideoArrayList.get(position).getThumbnail();

        if (thumbnail !=null && !thumbnail.equals("")){
            Glide.with(mContext).load(thumbnail).dontAnimate().into(holder.mThumbnail);
        }
        holder.loading.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("videoId", modelVideo.getId()).apply();
                mContext.startActivity(intent);
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (modelVideo.getUserId().equals(firebaseUser.getUid())){

                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) mContext)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(true)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    deleteVideo(modelVideo);
                                    deleteNotification(modelVideo.getId(), modelVideo.getUserId());
                                    ((Activity)mContext).finish();
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            }).build();

                    materialDialog.show();






                    /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure you want to delete video?")
                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteVideo(modelVideo);
                                    deleteNotification(modelVideo.getId(), modelVideo.getUserId());
                                    ((Activity)mContext).finish();

                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();*/

                }
                return true;
            }
        });


        /*FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").child(modelVideo.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                holder.usernameVideo.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

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

    private void deleteVideo(ModelVideo modelVideo) {
        // used to delete the file
        final String videoID=modelVideo.getId();
        String videoUrl=modelVideo.getVideoUrl();

        // delete from firebase
        StorageReference reference= FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        reference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //deleted from firebase storage

                        //delete from firebase database
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Videos");
                        databaseReference.child(videoID)
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Successfully deleted from firebase database
                                StyleableToast.makeText(mContext, "Video Deleted!", R.style.customToast).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull  Exception e) {
                                // failed in deleteing from storage
                                Toast.makeText(mContext, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });




    }



    @Override
    public int getItemCount() {
        return null!=modelVideoArrayList?modelVideoArrayList.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView searchName;
        private TextView searchDesc;
        private TextView usernameVideo;
        private ImageView mThumbnail ;
        private ProgressBar loading;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searchName=itemView.findViewById(R.id.movieName);
            searchDesc=itemView.findViewById(R.id.descName);
            usernameVideo=itemView.findViewById(R.id.usernameVideo);
            loading=itemView.findViewById(R.id.exploreLoadingProgressBar);
            mThumbnail=itemView.findViewById(R.id.thumbnailImage);

        }
    }
}
