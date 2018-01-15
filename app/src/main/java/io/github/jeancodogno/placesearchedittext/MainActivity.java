package io.github.jeancodogno.placesearchedittext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private PlaceSearchEditText place_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.place_search = (PlaceSearchEditText) findViewById(R.id.place_search);

    }
}
