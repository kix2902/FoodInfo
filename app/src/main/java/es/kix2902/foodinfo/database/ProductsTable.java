package es.kix2902.foodinfo.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductsTable {

    // Database table
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CODE = "ndbno";

    public static final String SORT_ORDER_DEFAULT = COLUMN_ID + " DESC";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_PRODUCTS
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_CODE + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ProductsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(database);
    }
}
