package com.example.amr.capstone.DataBase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;

public class BookProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.amr.capstone.DataBase.BookProvider";
    public static final Uri MOVIE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/FavouriteTable");
    public static final int MOVIE_URI_CODE = 0;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String TAG = "BookProvider";

    static {
        sUriMatcher.addURI(AUTHORITY, "FavouriteTable", MOVIE_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDb;
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
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
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
            case MOVIE_URI_CODE:
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
