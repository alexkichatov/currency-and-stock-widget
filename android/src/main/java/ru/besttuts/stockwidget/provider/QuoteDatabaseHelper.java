package ru.besttuts.stockwidget.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import ru.besttuts.stockwidget.provider.QuoteContract.*;

/**
 * Created by roman on 10.01.2015.
 */
public class QuoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "EconomicWidget.QuoteDatabase";

    private static final String DATABASE_NAME = "quote.db";
    private static final int DATABASE_VERSION = 2;

    private final Context mContext;

    public QuoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public interface Tables {
        String SETTINGS = "settings";
        String MODELS = "models";
        String CURRENCY_EXCHANGE = "currency_exchange";
        String STOCK = "stock";
        String GOODS = "goods";
        String INDICES = "indices";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.SETTINGS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SettingColumns.SETTING_ID + " TEXT NOT NULL,"
                + SettingColumns.SETTING_WIDGET_ID + " INTEGER NOT NULL,"
                + SettingColumns.SETTING_QUOTE_POSITION + " INTEGER NOT NULL,"
                + SettingColumns.SETTING_QUOTE_TYPE + " TEXT NOT NULL,"
                + SettingColumns.SETTING_QUOTE_SYMBOL + " TEXT NOT NULL,"
                + "UNIQUE (" + SettingColumns.SETTING_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.MODELS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ModelColumns.MODEL_ID + " TEXT NOT NULL,"
                + ModelColumns.MODEL_NAME + " TEXT NOT NULL,"
                + ModelColumns.MODEL_RATE + " NUMERIC(10,4) NOT NULL,"
                + ModelColumns.MODEL_CHANGE + " NUMERIC(10,4) NOT NULL,"
                + ModelColumns.MODEL_PERCENT_CHANGE + " TEXT NOT NULL,"
                + "UNIQUE (" + ModelColumns.MODEL_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MODELS);
        onCreate(db);
    }

}
