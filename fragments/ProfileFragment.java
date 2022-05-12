package com.yeslabapps.fictionfocus.fragments;

import static android.content.Context.MODE_PRIVATE;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.UnityAds;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.ChatActivity;
import com.yeslabapps.fictionfocus.activities.EditProfileActivity;
import com.yeslabapps.fictionfocus.activities.FollowersActivity;
import com.yeslabapps.fictionfocus.activities.ListsActivity;
import com.yeslabapps.fictionfocus.activities.MyVideosActivity;
import com.yeslabapps.fictionfocus.activities.OptionActivity;
import com.yeslabapps.fictionfocus.activities.ProfileDetailsActivity;
import com.yeslabapps.fictionfocus.activities.ReportUserActivity;
import com.yeslabapps.fictionfocus.activities.SavedListsActivity;
import com.yeslabapps.fictionfocus.activities.SavedQuotesActivity;
import com.yeslabapps.fictionfocus.activities.SavedVideosActivity;
import com.yeslabapps.fictionfocus.activities.SearchActivity;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.activities.UserQuotesActivity;
import com.yeslabapps.fictionfocus.adapter.ProfileHorizontalAdapter;

import com.yeslabapps.fictionfocus.model.FavoriteMovie;
import com.yeslabapps.fictionfocus.model.FilmLists;
import com.yeslabapps.fictionfocus.model.FirebaseMovie;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Post;
import com.yeslabapps.fictionfocus.model.Token;
import com.yeslabapps.fictionfocus.model.User;
import com.yeslabapps.fictionfocus.notify.Data;
import com.yeslabapps.fictionfocus.notify.Sender;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.muddz.styleabletoast.StyleableToast;

public class ProfileFragment extends Fragment {


    private CircleImageView imageprofile;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView username;
    private TextView bio;
    private TextView videos;

    private FirebaseUser firebaseUser;
    private String profileId;
    private Button editprofile;

    private TextView moviesText;
    private TextView seriesText;
    private TextView favoritesText;

    private TextView userCountry;

    private TextView listText;

    private ProfileHorizontalAdapter profileHorizontalAdapter;
    private ArrayList<FavoriteMovie> movieArrayList;
    private RecyclerView recyclerViewHorizontal;
    LinearLayoutManager horizontalLayout;

    //String userNameChat;
    private String userIdChat;
    private TextView userPrefer;
    private ProgressDialog pd;

    private String GAME_ID = "4677213";
    private String INTERSTITIAL_ID_PROFILE ="EditProfileAlternative";

    private boolean test = false;

    private DatabaseReference feedbackRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }



        ImageView dmButton=view.findViewById(R.id.dmButton);
        TextView addHorizontal=view.findViewById(R.id.addHorizontal);
        ImageView moreOptions=view.findViewById(R.id.moreOptions);
        ImageView savedQuotes=view.findViewById(R.id.saved_quotes);
        userPrefer=view.findViewById(R.id.userPreferImage);
        imageprofile = view.findViewById(R.id.profile_image);

        videos=view.findViewById(R.id.videos);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        username = view.findViewById(R.id.username);
        bio = view.findViewById(R.id.bio);
        editprofile = view.findViewById(R.id.edit_profile);
        moviesText=view.findViewById(R.id.movies);
        favoritesText=view.findViewById(R.id.favorites);
        seriesText=view.findViewById(R.id.series);
        userCountry=view.findViewById(R.id.country);

        listText = view.findViewById(R.id.lists);


        recyclerViewHorizontal = view.findViewById(R.id.movie_list_profile);
        profileHorizontalAdapter = new ProfileHorizontalAdapter(getContext(), movieArrayList);
        recyclerViewHorizontal.setAdapter(profileHorizontalAdapter);
        movieArrayList=new ArrayList<>();
        horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        recyclerViewHorizontal.setLayoutManager(horizontalLayout);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewHorizontal.getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerViewHorizontal.addItemDecoration(dividerItemDecoration);


        moviesText=view.findViewById(R.id.movies);
        favoritesText=view.findViewById(R.id.favorites);
        seriesText=view.findViewById(R.id.series);
        userCountry=view.findViewById(R.id.country);




        Intent intent = getActivity().getIntent();
        //userNameChat = intent.getStringExtra("name");
        userIdChat= intent.getStringExtra("receiverId");

        UnityAds.initialize(getContext(),GAME_ID,test);



        addHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieArrayList.size()<5) {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("ID", profileId);
                    intent.putExtra("TITLE", "Horizontal");
                    intent.putExtra("userCountry",userCountry.getText().toString());
                    intent.putExtra("userName",username.getText().toString());

                    startActivity(intent);
                }else{
                    StyleableToast.makeText(getContext(), "You can add up to 5 items!", R.style.customToast).show();
                }

            }
        });

        if (profileId.equals(firebaseUser.getUid())){
            addHorizontal.setVisibility(View.VISIBLE);

        }else {
            addHorizontal.setVisibility(View.INVISIBLE);
        }

        if (profileId.equals(firebaseUser.getUid())){
            dmButton.setVisibility(View.INVISIBLE);
        }
        else {
            dmButton.setVisibility(View.VISIBLE);
        }


        dmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!username.getText().toString().equals("Filmate")){
                    Intent intent1=new Intent(getContext(),ChatActivity.class);
                    intent1.putExtra("receiverId",userIdChat);
                    startActivity(intent1);
                }else{
                    showFeedbackDialog();
                }
                

            }

        });



        if (profileId.equals(firebaseUser.getUid())){
            savedQuotes.setVisibility(View.VISIBLE);
        }else {
            savedQuotes.setVisibility(View.GONE);
        }


        savedQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getContext(),SavedListsActivity.class);
                intent1.putExtra("profileReceiverId",profileId);
                startActivity(intent1);
                /*SavedQuotesFragment nextFrag= new SavedQuotesFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();*/
            }
        });




        if (profileId.equals(firebaseUser.getUid())){
            moreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu1 = new PopupMenu(getContext(),moreOptions);
                    popupMenu1.getMenuInflater().inflate(R.menu.popup_profile,popupMenu1.getMenu());
                    popupMenu1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.editProfilePopItem:
                                    if (UnityAds.isInitialized()){
                                        UnityAds.show((Activity) getContext(),INTERSTITIAL_ID_PROFILE);
                                    }
                                    Intent intent2= new Intent(getContext(),EditProfileActivity.class);
                                    startActivity(intent2);
                                    break;
                                case R.id.goProfileDetails:

                                    Intent intent1 = new Intent(getContext(),ProfileDetailsActivity.class);
                                    intent1.putExtra("profileReceiverId",profileId);
                                    startActivity(intent1);
                                    break;
                            }
                            return true;
                        }

                    });

                    popupMenu1.show();

                }
            });

        }else {
            moreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), moreOptions);

                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.reportUser:
                                    Intent intent1 = new Intent(getContext(), ReportUserActivity.class);
                                    intent1.putExtra("userNameReport",username.getText().toString());
                                    startActivity(intent1);
                                    break;

                                case R.id.blockUser:
                                    Intent intent2 = new Intent(getContext(),ChatActivity.class);
                                    intent2.putExtra("receiverId",userIdChat);

                                    StyleableToast.makeText(getContext(), "You can block users by pressing the icon in the upper right. If you block them, they won't be able to message you.", R.style.chatToast).show();
                                    startActivity(intent2);

                                    break;
                            }

                            //Toast.makeText(getContext(), "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });

                    popupMenu.show();
                }
            });
        }


        if (profileId.equals(firebaseUser.getUid())){
            editprofile.setVisibility(View.INVISIBLE);
        }else{
            checkFollowingStatus();
        }

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();

        userInfo();
        countFollowersAndFollowing();
        countVideos();
        countPost();
        countFavorites();
        countSeries();
        countMovies();
        readHorizontal();
        countLists();

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttontext = editprofile.getText().toString();
                if (buttontext.equals("EDIT PROFILE")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                    //gotoeditprofile
                }else {
                    if (editprofile.getText().toString().equals("Follow")){
                        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(profileId)
                                .setValue(true);
                        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(profileId).child("Followers")
                                .child(firebaseUser.getUid()).setValue(true);
                        addNotification(firebaseUser.getUid(),profileId,"has started following you");


                    }else {
                        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(profileId)
                                .removeValue();
                        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(profileId).child("Followers")
                                .child(firebaseUser.getUid()).removeValue();
                        addNotification(firebaseUser.getUid(),profileId,"has unfollowed you");


                    }



                }
            }
        });

        LinearLayout quoteLayout= view.findViewById(R.id.quote_layout);
        LinearLayout videoLayout= view.findViewById(R.id.video_layout);
        LinearLayout followersLayout= view.findViewById(R.id.followers_layout);
        LinearLayout followingsLayout= view.findViewById(R.id.followings_layout);
        LinearLayout movieLayout= view.findViewById(R.id.movies_layout);
        LinearLayout seriesLayout= view.findViewById(R.id.series_layout);
        LinearLayout favoriteLayout= view.findViewById(R.id.favorites_layout);
        LinearLayout listsLayout= view.findViewById(R.id.lists_layout);

        listsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listIntent =new Intent(getContext(), ListsActivity.class);
                listIntent.putExtra("profileReceiverId",profileId);

                startActivity(listIntent);
            }
        });


        quoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(getContext(), UserQuotesActivity.class);
                intent1.putExtra("profileReceiverId",profileId);
                startActivity(intent1);
                /*UserQuotesFragment userQuotesFragment= new UserQuotesFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, userQuotesFragment, "findThisQuotesFragment")
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), MyVideosActivity.class);
                intent.putExtra("profileReceiverId",profileId);
                startActivity(intent);
            }
        });

        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("ID",profileId);
                intent.putExtra("TITLE","Followers");
                startActivity(intent);
            }
        });

        followingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("ID",profileId);
                intent.putExtra("TITLE","Followings");
                startActivity(intent);
            }
        });

        movieLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
                intent.putExtra("profileReceiverId",profileId);
                startActivity(intent);
            }
        });

        seriesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
                intent.putExtra("profileReceiverId",profileId);
                startActivity(intent);
            }
        });

        favoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
                intent.putExtra("profileReceiverId",profileId);

                startActivity(intent);
            }
        });


        loadInterstitialId();





        return view;

    }



    /*private void openLink(String sAppLink,String sPackage,String sWebLink){
        try {
            Uri uri = Uri.parse(sAppLink);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(sPackage);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (ActivityNotFoundException activityNotFoundException){
            Uri uri =Uri.parse(sWebLink);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void showSocial(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_social);

        TextView twitterBtn = dialog.findViewById(R.id.goTwitter);
        TextView instagramBtn = dialog.findViewById(R.id.goInstagram);


        if (instagramLink==null){
            instagramBtn.setText("No Instagram");

        }else if (instagramLink.equals("")){
            instagramBtn.setText("No Instagram");
        }else{
            instagramBtn.setText(username.getText().toString()+"'s Instagram");
            instagramBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sAppLink= instagramLink;
                    String sPackage ="com.instagram.android";

                    openLink(sAppLink,sPackage,sAppLink);
                }
            });
        }

        if (twitterLink==null){
            twitterBtn.setText("No Twitter");

        }else if (twitterLink.equals("")){
            twitterBtn.setText("No Twitter");
        }else{
            twitterBtn.setText(username.getText().toString()+"'s Twitter");
            twitterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sAppLink= twitterLink;
                    String sPackage ="com.twitter.android";

                    openLink(sAppLink,sPackage,sAppLink);
                }
            });
        }


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
*/

    private void loadInterstitialId(){
        if (UnityAds.isInitialized()){
            UnityAds.load(INTERSTITIAL_ID_PROFILE);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(INTERSTITIAL_ID_PROFILE);
                }
            },5000);
        }
    }


    private void showFeedbackDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_feedback);

        MaterialEditText editText = dialog.findViewById(R.id.feedbackEt);
        Button button = dialog.findViewById(R.id.sendFeedback);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length()>0){
                    dialog.dismiss();
                    feedbackRef = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Feedbacks");
                    feedbackRef.push().setValue(editText.getText().toString().trim());
                    StyleableToast.makeText(getContext(), "Thanks for your interest.", R.style.customToast).show();
                }
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void checkFollowingStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(firebaseUser.getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()){
                    editprofile.setText("UNFOLLOW");
                }else
                    editprofile.setText("Follow");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void countPost() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Posts")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter =0;
                for (DataSnapshot np: snapshot.getChildren()){
                    Post post = np.getValue(Post.class);

                    if (post.getPublisher().equals(profileId)){
                        counter++;
                    }

                }
                posts.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countVideos() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter =0;
                for (DataSnapshot np: snapshot.getChildren()){
                    ModelVideo modelVideo = np.getValue(ModelVideo.class);
//                    try {
//                        if (post.getPublisher().equals(profileId)){
//                            counter++;
//                        }
//
//                    }catch (Exception e){
//                        Log.d("Closing Because",e.getMessage());
//
//                    }
                    if (modelVideo.getUserId().equals(profileId)){
                        counter++;
                    }


                }
                videos.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countFollowersAndFollowing() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Follow").child(profileId);
        ref.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        map.put("timeStamp","" + timeStamp);
        map.put("isPost",false);
        map.put("NotificationId",notificationId);
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notification").child(postpersonid).child(notificationId).setValue(map);

    }

    private void userInfo() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    if(user.getImageUrl().equals("default")){
                        imageprofile.setImageResource(R.drawable.unkown_person_24);
                    }else{
                        Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.unkown_person_24).into(imageprofile);
                    }
                    username.setText(user.getUsername());
                    bio.setText(user.getBio());
                    userCountry.setText(user.getCountry());



                    if (user.getPrefer().equals("Movie")){
                        userPrefer.setText("M");
                    }else if (user.getPrefer().equals("Series")){
                        userPrefer.setText("S");

                    }
                    pd.dismiss();

                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countMovies() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Movies").child(profileId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot np : snapshot.getChildren()) {
                    FirebaseMovie post = np.getValue(FirebaseMovie.class);

                    if (post.getUserId().equals(profileId)) {
                        counter++;
                    }


                }
                moviesText.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void countSeries(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Series").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot np : snapshot.getChildren()) {
                    FirebaseMovie post = np.getValue(FirebaseMovie.class);

                    if (post.getUserId().equals(profileId)) {
                        counter++;
                    }


                }
                seriesText.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countFavorites(){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Favorites").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot np : snapshot.getChildren()) {
                    FirebaseMovie post = np.getValue(FirebaseMovie.class);

                    if (post.getUserId().equals(profileId)) {
                        counter++;
                    }


                }
                favoritesText.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readHorizontal() {
        movieArrayList=new ArrayList<>();

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("ForProfile")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        movieArrayList.clear();
                        for (DataSnapshot np : snapshot.getChildren()){
                            FavoriteMovie post = np.getValue(FavoriteMovie.class);
                            if (post.getFavoriteUserId().equals(profileId)){
                                movieArrayList.add(post);
                            }
                        }
                        profileHorizontalAdapter=new ProfileHorizontalAdapter(getContext(),movieArrayList);
                        recyclerViewHorizontal.setAdapter(profileHorizontalAdapter);
                        profileHorizontalAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void countLists() {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Lists")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int counter = 0;
                        for (DataSnapshot np : snapshot.getChildren()) {
                            FilmLists filmLists = np.getValue(FilmLists.class);

                            if (filmLists.getListPublisher().equals(profileId) && filmLists.getListType().equals("Public")) {
                                counter++;
                            }


                        }
                        listText.setText(String.valueOf(counter));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
