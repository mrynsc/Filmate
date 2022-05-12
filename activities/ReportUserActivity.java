package com.yeslabapps.fictionfocus.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yeslabapps.fictionfocus.MainActivity;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;

public class ReportUserActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    Button submit;
    private DatabaseReference refForReport;
    String reportUserName;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);

        TextView reasonText = findViewById(R.id.reasonText);

        Toolbar toolbar = findViewById(R.id.toolbarReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit = findViewById(R.id.submit);
        radioGroup = findViewById(R.id.groupradio);

        Intent intent = getIntent();
        reportUserName = intent.getStringExtra("userNameReport");

        reasonText.setText("Select your report reason for " + reportUserName);


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


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDialog materialDialog = new MaterialDialog.Builder(ReportUserActivity.this)
                        .setTitle("Report")
                        .setMessage("Are you sure you want to report this user?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", R.drawable.ic_baseline_send_24, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                reportUser();
                                dialogInterface.dismiss();

                            }
                        }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        }).build();

                materialDialog.show();




                /*AlertDialog.Builder builder = new AlertDialog.Builder(ReportUserActivity.this);
                builder.setTitle("Report")
                        .setMessage("Are you sure you want report?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                reportUser();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel deleting, dismiss dialog
                        dialogInterface.dismiss();
                    }
                }).show();*/
            }
        });



    }

    private void reportUser() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            StyleableToast.makeText(ReportUserActivity.this,
                    "No reason has been selected",
                    R.style.customToast)
                    .show();
        } else {

            RadioButton radioButton = radioGroup.findViewById(selectedId);
            refForReport = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("UserReports");
            refForReport.push().setValue(radioButton.getText() + "/ " + reportUserName);
            StyleableToast.makeText(ReportUserActivity.this, "Thanks for providing feedback!", R.style.customToast).show();
            finish();
            //Intent intent1 = new Intent(ReportUserActivity.this, StartActivity.class);
            //startActivity(intent1);
        }

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