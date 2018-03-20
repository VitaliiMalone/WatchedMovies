package com.example.android.watchedmovies;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.android.watchedmovies.data.MovieContract.MovieEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EDITOR_LOADER = 0;

    private EditText movieTitle;
    private EditText movieGenre;
    private EditText movieReview;
    private RatingBar movieRating;

    private Uri currentUri;

    private boolean movieHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            movieHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        movieTitle = findViewById(R.id.movie_title);
        movieGenre = findViewById(R.id.movie_genre);
        movieReview = findViewById(R.id.movie_review);
        movieRating = findViewById(R.id.movie_rating);

        movieTitle.setOnTouchListener(touchListener);
        movieGenre.setOnTouchListener(touchListener);
        movieReview.setOnTouchListener(touchListener);
        movieRating.setOnTouchListener(touchListener);

        currentUri = getIntent().getData();
        if (currentUri != null) {
            setTitle(R.string.edit_movie);
            getLoaderManager().initLoader(EDITOR_LOADER, null, this);
        }
        invalidateOptionsMenu();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            menu.findItem(R.id.editor_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editor_save:
                saveMovie();
                finish();
                return true;
            case R.id.editor_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!movieHasChanged) {
                    super.onBackPressed();
                    return true;
                }
                showDiscardAlertDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDiscardAlertDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_discard_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (!movieHasChanged) {
            super.onBackPressed();
            return;
        }

        showDiscardAlertDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deletePet() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);
            if (rowsDeleted == 0)
                Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void saveMovie() {
        String titleString = movieTitle.getText().toString().trim();
        String genreString = movieGenre.getText().toString().trim();
        float ratingFloat = movieRating.getRating();
        String reviewString = movieReview.getText().toString().trim();

        if (currentUri == null
                && TextUtils.isEmpty(titleString)
                && TextUtils.isEmpty(genreString)
                && ratingFloat == 0
                && TextUtils.isEmpty(reviewString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MovieEntry.TITLE, movieTitle.getText().toString().trim());
        values.put(MovieEntry.GENRE, movieGenre.getText().toString().trim());
        values.put(MovieEntry.RATING, movieRating.getRating());
        values.put(MovieEntry.REVIEW, movieReview.getText().toString().trim());

        if (currentUri == null) {
            Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

            if (uri == null) Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();

        } else {
            int rowsUpdated = getContentResolver().update(currentUri, values, null, null);

            if (rowsUpdated == 0)
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String projection[] = {
                MovieEntry._ID,
                MovieEntry.TITLE,
                MovieEntry.GENRE,
                MovieEntry.RATING,
                MovieEntry.REVIEW};
        return new CursorLoader(this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            String title = data.getString(data.getColumnIndex(MovieEntry.TITLE));
            String genre = data.getString(data.getColumnIndex(MovieEntry.GENRE));
            int rating = data.getInt(data.getColumnIndex(MovieEntry.RATING));
            String review = data.getString(data.getColumnIndex(MovieEntry.REVIEW));

            movieTitle.setText(title);
            movieGenre.setText(genre);
            movieRating.setRating((float) rating);
            movieReview.setText(review);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
