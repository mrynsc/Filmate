package com.yeslabapps.fictionfocus.adapter;

import static android.content.Context.MODE_PRIVATE;
import static com.yeslabapps.fictionfocus.adapter.MovieAdapter.MovieHolder.EXTRA_MESSAGE;

import android.app.Activity;
import android.content.Context;
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
import com.yeslabapps.fictionfocus.model.ListMovie;

import java.util.ArrayList;



public class AlternativeListDetailsAdapter extends  RecyclerView.Adapter<AlternativeListDetailsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<ListMovie> listMovies;
    private FirebaseUser firebaseUser;
    private String profileId;

    public AlternativeListDetailsAdapter( ArrayList<ListMovie> listMovies,Context context) {
        this.listMovies = listMovies;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_movie_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        /*String data = holder.itemView.getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }*/

        final ListMovie listMovie = listMovies.get(position);




        holder.title.setVisibility(View.INVISIBLE);

        Picasso.get().load(listMovie.getListMoviePoster()).into(holder.poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, listMovie.getListMovieJsonObject());  //parameter for activity
                context.startActivity(intent);
            }
        });


        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listMovie.getListMoviePubId().equals(firebaseUser.getUid())) {

                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete?")
                            .setCancelable(true)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference().child("ListFilms").child(listMovie.getListOwnerId()).child(listMovie.getListMovieId())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
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


                }
                return true;
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return null!=listMovies?listMovies.size():0;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView poster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.filmNameList);
            poster = itemView.findViewById(R.id.list_movie_poster);

        }
    }

}