package com.yeslabapps.fictionfocus.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.StartActivity;
import com.yeslabapps.fictionfocus.model.Comments;
import com.yeslabapps.fictionfocus.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;

public class CommentsAdapter extends  RecyclerView.Adapter<CommentsAdapter.ViewHolder>{

    private Context mcontext;
    private List<Comments> mcomments;
    private FirebaseUser firebaseUser;
    private DatabaseReference refForReport;

    public CommentsAdapter(Context mcontext, List<Comments> mcomments) {
        this.mcontext = mcontext;
        this.mcomments = mcomments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comments comments = mcomments.get(position);
        holder.comment.setText(comments.getComment());
        FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(comments.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.username.setText(user.getUsername());

                if(user.getImageUrl().equals("default")){
                    holder.imageprofile.setImageResource(R.drawable.unkown_person_24);
                }else{
                    Picasso.get().load(user.getImageUrl()).into(holder.imageprofile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("PublisherId",comments.getPublisher());
                mcontext.startActivity(intent);
            }
        });*/



        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("PublisherId",comments.getPublisher());
                intent.putExtra("receiverId",comments.getPublisher());

                mcontext.startActivity(intent);
            }
        });
        holder.imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, StartActivity.class);
                intent.putExtra("PublisherId",comments.getPublisher());
                intent.putExtra("receiverId",comments.getPublisher());

                mcontext.startActivity(intent);
            }
        });

        holder.reportComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mcontext, holder.reportComment);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.report_comment, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){

                            case R.id.reportCommentItem:

                                AlertDialog builder1 =new AlertDialog.Builder(mcontext).create();
                                LayoutInflater layoutInflater=((Activity)mcontext).getLayoutInflater();
                                View dialogView=layoutInflater.inflate(R.layout.report_video_dialog,null);
                                builder1.setView(dialogView);
                                Button button= dialogView.findViewById(R.id.sendReport);
                                RadioGroup radioGroup=dialogView.findViewById(R.id.reportVideoGroup);
                                radioGroup.setOnCheckedChangeListener(
                                        new RadioGroup
                                                .OnCheckedChangeListener() {
                                            @Override


                                            public void onCheckedChanged(RadioGroup group,int checkedId) {

                                                // Get the selected Radio Button
                                                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                                            }
                                        });

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int selectedId = radioGroup.getCheckedRadioButtonId();
                                        if (selectedId == -1) {
                                            StyleableToast.makeText(mcontext,
                                                    "No reason has been selected",
                                                    R.style.customToast)
                                                    .show();
                                        } else {

                                            RadioButton radioButton = radioGroup.findViewById(selectedId);
                                            refForReport = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                                    .getReference().child("PostCommentReports");
                                            refForReport.push().setValue(radioButton.getText() + "/ " + comments.getPostId() +" " + comments.getCommentid());
                                            StyleableToast.makeText(mcontext, "Thanks for providing feedback!", R.style.customToast).show();
                                            builder1.dismiss();

                                        }
                                    }
                                });
                                builder1.show();
                                break;

                        }
                        return true;
                    }

                });
                popupMenu.show();

            }
        });





        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (comments.getPublisher().equals(firebaseUser.getUid())){

                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) mcontext)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(true)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").
                                            getReference().child("Comments").child(comments.getPostId()).child(comments.getCommentid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                StyleableToast.makeText(mcontext, "Comment Deleted!",R.style.customToast).show();

                                                dialogInterface.dismiss();
                                            }
                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            }).build();

                    materialDialog.show();







                    /*final AlertDialog ad =  new AlertDialog.Builder(mcontext).create();
                    ad.setTitle("Do you want to delete this comment?");
                    ad.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    ad.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").
                                    getReference().child("Comments").child(comments.getPostId()).child(comments.getCommentid())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        StyleableToast.makeText(mcontext, "Comment Deleted!",R.style.customToast).show();

                                        ad.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    ad.show();*/

                }
                return true;
            }
        });


    }


    @Override
    public int getItemCount() {
        return mcomments.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageprofile;
        private TextView username;
        private TextView comment;
        private ImageView reportComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageprofile = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            reportComment=itemView.findViewById(R.id.moreItemComment);
        }
    }
}
