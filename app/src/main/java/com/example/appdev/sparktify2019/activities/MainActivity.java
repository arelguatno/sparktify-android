package com.example.appdev.sparktify2019.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.example.appdev.sparktify2019.R;
import com.example.appdev.sparktify2019.pojos.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;


public class MainActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = "MainActivity";

    private CallbackManager mCallbackManager;
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full Screen
        FacebookSdk.sdkInitialize(getApplicationContext());
        setToFullScreen();
        setContentView(R.layout.activity_main);
//        Crashlytics.getInstance().crash(); // Force a crash

        findViewById(R.id.imageView).setVisibility(View.GONE);
        findViewById(R.id.imageView3).setVisibility(View.GONE);
        findViewById(R.id.imageView4).setVisibility(View.GONE);
        findViewById(R.id.textView2).setVisibility(View.GONE);
        findViewById(R.id.textView).setVisibility(View.GONE);
        findViewById(R.id.imageView5).setVisibility(View.GONE);
        findViewById(R.id.imageView6).setVisibility(View.GONE);


        if (!iBgMusicsPlaying) {

            iBgMusicsPlaying = true;
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        findViewById(R.id.signInButton).setOnClickListener(this);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]

        // load username
        loadUserProfile();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void loadUserProfile() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("users").document(mAuth.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            setCharacterName(document.getString("displayName"));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void userProfileOnClick(View v) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.user_profile, null);
        final EditText displayName = mView.findViewById(R.id.userNameText);

        displayName.setFocusable(true);
        displayName.setFocusableInTouchMode(true);
        displayName.requestFocus();

        Button saveButton = mView.findViewById(R.id.saveProfileButton);
        Button cancelProfileButton = mView.findViewById(R.id.cancelProfileButton);

        Button buyPremiumButton = mView.findViewById(R.id.buyPremiumButton);

        mBuilder.setView(mView);
        mBuilder.setCancelable(false);

        final AlertDialog alertDialog = mBuilder.create();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dd = displayName.getText().toString();
                dd = dd.replaceAll("\\s+", "");

                if (!dd.isEmpty()) {
                    db.collection("users").document(mAuth.getUid())
                            .update("displayName", dd);
                    loadUserProfile();
                    alertDialog.cancel();
                    Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });


        Glide.with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .into((ImageView) mView.findViewById(R.id.imageProfile));

        //ShowDisplayName
        displayName.setText(getCharacterName());
        displayName.setSelection(getCharacterName().length());
        alertDialog.show();

        buyPremiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();

                Intent intent = new Intent(getApplicationContext(), PremiumVersionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    public void play_button(View view) {
        Intent intent = new Intent(this, GenresActivity.class);
        this.startActivity(intent);
    }

    public void play_multiplayer(View v) {
        Intent intent = new Intent(this, LocalMultiplayerActivity.class);
        this.startActivity(intent);
    }

    public void play_multiplayer2(View v) {
        Intent intent = new Intent(this, MultiplayerFindRoom.class);
        this.startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            updateDBUserLastLogin(user);
            findViewById(R.id.signInButton).setVisibility(View.GONE);
            findViewById(R.id.button_facebook_login).setVisibility(View.GONE);
            findViewById(R.id.imageView).setVisibility(View.VISIBLE);
            findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
            findViewById(R.id.imageView4).setVisibility(View.VISIBLE);
            findViewById(R.id.textView2).setVisibility(View.VISIBLE);
            findViewById(R.id.textView).setVisibility(View.VISIBLE);
            findViewById(R.id.imageView5).setVisibility(View.VISIBLE);
            findViewById(R.id.imageView6).setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(mAuth.getCurrentUser().getPhotoUrl())
                    .into((ImageView) findViewById(R.id.imageView));

        } else {
            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.button_facebook_login).setVisibility(View.VISIBLE);

            findViewById(R.id.imageView).setVisibility(View.GONE);
            findViewById(R.id.imageView3).setVisibility(View.GONE);
            findViewById(R.id.imageView4).setVisibility(View.GONE);
            findViewById(R.id.textView2).setVisibility(View.GONE);
            findViewById(R.id.textView).setVisibility(View.GONE);
            findViewById(R.id.imageView5).setVisibility(View.GONE);
            findViewById(R.id.imageView6).setVisibility(View.GONE);

        }
    }

    public void logout_facebook(View v) {
        final

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Logged in as : " + mAuth.getCurrentUser().getDisplayName());

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                // Google sign out
                mGoogleSignInClient.signOut();

                updateUI(null);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserInfotoDB(user, task);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog("Logging in..");
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserInfotoDB(user, task);
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    private void updateDBUserLastLogin(FirebaseUser user) {
        db.collection(DB_USERS_COLLECTION_NAME)
                .document(user.getUid())
                .update(DB_LAST_LOGIN_FIELD, System.currentTimeMillis());
    }

    private void saveUserInfotoDB(FirebaseUser user, Task<AuthResult> task) {
        // Save user to Firestore
        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
            // function will trigger automatically

            Bundle params = new Bundle();
            params.putString("new_user", getCharacterName());
            params.putString("full_text", "asdasd");
            mFirebaseAnalytics.logEvent("new_user", params);

        } else {
            updateDBUserLastLogin(user);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        }
    }

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

}
