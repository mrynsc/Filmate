package com.yeslabapps.fictionfocus.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.FollowersActivity;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.activities.VideosCommentActivity;
import com.yeslabapps.fictionfocus.model.ModelVideo;
import com.yeslabapps.fictionfocus.model.Notification;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MyAdapterVideo extends RecyclerView.Adapter<MyAdapterVideo.MyHolderVideo> {
    //context
    private DatabaseReference refForReport;
    private Context context;
    //arrayylist
    private ArrayList<ModelVideo> videoArrayList;
    private FirebaseUser firebaseUser;
    private String profileId;

    public MyAdapterVideo( ArrayList<ModelVideo> videoArrayList,Context context) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public MyHolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_video.xml
        View view= LayoutInflater.from(context).inflate(R.layout.my_row_video,parent,false);
        return new MyHolderVideo(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterVideo.MyHolderVideo holder, int position) {

        final ModelVideo post= videoArrayList.get(position);

        String data = holder.itemView.getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }



        if (profileId.equals(firebaseUser.getUid())){
            holder.deleteButton.setVisibility(View.VISIBLE);
        }else {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }


        if (profileId.equals(firebaseUser.getUid())){
            holder.reportButton.setVisibility(View.INVISIBLE);
        }else {
            holder.reportButton.setVisibility(View.VISIBLE);
        }

        if (profileId.equals(firebaseUser.getUid())){
            holder.saveVideo.setVisibility(View.INVISIBLE);
        }else {
            holder.saveVideo.setVisibility(View.VISIBLE);
        }



        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(post.getUserId()).addValueEventListener(new ValueEventListener() {
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


        holder.imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("PublisherId",post.getUserId());
                intent.putExtra("receiverId",post.getUserId());

                context.startActivity(intent);
            }
        });

        holder.usernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                persons_searching user= new persons_searching(id);
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("PublisherId",post.getUserId());
                intent.putExtra("receiverId",post.getUserId());

                context.startActivity(intent);

            }
        });


        // Get, format, set data, handle click etc

        //get data
        ModelVideo modelVideo=videoArrayList.get(position);

        String id=modelVideo.getId();
        String title=modelVideo.getTitle();
        String timestamp=modelVideo.getTimestamp();
        String videoUrl=modelVideo.getVideoUrl();
        String videoDescRow=modelVideo.getVideoDesc();

        //formating the timestamp
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String formattedDateTime = DateFormat.format("dd/MM/yyyy k:mm a",calendar).toString();

        //set data
        holder.timeTv.setText(formattedDateTime);
        holder.timeTv.setVisibility(View.INVISIBLE);
        holder.titleTv.setText(title);
        holder.videoDescText.setText(videoDescRow);
        setVideoUrl(modelVideo,holder);



        isliked(modelVideo.getId(),holder.likeButton);
        nooflikes(modelVideo.getId(),holder.likeText);
        countcomments(modelVideo.getId(),holder.commentsText);
        isVideoSaved(modelVideo.getId(),holder.saveVideo);



        //handle click, download video
        /*holder.downloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadVideo(modelVideo);

            }
        });*/

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


        final int[] checkedItem = {-1};
        holder.reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.reportButton);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.report_quote, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.reportQuoteItem:

                                AlertDialog builder1 =new AlertDialog.Builder(context).create();
                                LayoutInflater layoutInflater=((Activity)context).getLayoutInflater();
                                View dialogView=layoutInflater.inflate(R.layout.report_video_dialog,null);
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
                                            Toast.makeText(context,
                                                    "No reason has been selected",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {

                                            RadioButton radioButton = radioGroup.findViewById(selectedId);
                                            refForReport = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoReports");
                                            refForReport.push().setValue(radioButton.getText() + "/ " + id);
                                            Toast.makeText(context, "Thanks for providing feedback!", Toast.LENGTH_SHORT).show();
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
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show alert dialogconfirm to delete
                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete video?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // confirm to delete
                                if (videoArrayList.size()==1){
                                    deleteVideo(modelVideo);
                                    deletenotification(modelVideo.getId(),modelVideo.getUserId());
                                    Intent intent= new Intent(view.getContext(), StartActivity.class);
                                    view.getContext().startActivity(intent);
                                }else{
                                    deleteVideo(modelVideo);
                                    deletenotification(modelVideo.getId(),modelVideo.getUserId());
                                }

                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel deleting, dismiss dialog
                        dialogInterface.dismiss();
                    }
                }).show();


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
                    addNotification(modelVideo.getId(),firebaseUser.getUid(),modelVideo.getUserId(),"liked your video");

                }else{
                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoLikes").child(modelVideo.getId()).child(firebaseUser.getUid()).removeValue();
                    //deletenotification(modelVideo.getId(),modelVideo.getUserId());
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

    private void addNotification( String postId, String publisher,String postpersonid,String text) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Notification");
        String notificationId= ref.push().getKey();
        String timeStamp=""+System.currentTimeMillis();

        HashMap<String,Object> map = new HashMap<>();
        map.put("userid",publisher);//who is liking the image postperson is person ehiose image is liked
        map.put("text",text);
        map.put("timeStamp","" + timeStamp);

        map.put("postid",postId);
        map.put("isPost",true);
        map.put("NotificationId",notificationId);
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notification").child(postpersonid).child(notificationId).setValue(map);

    }

    private void deletenotification(final String postId, final String postperonid) {
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

    private  void isliked(String postid, final ImageView imageView){
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

    private void nooflikes(String postid, final TextView textView){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("VideoLikes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                textView.setText(snapshot.getChildrenCount()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void countcomments(String postid, final TextView text){
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("VideoComments").child(postid).addValueEventListener(new ValueEventListener() {
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

    private void setVideoUrl(ModelVideo modelVideo, MyHolderVideo holder) {

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
            }
        });

        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                // to check if buffering, randering etc
                switch(what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    {
                        //redering start
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
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Videos");
                        databaseReference.child(videoID)
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Successfully deleted from firebase database
                                Toast.makeText(context, "Video Deleted!", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull  Exception e) {
                                // failed in deleteing from storage
                                Toast.makeText(context, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
    }

    /*private void downloadVideo(ModelVideo modelVideo) {
        final String videoUrl =modelVideo.getVideoUrl();

        //get video reference using video url
        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        storageReference.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get file/video basic info e.g tile, type
                        String filenName= storageMetadata.getName();
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

    }*/




    @Override
    public int getItemCount() {
        return null!=videoArrayList?videoArrayList.size():0; //return size of list
    }

    //view holder class, holds,inits the UI views
    class MyHolderVideo extends  RecyclerView.ViewHolder{

        //Ui Views of row_video.xml
        VideoView videoView;
        TextView titleTv, timeTv, videoDescText;
        ProgressBar progressBar;
        ImageView likeButton;
        ImageView addComment;
        ImageView deleteButton;
        ImageView reportButton;
        TextView likeText;
        TextView commentsText;
        TextView usernameText;
        ImageView imageUser;
        ImageView saveVideo;

        //constructor


        public MyHolderVideo(@NonNull View itemView) {
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
            deleteButton=itemView.findViewById(R.id.deleteButton);
            reportButton=itemView.findViewById(R.id.reportButton);
            usernameText=itemView.findViewById(R.id.usernameText);
            imageUser=itemView.findViewById(R.id.userImage);
            saveVideo=itemView.findViewById(R.id.saveVideo2);
        }
    }
}



