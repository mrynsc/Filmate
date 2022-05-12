package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();

        TextView textView=findViewById(R.id.textview);
        Button registerBtn=findViewById(R.id.registerbtn);
        EditText usernameEt=findViewById(R.id.username);
        EditText passwordEt=findViewById(R.id.Password);
        EditText emailEt=findViewById(R.id.Email);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this,R.style.CustomDialogLogin);
                pd.setMessage("Please wait");
                pd.setCancelable(false);
                pd.show();
                final String email=emailEt.getText().toString().trim();
                final String username=usernameEt.getText().toString().trim();
                final String password=passwordEt.getText().toString().trim();
                Query usernameQuery= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Users")
                        .orderByChild("username").equalTo(username);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            pd.dismiss();
                            CookieBar.build(RegisterActivity.this)
                                    .setTitle("Username already exists.")
                                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                    .show();
                            //Toast.makeText(RegisterActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
                        }else if (usernameEt.getText().toString().trim().length()>1){
                            firebaseAuth.createUserWithEmailAndPassword(emailEt.getText().toString().trim(), passwordEt.getText().toString().trim())
                                    .addOnCompleteListener(RegisterActivity.this,(task)->{
                                        if (!task.isSuccessful()){
                                            pd.dismiss();
                                            Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                        }
                                        else{





                                            String user_id =firebaseAuth.getCurrentUser().getUid();
                                            final DatabaseReference databaseReference=FirebaseDatabase
                                                    .getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                                    .getReference()
                                                    .child("Users").child(user_id);

                                            Map newPost =new HashMap();
                                            newPost.put("username",username.trim());
                                            //newPost.put("password",password);
                                            newPost.put("email",email.trim());
                                            newPost.put("bio","");
                                            newPost.put("status", "offline");
                                            newPost.put("country","");
                                            newPost.put("countryLower","");
                                            newPost.put("userId",user_id);
                                            newPost.put("ImageUrl","default");
                                            newPost.put("prefer","");
                                            databaseReference.setValue(newPost);
                                            pd.dismiss();
                                            Intent intent=new Intent(RegisterActivity.this,StartActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    });
                        }else{
                            CookieBar.build(RegisterActivity.this)
                                    .setTitle("Please fill in all fields!")
                                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                    .show();
                            //Toast.makeText(RegisterActivity.this,"Please fill in each field!",Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}