package com.yeslabapps.fictionfocus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.fictionfocus.R;
import com.yeslabapps.fictionfocus.adapter.MovieAdapter;
import com.yeslabapps.fictionfocus.model.Movie;
import com.yeslabapps.fictionfocus.util.NetworkChangeListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

public class SearchActivity extends AppCompatActivity {


    private  String title;


    private String movieTitle;

    private RequestQueue requestQueue;
    private AutoCompleteTextView searchBar;
    private ImageView searchButton;

    private MovieAdapter movieAdapter;
    private ArrayList<Movie> moviesList = new ArrayList<>();
    private RecyclerView recyclerView;

    private ProgressDialog pd;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchBar = findViewById(R.id.movie_search);
        searchButton = findViewById(R.id.search_button);


        searchBar.setText(movieTitle);

        recyclerView = findViewById(R.id.movie_list);
        movieAdapter = new MovieAdapter(moviesList, this);
        recyclerView.setAdapter(movieAdapter);
        GridLayoutManager layoutManager=new GridLayoutManager(SearchActivity.this,3);

        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbarMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMovie();
            }
        });

        searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction()==KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    searchMovie();
                    return true;
                }
                return false;
            }
        });

  /*      DatabaseReference reference = FirebaseDatabase.getInstance("https://fictionary-d52fc-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


        final ArrayAdapter<String> auto = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        reference.child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot suggestionSnapshot : snapshot.getChildren()) {
                    String suggestion = suggestionSnapshot.child("searchTitle").getValue(String.class);
                    auto.add(suggestion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchBar.setAdapter(auto);
*/





        pd = new ProgressDialog(SearchActivity.this,R.style.CustomDialog);
        pd.setCancelable(false);



        requestQueue = Volley.newRequestQueue(this);

        TextView textView=findViewById(R.id.app_header);


        Intent intent = SearchActivity.this.getIntent();
        title = intent.getStringExtra("TITLE");


        switch (title){

            case "List":
                textView.setText("Add movie or series for your selected list.");
                break;

            case "Favorites":
                textView.setText("Add movies or series to your favorites by tapping. Long press for details.");
                break;

            case "Movies":
                textView.setText("Add movies you watched by tapping. Long press for details.");
                break;

            case "Series":
                textView.setText("Add series you watched by tapping. Long press for details.");

                break;


            case "Horizontal":
                textView.setText("Add the movie or series you want to appear on your profile by tapping (You can add up to 5 of them.)");

                break;
        }


    }

    private void searchMovie(){
        movieTitle = searchBar.getText().toString();
        pd.show();

        if (!movieTitle.matches("")) {
            moviesList.clear();
            //Web request handled through separate thread
            Thread thread = new Thread(new VolleyRequestThread());
            thread.start();
        } else {
            pd.dismiss();
            StyleableToast.makeText(SearchActivity.this, "Type movie or series name",R.style.customToast).show();
        }
    }

    //Used for saving search term to shared preferences file
    /*@Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(TITLE_KEY, movieTitle);
        editor.apply();
    }
*/

    //onClick handler for the search button
    /*public void SearchMovies(View view) {
        movieTitle = searchBar.getText().toString();

        //So long as user actually typed in a search term, request movies from API
        if (!movieTitle.matches("")) {
            moviesList.clear();
            //Web request handled through separate thread
            Thread thread = new Thread(new VolleyRequestThread());
            thread.start();
        } else {
            Toast.makeText(this, "Please enter a movie name", Toast.LENGTH_SHORT).show();
        }

    }*/
//https://www.omdbapi.com/?apikey=52c203ce&s=
//713b0aa0
    class VolleyRequestThread implements Runnable {
        public void run() {
            try {
                JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(
                                Request.Method.GET,
                                "https://www.omdbapi.com/?apikey=713b0aa0&s=" + movieTitle,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("JSON response", response.toString());
                                        try {
                                            pd.dismiss();
                                            JSONArray jsonArray = response.getJSONArray("Search");
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                String tempURI  = jsonObject.getString("Poster");
                                                String tempTitle  = jsonObject.getString("Title") +
                                                        " - " + jsonObject.getString("Year");

                                                moviesList.add(new Movie
                                                        (tempURI, tempTitle ,
                                                        jsonObject.toString()));
                                            }
                                            movieAdapter.notifyDataSetChanged();
                                        } catch (JSONException e) {
                                            try {
                                                Toast.makeText(SearchActivity.this, response.getString("Error"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
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