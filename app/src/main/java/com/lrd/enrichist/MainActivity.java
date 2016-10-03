package com.lrd.enrichist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstellationView constellationView = (ConstellationView) findViewById(R.id.stars);
        constellationView.move();
    }
}
