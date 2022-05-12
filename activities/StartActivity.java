package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.unity3d.ads.UnityAds;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.fragments.ChatsFragment;
import com.yeslabapps.fictionfocus.fragments.ExploreListFragment;
import com.yeslabapps.fictionfocus.fragments.NotificationFragment;
import com.yeslabapps.fictionfocus.fragments.PeopleFragment;
import com.yeslabapps.fictionfocus.fragments.ProfileFragment;
import com.yeslabapps.fictionfocus.fragments.QuotesFragment;
import com.yeslabapps.fictionfocus.fragments.VideosFragment;
import com.yeslabapps.fictionfocus.model.Token;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private String GAME_ID = "4677213";
    private String INTERSTITIAL_ID ="QuoteInterstitial";
    private String VIDEO_INTERSTITIAL_ID = "VideoInterstitial";
    private boolean test = false;

    private String mUid;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    //private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();




        UnityAds.initialize(StartActivity.this,GAME_ID,test);
        drawer = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbarStart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quotes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

*/
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new QuotesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_quotes);
        }

        Bundle intent = getIntent().getExtras();
        if (intent!=null){
            String profileid =intent.getString("PublisherId");
            getSharedPreferences("Profile",MODE_PRIVATE).edit().putString("ProfileId",profileid).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new QuotesFragment()).commit();


        }

        loadInterstitialId();
        loadInterstitialId2();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        SharedPreferences preferences= StartActivity.this.getSharedPreferences("PREFS",0);

        boolean ifShowDialog=preferences.getBoolean("showDialog",true);
        if (ifShowDialog){
            showDialog();
        }






    }




    private void showDialog(){
        AlertDialog.Builder builder =new AlertDialog.Builder(StartActivity.this);
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View dialogView=layoutInflater.inflate(R.layout.welcome_dialog,null);
        builder.setView(dialogView);


        builder.setCancelable(false).setPositiveButton("Never Show Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                SharedPreferences sharedPreferences = StartActivity.this.getSharedPreferences("PREFS",0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("showDialog",false);
                editor.apply();
            }
        });

        AlertDialog alertDialog =builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Tokens");
        Token mToken=new Token(token);
        ref.child(mUid).setValue(mToken);
    }

    private void loadInterstitialId(){
        if (UnityAds.isInitialized()){
            UnityAds.load(INTERSTITIAL_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(INTERSTITIAL_ID);
                }
            },5000);
        }
    }

    private void loadInterstitialId2(){
        if (UnityAds.isInitialized()){
            UnityAds.load(VIDEO_INTERSTITIAL_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(VIDEO_INTERSTITIAL_ID);
                }
            },5000);
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_quotes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new QuotesFragment()).commit();
                break;
            case R.id.nav_videos:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new VideosFragment()).commit();
                break;
            case R.id.nav_people:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PeopleFragment()).commit();
                break;

            case R.id.nav_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ExploreListFragment()).commit();
                break;

            case R.id.nav_notify:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NotificationFragment()).commit();

                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatsFragment()).commit();

                break;

            case R.id.nav_share_quote:
                if (UnityAds.isInitialized()){
                    UnityAds.show(StartActivity.this,INTERSTITIAL_ID);

                }
                Intent intent= new Intent(StartActivity.this,ShareQuoteActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_share_video:
                if (UnityAds.isInitialized()){
                    UnityAds.show(StartActivity.this,VIDEO_INTERSTITIAL_ID);

                }
                Intent intent2= new Intent(StartActivity.this,ShareVideoActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_profile:
                getSharedPreferences("Profile",MODE_PRIVATE).edit().putString("ProfileId", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();


                break;

            case R.id.nav_settings:
                Intent intent1= new Intent(StartActivity.this,OptionActivity.class);
                startActivity(intent1);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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