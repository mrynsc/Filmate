package com.yeslabapps.fictionfocus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.RegisterActivity;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class BottomSheetUsername extends BottomSheetDialogFragment {

    private FirebaseUser fUser;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottom_sheet_username, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        MaterialEditText editText = v.findViewById(R.id.usernameEt);
        Button button = v.findViewById(R.id.saveUsername);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = editText.getText().toString();
                Query query = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Users").orderByChild("username").equalTo(userName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            StyleableToast.makeText(getContext(), "Username already exists.", R.style.customToast).show();

                        }else if(userName.trim().length()>1){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("username", editText.getText().toString().trim());

                            FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference().child("Users").child(fUser.getUid()).updateChildren(hashMap);
                            StyleableToast.makeText(getContext(), "Updated!", R.style.customToast).show();
                            dismiss();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });




        return v;
    }
}