/*package com.yeslabapps.fictionfocus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.MovieAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class BottomMovieDetails extends BottomSheetDialogFragment {

    private TextView headerText; //Movie title serving as page header
    private String jsonString;  //The json object sent in from previous activity
    private RequestQueue requestQueue;  //Volley request queue
    private String imdbID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_movie_details, container, false);


        headerText = view.findViewById(R.id.header);
        Intent intent = getActivity().getIntent();
        jsonString = intent.getStringExtra(MovieAdapter.MovieHolder.EXTRA_MESSAGE);

        JSONObject jsonObject;
        try {
            if (!jsonString.equals("")) {
                jsonObject = new JSONObject(jsonString);
                headerText.setText(jsonObject.toString());

                imdbID = jsonObject.getString("imdbID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(getContext());

        //Seperate thread used to query the API for movie details
        Thread thread = new Thread(new VolleyRequestThread());
        thread.start();


        return view;

    }

    class VolleyRequestThread implements Runnable {

        //The eight view elements used for portraying the movie details
        private TextView titleText;
        private TextView runtimeText;
        private TextView ratedText;
        private TextView genreText;
        private TextView actorsText;
        private TextView imdbRatingText;
        private ImageView moviePoster;
        private TextView overviewText;

        //This method is to bind the view elements with the class fields
        public void GetReferencesForViews() {
            titleText = getActivity().findViewById(R.id.header);
            runtimeText =  getActivity().findViewById(R.id.runtime);
            ratedText =  getActivity().findViewById(R.id.rated);
            genreText =  getActivity().findViewById(R.id.genre);
            actorsText =  getActivity().findViewById(R.id.actors);
            imdbRatingText =  getActivity().findViewById(R.id.imdb_score);
            moviePoster =  getActivity().findViewById(R.id.poster);
            overviewText =  getActivity().findViewById(R.id.overview);
        }

        public void run() {
            GetReferencesForViews();

            //Actual request handled by Volley
            try {
                JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(
                                Request.Method.GET,
                                "https://www.omdbapi.com/?apikey=713b0aa0&i=" + imdbID,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("JSON Details response", response.toString());
                                        try {
                                            //Parsing JSON response for relevant data
                                            String title = response.getString("Title") +
                                                    " - " + response.getString("Year");
                                            String runtime = response.getString("Runtime");
                                            String rated = response.getString("Rated");
                                            String genre = response.getString("Genre");
                                            String actors = response.getString("Actors");
                                            String imdbRating = response.getString("imdbRating");
                                            String poster = response.getString("Poster");
                                            String plot = response.getString("Plot");

                                            //Setting the data in the view elements
                                            titleText.setText(title);
                                            runtimeText.append(runtime);
                                            ratedText.append(rated);
                                            genreText.append(genre);
                                            actorsText.append("\n" + actors);
                                            imdbRatingText.append(imdbRating);
                                            overviewText.append("\n" + plot);

                                            //Picasso Library used as a way to set ImageView resource
                                            //to an online image
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

*/

