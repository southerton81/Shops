package dmitriy.com.musicshop.db;

import android.database.sqlite.SQLiteDatabase;

public class MusicShopsTable {
    public static final String TABLE_NAME = "ShopsTable";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NAME_TYPE = "TEXT";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_ADDRESS_TYPE = "TEXT";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PHONE_TYPE = "TEXT";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_WEBSITE_TYPE = "TEXT";

    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LATITUDE_TYPE = "INTEGER";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LONGITUDE_TYPE = "INTEGER";

    private static final String COMMA_SEP = ", ";

    static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
            COLUMN_NAME + " " + COLUMN_NAME_TYPE + COMMA_SEP +
            COLUMN_ADDRESS + " " + COLUMN_ADDRESS_TYPE + COMMA_SEP +
            COLUMN_PHONE + " " + COLUMN_PHONE_TYPE + COMMA_SEP +
            COLUMN_WEBSITE + " " + COLUMN_WEBSITE_TYPE + COMMA_SEP +
            COLUMN_LATITUDE + " " + COLUMN_LATITUDE_TYPE + COMMA_SEP +
            COLUMN_LONGITUDE + " " + COLUMN_LONGITUDE_TYPE + ")";

    static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void create(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void upgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(SQL_DROP_TABLE);
        database.execSQL(CREATE_TABLE);
    }
}
