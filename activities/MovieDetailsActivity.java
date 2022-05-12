package com.yeslabapps.fictionfocus.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.yeslabapps.fictionfocus.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.adapter.MovieAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView headerText; //Movie title serving as page header
    private String jsonString;  //The json object sent in from previous activity
    private RequestQueue requestQueue;  //Volley request queue
    private String imdbID;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);


        headerText = findViewById(R.id.header);
        Intent intent = getIntent();
        jsonString = intent.getStringExtra(MovieAdapter.MovieHolder.EXTRA_MESSAGE);

        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        pd = new ProgressDialog(MovieDetailsActivity.this,R.style.CustomDialog);
        //pd.setMessage("Loading");
        pd.setCancelable(false);

        pd.show();


        JSONObject jsonObject;
        try {
            if (!jsonString.equals("")) {
                jsonObject = new JSONObject(jsonString);
                //headerText.setText(jsonObject.toString());

                imdbID = jsonObject.getString("imdbID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(this);

        Thread thread = new Thread(new VolleyRequestThread());
        thread.start();
    }

    class VolleyRequestThread implements Runnable {

        private TextView titleText;
        private TextView runtimeText;
        private TextView directorText;
        private TextView genreText;
        private TextView actorsText;
        private TextView imdbRatingText;
        private ImageView moviePoster;
        private TextView overviewText;
        private TextView writerText;
        private TextView countryText;

        public void getReferencesForViews() {
            titleText = findViewById(R.id.header);
            runtimeText = findViewById(R.id.runtime);
            directorText = findViewById(R.id.director);
            genreText = findViewById(R.id.genre);
            actorsText = findViewById(R.id.actors);
            imdbRatingText = findViewById(R.id.imdb_score);
            moviePoster = findViewById(R.id.poster);
            overviewText = findViewById(R.id.overview);
            writerText = findViewById(R.id.writer);
            countryText = findViewById(R.id.countryFilm);
        }

        public void run() {
            getReferencesForViews();

            //Actual request handled by Volley
            try {
                JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(
                                Request.Method.GET,
                                "https://www.omdbapi.com/?apikey=713b0aa0&plot=full&i=" + imdbID,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("JSON Details response", response.toString());
                                        try {
                                            pd.dismiss();
                                            //Parsing JSON response for relevant data
                                            String title = response.getString("Title") +
                                                    " - " + response.getString("Year");
                                            String runtime = response.getString("Runtime");
                                            String director = response.getString("Director");
                                            String writer = response.getString("Writer");
                                            String genre = response.getString("Genre");
                                            String actors = response.getString("Actors");
                                            String imdbRating = response.getString("imdbRating");
                                            String poster = response.getString("Poster");
                                            String plot = response.getString("Plot");
                                            String country = response.getString("Country");

                                            //Setting the data in the view elements
                                            titleText.setText(title);
                                            runtimeText.setText(runtime+"s");
                                            genreText.setText(genre);
                                            actorsText.setText("Main Cast: "+actors);
                                            imdbRatingText.setText("IMDB: "+imdbRating);
                                            overviewText.setText("Overview"+"\n"+plot);
                                            countryText.setText(country);


                                            if (director.equals("N/A")){
                                                directorText.setText("");
                                            }else{
                                                directorText.setText("Director: "+director);
                                            }

                                            if (writer.equals("N/A")){
                                                writerText.setText("");
                                            }else{
                                                writerText.setText("Writer: "+writer);
                                            }


                                            if(poster.equals("N/A")){
                                                moviePoster.setImageResource(R.drawable.person_24);
                                            }else{
                                                Picasso.get().load(poster).into(moviePoster);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Request Error", error.toString());
                                    }
                                }
                        );
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}