package com.example.android.watchedmovies;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.watchedmovies.data.MovieContract.MovieEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER = 0;

    MovieCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.listView);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        cursorAdapter = new MovieCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.add_random_movie):
                addRandomMovie();
                return true;
            case (R.id.delete_all_movies):
                showDeleteAllConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addRandomMovie() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.TITLE, "Game of Thrones");
        values.put(MovieEntry.GENRE, "Action, Adventure, Drama");
        values.put(MovieEntry.RATING, 5);
        values.put(MovieEntry.REVIEW, "Favorite TV show, can't wait for the last season");

        getContentResolver().insert(MovieEntry.CONTENT_URI, values);
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllMovies();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteAllMovies() {
        getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                MovieEntry._ID,
                MovieEntry.TITLE,
                MovieEntry.GENRE,
                MovieEntry.RATING};
        return new CursorLoader(this,
                MovieEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
