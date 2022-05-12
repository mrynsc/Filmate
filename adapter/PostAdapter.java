package com.yeslabapps.fictionfocus.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.artjimlop.altex.AltexImageDownloader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.UnityAds;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.CommentActivity;
import com.yeslabapps.fictionfocus.activities.FollowersActivity;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.model.Post;
import com.yeslabapps.fictionfocus.model.User;



import java.util.HashMap;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class PostAdapter  extends  RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context mcontext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private String GAME_ID = "4677213";
    private String INTERSTITIAL_ID_DOWNLOAD ="GoWatched";
    private boolean test = false;


    public PostAdapter(Context mcontext, List<Post> mPosts, boolean b) {
        this.mcontext = mcontext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.post_item,parent,false);


        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        /*holder.filmname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("key",holder.filmname.getText().toString());

                FilmSearchFragment filmSearchFragment=new FilmSearchFragment();
                filmSearchFragment.setArguments(bundle);
                ((AppCompatActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,filmSearchFragment).commit();
            }
        });*/


        final Post post = mPosts.get(position);



        Picasso.get().load(post.getImageUrl()).into(holder.postimage);
        holder.description.setText(post.getDescription());
        holder.filmname.setText(post.getFilmName());
        holder.tagname.setText(post.getTag());

        UnityAds.initialize(mcontext,GAME_ID,test);
        loadInterstitialId2();


        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageUrl().equals("default")){
                    holder.imageprofile.setImageResource(R.drawable.unkown_person_24);
                }else{
                    Picasso.get().load(user.getImageUrl()).into(holder.imageprofile);
                }

                holder.username.setText(user.getUsername());





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        isliked(post.getPostId(),holder.like);
        nooflikes(post.getPostId(),holder.nooflikes);
        countComments(post.getPostId(),holder.noofcomments);
        ispostsaved(post.getPostId(),holder.save);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("Like")){
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPostId(),firebaseUser.getUid(),post.getPublisher(),"liked your post");
                }else{
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                    //addNotification(post.getPostId(),firebaseUser.getUid(),post.getPublisher(),"removed like from your post");

                    //deletenotification(post.getPostId(),post.getPublisher());
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mcontext.startActivity(intent);
            }
        });

        holder.noofcomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("PublisherId",post.getPublisher());
                intent.putExtra("receiverId",post.getPublisher());

                mcontext.startActivity(intent);

            }
        });








        /*holder.filmname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("FilmNameId",post.getFilmName());
                mcontext.startActivity(intent);

            }
        });*/



        holder.imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("PublisherId",post.getPublisher());
                intent.putExtra("receiverId",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });
/*        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                persons_searching user= new persons_searching(id);
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("PublisherId",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });*/
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId())
                            .setValue(true);

                }else{
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId())
                            .removeValue();

                }
            }
        });
        holder.nooflikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mcontext, FollowersActivity.class);
                intent.putExtra("ID",post.getPostId());
                intent.putExtra("TITLE","likes");
                mcontext.startActivity(intent);




            }
        });



        final int[] checkedItem = {-1};
        holder.reportQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mcontext, holder.reportQuote);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.report_quote, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.downloadPhotoItem:
                                try {
                                    if (UnityAds.isInitialized()){
                                        UnityAds.show((Activity) mcontext,INTERSTITIAL_ID_DOWNLOAD);
                                    }
                                    AltexImageDownloader.writeToDisk(mcontext, post.getImageUrl(), "Filmate ");

                                }
                                catch (Exception e){
                                    Toast.makeText(mcontext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.shareQuoteItem:

                                shareQuote(holder.description.getText().toString() + "\n" + holder.filmname.getText().toString());
                                break;

                            case R.id.reportQuoteItem:
                                AlertDialog builder1 =new AlertDialog.Builder(mcontext).create();
                                LayoutInflater layoutInflater=((Activity)mcontext).getLayoutInflater();
                                View dialogView=layoutInflater.inflate(R.layout.report_dialog,null);
                                builder1.setView(dialogView);
                                Button button= dialogView.findViewById(R.id.sendReport);
                                RadioGroup radioGroup=dialogView.findViewById(R.id.reportQuoteGroup);
                                radioGroup.setOnCheckedChangeListener(
                                        new RadioGroup
                                                .OnCheckedChangeListener() {
                                            @Override

                                            // The flow will come here when
                                            // any of the radio buttons in the radioGroup
                                            // has been clicked

                                            // Check which radio button has been clicked
                                            public void onCheckedChanged(RadioGroup group,
                                                                         int checkedId) {

                                                // Get the selected Radio Button
                                                RadioButton
                                                        radioButton
                                                        = (RadioButton) group
                                                        .findViewById(checkedId);
                                            }
                                        });

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int selectedId = radioGroup.getCheckedRadioButtonId();
                                        if (selectedId == -1) {
                                            StyleableToast.makeText(mcontext,
                                                    "No reason has been selected",
                                                    R.style.customToast)
                                                    .show();
                                        } else {

                                            RadioButton radioButton = radioGroup.findViewById(selectedId);
                                            reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("QuoteReports");
                                            reference.push().setValue(radioButton.getText() + "/ " + post.getPostId()+" " + post.getFilmName());
                                            StyleableToast.makeText(mcontext, "Thanks for providing feedback!", R.style.customToast).show();
                                            builder1.dismiss();

                                        }
                                    }
                                });

                                builder1.show();
                                break;

                        }
                        //Toast.makeText(getContext(), "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();



            }
        });


    }

    /*private void deletenotification(final String postId, final String postperonid) {
        FirebaseDatabase.getInstance("https://quotebox-69d55-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notification").child(postperonid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Notification notification = snapshot1.getValue(Notification.class);
                    if (notification.getPostid().equals(postId)){
                        FirebaseDatabase.getInstance("https://quotebox-69d55-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference().child("Notification").child(postperonid).child(notification.getNotificationId()).removeValue();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/

    private void loadInterstitialId2(){
        if (UnityAds.isInitialized()){
            UnityAds.load(INTERSTITIAL_ID_DOWNLOAD);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(INTERSTITIAL_ID_DOWNLOAD);
                }
            },5000);
        }
    }


    private void shareQuote(String title){
        String shareText = title;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        // Adding the text to share using putExtra
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        mcontext.startActivity(Intent.createChooser(intent, "Share Via"));
    }


    private void addNotification( String postId, String publisher,String postpersonid,String text) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Notification");
        String notificationId= ref.push().getKey();
        String timeStamp=""+System.currentTimeMillis();

        HashMap<String,Object> map = new HashMap<>();
        map.put("userid",publisher);//who is liking the image postperson is person ehiose image is liked
        map.put("text",text);
        map.put("timeStamp" , timeStamp);

        map.put("postid",postId);
        map.put("isPost",true);
        map.put("NotificationId",notificationId);
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notification").child(postpersonid).child(notificationId).setValue(map);

    }


    private void ispostsaved(final String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.saved_24);

                    imageView.setTag("saved");


                }else
                {
                    imageView.setImageResource(R.drawable.save_24);
                    imageView.setTag("save");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageprofile;
        public ImageView postimage;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;
        public ImageView reportQuote;
        public TextView username;
        public TextView nooflikes;
        public TextView noofcomments;
        public TextView description;
        public TextView filmname;
        public TextView tagname;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageprofile =itemView.findViewById(R.id.profile_image);
            postimage =itemView.findViewById(R.id.post_image);
            like =itemView.findViewById(R.id.like);
            reportQuote=itemView.findViewById(R.id.reportQuote);
            comment =itemView.findViewById(R.id.comment);
            save =itemView.findViewById(R.id.save);
            tagname=itemView.findViewById(R.id.tagname);
            more =itemView.findViewById(R.id.more);
            username =itemView.findViewById(R.id.username);
            nooflikes =itemView.findViewById(R.id.liketext);
            noofcomments =itemView.findViewById(R.id.no_of_comments);
            description =itemView.findViewById(R.id.description);
            filmname=itemView.findViewById(R.id.filmname);


        }
    }
    private  void isliked(String postid, final ImageView imageView){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.liked_24);
                    imageView.setTag("Liked");

                }else{
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    imageView.setTag("Like");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void nooflikes(String postid, final TextView textView){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                textView.setText(snapshot.getChildrenCount()+" likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void countComments(String postid, final TextView text){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Comments").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()==0){
                    text.setText("Add first comment");
                }else{
                    text.setText(snapshot.getChildrenCount()+" comments");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
