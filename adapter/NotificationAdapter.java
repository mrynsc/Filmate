package com.yeslabapps.fictionfocus.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.AlternativeListActivity;
import com.yeslabapps.fictionfocus.activities.ListDetailsActivity;
import com.yeslabapps.fictionfocus.activities.PostDetailsActivity;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.activities.VideoDetailsActivity;
import com.yeslabapps.fictionfocus.fragments.PostDetailsFragment;
import com.yeslabapps.fictionfocus.fragments.VideoDetailsFragment;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.User;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context mContext;
    private List<Notification> notification_list;

    public NotificationAdapter(Context mContext, List<Notification> notification_list) {
        this.mContext = mContext;
        this.notification_list = notification_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return  new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Notification notification =notification_list.get(position);
        getuser(holder.profile_image,holder.username,notification.getUserid());
        holder.comment.setText(notification.getText());
        /*if (!notification.getPostid().equals("not a post")){
            holder.postimage.setVisibility(View.VISIBLE);
            getPostImage(holder.postimage,notification.getPostid());
        }else {
            holder.postimage.setVisibility(View.GONE);
        }*/
        String timeStamp=notification.getTimeStamp();

        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String formattedDateTime = DateFormat.format("dd MMMM k:mm",calendar).toString();

        holder.timeStampText.setText(formattedDateTime);

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StartActivity.class);
                intent.putExtra("PublisherId",notification.getUserid());
                intent.putExtra("receiverId", notification.getUserid());
                mContext.startActivity(intent);

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getText().equals("liked your post")) {
                    Intent intent = new Intent(mContext, PostDetailsActivity.class);
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("postid", notification.getPostid()).apply();
                    mContext.startActivity(intent);

                   /* mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("postid", notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, new PostDetailsFragment()).commit();*/
                }
                else if (notification.getText().equals("liked your video")){
                    Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("videoId", notification.getPostid()).apply();
                    mContext.startActivity(intent);

                }
                else if (notification.getText().equals("commented on your video")){
                    Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("videoId", notification.getPostid()).apply();
                    mContext.startActivity(intent);

                }

                else if (notification.getText().equals("commented on your post")){
                    Intent intent = new Intent(mContext, PostDetailsActivity.class);
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("postid", notification.getPostid()).apply();
                    mContext.startActivity(intent);

                   /* mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("postid", notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, new PostDetailsFragment()).commit();*/
                }


                else {
                    Intent intent = new Intent(mContext, StartActivity.class);
                    intent.putExtra("PublisherId",notification.getUserid());
                    intent.putExtra("receiverId", notification.getUserid());
                    mContext.startActivity(intent);
//                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
//                            .edit().putString("profileId", notification.getUserid()).apply();
//
//                    ((FragmentActivity)mContext).getSupportFragmentManager()
//                            .beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }



            }
        });




//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid",notification.getPostid()).apply();
////                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment())
////                        .commit();
////                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid",notification.getPostid()).apply();
////                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment())
////                        .commit();
//
//                if (notification.isPost()){
//                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", notification.getPostid()).apply();
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment())
//                            .commit();
//                }else {
//                    mContext.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("ProfileId",notification.getUserid())
//                            .apply();
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment())
//                            .commit();
//                }
//
//            }
//        });


    }

    /*private void getPostImage(final TextView post_image, String postid) {
        FirebaseDatabase.getInstance("https://quotebox-69d55-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Posts").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);


                //post_image.setText(post.getFilmName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void getuser(final ImageView profile_image, final TextView username, String userid) {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageUrl().equals("defaults")){
                    profile_image.setImageResource(R.drawable.unkown_person_24);
                }
                else{
                    Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.unkown_person_24).into(profile_image);

                }

                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }




    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile_image;
        public TextView username;
        public  TextView comment;

        public TextView timeStampText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            timeStampText=itemView.findViewById(R.id.notifyTimeStampText);
        }
    }
}