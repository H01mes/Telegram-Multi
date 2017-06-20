package org.telegram.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.telegram.messenger.FileLog;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favourites";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_CHAT_ID = "chat_id";
    private static final String KEY_ID = "id";
    private static final String TABLE_FAVS = "tbl_favs";
    private static final String TAG = "DatabaseHandler";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tbl_favs(id INTEGER PRIMARY KEY AUTOINCREMENT,chat_id INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbl_favs");
        onCreate(db);
    }

    public ArrayList<Long> getList() {
        Throwable e;
        Throwable th;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Long> data = null;
        try {
            ArrayList<Long> data2 = new ArrayList();
            try {
                cursor = db.query(TABLE_FAVS, new String[]{KEY_CHAT_ID}, null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        data2.add(Long.valueOf(cursor.getLong(0)));
                    } while (cursor.moveToNext());
                }
                db.close();
                if (cursor == null) {
                    return data2;
                }
                cursor.close();
                return data2;
            } catch (Exception e2) {
                e = e2;
                data = data2;
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable th2) {
                        th = th2;
                        if (cursor != null) {
                            cursor.close();
                        }
//                        throw th;
                    }
                }
                FileLog.e(e);
                if (cursor != null) {
                    return data;
                }
                cursor.close();
                return data;
            } catch (Throwable th3) {
                th = th3;
                data = data2;
                if (cursor != null) {
                    cursor.close();
                }
//                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            if (cursor != null) {
                cursor.close();
            }
            FileLog.e(e);
            if (cursor != null) {
                return data;
            }
            cursor.close();
            return data;
        }
        return null;
    }

    public void addFavorite(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHAT_ID, id);
        db.insert(TABLE_FAVS, null, values);
        db.close();
    }

    public void deleteFavorite(Long chat_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_FAVS, "chat_id = ?", new String[]{String.valueOf(chat_id)});
        db.close();
    }
}
