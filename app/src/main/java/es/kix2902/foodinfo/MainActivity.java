package es.kix2902.foodinfo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Boolean expandedFab = false;
    private FloatingActionButton fabBarcode, fabName, fabSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(this);

        fabBarcode = (FloatingActionButton) findViewById(R.id.fabBarcode);
        fabName = (FloatingActionButton) findViewById(R.id.fabName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabSearch:
                if (expandedFab) {
                    collapseFab();
                } else {
                    expandFab();
                }
                break;
        }
    }

    private void expandFab() {
        fabBarcode.setVisibility(View.VISIBLE);
        fabName.setVisibility(View.VISIBLE);
        fabSearch.setImageResource(R.drawable.ic_close);
        expandedFab = true;
    }

    private void collapseFab() {
        fabBarcode.setVisibility(View.GONE);
        fabName.setVisibility(View.GONE);
        fabSearch.setImageResource(R.drawable.ic_search);
        expandedFab = false;
    }
}
