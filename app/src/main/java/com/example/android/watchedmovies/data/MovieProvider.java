package com.example.android.watchedmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.watchedmovies.data.MovieContract.MovieEntry;


public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();
    public static final int MOVIES = 100;
    public static final int MOVIE_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieDbHelper dbHelper;

    static {
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                return insertMovie(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values) {
        String title = values.getAsString(MovieEntry.TITLE);
        if (title == null) throw new IllegalArgumentException("Title cannot be null");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(MovieEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Cannot delete " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                return updateMovie(uri, values, selection, selectionArgs);
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMovie(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(MovieEntry.TITLE)) {
            String title = values.getAsString(MovieEntry.TITLE);
            if (title == null) throw new IllegalArgumentException("Title cannot be null");
        }
        if (values.size() == 0) return 0;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
