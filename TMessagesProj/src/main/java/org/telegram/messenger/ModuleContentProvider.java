package org.telegram.messenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.telegram.ui.ActionBar.Theme;

import java.io.File;
import java.io.IOException;

public class ModuleContentProvider extends ContentProvider {
    private static final String AUTHORITY = "org.telegram.plus.android.provider.content";
    private static final String AUTHORITY_BETA = "org.telegram.plus.beta.android.provider.content";
    public static Uri GET_NAME = Uri.parse("content://org.telegram.plus.android.provider.content/name");
    public static Uri SET_NAME = Uri.parse("content://org.telegram.plus.android.provider.content/newname");
    private static final String TAG = "ModuleContentProvider";
    public static Uri THEME_URI = Uri.parse("content://org.telegram.plus.android.provider.content/theme");

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        Log.d(TAG, "query with uri: " + uri.toString());
        return null;
    }

    public String getType(Uri uri) {
        if (uri.equals(GET_NAME)) {
            return ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).getString("themeName", "empty");
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert uri: " + uri.toString());
        if (uri.toString().contains(SET_NAME.toString())) {
            AndroidUtilities.themeUpdated = true;
            return uri;
        }
        throw new UnsupportedOperationException();
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uri.toString().contains(THEME_URI.toString())) {
            String theme = uri.toString();
            theme = theme.substring(theme.lastIndexOf(":") + 1, theme.length());
            if (!"mounted".equals(Environment.getExternalStorageState())) {
                return 30;
            }
            File themeFile = new File(theme);
            if (!themeFile.exists()) {
                return 20;
            }
            if (themeFile.getAbsolutePath().contains(".attheme")) {
                Theme.setUsePlusThemeKey(false);
                Theme.applyThemeFile(themeFile, themeFile.getName(), false);
                return 11;
            }
            try {
                applyTheme(theme);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 10;
        }
        throw new UnsupportedOperationException();
    }

    public int delete(Uri uri, String where, String[] selectionArgs) {
        Log.d(TAG, "delete uri: " + uri.toString());
        throw new UnsupportedOperationException();
    }

    private void applyTheme(String xmlFile) throws IOException {
        String wName = xmlFile.substring(0, xmlFile.lastIndexOf(".")) + "_wallpaper.jpg";
        if (new File(wName).exists()) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            if (preferences.getInt("selectedBackground", 1000001) == 1000001) {
                Editor editor = preferences.edit();
                editor.putInt("selectedBackground", 113);
                editor.putInt("selectedColor", 0);
                editor.commit();
            }
        }
        if (Utilities.loadPrefFromSD(ApplicationLoader.applicationContext, xmlFile) == 4) {
            Utilities.applyWallpaper(wName);
            AndroidUtilities.needRestart = true;
            Theme.setUsePlusThemeKey(true);
        }
    }
}
