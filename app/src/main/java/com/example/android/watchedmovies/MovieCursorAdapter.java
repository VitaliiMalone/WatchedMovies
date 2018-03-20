package com.example.android.watchedmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.watchedmovies.data.MovieContract;

public class MovieCursorAdapter extends CursorAdapter {

    public MovieCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = view.findViewById(R.id.title_item);
        TextView genre = view.findViewById(R.id.genre_item);
        RatingBar rating = view.findViewById(R.id.rating_item);

        title.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE)));
        genre.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.GENRE)));
        rating.setRating((float) cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.RATING)));
    }
}
