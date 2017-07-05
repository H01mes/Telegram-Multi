/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Locale;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {
    private SharedPreferences themePrefs;
    private Context mContext;
    private ArrayList<Item> items = new ArrayList<>(11);

    public DrawerLayoutAdapter(Context context) {
        mContext = context;
        this.themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        Theme.createDialogsResources(context);
        resetItems();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return holder.getItemViewType() == 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new DrawerProfileCell(mContext);
                break;
            case 1:
            default:
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
                break;
            case 2:
                view = new DividerCell(mContext);
                break;
            case 3:
                view = new DrawerActionCell(mContext);
                break;
            case 4:
                view = new TextInfoCell(this.mContext);
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((DrawerProfileCell) holder.itemView).setUser(MessagesController.getInstance().getUser(UserConfig.getClientUserId()));
                holder.itemView.setBackgroundColor(Theme.usePlusTheme ? Theme.drawerHeaderColor : Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
                if (Theme.usePlusTheme) {
                    ((DrawerProfileCell) holder.itemView).refreshAvatar(this.themePrefs.getInt("drawerAvatarSize", 64), this.themePrefs.getInt("drawerAvatarRadius", 32));
                    return;
                }
                return;
            case 1:
                updateViewColor(holder.itemView);
                return;
            case 2:
                holder.itemView.setTag("drawerListDividerColor");
                updateViewColor(holder.itemView);
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
                break;
            case 3:
                items.get(position).bind((DrawerActionCell) holder.itemView);
                updateViewColor(holder.itemView);
                break;
            case 4:
                updateViewColor(holder.itemView);
                try {
                    int i;
                    PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    int code = pInfo.versionCode / 10;
                    String abi = "";
                    switch (pInfo.versionCode % 10) {
                        case 0:
                            abi = "arm";
                            break;
                        case 1:
                            abi = "arm-v7a";
                            break;
                        case 2:
                            abi = "x86";
                            break;
                        case 3:
                            abi = "universal";
                            break;
                    }
                    ((TextInfoCell) holder.itemView).setText(String.format(Locale.US, LocaleController.getString("TelegramForAndroid", R.string.TelegramForAndroid) + "\nv%s (%d) %s", new Object[]{pInfo.versionName, Integer.valueOf(code), abi}));
                    TextInfoCell textInfoCell = (TextInfoCell) holder.itemView;
                    if (Theme.usePlusTheme) {
                        i = this.themePrefs.getInt("drawerVersionColor", -6052957);
                    } else {
                        i = Theme.getColor(Theme.key_chats_menuItemText);
                    }
                    textInfoCell.setTextColor(i);
                    ((TextInfoCell) holder.itemView).setTextSize(this.themePrefs.getInt("drawerVersionSize", 13));
                    return;
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                    return;
                }
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        } else if (i == 1) {
            return 1;
        } else if (i == 5) {
            return 2;
        }
        if (i == this.items.size() - 1) {
            return Theme.plusMoveVersionToSettings ? -1 : 4;
        } else {
            return 3;
        }    }

//    private void resetItems() {
//        items.clear();
//        if (!UserConfig.isClientActivated()) {
//            return;
//        }
//        items.add(null); // profile
//        items.add(null); // padding
//        items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup));
//        items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret));
//        items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast));
//        items.add(null); // divider
//        items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts));
//
//        this.items.add(new Item(7, LocaleController.getString("DownloadThemes", R.string.DownloadThemes), R.drawable.menu_themes));
//
//        this.items.add(new Item(8, LocaleController.getString("Theming", R.string.Theming), R.drawable.menu_theming));
//        this.items.add(new Item(9, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings));
//        if (MessagesController.getInstance().callsEnabled) {
//            items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls));
//        }
//        items.add(new Item(11, LocaleController.getString("Change_another_user", R.string.Change_another_user), R.drawable.menu_invite));


    private void resetItems() {
        this.items.clear();
        if (UserConfig.isClientActivated()) {
            this.items.add(null);
            this.items.add(null);
            this.items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup));
            this.items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret));
            this.items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast));
            this.items.add(null);
            this.items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts));
            if (MessagesController.getInstance().callsEnabled) {
                this.items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls));
            }
            this.items.add(new Item(7, LocaleController.getString("DownloadThemes", R.string.DownloadThemes), R.drawable.menu_themes));
            this.items.add(new Item(8, LocaleController.getString("Theming", R.string.Theming), R.drawable.menu_theming));
            this.items.add(new Item(9, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings));
            this.items.add(new Item(11, LocaleController.getString("PlusSettings", R.string.PlusSettings), R.drawable.menu_plus));
            this.items.add(new Item(12, LocaleController.getString("OfficialChannel", R.string.OfficialChannel), R.drawable.menu_broadcast));
            this.items.add(new Item(13, LocaleController.getString("Change_another_user", R.string.Change_another_user), R.drawable.menu_invite));
            this.items.add(new Item(14, LocaleController.getString("ChatsCounters", R.string.ChatsCounters), R.drawable.profile_list));
            this.items.add(null);
        }
    }

    public int getId(int position) {
        if (position < 0 || position >= items.size()) {
            return -1;
        }
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    private class Item {
        public int icon;
        public String text;
        public int id;

        public Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(text, icon);
        }
    }
    private void updateViewColor(View v) {
        if (Theme.usePlusTheme) {
            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
            int mainColor = themePrefs.getInt("drawerListColor", -1);
            int value = themePrefs.getInt("drawerRowGradient", 0);
            if (value > 0 && !true) {
                GradientDrawable.Orientation go;
                switch (value) {
                    case 2:
                        go = GradientDrawable.Orientation.LEFT_RIGHT;
                        break;
                    case 3:
                        go = GradientDrawable.Orientation.TL_BR;
                        break;
                    case 4:
                        go = GradientDrawable.Orientation.BL_TR;
                        break;
                    default:
                        go = GradientDrawable.Orientation.TOP_BOTTOM;
                        break;
                }
                int gradColor = themePrefs.getInt("drawerRowGradientColor", -1);
                v.setBackgroundDrawable(new GradientDrawable(go, new int[]{mainColor, gradColor}));
            }
        }
    }
}
