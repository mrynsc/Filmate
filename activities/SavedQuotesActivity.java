package com.yeslabapps.fictionfocus.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.SavedViewPagerAdapter;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;



public class SavedQuotesActivity extends AppCompatActivity {

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    TabLayout tabLayout;
    ViewPager viewPager;
    //SavedViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_quotes);

        viewPager = findViewById(R.id.view_pager_saveds);
        tabLayout = findViewById(R.id.tabsSaveds);

        //viewPagerAdapter = new SavedViewPagerAdapter(getSupportFragmentManager());
        //viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        Toolbar toolbar = findViewById(R.id.toolbarSaveds);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
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
