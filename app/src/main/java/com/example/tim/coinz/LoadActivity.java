package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        DownloadFileTask task = new DownloadFileTask();
        DownloadCompleteRunner.setCurrent(LoadActivity.this);
        task.execute("http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson");
    }
}


