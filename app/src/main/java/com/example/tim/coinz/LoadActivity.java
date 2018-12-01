package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class LoadActivity extends AppCompatActivity {
    public static FirebaseListener firebaseListener = new FirebaseListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        String userId = getIntent().getStringExtra("userId");
        firebaseListener.downloadData(LoadActivity.this, userId);
    }

    public void onCompleteDownloadFirebaseData() {
        // start download map after firebase data is finished
        DownloadFileTask task = new DownloadFileTask(LoadActivity.this);
        task.execute("http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson");
    }
}


