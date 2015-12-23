package dmitriy.com.musicshop.db;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dmitriy.com.musicshop.models.InstrumentModel;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MusicShops.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MusicShopsTable.create(db);
        InstrumentsTable.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MusicShopsTable.upgrade(db, oldVersion, newVersion);
        InstrumentsTable.upgrade(db, oldVersion, newVersion);
    }

    public int removeRows(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(tableName, null, null);
    }

    public Cursor queryListAllShops() {
        SQLiteDatabase database = getReadableDatabase();
        String[] columns =  new String[] {"*"};
        return database.query(MusicShopsTable.TABLE_NAME, columns, null, null, null, null, null);
    }

    public Cursor querySelectInstruments(String selection, String[] selectionArgs) {
        SQLiteDatabase database = getReadableDatabase();

        // Treat rowid that is automatically added by SQLLite as _id because
        // SimpleCursorAdapter requires the cursors result set to include a column named "_id"
        String[] columns =  new String[] { "rowid _id", "*"};
        return database.query(InstrumentsTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }
}