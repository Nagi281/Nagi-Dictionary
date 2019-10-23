package dbhelpers;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final int DB_VERSION = 1;

    public DatabaseOpenHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
    }
}
