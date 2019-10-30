package com.example.appdev.sparktify2019.activities;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class BaseActivity extends AppCompatActivity {
    public ProgressDialog mProgressDialog;

    private int bgMusicLength = 0;
    static boolean iBgMusicsPlaying = false;

    protected FirebaseFirestore db;
    protected FirebaseAuth mAuth;
    protected FirebaseAnalytics mFirebaseAnalytics;



    public static final String SONG_SPACE = "_";

    // Collections
    static final String DB_LIST_OF_SONGS = "list_of_songs";
    static final String DB_LIST_OF_GENRES = "list_of_genres";
    static final String DB_USERS_COLLECTION_NAME = "users";


    //Documents
    static final String DB_GENRES = "genres";


    // Sub Collections
    static final String DB_POP = "pop";

    // Fields
    static final String DB_LAST_LOGIN_FIELD = "lastLogin";

    public static String characterName = "";


    protected void setToFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public static int getImageIdByName(Context c, String ImageName) {
        return c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName());
    }

    public static int getSongIdByName(Context c, String ImageName) {
        return c.getResources().getIdentifier(ImageName, "raw", c.getPackageName());
    }

    public static String getCharacterName() {
        return characterName;
    }

    public static void setCharacterName(String characterName) {
        BaseActivity.characterName = characterName;
    }
}
