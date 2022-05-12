package com.yeslabapps.fictionfocus.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;

import com.yeslabapps.fictionfocus.R;

public class LoadingDialog {


    Context activity;
    AlertDialog alertDialog;

    public LoadingDialog (Context mActivity){
        activity=mActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }

}
