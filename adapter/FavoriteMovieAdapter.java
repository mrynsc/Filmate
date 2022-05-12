package com.yeslabapps.fictionfocus.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.model.FavoriteMovie;
import com.yeslabapps.fictionfocus.model.User;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteMovieAdapter  extends  RecyclerView.Adapter<FavoriteMovieAdapter.ViewHolder>{

    private Context mcontext;
    private List<FavoriteMovie> mUsers;
    private  boolean isfragment;
    private FirebaseUser firebaseUser;

    private String profileId;

    public FavoriteMovieAdapter(Context montext, List<FavoriteMovie> mUsers, boolean isfragment) {
        this.mcontext = montext;
        this.mUsers =  mUsers;
        this.isfragment = isfragment;
    }

    @NonNull
    @Override
    public FavoriteMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
        return  new FavoriteMovieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteMovieAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final FavoriteMovie user = mUsers.get(position);


        String data = holder.itemView.getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId", "");
        if (data.equals("")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
        }

        holder.btnfollow.setVisibility(View.VISIBLE);
        //holder.username.setText(user.getFavoriteUserName());
        //holder.countryItemText.setText(user.getFavoriteUserCountry());


        //Picasso.get().load(user.getFavoriteUserImage()).placeholder(R.drawable.unkown_person_24).into(holder.profileimage);

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").child(user.getFavoriteUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user2 = snapshot.getValue(User.class);

                if(user2.getImageUrl().equals("default")){
                    holder.profileimage.setImageResource(R.drawable.unkown_person_24);
                }else{
                    Picasso.get().load(user2.getImageUrl()).into(holder.profileimage);
                }
                holder.username.setText(user2.getUsername());
                holder.countryItemText.setText(user2.getCountry());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        isfollowed(user.getFavoriteUserId(), holder.btnfollow);
        if (user.getFavoriteUserId().equals(firebaseUser.getUid())) {
            holder.btnfollow.setVisibility(View.GONE);
        }
        holder.btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnfollow.getText().toString().equals("Follow")) {
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getFavoriteUserId())
                            .setValue(true);
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(user.getFavoriteUserId()).child("Followers")
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(firebaseUser.getUid(), user.getFavoriteUserId(), "has started following you");


                } else {
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getFavoriteUserId())
                            .removeValue();
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(user.getFavoriteUserId()).child("Followers")
                            .child(firebaseUser.getUid()).removeValue();
                    addNotification(firebaseUser.getUid(), user.getFavoriteUserId(), "has unfollowed you");

                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("receiverId", user.getFavoriteUserId());
                //intent.putExtra("name", user.getUsername());
                intent.putExtra("PublisherId", user.getFavoriteUserId());
                mcontext.startActivity(intent);
//                if (isfragment){
//                    mcontext.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("ProfileId",user.getUserId()).apply();
////                    Intent intent = new Intent(mcontext, Start_activity.class);
////                    intent.putExtra("PublisherId",user.getUserId());
////                    mcontext.startActivity(intent);
//                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment())
//                         .commit();
//
//                }else {
//                    Intent intent = new Intent(mcontext,Start_activity.class);
//                    intent.putExtra("PublisherId",user.getUserId());
//                    mcontext.startActivity(intent);
//                }
            }
        });

    }




    private void addNotification(  String publisher,String postpersonid,String text) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Notification");
        String notificationId= ref.push().getKey();
        String timeStamp=""+System.currentTimeMillis();

        HashMap<String,Object> map = new HashMap<>();
        map.put("userid",publisher);//who is liking the image postperson is person whose image is liked
        map.put("text",text);
        map.put("postid","not a post");
        map.put("isPost",false);
        map.put("timeStamp","" + timeStamp);

        map.put("NotificationId",notificationId);
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Notification").child(postpersonid).child(notificationId).setValue(map);

    }

    private void isfollowed(final String userId, final Button btnfollow) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(firebaseUser.getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userId).exists()){
                    btnfollow.setText("Followıng");
                }else
                    btnfollow.setText("Follow");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return  mUsers.size();

    }

    public  class  ViewHolder extends  RecyclerView.ViewHolder {

        public CircleImageView profileimage;
        public TextView username;
        public TextView countryItemText;
        public Button btnfollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            btnfollow = itemView.findViewById(R.id.btnfollow);
            countryItemText=itemView.findViewById(R.id.countryItemText);


        }
    }


}

