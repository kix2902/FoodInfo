package es.kix2902.foodinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SelectorActivity extends AppCompatActivity {

    public static final String NAME = "name";
    public static final String CODE = "code";

    private OkHttpClient client;

    private String name, image, barcodeImg;
    private ArrayList<Item> items;

    private RecyclerView recycler;
    private ResultAdapter adapter;

    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        client = new OkHttpClient();
        items = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loading = (ProgressBar) findViewById(R.id.loading);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new SimpleDividerItemDecoration(SelectorActivity.this));

        if (getIntent().hasExtra(CODE)) {
            String code = getIntent().getStringExtra(CODE);
            new SearchEAN().execute(code);

        } else if (getIntent().hasExtra(NAME)) {
            name = getIntent().getStringExtra(NAME);
            new SearchName().execute();
        }
    }

    private class SearchEAN extends AsyncTask<String, Void, Void> {

        private static final String EAN_URL = "http://eandata.com/feed/?v=3&keycode=6F75725455E71813&mode=json&find=";

        @Override
        protected Void doInBackground(String... params) {
            try {
                Request request = new Request.Builder()
                        .url(EAN_URL + params[0])
                        .build();
                Response response = client.newCall(request).execute();

                try {
                    JSONObject product = new JSONObject(response.body().string()).getJSONObject("product");

                    if (product.has("attributes")) {
                        JSONObject attributes = product.getJSONObject("attributes");
                        if (attributes.has("english")) {
                            name = attributes.getString("english");
                        } else if (attributes.has("product")) {
                            name = attributes.getString("product");
                        }
                    }

                    if ((name.contains("(")) && (name.contains(")"))) {
                        if (name.indexOf("(") < name.indexOf(")")) {
                            name = name.substring(name.indexOf("("), name.indexOf(")"));
                        }
                    }

                    if (product.has("image")) {
                        image = product.getString("image");
                    }

                    if (product.has("barcode")) {
                        JSONObject barcode = product.getJSONObject("barcode");
                        if (barcode.has("EAN13")) {
                            barcodeImg = barcode.getString("EAN13");
                        } else if (barcode.has("UPCA")) {
                            barcodeImg = barcode.getString("UPCA");
                        } else if (barcode.has("UPCB")) {
                            barcodeImg = barcode.getString("UPCB");
                        }
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

            if (name != null) {
                new SearchName().execute();

            } else {
                new MaterialDialog.Builder(SelectorActivity.this)
                        .title(R.string.dialog_noproduct_title)
                        .content(R.string.dialog_noproduct_content)
                        .positiveText(R.string.dialog_button_continue)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                SelectorActivity.this.finish();
                            }
                        })
                        .show();
            }
        }
    }

    private class SearchName extends AsyncTask<Void, Void, Void> implements ResultAdapter.OnItemClickResultListener {

        private final static String URL_SEARCH = "http://api.nal.usda.gov/ndb/search/?format=json&api_key=OQoY8dgeIGkJb471pqm5Gf6s3mZx31cQt9zCcLrb&q=";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Request request = new Request.Builder()
                        .url(URL_SEARCH + name)
                        .build();
                Response response = client.newCall(request).execute();

                try {
                    JSONArray itemsJson = new JSONObject(response.body().string()).getJSONObject("list").getJSONArray("item");

                    for (int i = 0; i < itemsJson.length(); i++) {
                        JSONObject itemJson = itemsJson.getJSONObject(i);
                        Item item = new Item();
                        item.setName(itemJson.getString("name"));
                        item.setNdbno(itemJson.getString("ndbno"));
                        items.add(item);
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

            if (items.size() > 0) {
                recycler.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);

                adapter = new ResultAdapter(items, this);
                recycler.setAdapter(adapter);

            } else {
                new MaterialDialog.Builder(SelectorActivity.this)
                        .title(R.string.dialog_noinfo_title)
                        .content(R.string.dialog_noinfo_content)
                        .positiveText(R.string.dialog_button_continue)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                SelectorActivity.this.finish();
                            }
                        })
                        .show();
            }
        }

        @Override
        public void OnItemClickResult(Item item) {
            Intent intent = new Intent(SelectorActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.PROD_CODE, item.getNdbno());
            intent.putExtra(DetailActivity.PROD_IMG, image);
            intent.putExtra(DetailActivity.BARCODE, barcodeImg);
            startActivity(intent);
        }
    }
}
