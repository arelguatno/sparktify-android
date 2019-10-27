package com.example.appdev.sparktify2019.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.appdev.sparktify2019.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerFindRoom extends BaseActivity {
    ListView listView;
    private static final String TAG = "MultiplayerFindRoom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_find_room);

        listView = (ListView) findViewById(R.id.list);

        db = FirebaseFirestore.getInstance();


        db.collection("game_room").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        String[] values = new String[value.size()];
                        int i = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.getId() != null) {
                                values[i] = doc.getId();
                                i +=1;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_list_item_1, android.R.id.text1, values);

                        // Assign adapter to ListView
                        listView.setAdapter(adapter);
                    }
                });

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);


                Intent intent = new Intent(view.getContext(), MultiplayerActivity.class);
                Bundle mBundle = new Bundle();

                mBundle.putString("roomName", itemValue);

                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }

        });
    }
}
