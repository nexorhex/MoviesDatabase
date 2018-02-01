package com.example.student229665.moviesdatabase;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Student229665 on 1/17/2018.
 */

public class Movie {

    public static final String TMDB_IMAGE_PATH = "http://image.tmdb.org/t/p/w500";

    private String title;

    @SerializedName("poster_path") //zeby GSON wiedzial co polaczyc z JSONem
    private String poster;

    @SerializedName("overview")
    private String description;

    @SerializedName("backdrop_path")
    private String backdrop;

    public Movie() {
        super();
    }

    public static final Comparator<Movie> BY_NAME_ALPHABETICAL = new Comparator<Movie>() {
        @Override
        public int compare(Movie movie, Movie t1) {

            return movie.title.compareTo(t1.title);
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return TMDB_IMAGE_PATH + poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackdrop() {
        return TMDB_IMAGE_PATH + backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public static class MovieResult {
        private List<Movie> results;

        public List<Movie> getResults() {
            return results;
        }
    }
}
