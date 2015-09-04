package es.kix2902.foodinfo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import es.kix2902.foodinfo.database.FoodContentProvider;
import es.kix2902.foodinfo.database.ProductsTable;
import es.kix2902.foodinfo.helpers.EmptyRecyclerView;
import es.kix2902.foodinfo.helpers.SimpleDividerItemDecoration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, DatabaseAdapter.OnItemClickHistoryListener {

    private DatabaseAdapter adapter;

    private Boolean expandedFab = false;
    private FloatingActionButton fabBarcode, fabName, fabSearch;

    private EmptyRecyclerView recycler;
    private TextView emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emptyLayout = (TextView) findViewById(R.id.empty);

        recycler = (EmptyRecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new SimpleDividerItemDecoration(MainActivity.this));
        recycler.setEmptyView(emptyLayout);

        fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(this);

        fabBarcode = (FloatingActionButton) findViewById(R.id.fabBarcode);
        fabBarcode.setOnClickListener(this);

        fabName = (FloatingActionButton) findViewById(R.id.fabName);
        fabName.setOnClickListener(this);

        adapter = new DatabaseAdapter(null, this);
        recycler.setAdapter(adapter);

        getSupportLoaderManager().restartLoader(0, null, this);
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
                                if (!TextUtils.isEmpty(input.toString())) {
                                    Intent intent = new Intent(MainActivity.this, SelectorActivity.class);
                                    intent.putExtra(SelectorActivity.NAME, input.toString());
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
        if (resultCode == RESULT_OK) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                Intent intent = new Intent(MainActivity.this, SelectorActivity.class);
                intent.putExtra(SelectorActivity.CODE, scanResult.getContents());
                startActivity(intent);
            }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FoodContentProvider.CONTENT_URI_PRODUCTS, null, null, null, ProductsTable.SORT_ORDER_DEFAULT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    public void OnItemClickHistory(Cursor cursor) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.PROD_NAME, cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_NAME)));
        intent.putExtra(DetailActivity.PROD_CODE, cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_CODE)));
        startActivity(intent);
    }
}
