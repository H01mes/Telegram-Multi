package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;

public class Favorite {
    private static Favorite Instance = null;
    private static final String TAG = "Favorite";
    private ArrayList<Long> list = ApplicationLoader.databaseHandler.getList();

    public static Favorite getInstance() {
        Favorite localInstance = Instance;
        if (localInstance != null) {
            return localInstance;
        }
        localInstance = new Favorite();
        Instance = localInstance;
        return localInstance;
    }

    public ArrayList<Long> getList() {
        return this.list;
    }

    public void addFavorite(Long id) {
        this.list.add(id);
        ApplicationLoader.databaseHandler.addFavorite(id);
    }

    public void deleteFavorite(Long id) {
        this.list.remove(id);
        ApplicationLoader.databaseHandler.deleteFavorite(id);
    }

    public boolean isFavorite(Long id) {
        return this.list.contains(id);
    }

    public int getCount() {
        return this.list.size();
    }
}
