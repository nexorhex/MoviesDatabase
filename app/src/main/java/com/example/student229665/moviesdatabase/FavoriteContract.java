package com.example.student229665.moviesdatabase;

import android.provider.BaseColumns;

/**
 * Created by Student229665 on 1/18/2018.
 */

public class


FavoriteContract {

    public static final class FavoriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_PLOT_SYNOPSIS = "overview";

    }
}
