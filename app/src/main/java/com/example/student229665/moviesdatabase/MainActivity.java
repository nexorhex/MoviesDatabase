package com.example.student229665.moviesdatabase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    public List<Movie> movies, favorites;
    public static final String LOG_TAG = MoviesAdapter.class.getName();
    private AppCompatActivity activity = MainActivity.this;
    private FavoriteDbHelper favoriteDbHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
    }

    private RestAdapter initializeRestAdapter() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", "bd1f0e5dee0add3ba530f428de04c3cb");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }

    public void initializeRetrofit() {
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );

        MoviesApiService service = initializeRestAdapter().create(MoviesApiService.class);
        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            service.getPopularMovies(new Callback<Movie.MovieResult>() {

                @Override
                public void success(Movie.MovieResult movieResult, Response response) {
                    mAdapter.setmMovieList(movieResult.getResults());
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        } else if (sortOrder.equals(this.getString(R.string.pref_highest_rated))) {
            service.getTopRatedMovies(new Callback<Movie.MovieResult>() {
                @Override
                public void success(Movie.MovieResult movieResult, Response response) {
                    mAdapter.setmMovieList(movieResult.getResults());
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        } else if (sortOrder.equals(this.getString(R.string.alphabetical_popular))) {
            service.getPopularMovies(new Callback<Movie.MovieResult>() {

                @Override
                public void success(Movie.MovieResult movieResult, Response response) {
                    List<Movie> movies = movieResult.getResults();
                    Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                    mAdapter.setmMovieList(movies);
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        } else if (sortOrder.equals(this.getString(R.string.alphabetical_top))) {
            service.getTopRatedMovies(new Callback<Movie.MovieResult>() {
                @Override
                public void success(Movie.MovieResult movieResult, Response response) {
                    List<Movie> movies = movieResult.getResults();
                    Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                    mAdapter.setmMovieList(movies);
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }
    }

    private void initViewsFavorites() {
        favorites = favoriteDbHelper.getAllFavorite();
        for (int i = 0; i < favorites.size(); i++) {
            String s = favorites.get(i).getPoster().substring(90); //konieczne, żeby prawidlowo generowac linki plakatow
            favorites.get(i).setPoster(s);
        }
        mAdapter.setmMovieList(favorites);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        movies = new ArrayList<>();
        mAdapter = new MoviesAdapter(this, movies);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        favoriteDbHelper = new FavoriteDbHelper(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(LOG_TAG, "Preferences updated");
        checkSortOrder();
    }

    private void checkSortOrder() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.pref_most_popular)) ||
                sortOrder.equals(this.getString(R.string.pref_highest_rated)) ||
                sortOrder.equals(this.getString(R.string.alphabetical_popular)) ||
                sortOrder.equals(this.getString(R.string.alphabetical_top))) {

            initializeRetrofit();

        } else if (sortOrder.equals(this.getString(R.string.favorite))) {
            Log.d(LOG_TAG, "Sorting by favorites");
            initViewsFavorites();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (movies.isEmpty()) {
            checkSortOrder();
        } else {

        }
    }

    public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

        private List<Movie> mMovieList;
        private LayoutInflater mInflater;
        public Context mContext;

        public MoviesAdapter(Context context, List<Movie> movies) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mMovieList = new ArrayList<>();
        }

        public class MovieViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;

            public MovieViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            Movie clickedDataItem = mMovieList.get(pos);
                            Intent intent = new Intent(mContext, MovieDetailActivity.class);
                            intent.putExtra("original_title", mMovieList.get(pos).getTitle());
                            intent.putExtra("poster_path", mMovieList.get(pos).getPoster());
                            intent.putExtra("overview", mMovieList.get(pos).getDescription());
                            intent.putExtra("backdrop", mMovieList.get(pos).getBackdrop());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            Toast.makeText(view.getContext(), "You clicked " + clickedDataItem.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_movie, parent, false);
            MovieViewHolder viewHolder = new MovieViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            Movie movie = mMovieList.get(position);
            Picasso.with(mContext)
                    .load(movie.getPoster())
                    .placeholder(R.color.colorPrimaryDark)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            int itemCount;
            if (mMovieList == null)
                itemCount = 0;
            else
                itemCount = mMovieList.size();

            return itemCount;
        }

        public void setmMovieList(List<Movie> movieList) {
            this.mMovieList.clear();
            this.mMovieList.addAll(movieList);
            notifyDataSetChanged(); //info dla adaptera
        }
    }
}

