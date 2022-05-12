package com.yeslabapps.fictionfocus.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class ShareQuoteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private Uri imageuri;
    private String imageUrl;
    ImageView imageadded;
    TextView post;
    AutoCompleteTextView description;
    AutoCompleteTextView filmname;
    TextView selectImage;
    //private AdView mAdView;
    Spinner spinner;
    TextView tagtext;

    private RadioGroup radioGroup;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_quote);
        imageadded = findViewById(R.id.imageadded);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        tagtext=findViewById(R.id.tagtext);
        filmname=findViewById(R.id.filmname);
        selectImage=findViewById(R.id.selectImageText);

        radioGroup = findViewById(R.id.groupradio);

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tags, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(ShareQuoteActivity.this);

            }
        });

        imageadded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(ShareQuoteActivity.this);
            }
        });


        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (description.getText().toString().length() == 1 && description.getTag().toString().equals("true"))
                {
                    description.setTag("false");
                    description.setText(description.getText().toString().toUpperCase());
                    description.setSelection(description.getText().toString().length());
                }
                if(description.getText().toString().length() == 0)
                {
                    description.setTag("true");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filmname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (filmname.getText().toString().length() == 1 && filmname.getTag().toString().equals("true"))
                {
                    filmname.setTag("false");
                    filmname.setText(filmname.getText().toString().toUpperCase());
                    filmname.setSelection(filmname.getText().toString().length());
                }
                if(filmname.getText().toString().length() == 0)
                {
                    filmname.setTag("true");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    private void upload() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = radioGroup.findViewById(selectedId);

        if (description.length()>2&&filmname.length()>1&&tagtext.length()>1&& !(selectedId ==-1)){

            final ProgressDialog pd = new ProgressDialog(this,R.style.CustomDialogUpload);
            pd.setTitle("Please Wait");
            pd.setMessage("Uploading");
            pd.setCancelable(false);

            pd.show();
            if (imageuri!=null){
                final StorageReference filepath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+ getExtension(imageuri));
                StorageTask uploadtask  = filepath.putFile(imageuri);
                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri  downloadUri = task.getResult();
                        imageUrl = downloadUri.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Posts");
                        String PostId= ref.push().getKey();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("PostId",PostId);
                        map.put("ImageUrl",imageUrl);
                        map.put("Tag",tagtext.getText().toString());
                        map.put("searchFilm",filmname.getText().toString().toLowerCase());
                        map.put("FilmName",filmname.getText().toString());
                        map.put("QuoteType",radioButton.getText().toString());
                        map.put("Description",description.getText().toString());
                        map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        ref.child(PostId).setValue(map);
                        pd.dismiss();
                        //Toast.makeText(PostActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        startActivity( new Intent(ShareQuoteActivity.this,StartActivity.class));
                        finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();

                        Toast.makeText(ShareQuoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                pd.dismiss();
                CookieBar.build(ShareQuoteActivity.this)
                        .setTitle("Please add a photo related to movie!")
                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                        .show();
                //StyleableToast.makeText(this, "Please add a photo related to movie!", R.style.customToast).show();
            }
        }else
            CookieBar.build(ShareQuoteActivity.this)
                    .setTitle("Please fill in each field!")
                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                    .show();
            //StyleableToast.makeText(this,"Please fill in each field!",R.style.customToast).show();


    }


    private String getExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            imageuri = activityResult.getUri();
            imageadded.setImageURI(imageuri);
        }else{
            StyleableToast.makeText(this, "Try Again!",R.style.customToast).show();
            startActivity(new Intent(ShareQuoteActivity.this,StartActivity.class));
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (i==0){
            tagtext.setText("");
        }else{
            String text = adapterView.getItemAtPosition(i).toString();
            tagtext.setText(text);
            tagtext.setVisibility(View.INVISIBLE);


        }




    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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