package com.example.appdev.sparktify2019.activities;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.appdev.sparktify2019.R;
import com.example.appdev.sparktify2019.fragments.TwoPlayerLocal;
import com.example.appdev.sparktify2019.interfaces.TwoPlayerLocal_ViewEvents;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocalMultiplayerActivity extends BaseActivity implements TwoPlayerLocal_ViewEvents {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToFullScreen();
        // Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_offline_multiplayer);

        if (findViewById(R.id.fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            TwoPlayerLocal firstFragment = new TwoPlayerLocal();
            // firstFragment.setArguments(bundle);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onback_press() {

    }

}
