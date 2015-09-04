package es.kix2902.foodinfo.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class FoodContentProvider extends ContentProvider {

    private DatabaseHelper helper;
    private static final String AUTHORITY = "es.kix2902.foodinfo.provider";

    private static final String BASE_PATH_PRODUCTS = ProductsTable.TABLE_PRODUCTS;
    public static final Uri CONTENT_URI_PRODUCTS = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_PRODUCTS);

    private static final String CONTENT_TYPE_PRODUCTS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + BASE_PATH_PRODUCTS;
    private static final String CONTENT_ITEM_TYPE_PRODUCTS = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + BASE_PATH_PRODUCTS;

    private static final int PRODUCTS_LIST = 10;
    private static final int PRODUCTS_ID = 20;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PRODUCTS, PRODUCTS_LIST);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case PRODUCTS_LIST:
                return CONTENT_TYPE_PRODUCTS;
            case PRODUCTS_ID:
                return CONTENT_ITEM_TYPE_PRODUCTS;

            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();

        long id = -1;
        switch (sURIMatcher.match(uri)) {
            case PRODUCTS_LIST:
                id = db.insert(ProductsTable.TABLE_PRODUCTS, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }

        Uri itemUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(itemUri, null);
        return itemUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sURIMatcher.match(uri)) {
            case PRODUCTS_LIST:
                builder.setTables(ProductsTable.TABLE_PRODUCTS);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ProductsTable.SORT_ORDER_DEFAULT;
                }
                break;
            case PRODUCTS_ID:
                builder.setTables(ProductsTable.TABLE_PRODUCTS);
                builder.appendWhere(ProductsTable.COLUMN_ID + " = " + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int updateCount = 0;

        String idStr = "";
        String where = "";
        switch (sURIMatcher.match(uri)) {
            case PRODUCTS_LIST:
                updateCount = db.update(ProductsTable.TABLE_PRODUCTS, values, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                idStr = uri.getLastPathSegment();
                where = ProductsTable.COLUMN_ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(ProductsTable.TABLE_PRODUCTS, values, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int delCount = 0;

        String idStr = "";
        String where = "";
        switch (sURIMatcher.match(uri)) {
            case PRODUCTS_LIST:
                delCount = db.delete(ProductsTable.TABLE_PRODUCTS, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                idStr = uri.getLastPathSegment();
                where = ProductsTable.COLUMN_ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(ProductsTable.TABLE_PRODUCTS, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return delCount;
    }
}
