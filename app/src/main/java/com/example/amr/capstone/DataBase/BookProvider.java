package com.example.amr.capstone.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;

public class BookProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.amr.capstone.DataBase.BookProvider";
    public static final Uri Book_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/FavouriteTable");
    public static final int Book_URI_CODE = 0;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String TAG = "BookProvider";

    static {
        sUriMatcher.addURI(AUTHORITY, "FavouriteTable", Book_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDb;
    private UriMatcher uriMatcher;
    private SafeHandler mHandler = new SafeHandler(getContext());

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Log.d(TAG, "onCreate,current thread:" + Thread.currentThread().getName());
        mContext = getContext();
        initDB();
        return true;
    }

    private void initDB() {
        mDb = new DBHelper(mContext).getWritableDatabase();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = mDb.insert("FavouriteTable", "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(Book_CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query,current thread:" + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        Log.d(TAG, "table name:" + table);
        return mDb.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType");
        return null;
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case Book_URI_CODE:
                tableName = DBHelper.TableName;
                break;
            default:
                break;
        }
        return tableName;
    }

    private static class SafeHandler extends Handler {
        private WeakReference<Context> mRef;

        public SafeHandler(Context context) {
            mRef = new WeakReference<Context>(context);
        }
    }
}
