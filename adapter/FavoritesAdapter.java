package com.yeslabapps.fictionfocus.adapter;

import static com.yeslabapps.fictionfocus.adapter.MovieAdapter.MovieHolder.EXTRA_MESSAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.activities.MovieDetailsActivity;
import com.yeslabapps.fictionfocus.model.FirebaseMovie;

import java.util.ArrayList;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FirebaseMovie> movieArrayList;
    private FirebaseUser firebaseUser;

    public FavoritesAdapter(Context context,ArrayList<FirebaseMovie>movieArrayList){
        this.context=context;
        this.movieArrayList=movieArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final FirebaseMovie post = movieArrayList.get(position);


        //Picasso.get().load(post.getMoviePoster()).into(holder.postimage);


        holder.setFilmname(post.getMovieName());

        holder.setPostimage(post.getMoviePoster());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, post.getMovieJsonObject());  //parameter for activity
                context.startActivity(intent);
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (post.getUserId().equals(firebaseUser.getUid())){
                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(true)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference().child("Favorites").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                //Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
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



                    /*final AlertDialog alertDialog=new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Are you sure you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference().child("Favorites").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();

                                    }
                                }
                            });
                        }
                    });

                    alertDialog.show();
*/
                }

                return true;
            }

        });





    }





    @Override
    public int getItemCount() {
        return null!=movieArrayList?movieArrayList.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView postimage;
        private TextView filmname;

        //public static final String EXTRA_MESSAGE = "com.yeslabapps.searchmovie.extra.MESSAGE";


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filmname=itemView.findViewById(R.id.movie_title);
            postimage = itemView.findViewById(R.id.movie_poster);

            itemView.setOnClickListener(this);

        }

        public void setFilmname(String filmName) {
            filmname.setVisibility(View.INVISIBLE);
            //filmname.setText(filmName);
        }

        public void setPostimage(String postImage) {
            Picasso.get().load(postImage).into(postimage);
        }

        @Override
        public void onClick(View v) {
            /*int position = getLayoutPosition();
            FirebaseMovie movie = movieArrayList.get(position);  //get selected movie
            Context context = v.getContext();  //Activity context
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra(EXTRA_MESSAGE, movie.getPostId());  //parameter for activity
            context.startActivity(intent);  //starting next activity MovieDetailsActivity*/
        }
    }
}
