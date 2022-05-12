package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yeslabapps.fictionfocus.R;

import org.aviran.cookiebar2.CookieBar;

public class LoginActivity extends AppCompatActivity {

    TextView signup;
    EditText emailname;
    private  EditText password;
    Button login;
    FirebaseAuth mauth;
    private ProgressDialog pd;
    TextView forgetpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup = findViewById(R.id.textview);
        emailname = findViewById(R.id.Emaillogin);
        password = findViewById(R.id.Passwordlogin);
        login = findViewById(R.id.btnLogin);
        forgetpassword = findViewById(R.id.forgetpasswoed);
        mauth = FirebaseAuth.getInstance();





        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(LoginActivity.this,RegisterActivity.class));
                finish();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailname.getText().toString();
                String pass = password.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
                    CookieBar.build(LoginActivity.this)
                            .setTitle("Who are you?")
                            .setMessage("Please fill in all fields.")
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                    //Toast.makeText(LoginActivity.this, "Ooops!", Toast.LENGTH_SHORT).show();
                }
                else{
                    pd = new ProgressDialog(LoginActivity.this,R.style.CustomDialogLogin);
                    pd.setMessage("Please Wait");
                    pd.setCancelable(false);
                    pd.show();

                    signIn(email,pass);
                }
            }
        });
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialEditText resetmail = new MaterialEditText(view.getContext());
                resetmail.setTextColor(Color.WHITE);
                final AlertDialog ad =  new AlertDialog.Builder(view.getContext()).create();
                ad.setTitle("Reset Password");
                ad.setMessage("Enter Your Email to Receive Reset Link");
                ad.setView(resetmail);
                ad.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ad.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetmail.getText().toString();
                        mauth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ad.dismiss();
                                CookieBar.build(LoginActivity.this)
                                        .setTitle("Please check your email and spam box!")
                                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                        .show();
                                //Toast.makeText(LoginActivity.this, "Check your email and spam box!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ad.dismiss();
                                        Toast.makeText(LoginActivity.this, "Error!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
                ad.setCanceledOnTouchOutside(false);

                ad.show();
            }
        });



    }

    private void signIn(String email, String pass) {
        mauth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            pd.dismiss();
                            finish();
                            Intent intent = new Intent(LoginActivity.this,StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);



                        } else {
                            pd.dismiss();

                            Toast.makeText(getApplicationContext(), "Error " + task.getException(),
                                    Toast.LENGTH_SHORT).show();


                        }


                    }
                });


    }
}