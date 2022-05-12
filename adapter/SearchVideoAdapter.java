package com.yeslabapps.fictionfocus.adapter;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.FragmentActivity;
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
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.CommentActivity;
import com.yeslabapps.fictionfocus.activities.VideoDetailsActivity;
import com.yeslabapps.fictionfocus.fragments.PostDetailsFragment;
import com.yeslabapps.fictionfocus.fragments.VideoDetailsFragment;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.Post;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchVideoAdapter extends RecyclerView.Adapter<SearchVideoAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<ModelVideo> modelVideoArrayList;
    private FirebaseUser firebaseUser;

    public SearchVideoAdapter(Context mContext, ArrayList<ModelVideo> modelVideoArrayList) {
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

        //holder.mThumbnail.setImageResource(R.drawable.person_24);

        String thumbnail=modelVideoArrayList.get(position).getThumbnail();

        if (thumbnail !=null && !thumbnail.equals("")){
            Picasso.get().load(modelVideo.getThumbnail()).into(holder.mThumbnail);
            holder.loading.setVisibility(View.GONE);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("videoId", modelVideo.getId()).apply();
                mContext.startActivity(intent);

                /*Intent intent = new Intent(mContext,VideoDetailsActivity.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);*/

            }
        });

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
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
