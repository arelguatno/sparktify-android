package com.example.appdev.sparktify2019;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}