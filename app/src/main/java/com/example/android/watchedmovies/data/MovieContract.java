package com.example.android.watchedmovies.data;

import android.content.ContentResolver;
import android.net.Uri;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.watchedmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry {
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);
        public static final String TABLE_NAME = "movies";
        //columns
        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String GENRE = "genre";
        public static final String RATING = "rating";
        public static final String REVIEW = "review";

    }
}
