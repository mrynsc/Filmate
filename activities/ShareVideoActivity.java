package com.yeslabapps.fictionfocus.activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;


import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.UUID;

import io.github.muddz.styleabletoast.StyleableToast;

public class ShareVideoActivity extends AppCompatActivity {

    //private ActionBar actionBar;
    private EditText titleEt;
    private EditText videoDescEt;
    private VideoView videoView;
    private TextView uploadVideoBtn;
    private ImageView selectVideoBtn;

    private static final int VIDEO_PICK_GALLERY_CODE= 100;
    FirebaseAuth mAuth;

    private Uri videoUri=null; //uri of pick video
    private String title;

    private String videoDesc;

    private ProgressDialog progressDialog;


    private RadioGroup radioGroup;


    private Uri selectedImage=null;
    private String thumbnailUrl;

    private ImageButton chooseThumbnail;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_video);

       /* actionBar=getSupportActionBar();
        actionBar.setTitle("Add New Video");


        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);*/
        mAuth=FirebaseAuth.getInstance();
        titleEt=findViewById(R.id.titleEt);
        videoDescEt=findViewById(R.id.videoDescEt);
        videoView=findViewById(R.id.videoView);
        uploadVideoBtn=findViewById(R.id.uploadVideoBtn);
        selectVideoBtn=findViewById(R.id.goGallery);
        chooseThumbnail=findViewById(R.id.postUploadChooseThumbnail);

        chooseThumbnail.setOnClickListener(v -> {
            Intent intent1 =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent1,1);
        });

        progressDialog=new ProgressDialog(this,R.style.CustomDialogUpload);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading Video");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);



        radioGroup = findViewById(R.id.groupradio);
        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override


                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {

                        RadioButton
                                radioButton
                                = (RadioButton) group
                                .findViewById(checkedId);
                    }
                });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





        // handle click and upload
        uploadVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = titleEt.getText().toString().trim();
                videoDesc= videoDescEt.getText().toString().trim();


                if(TextUtils.isEmpty(title)){
                    CookieBar.build(ShareVideoActivity.this)
                            .setTitle("Name Required!")
                            .setMessage("Please enter a movie/series name.")
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                    //StyleableToast.makeText(ShareVideoActivity.this, "Name Required.", R.style.customToast).show();
                }
                else if( videoUri==null ){
                    CookieBar.build(ShareVideoActivity.this)
                            .setTitle("Please Select a Video!")
                            .setMessage("You can upload a movie/series clip up to 3 minutes long.")
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                    //StyleableToast.makeText(ShareVideoActivity.this, "Please Select a Video", R.style.customToast).show();
                }



                else {
                    //upload function call
                    uploadVideoFirebase();
                    uploadToFirebaseStorage(selectedImage);

                }
            }
        });

        // handle click,pick video from camera
        selectVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPickDialog();

            }
        });


        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (titleEt.getText().toString().length() == 1 && titleEt.getTag().toString().equals("true"))
                {
                    titleEt.setTag("false");
                    titleEt.setText(titleEt.getText().toString().toUpperCase());
                    titleEt.setSelection(titleEt.getText().toString().length());
                }
                if(titleEt.getText().toString().length() == 0)
                {
                    titleEt.setTag("true");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        videoDescEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (videoDescEt.getText().toString().length() == 1 && videoDescEt.getTag().toString().equals("true"))
                {
                    videoDescEt.setTag("false");
                    videoDescEt.setText(videoDescEt.getText().toString().toUpperCase());
                    videoDescEt.setSelection(videoDescEt.getText().toString().length());
                }
                if(videoDescEt.getText().toString().length() == 0)
                {
                    videoDescEt.setTag("true");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





    }

    private void uploadVideoFirebase() {

        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = radioGroup.findViewById(selectedId);


        MediaPlayer mpl = MediaPlayer.create(this,videoUri);
        int si = mpl.getDuration();


        if (si>180000){
            CookieBar.build(ShareVideoActivity.this)
                    .setTitle("You cannot upload videos longer than 3 minutes!")
                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                    .show();
            //StyleableToast.makeText(ShareVideoActivity.this, "You cannot upload videos longer than 3 minutes!", R.style.customToast).show();
        }
        else if (selectedId == -1){
            CookieBar.build(ShareVideoActivity.this)
                    .setTitle("Choose a Video Type!")
                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                    .show();
            //StyleableToast.makeText(ShareVideoActivity.this, "Choose a video type!", R.style.customToast).show();

        }
        else{
            progressDialog.show();

            String timestamp=""+System.currentTimeMillis();

            String filePathAndName="Videos/"+"video_"+ timestamp;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);

            storageReference.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadUri=uriTask.getResult();
                            if (uriTask.isSuccessful()){

                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("id",""+timestamp);
                                hashMap.put("title","" + title);
                                hashMap.put("videoType", radioButton.getText().toString());
                                hashMap.put("searchTitle","" + title.toLowerCase());
                                hashMap.put("videoDesc","" + videoDesc);
                                hashMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("timestamp","" + timestamp);
                                hashMap.put("videoUrl","" + downloadUri);
                                hashMap.put("thumbnail",(thumbnailUrl==null)? "default" :thumbnailUrl);

                                DatabaseReference reference= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Videos");
                                reference.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                //Toast.makeText(AddVideoActivity.this, "Video Uploaded.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ShareVideoActivity.this, StartActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull  Exception e) {
                                                //failing adding details to db
                                                progressDialog.dismiss();
                                                Toast.makeText(ShareVideoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed uploading to storage
                    progressDialog.dismiss();
                    Toast.makeText(ShareVideoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        //show progress

    }



    private void videoPickDialog() {
        videoPickGallery();

    }



    private void videoPickGallery(){

        Intent intent =new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Videos"), VIDEO_PICK_GALLERY_CODE);
    }



    private void setVideoToVideoView(){
        MediaController mediaController=new MediaController(this);
        mediaController.setAnchorView(videoView);

        mediaController.setVisibility(View.INVISIBLE);
        //set media controller to video view
        videoView.setMediaController(mediaController);
        //set video uri
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //called after picking video from camera/gallery
        if(resultCode==RESULT_OK){
            if (requestCode== VIDEO_PICK_GALLERY_CODE){
                videoUri=data.getData();
                //show picked video in VideoView
                //Toast.makeText(this, "Ready to upload video", Toast.LENGTH_SHORT).show();

                setVideoToVideoView();
            }
            else {

            }
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage=data.getData();

            Glide.with(this).load(selectedImage).into(chooseThumbnail);

        }
    }

    private void uploadToFirebaseStorage(Uri imageUri) {
        if(imageUri==null) return;

        String filename = UUID.randomUUID().toString();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("/thumbnails").child(filename);

        //Uploading
        UploadTask uploadTask = ref.putFile(imageUri);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> {

            //Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
            ref.getDownloadUrl().addOnSuccessListener(uri -> thumbnailUrl=uri.toString());
        });
    }


    @Override
    protected void onStart() {
        IntentFilter intentFilter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}


