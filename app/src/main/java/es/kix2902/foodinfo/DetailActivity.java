package es.kix2902.foodinfo;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import es.kix2902.foodinfo.data.Nutrient;
import es.kix2902.foodinfo.database.FoodContentProvider;
import es.kix2902.foodinfo.database.ProductsTable;

public class DetailActivity extends AppCompatActivity {

    public static final String PROD_NAME = "name";
    public static final String PROD_CODE = "ndbno";
    public static final String PROD_IMG = "image";
    public static final String BARCODE = "barcode";

    private String name, code;

    private OkHttpClient client;

    private ArrayList<Nutrient> nutrients;
    private TableLayout tableNutrients;

    private ProgressBar loading;
    private LinearLayout base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tableNutrients = (TableLayout) findViewById(R.id.table_nutrients);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        loading = (ProgressBar) findViewById(R.id.loading);
        base = (LinearLayout) findViewById(R.id.base_table);

        if (getIntent().getExtras() != null) {
            name = getIntent().getStringExtra(PROD_NAME);
            code = getIntent().getStringExtra(PROD_CODE);

            if (actionBar != null) {
                actionBar.setTitle(name);
            }

            client = new OkHttpClient();
            nutrients = new ArrayList<>();

            new SearchProduct().execute();

        } else {
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class SearchProduct extends AsyncTask<Void, Void, Void> {

        private final static String URL_SEARCH = "http://api.nal.usda.gov/ndb/reports/?format=json&api_key=OQoY8dgeIGkJb471pqm5Gf6s3mZx31cQt9zCcLrb&ndbno=";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Request request = new Request.Builder()
                        .url(URL_SEARCH + code)
                        .build();
                Response response = client.newCall(request).execute();

                try {
                    JSONArray itemsJson = new JSONObject(response.body().string()).getJSONObject("report").getJSONObject("food").getJSONArray("nutrients");

                    for (int i = 0; i < itemsJson.length(); i++) {
                        JSONObject itemJson = itemsJson.getJSONObject(i);
                        Nutrient nutrient = new Nutrient();
                        nutrient.setId(itemJson.getString("nutrient_id"));
                        nutrient.setName(itemJson.getString("name"));
                        nutrient.setGroup(itemJson.getString("group"));
                        nutrient.setUnit(itemJson.getString("unit"));
                        nutrient.setValue(itemJson.getString("value"));
                        nutrients.add(nutrient);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (nutrients.size() > 0) {
                LayoutInflater inflater = LayoutInflater.from(DetailActivity.this);
                String group = "";

                for (Nutrient nutrient : nutrients) {
                    if (!group.equalsIgnoreCase(nutrient.getGroup())) {
                        group = nutrient.getGroup();

                        TableRow groupRow = (TableRow) inflater.inflate(R.layout.nutrient_table_group, tableNutrients, false);
                        TextView txtGroup = (TextView) groupRow.findViewById(R.id.group);

                        txtGroup.setText(group);
                        tableNutrients.addView(groupRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    }

                    TableRow nutrientRow = (TableRow) inflater.inflate(R.layout.nutrient_row, tableNutrients, false);
                    TextView txtName = (TextView) nutrientRow.findViewById(R.id.nutrient_name);
                    TextView txtValue = (TextView) nutrientRow.findViewById(R.id.nutrient_value);

                    txtName.setText(nutrient.getName());
                    txtValue.setText(nutrient.getValue() + " " + nutrient.getUnit());
                    tableNutrients.addView(nutrientRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }

                loading.setVisibility(View.GONE);
                base.setVisibility(View.VISIBLE);

                getContentResolver().delete(FoodContentProvider.CONTENT_URI_PRODUCTS, ProductsTable.COLUMN_CODE + "=?", new String[]{code});

                ContentValues values = new ContentValues();
                values.put(ProductsTable.COLUMN_NAME, name);
                values.put(ProductsTable.COLUMN_CODE, code);
                getContentResolver().insert(FoodContentProvider.CONTENT_URI_PRODUCTS, values);

            } else {
                new MaterialDialog.Builder(DetailActivity.this)
                        .title(R.string.dialog_nonutrients_title)
                        .content(R.string.dialog_nonutrients_content)
                        .positiveText(R.string.dialog_button_continue)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                DetailActivity.this.finish();
                            }
                        })
                        .show();
            }
        }
    }

}
