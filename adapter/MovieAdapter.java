package com.yeslabapps.fictionfocus.adapter;

import static android.content.Context.MODE_PRIVATE;

import static com.yeslabapps.fictionfocus.adapter.MovieAdapter.MovieHolder.EXTRA_MESSAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;

import com.yeslabapps.fictionfocus.activities.MovieDetailsActivity;
import com.yeslabapps.fictionfocus.model.Movie;
import com.yeslabapps.fictionfocus.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    //List to store movies
    private ArrayList<Movie> moviesList;
    private Context mContext;  //Activity context referencing which activity this is bound to
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private  String personId;
    private  String title;

    String userNameHorizontal;
    String userCountry;

    private String profileId;
    private String imageUrl;

    private String listId;

    public MovieAdapter(ArrayList<Movie> moviesList, Context context) {
        this.moviesList = moviesList;
        this.mContext = context;
    }

    //Creating the View
    @Override
    public MovieAdapter.MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public int getItemCount() {
        return null!=moviesList?moviesList.size():0;

    }

    //Binding the data with the View
    @Override
    public void onBindViewHolder(MovieAdapter.MovieHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        String data = holder.itemView.getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId", "");
        if (data.equals("")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
        }


        Intent intent = ((Activity) mContext).getIntent();
        personId = intent.getStringExtra("ID");
        title = intent.getStringExtra("TITLE");
        listId = intent.getStringExtra("ListId");

        userNameHorizontal = intent.getStringExtra("userName");
        userCountry = intent.getStringExtra("userCountry");


        final Movie movie = moviesList.get(position);

        holder.setMovieTitle(movie.getMovieTitle());
        holder.setMoviePoster(movie.getMoviePosterResource());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (title) {

                    case "List":
                        String controlList = movie.getMovieTitle();
                        Query queryList = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference().child("ListFilms").child(listId)
                                .orderByChild("listMovieName").equalTo(controlList);
                        queryList.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 0) {
                                    StyleableToast.makeText(mContext, "You already added!", R.style.customToast).show();

                                } else {
                                    DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference("ListFilms");
                                    String listMovieId = reference.push().getKey();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("listMovieName", movie.getMovieTitle());
                                    hashMap.put("listMovieNameLower", movie.getMovieTitle().toLowerCase());
                                    hashMap.put("listMoviePoster", movie.getMoviePosterResource());
                                    hashMap.put("listMoviePubId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("listMovieId", listMovieId);
                                    hashMap.put("listMovieJsonObject",movie.getJsonObject());
                                    hashMap.put("listOwnerId",listId);
                                    reference.child(listId).child(listMovieId).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    StyleableToast.makeText(mContext, "Added to list!", R.style.customToast).show();
                                                }}).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                        //reference2.child(movieId2).setValue(hashMap2);


                    case "Favorites":
                        String controlNameFavorites = movie.getMovieTitle();
                        Query queryFav = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference().child("Favorites").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .orderByChild("movieName").equalTo(controlNameFavorites);
                        queryFav.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 0) {
                                    StyleableToast.makeText(mContext, "You already added!", R.style.customToast).show();

                                } else {
                                    DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference("Favorites");

                                    String movieId = reference.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("movieName", movie.getMovieTitle());
                                    hashMap.put("movieNameLower", movie.getMovieTitle().toLowerCase());
                                    hashMap.put("moviePoster", movie.getMoviePosterResource());
                                    hashMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("postId", movieId);
                                    hashMap.put("movieJsonObject",movie.getJsonObject());

                                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(movieId)
                                            .setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    StyleableToast.makeText(mContext, "Added to favorites!", R.style.customToast).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    //reference2.child(movieId2).setValue(hashMap2);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                       /* DatabaseReference reference2=FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Movies");*/

                        break;

                    case "Movies":
                        String controlNameMovies = movie.getMovieTitle();
                        Query queryMov = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference().child("Movies").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .orderByChild("movieName").equalTo(controlNameMovies);
                        queryMov.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 0) {
                                    StyleableToast.makeText(mContext, "You already added!", R.style.customToast).show();

                                } else {
                                    DatabaseReference reference2 = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference("Movies");

                                    String movieId2 = reference2.push().getKey();


                                    HashMap<String, Object> hashMap2 = new HashMap<>();
                                    hashMap2.put("movieName", movie.getMovieTitle());
                                    hashMap2.put("movieNameLower", movie.getMovieTitle().toLowerCase());
                                    hashMap2.put("moviePoster", movie.getMoviePosterResource());
                                    hashMap2.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap2.put("postId", movieId2);
                                    hashMap2.put("movieJsonObject",movie.getJsonObject());

                                    reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(movieId2)
                                            .setValue(hashMap2)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    StyleableToast.makeText(mContext, "Added to movies!", R.style.customToast).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    //reference2.child(movieId2).setValue(hashMap2);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                       /* DatabaseReference reference2=FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Movies");*/

                        break;


                    case "Series":
                        String controlNameSeries = movie.getMovieTitle();
                        Query querySeri = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference().child("Series").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .orderByChild("movieName").equalTo(controlNameSeries);
                        querySeri.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 0) {
                                    StyleableToast.makeText(mContext, "You already added!",R.style.customToast).show();

                                } else {
                                    DatabaseReference reference3 = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference("Series");

                                    String movieId3 = reference3.push().getKey();

                                    HashMap<String, Object> hashMap3 = new HashMap<>();
                                    hashMap3.put("movieName", movie.getMovieTitle());
                                    hashMap3.put("movieNameLower", movie.getMovieTitle().toLowerCase());
                                    hashMap3.put("moviePoster", movie.getMoviePosterResource());
                                    hashMap3.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap3.put("postId", movieId3);
                                    hashMap3.put("movieJsonObject",movie.getJsonObject());

                                    reference3.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(movieId3)
                                            .setValue(hashMap3)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    StyleableToast.makeText(mContext, "Added to series!", R.style.customToast).show();


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    //reference2.child(movieId2).setValue(hashMap2);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                       /* DatabaseReference reference2=FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Movies");*/


                        break;


                    case "Horizontal":


                        DatabaseReference reference5 = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("ForProfile");
                        String movieId5 = reference5.push().getKey();
                        HashMap<String, Object> hashMap5 = new HashMap<>();
                        hashMap5.put("favoriteMovieName", movie.getMovieTitle());
                        hashMap5.put("favoriteMovieNameLower", movie.getMovieTitle().toLowerCase());
                        hashMap5.put("favoriteMoviePoster", movie.getMoviePosterResource());
                        hashMap5.put("favoriteUserName", userNameHorizontal);
                        hashMap5.put("favoriteJsonObject",movie.getJsonObject());

                        //hashMap5.put("favoriteUserCountry", userCountry);
                        //hashMap5.put("favoriteUserImage",imageUrl);
                        hashMap5.put("favoriteUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap5.put("favoritePostId", movieId5);
                        reference5.child(movieId5).setValue(hashMap5);

                        ((Activity) mContext).finish();
                        /*Intent intent1 = new Intent(mContext, StartActivity.class);
                        mContext.startActivity(intent1);*/

                        //Toast.makeText(mContext, "Added to your profile!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                //int position = getLayoutPosition();
                //Movie movie = moviesList.get(position);  //get selected movie
                Context context = view.getContext();  //Activity context
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, movie.getJsonObject());  //parameter for activity
                context.startActivity(intent);  //starting next activity MovieDetailsAc

                return true;
            }
        });


    }




    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView moviePoster;
        private TextView movieTitle;

        //Used to pass information to second activity via an intent
        public static final String EXTRA_MESSAGE = "com.yeslabapps.fictionfocus.extra.MESSAGE";


        public MovieHolder(View itemView) {
            super(itemView);


            moviePoster = itemView.findViewById(R.id.movie_poster);
            movieTitle = itemView.findViewById(R.id.movie_title);

            itemView.setOnClickListener(this);
        }

        public void setMovieTitle(String name) {
            movieTitle.setVisibility(View.INVISIBLE);
            //movieTitle.setText(name);
        }


        public void setMoviePoster(String uri) {
            if(uri.equals("N/A")){
                moviePoster.setImageResource(R.drawable.no_film_photo);
            }else{
                Picasso.get().load(uri).into(moviePoster);
            }
        }


        @Override
        public void onClick(View v) {
        }




    }

}

