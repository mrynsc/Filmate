package com.yeslabapps.fictionfocus.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.EditProfileActivity;
import com.yeslabapps.fictionfocus.adapter.NotificationAdapter;
import com.yeslabapps.fictionfocus.model.Notification;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {


    private RecyclerView recyclerView_notification;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    private ProgressDialog pd;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView_notification = view.findViewById(R.id.recyclerview_notification);
        recyclerView_notification.setHasFixedSize(true);
        recyclerView_notification.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(),notificationList);
        recyclerView_notification.setAdapter(notificationAdapter);




        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();


        readNotifications();


        return view;
    }

    private void readNotifications() {
        notificationList.clear();
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .limitToLast(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        notificationList.add(np.getValue(Notification.class));
                        pd.dismiss();
                    }
                }
                pd.dismiss();

                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}