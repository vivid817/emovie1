package j2.com.emovie.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import j2.com.emovie.adapter.ImageAdapter;

import j2.com.emovie.R;

public class MainActivityFragment extends Fragment {
  private static final String LOG_TAG = "main";
  private String forecastJsonStr = null;
  private JSONArray MovieArray;
  private ArrayList<String> movieList;
  private ImageAdapter movieAdapter;
  private GridView gridView;
  private final static String IMAGE_BASE_URL ="http://image.tmdb.org/t/p/";
  private final static String IMAGE_W342="w342/";

  private final static String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
  private final static String SORT_BY_PARAM = "sort_by";//sort_by=popularity.desc
  private final static String API_KEY = "api_key";
  private final static String API_KEY_VALUE = "ab91b285830d89f96eb22fc5bcce9e7c";
  private final static String IMAGE_SRC = "poster_path";
  private final static String ROOT_NODE = "results";


  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }


  @Override
  public void onResume() {
    super.onResume();
    movieList.clear();
    FetchMovieTask movieTask = new FetchMovieTask();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
    String sortBy = prefs.getString(getString(R.string.pref_sort_by_key),
        getString(R.string.pref_sort_by_default));
    movieTask.execute(sortBy);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View layout =  inflater.inflate(R.layout.main_fragment, container, false);
    gridView = (GridView) layout.findViewById(R.id.movieGrid);

    movieList = new ArrayList<String>();
    movieAdapter = new ImageAdapter(this.getActivity(), movieList);
    gridView.setAdapter(movieAdapter);

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v,
                              int position, long id) {

        String movieInfoStr = "";
        String movieId = "";
        JSONObject MovieObj = null;
        try {
          MovieObj = MovieArray.getJSONObject(position);
          movieInfoStr = MovieObj.toString();
          movieId = MovieObj.getString("id");

        } catch (JSONException e) {

        }

        Intent intent = new Intent(getActivity(), MovieDetail.class);
        intent.putExtra("movieInfoStr", movieInfoStr);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
      }
    });
    return layout;
  }

  public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

    @Override
    protected String[] doInBackground(String... params) {
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;
      try {
        Uri builUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
            .appendQueryParameter(SORT_BY_PARAM, params[0])
            .appendQueryParameter(API_KEY,API_KEY_VALUE)
            .build();
        URL url = new URL(builUri.toString());

        // Create the request to OpenWeatherMap, and open the connection
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        Log.d(LOG_TAG, url.toString());

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
          // Nothing to do.
          return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
          // Stream was empty.  No point in parsing.
          return null;
        }
        forecastJsonStr = buffer.toString();

      } catch (IOException e) {
        Log.e("PlaceholderFragment", "Error ", e);
        // If the code didn't successfully get the weather data, there's no point in attemping
        // to parse it.
        return null;
      } finally{
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e("PlaceholderFragment", "Error closing stream", e);
          }
        }
      }
      try {
        return getMovieDataFromJson(forecastJsonStr);
      } catch (JSONException e) {
        Log.e(LOG_TAG, e.getMessage(),e);

        e.printStackTrace();
        return null;
      }

    }

    protected void onPostExecute(String[] result) {

      if(result !=null){
        movieList.clear();

        for (String movieUrl:result){
          movieList.add(movieUrl);
          //Log.d(LOG_TAG,"----:"+movieUrl);
          movieAdapter.notifyDataSetChanged();
        }
      }
    }
  }

  private String[] getMovieDataFromJson(String MovieJsonStr)
      throws JSONException {

    JSONObject MoiveJson = new JSONObject(MovieJsonStr);
    MovieArray = MoiveJson.getJSONArray(ROOT_NODE);

    String[] resultStrs = new String[MovieArray.length()];

    for(int i = 0; i < MovieArray.length(); i++) {
      JSONObject MovieObj = MovieArray.getJSONObject(i);

      String url = MovieObj.getString(IMAGE_SRC);
      //Log.d(LOG_TAG,"---:"+url);
      resultStrs[i] = IMAGE_BASE_URL + IMAGE_W342 + url;
    }
    return resultStrs;

  }
}
