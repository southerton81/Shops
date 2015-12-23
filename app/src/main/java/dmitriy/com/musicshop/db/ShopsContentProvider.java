package dmitriy.com.musicshop.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import dmitriy.com.musicshop.models.InstrumentModel;

public class ShopsContentProvider extends ContentProvider {

    private DbHelper mDbHelper;
    private static final String AUTHORITY = "com.musicshopssample.contentprovider";
    private static final String LISTSHOPS = "listshops";
    private static final String LISTINSTRUMENTS = "listinstruments";
    private static final String REMOVESHOPS = "removeshops";
    private static final String REMOVEINSTRUMENTS = "removeinstruments";

    public static final Uri LISTSHOPS_URI = Uri.parse("content://" + AUTHORITY + "/" + LISTSHOPS);
    public static final Uri LISTINSTRUMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + LISTINSTRUMENTS);
    public static final Uri REMOVESHOPS_URI = Uri.parse("content://" + AUTHORITY + "/" + REMOVESHOPS);
    public static final Uri REMOVEINSTRUMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + REMOVEINSTRUMENTS);



    private static final int LISTSHOPS_CODE = 1;
    private static final int LISTINSTRUMENTS_CODE = 2;
    private static final int REMOVESHOPS_URI_CODE = 3;
    private static final int REMOVEINSTRUMENTS_URI_CODE = 4;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, LISTSHOPS, LISTSHOPS_CODE);
        uriMatcher.addURI(AUTHORITY, LISTINSTRUMENTS, LISTINSTRUMENTS_CODE);
        uriMatcher.addURI(AUTHORITY, REMOVESHOPS, REMOVESHOPS_URI_CODE);
        uriMatcher.addURI(AUTHORITY, REMOVEINSTRUMENTS, REMOVEINSTRUMENTS_URI_CODE);
    }

    @Override
    public boolean onCreate() {
        if (mDbHelper == null)
            mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case LISTSHOPS_CODE:
                return mDbHelper.queryListAllShops();
            case LISTINSTRUMENTS_CODE:
                return mDbHelper.querySelectInstruments(selection, selectionArgs);
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case REMOVESHOPS_URI_CODE:
                return mDbHelper.removeRows(MusicShopsTable.TABLE_NAME);
            case REMOVEINSTRUMENTS_URI_CODE:
                return mDbHelper.removeRows(InstrumentsTable.TABLE_NAME);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
