package dmitriy.com.musicshop.db;

import android.database.sqlite.SQLiteDatabase;

public class InstrumentsTable {
    public static final String TABLE_NAME = "InstrumentsTable";

    public static final String COLUMN_SHOPID = "shop_id";
    public static final String COLUMN_SHOPID_TYPE = "INTEGER";
    public static final String COLUMN_INSTRUMENTID = "instrument_id";
    public static final String COLUMN_INSTRUMENTID_TYPE = "INTEGER";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_BRAND_TYPE = "TEXT";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_MODEL_TYPE = "TEXT";
    public static final String COLUMN_TYPEOFINSTRUMENT = "type";
    public static final String COLUMN_TYPEOFINSTRUMENT_TYPE = "TEXT";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_PRICE_TYPE = "TEXT";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_QUANTITY_TYPE = "INTEGER";

    private static final String COMMA_SEP = ", ";

    static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +

            COLUMN_SHOPID + " " + COLUMN_SHOPID_TYPE + COMMA_SEP +
            COLUMN_INSTRUMENTID + " " + COLUMN_INSTRUMENTID_TYPE + COMMA_SEP +
            COLUMN_BRAND + " " + COLUMN_BRAND_TYPE + COMMA_SEP +
            COLUMN_MODEL + " " + COLUMN_MODEL_TYPE + COMMA_SEP +
            COLUMN_TYPEOFINSTRUMENT + " " + COLUMN_TYPEOFINSTRUMENT_TYPE + COMMA_SEP +
            COLUMN_PRICE + " " + COLUMN_PRICE_TYPE + COMMA_SEP +
            COLUMN_QUANTITY + " " + COLUMN_QUANTITY_TYPE + ")";

    static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void create(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void upgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(SQL_DROP_TABLE);
        database.execSQL(CREATE_TABLE);
    }
}
