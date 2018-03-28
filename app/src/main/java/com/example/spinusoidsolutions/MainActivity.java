package com.example.spinusoidsolutions;

import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.spinusoidsolutions.R;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button collectBtn;
    Button analyzeBtn;
    Button uploadBtn;
    ProgressBar progBar;
    ImageView greenChck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        CollectedData.collectedData = new FormattedSpineData(new Date(), 30);

        collectBtn = (Button) findViewById(R.id.collectButton);
        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent collectIntent = new Intent(MainActivity.this, CameraActivity.class);
//                Intent collectIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(collectIntent);
            }
        });

        analyzeBtn = (Button) findViewById(R.id.analyseButton);
        analyzeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(MainActivity.this, AnalyzeActivity.class);
                startActivity(activityIntent);
            }
        });

        uploadBtn = (Button) findViewById(R.id.uploadButton);
        progBar = (ProgressBar) findViewById(R.id.progressBar);
        greenChck = (ImageView) findViewById(R.id.green_check) ;

//        uploadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        progBar.setVisibility(View.VISIBLE);
//                        long start = System.currentTimeMillis();
//                        while(System.currentTimeMillis() - start < 2000){}
//
//                        progBar.setVisibility(View.INVISIBLE);
//                        greenChck.setVisibility(View.VISIBLE);
//                        start = System.currentTimeMillis();
//                        while(System.currentTimeMillis() - start < 1000){}
//
//                        greenChck.setVisibility(View.INVISIBLE);
//
//
//
//                    }
//                });
////            Jframe frame = new JFrame("test");
//            }
//        });
    }


}
