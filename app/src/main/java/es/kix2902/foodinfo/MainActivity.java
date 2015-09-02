package es.kix2902.foodinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
        fabBarcode.setOnClickListener(this);

        fabName = (FloatingActionButton) findViewById(R.id.fabName);
        fabName.setOnClickListener(this);
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

            case R.id.fabBarcode:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
                collapseFab();
                break;

            case R.id.fabName:
                new MaterialDialog.Builder(this)
                        .title(R.string.dialog_title)
                        .content(R.string.dialog_content)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(R.string.dialog_hint, R.string.dialog_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (!TextUtils.isEmpty(input)) {
                                    Intent intent = new Intent(MainActivity.this, SelectorActivity.class);
                                    intent.putExtra(SelectorActivity.NAME, input);
                                    startActivity(intent);
                                    collapseFab();
                                }
                            }
                        }).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            Intent intent = new Intent(MainActivity.this, SelectorActivity.class);
            intent.putExtra(SelectorActivity.CODE, scanResult.getContents());
            startActivity(intent);
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
