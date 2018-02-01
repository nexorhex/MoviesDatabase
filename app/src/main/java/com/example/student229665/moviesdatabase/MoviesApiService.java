package com.example.student229665.moviesdatabase;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Student229665 on 1/17/2018.
 */

public interface MoviesApiService {

    @GET("/movie/popular")
    void getPopularMovies(Callback<Movie.MovieResult> cb);

    @GET("/movie/top_rated")
    void getTopRatedMovies(Callback<Movie.MovieResult> cb);

}
