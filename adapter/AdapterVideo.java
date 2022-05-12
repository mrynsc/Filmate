package com.yeslabapps.fictionfocus.adapter;

import static android.content.Context.MODE_PRIVATE;

import static com.unity3d.services.core.properties.ClientProperties.getActivity;
import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.UnityAds;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.FollowersActivity;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.activities.VideosCommentActivity;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderVideo> {

    private DatabaseReference refForReport;
    private Context context;

    private ArrayList<ModelVideo> videoArrayList;
    private FirebaseUser firebaseUser;
    private String profileId;

    private String GAME_ID = "4677213";
    private String INTERSTITIAL_ID_VIDEO ="DownloadVideo";
    private boolean test = false;


    public AdapterVideo( ArrayList<ModelVideo> videoArrayList,Context context) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public HolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_video.xml
        View view= LayoutInflater.from(context).inflate(R.layout.row_video,parent,false);
        return new HolderVideo(view) ;
    }



    @Override
    public void onBindViewHolder(@NonNull AdapterVideo.HolderVideo holder, int position) {


        final ModelVideo modelVideoPost= videoArrayList.get(position);

        String data = holder.itemView.getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }



        UnityAds.initialize(context,GAME_ID,test);

        loadInterstitialId3();

        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users").child(modelVideoPost.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageUrl().equals("default")){
                    holder.imageUser.setImageResource(R.drawable.unkown_person_24);
                }else{
                    Picasso.get().load(user.getImageUrl()).into(holder.imageUser);}


                holder.usernameText.setText(user.getUsername());





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.pauseVideo.setVisibility(View.INVISIBLE);


        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.videoView.isPlaying()){
                    holder.videoView.pause();
                    holder.pauseVideo.setVisibility(View.VISIBLE);
                }else{
                    holder.videoView.start();
                    holder.pauseVideo.setVisibility(View.INVISIBLE);
                }
            }
        });


        /*holder.videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (holder.videoView.isPlaying()){
                    holder.videoView.pause();
                    holder.pauseVideo.setVisibility(View.VISIBLE);
                }else{
                    holder.videoView.start();
                    holder.pauseVideo.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });*/

        holder.imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("receiverId",modelVideoPost.getUserId());
                //intent.putExtra("name", user.getUsername());
                intent.putExtra("PublisherId",modelVideoPost.getUserId());
                context.startActivity(intent);
            }
        });

        holder.usernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("receiverId",modelVideoPost.getUserId());

                intent.putExtra("PublisherId",modelVideoPost.getUserId());
                context.startActivity(intent);

            }
        });



        ModelVideo modelVideo=videoArrayList.get(position);

        String id=modelVideo.getId();
        String title=modelVideo.getTitle();
        String videoDescRow=modelVideo.getVideoDesc();
        String timestamp=modelVideo.getTimestamp();
        String videoUrl=modelVideo.getVideoUrl();

        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String formattedDateTime = DateFormat.format("dd MMMM k:mm",calendar).toString();

        holder.timeTv.setText(formattedDateTime);
       // holder.timeTv.setVisibility(View.INVISIBLE);
        holder.titleTv.setText(title);
        holder.videoDescText.setText(videoDescRow);
        setVideoUrl(modelVideo,holder);



        isLiked(modelVideo.getId(),holder.likeButton);
        noOfLikesCount(modelVideo.getId(),holder.likeText);
        countComments(modelVideo.getId(),holder.commentsText);
        isVideoSaved(modelVideo.getId(),holder.saveVideo);



        //handle click, download video
        /*holder.downloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadVideo(modelVideo);

            }
        });*/




        holder.reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.reportButton);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.report_video, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){

                            case R.id.downloadVideoItem:
                                try {
                                    if (UnityAds.isInitialized()){
                                        UnityAds.show((Activity) context,INTERSTITIAL_ID_VIDEO);
                                    }
                                    downloadVideo(modelVideo);
                                }catch (Exception e){
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }

                                break;

                            case R.id.reportVideoItem:

                                AlertDialog builder1 =new AlertDialog.Builder(context).create();
                                LayoutInflater layoutInflater=((Activity)context).getLayoutInflater();
                                View dialogView=layoutInflater.inflate(R.layout.report_video_dialog,null);
                                builder1.setView(dialogView);
                                Button button= dialogView.findViewById(R.id.sendReport);
                                RadioGroup radioGroup=dialogView.findViewById(R.id.reportVideoGroup);
                                radioGroup.setOnCheckedChangeListener(
                                        new RadioGroup
                                                .OnCheckedChangeListener() {
                                            @Override


                                            public void onCheckedChanged(RadioGroup group,int checkedId) {

                                                // Get the selected Radio Button
                                                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                                            }
                                        });

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int selectedId = radioGroup.getCheckedRadioButtonId();
                                        if (selectedId == -1) {
                                            StyleableToast.makeText(context,
                                                    "No reason has been selected",
                                                    R.style.customToast)
                                                    .show();
                                        } else {

                                            RadioButton radioButton = radioGroup.findViewById(selectedId);
                                            refForReport = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoReports");
                                            refForReport.push().setValue(radioButton.getText() + "/ " + id +" " + title);
                                            StyleableToast.makeText(context, "Thanks for providing feedback!", R.style.customToast).show();
                                            builder1.dismiss();

                                        }
                                    }
                                });
                                builder1.show();
                                break;

                        }
                        return true;
                    }

                });
                popupMenu.show();





            }
        });

        //handle click delete video

        holder.saveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.saveVideo.getTag().equals("save")) {
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoSaves").child(firebaseUser.getUid()).child(modelVideo.getId())
                            .setValue(true);

                }else{
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoSaves").child(firebaseUser.getUid()).child(modelVideo.getId())
                            .removeValue();

                }
            }
        });



        holder.likeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("ID",modelVideo.getId());
                intent.putExtra("TITLE","Likes");
                context.startActivity(intent);

            }
        });



        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.likeButton.getTag().equals("Like")){
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoLikes").child(modelVideo.getId()).child(firebaseUser.getUid()).setValue(true);
                    addNotification(modelVideoPost.getId(),firebaseUser.getUid(),modelVideoPost.getUserId(),"liked your video");

                }else{
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoLikes").child(modelVideo.getId()).child(firebaseUser.getUid()).removeValue();
                    //addNotification(modelVideoPost.getId(),firebaseUser.getUid(),modelVideoPost.getUserId(),"removed like from your video");

                    //deleteNotification(modelVideoPost.getId(),modelVideoPost.getUserId());

                }
            }
        });


        holder.addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideosCommentActivity.class);
                intent.putExtra("postId",modelVideo.getId());
                intent.putExtra("authorId",modelVideo.getUserId());
                context.startActivity(intent);
            }
        });




    }

    private void loadInterstitialId3(){
        if (UnityAds.isInitialized()){
            UnityAds.load(INTERSTITIAL_ID_VIDEO);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(INTERSTITIAL_ID_VIDEO);
                }
            },5000);
        }
    }



    private void isVideoSaved(final String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("VideoSaves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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




    /*private void deleteNotification(final String postId, final String postperonid) {
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

    }*/

    private  void isLiked(String postid, final ImageView imageView){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoLikes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.favorite_24);
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


    private void noOfLikesCount(String postid, final TextView textView){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("VideoLikes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                textView.setText(snapshot.getChildrenCount()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private  void countComments(String postid, final TextView text){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("VideoComments").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()==0){
                    text.setText("0");
                }else{
                    text.setText(+snapshot.getChildrenCount()+"");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification(String postId, String publisher,String postpersonid,String text) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Notification");
        String notificationId= ref.push().getKey();
        String timeStamp=""+System.currentTimeMillis();

        HashMap<String,Object> map = new HashMap<>();
        map.put("userid",publisher);
        map.put("text",text);
        map.put("timeStamp","" + timeStamp);

        map.put("postid",postId);
        map.put("isPost",true);
        map.put("NotificationId",notificationId);
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Notification").child(postpersonid).child(notificationId).setValue(map);

    }

    private void setVideoUrl(ModelVideo modelVideo, HolderVideo holder) {

        //show progress
        holder.progressBar.setVisibility(View.VISIBLE);

        //get video url
        String videoUrl = modelVideo.getVideoUrl();

        //Media controller for play, pause, seekbar, timer etc.
        MediaController mediaController=new MediaController(context);
        mediaController.setAnchorView(holder.videoView);
        mediaController.setVisibility(View.INVISIBLE);


        Uri videoUri =Uri.parse(videoUrl);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(videoUri);

        holder.videoView.requestFocus();

        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // video is ready to play

                mediaPlayer.start();

                /*float videoRatio=mediaPlayer.getVideoWidth()/(float)mediaPlayer.getVideoHeight();
                float screenRatio=holder.videoView.getWidth()/(float)holder.videoView.getHeight();
                float scale=videoRatio/screenRatio;

                if (scale >=1f){
                    holder.videoView.setScaleX(scale);
                }else{
                    holder.videoView.setScaleY(1f/scale);
                }*/

            }
        });



        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                // to check if buffering, rendering etc
                switch(what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    {
                        //rendering start
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        //buffering start
                        holder.progressBar.setVisibility(View.VISIBLE);
                        if(MediaPlayer.MEDIA_INFO_BUFFERING_END== 702){
                            holder.progressBar.setVisibility(View.GONE);
                        }
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        //buffering end
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }

                }
                return false;
            }
        });

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start(); //restart if video is completed
            }
        });


    }



    private void downloadVideo(ModelVideo modelVideo) {
        final String videoUrl =modelVideo.getVideoUrl();

        //get video reference using video url
        StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        storageReference.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get file/video basic info e.g tile, type
                        String fileName= storageMetadata.getName();
                        String fileType=storageMetadata.getContentType();
                        String fileDirectory= Environment.DIRECTORY_DOWNLOADS; // video will be save to this folder
                        //init download manager
                        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                        //get uri of file to be download
                        Uri uri =Uri.parse(videoUrl);

                        //create download request, now request for each download - yes we can download multiple file at a time
                        DownloadManager.Request request=new DownloadManager.Request(uri);

                        //notification visibility
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(""+fileDirectory,".mp4");
                        request.setTitle(modelVideo.getTitle());
                        //add request to queue - cana be multiple request so is added  to queue
                        downloadManager.enqueue(request);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // failing in downloading
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    @Override
    public int getItemCount() {
        return null!=videoArrayList?videoArrayList.size():0;
    }


    class HolderVideo extends  RecyclerView.ViewHolder{

        //Ui Views of row_video.xml
        VideoView videoView;
        TextView titleTv, timeTv,videoDescText;
        ProgressBar progressBar;
        ImageView likeButton;
        ImageView addComment;
        ImageView reportButton;
        TextView likeText;
        TextView commentsText;
        TextView usernameText;
        ImageView imageUser;
        ImageView saveVideo;
        //constructor

        ImageView pauseVideo;

        public HolderVideo(@NonNull View itemView) {
            super(itemView);

            //id to them of row_video layout
            videoView = itemView.findViewById(R.id.videoView);
            titleTv = itemView.findViewById(R.id.titleTv);
            timeTv =itemView.findViewById(R.id.timeTv);
            videoDescText=itemView.findViewById(R.id.videoDescriptionTexT);
            progressBar=itemView.findViewById(R.id.progressBar);
            likeButton=itemView.findViewById(R.id.likeButtonVideo);
            likeText=itemView.findViewById(R.id.likeTextVideo);
            addComment=itemView.findViewById(R.id.addCommentVideo);
            commentsText=itemView.findViewById(R.id.commentsVideo);
            usernameText=itemView.findViewById(R.id.usernameText);
            reportButton=itemView.findViewById(R.id.reportButton);
            imageUser=itemView.findViewById(R.id.userImage);
            saveVideo=itemView.findViewById(R.id.saveVideo);
            pauseVideo = itemView.findViewById(R.id.pauseVideo);
        }
    }
}
