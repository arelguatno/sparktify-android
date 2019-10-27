package com.example.appdev.sparktify2019.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdev.sparktify2019.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MultiplayerActivity extends BaseActivity {
    static String roomName;
    static String playerName;
    TextView yourTurnText;

    private static final String TAG = "MultiplayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        roomName = getIntent().getExtras().getString("roomName");
        yourTurnText = findViewById(R.id.yourTurnText);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final Button customButton = findViewById(R.id.custom_button);

        Map<String, Object> city = new HashMap<>();
        city.put("increment", 1);

        Map<String, Object> city2 = new HashMap<>();
        city2.put("score", 0);
        city2.put("turn", false);
        city2.put("enable", true);

        FirebaseUser user = mAuth.getCurrentUser();
        playerName = user.getDisplayName().replaceAll("\\s+","");

        db.collection("game_room").document(roomName).collection("players").document(playerName)
                .set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        db.collection("game_room").document(roomName).collection("stats").document(playerName)
                .set(city2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        final DocumentReference docRef = db.collection("game_room").document(roomName).collection("stats").document(playerName);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    customButton.setEnabled(snapshot.getBoolean("enable"));

                    if(snapshot.getBoolean("turn")){
                        yourTurnText.setVisibility(View.VISIBLE);
                    }else{
                        yourTurnText.setVisibility(View.GONE);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public void onClick(View v){
        final MediaPlayer player = MediaPlayer.create(this,R.raw.sounds_effect_button);
        player.start();
        pressTheButtonToSendSignal();
    }

    private void pressTheButtonToSendSignal(){
        db.collection("game_room").document(roomName).collection("players").document(playerName)
                .update("increment",FieldValue.increment(1));
    }
}
