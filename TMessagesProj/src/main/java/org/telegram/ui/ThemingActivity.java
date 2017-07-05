package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ColorSelectorDialog;
import org.telegram.ui.Components.ColorSelectorDialog.OnColorChangedListener;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;

import java.io.File;
import java.util.ArrayList;

public class ThemingActivity extends BaseFragment {
    public static final int CENTER = 0;
    private static final String TAG = "ThemingActivity";
    private int applyThemeRow;
    private int chatRow;
    private int chatsRow;
    private int contactsRow;
    private int dialogColorRow;
    private int drawerRow;
    private int generalSection2Row;
    private ListAdapter listAdapter;
    private ListView listView;
    private int profileRow;
    private int resetThemeRow;
    private boolean reseting = false;
    private int rowCount;
    private int saveThemeRow;
    private boolean saving = false;
    private int screensSection2Row;
    private int screensSectionRow;
    private int settingsRow;
    private boolean showPrefix;
    private int themeColorRow;
    private int themesSection2Row;
    private int themesSectionRow;
    private int usePlusThemeRow;

    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == ThemingActivity.this.themeColorRow || i == ThemingActivity.this.dialogColorRow || i == ThemingActivity.this.chatsRow || i == ThemingActivity.this.chatRow || i == ThemingActivity.this.contactsRow || i == ThemingActivity.this.drawerRow || i == ThemingActivity.this.profileRow || i == ThemingActivity.this.settingsRow || i == ThemingActivity.this.resetThemeRow || i == ThemingActivity.this.saveThemeRow || i == ThemingActivity.this.applyThemeRow || i == ThemingActivity.this.usePlusThemeRow;
        }

        public int getCount() {
            return ThemingActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            String prefix = ThemingActivity.this.showPrefix ? (i - ThemingActivity.this.screensSection2Row) + " " : "";
            if (type == 0) {
                if (view == null) {
                    view = new ShadowSectionCell(this.mContext);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new HeaderCell(this.mContext);
                }
                if (i == ThemingActivity.this.generalSection2Row) {
                    ((HeaderCell) view).setText(LocaleController.getString("General", R.string.General));
                } else if (i == ThemingActivity.this.screensSection2Row) {
                    ((HeaderCell) view).setText(LocaleController.getString("Screens", R.string.Screens));
                } else if (i == ThemingActivity.this.themesSection2Row) {
                    ((HeaderCell) view).setText(LocaleController.getString("Themes", R.string.Themes));
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == ThemingActivity.this.chatsRow) {
                    textCell.setText(prefix + LocaleController.getString("MainScreen", R.string.MainScreen), true);
                } else if (i == ThemingActivity.this.chatRow) {
                    textCell.setText(prefix + LocaleController.getString("ChatScreen", R.string.ChatScreen), true);
                } else if (i == ThemingActivity.this.contactsRow) {
                    textCell.setText(prefix + LocaleController.getString("ContactsScreen", R.string.ContactsScreen), true);
                } else if (i == ThemingActivity.this.drawerRow) {
                    textCell.setText(prefix + LocaleController.getString("NavigationDrawer", R.string.NavigationDrawer), true);
                } else if (i == ThemingActivity.this.profileRow) {
                    textCell.setText(prefix + LocaleController.getString("ProfileScreen", R.string.ProfileScreen), true);
                } else if (i == ThemingActivity.this.settingsRow) {
                    textCell.setText(prefix + LocaleController.getString("SettingsScreen", R.string.SettingsScreen), false);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell2 = (TextDetailSettingsCell) view;
                if (i == ThemingActivity.this.saveThemeRow) {
                    textCell2.setMultilineDetail(true);
                    textCell2.setMultilineDetail(true);
                    String text = LocaleController.getString("SaveTheme", R.string.SaveTheme).toLowerCase();
                    if (text.length() > 0) {
                        text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
                    }
                    textCell2.setTextAndValue(text, LocaleController.getString("SaveThemeSum", R.string.SaveThemeSum), true);
                } else if (i == ThemingActivity.this.applyThemeRow) {
                    textCell2.setMultilineDetail(true);
                    textCell2.setTextAndValue(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile), LocaleController.getString("ApplyThemeSum", R.string.ApplyThemeSum), true);
                } else if (i == ThemingActivity.this.resetThemeRow) {
                    textCell2.setMultilineDetail(true);
                    textCell2.setTextAndValue(LocaleController.getString("ResetThemeSettings", R.string.ResetThemeSettings), LocaleController.getString("ResetThemeSettingsSum", R.string.ResetThemeSettingsSum), false);
                }
            } else if (type == 4) {
                if (view == null) {
                    view = new TextColorCell(this.mContext);
                }
                TextColorCell textCell3 = (TextColorCell) view;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
                int defColor = preferences.getInt(Theme.pkey_themeColor, AndroidUtilities.defColor);
                if (i == ThemingActivity.this.themeColorRow) {
                    textCell3.setTextAndColor(LocaleController.getString(Theme.pkey_themeColor, R.string.themeColor), defColor, true);
                } else if (i == ThemingActivity.this.dialogColorRow) {
                    textCell3.setTextAndColor(LocaleController.getString("DialogColor", R.string.DialogColor), preferences.getInt(Theme.pkey_dialogColor, defColor), false);
                }
            } else if (type == 5) {
                if (view == null) {
                    view = new TextCheckCell(this.mContext);
                }
                TextCheckCell textCell4 = (TextCheckCell) view;
                if (i == ThemingActivity.this.usePlusThemeRow) {
                    textCell4.setTag("usePlusTheme");
                    textCell4.setTextAndCheck(LocaleController.getString("UsePlusTheme", R.string.UsePlusTheme), Theme.usePlusTheme, true);
                }
            }
            if (view != null) {
                view.setBackgroundColor(Theme.usePlusTheme ? Theme.prefBGColor : Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ThemingActivity.this.screensSectionRow || i == ThemingActivity.this.themesSectionRow) {
                return 0;
            }
            if (i == ThemingActivity.this.generalSection2Row || i == ThemingActivity.this.screensSection2Row || i == ThemingActivity.this.themesSection2Row) {
                return 1;
            }
            if (i == ThemingActivity.this.chatsRow) {
                return 2;
            }
            if (i == ThemingActivity.this.resetThemeRow || i == ThemingActivity.this.saveThemeRow || i == ThemingActivity.this.applyThemeRow) {
                return 3;
            }
            if (i == ThemingActivity.this.themeColorRow || i == ThemingActivity.this.dialogColorRow) {
                return 4;
            }
            if (i == ThemingActivity.this.usePlusThemeRow) {
                return 5;
            }
            return 2;
        }

        public int getViewTypeCount() {
            return 6;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.usePlusThemeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.generalSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.themeColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.screensSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.screensSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.drawerRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.profileRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.themesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.themesSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.saveThemeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.applyThemeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetThemeRow = i;
        this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("showPrefix", true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (AndroidUtilities.needRestart) {
            Utilities.restartApp();
        }
    }

    public View createView(Context context) {
        if (this.fragmentView == null) {
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            if (AndroidUtilities.isTablet()) {
                this.actionBar.setOccupyStatusBar(false);
            }
            this.actionBar.setTitle(LocaleController.getString("Theming", R.string.Theming));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        ThemingActivity.this.finishFragment();
                    }
                }
            });
            this.actionBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean z;
                    ThemingActivity themingActivity = ThemingActivity.this;
                    if (ThemingActivity.this.showPrefix) {
                        z = false;
                    } else {
                        z = true;
                    }
                    themingActivity.showPrefix = z;
                    ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("showPrefix", ThemingActivity.this.showPrefix).apply();
                    if (ThemingActivity.this.listAdapter != null) {
                        ThemingActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            });
            this.listAdapter = new ListAdapter(context);
            this.fragmentView = new FrameLayout(context);
            FrameLayout frameLayout = (FrameLayout) this.fragmentView;
            this.listView = new ListView(context);
            if (Theme.usePlusTheme) {
                this.listView.setBackgroundColor(Theme.prefBGColor);
            }
            this.listView.setDivider(null);
            this.listView.setDividerHeight(0);
            this.listView.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setListViewEdgeEffectColor(this.listView, Theme.prefActionbarColor);
            frameLayout.addView(this.listView);
            LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 48;
            this.listView.setLayoutParams(layoutParams);
            this.listView.setAdapter(this.listAdapter);
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
                    int defColor = preferences.getInt(Theme.pkey_themeColor, AndroidUtilities.defColor);
                    if (i == ThemingActivity.this.themeColorRow) {
                        if (ThemingActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingActivity.this.commitInt(color);
                                    Theme.updateAllColors();
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(0));
                                }
                            }, defColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingActivity.this.dialogColorRow) {
                        if (ThemingActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.dialogColor = color;
                                    ThemingActivity.this.commitInt(Theme.pkey_dialogColor, color);
                                }
                            }, preferences.getInt(Theme.pkey_dialogColor, defColor), 0, 0, false).show();
                        }
                    } else if (i == ThemingActivity.this.saveThemeRow) {
                        File file = new File(Utilities.findPrefFolder(ThemingActivity.this.getParentActivity()), "theme.xml");
                        if (!file.exists() || (file.exists() && file.length() < 100)) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ThemingActivity.this.getParentActivity(), LocaleController.getString("SaveErrorMsg0", R.string.SaveErrorMsg0), Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                        View promptsView = LayoutInflater.from(ThemingActivity.this.getParentActivity()).inflate(R.layout.editbox_dialog, null);
                        Builder builder = new Builder(ThemingActivity.this.getParentActivity());
                        builder.setView(promptsView);
                        EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                        userInput.setHint(LocaleController.getString("EnterName", R.string.EnterName));
                        userInput.setHintTextColor(Color.BLACK); //TODO Multi
                        userInput.getBackground().setColorFilter(preferences.getInt(Theme.pkey_dialogColor, defColor), Mode.SRC_IN);
                        AndroidUtilities.clearCursorDrawable(userInput);
                        builder.setTitle(LocaleController.getString("SaveTheme", R.string.SaveTheme));
                        final EditText editText = userInput;
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!ThemingActivity.this.saving) {
                                    final String pName = editText.getText().toString();
                                    if (pName.length() < 1) {
                                        Toast.makeText(ThemingActivity.this.getParentActivity(), LocaleController.getString("NameTooShort", R.string.NameTooShort), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    ThemingActivity.this.saving = true;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            ThemingActivity.this.saving = false;
                                            if (ThemingActivity.this.getParentActivity() != null) {
                                                AndroidUtilities.setStringPref(ThemingActivity.this.getParentActivity(), "themeName", pName);
                                                try {
                                                    AndroidUtilities.setStringPref(ThemingActivity.this.getParentActivity(), "version", ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0).versionName);
                                                } catch (Throwable e) {
                                                    FileLog.e(e);
                                                }
                                                AndroidUtilities.setStringPref(ThemingActivity.this.getParentActivity(), "model", Build.MODEL + "/" + VERSION.RELEASE);
                                                AndroidUtilities.setStringPref(ThemingActivity.this.getParentActivity(), "date", System.currentTimeMillis() + "");
                                                Utilities.savePreferencesToSD(ThemingActivity.this.getParentActivity(), "/Telegram/Themes", "theme.xml", pName + ".xml", true);
                                                Utilities.copyWallpaperToSD(ThemingActivity.this.getParentActivity(), pName, true);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingActivity.this.showDialog(builder.create());
                    } else if (i == ThemingActivity.this.applyThemeRow) {
                        DocumentSelectActivity fragment = new DocumentSelectActivity();
//                        fragment.fileFilter = ".xml";
//                        fragment.arrayFilter = new String[]{".xml"}; //TODO Multi
                        fragment.setDelegate(new DocumentSelectActivityDelegate() {
                            public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files) {
                                final String xmlFile = (String) files.get(0);
                                File themeFile = new File(xmlFile);
                                Builder builder = new Builder(ThemingActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
                                builder.setMessage(themeFile.getName());
                                final String wName = xmlFile.substring(0, xmlFile.lastIndexOf(".")) + "_wallpaper.jpg";
                                File wFile = new File(wName);
                                if (wFile.exists()) {
                                    builder.setMessage(themeFile.getName() + "\n" + wFile.getName());
                                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                                    if (preferences.getInt("selectedBackground", 1000001) == 1000001) {
                                        Editor editor = preferences.edit();
                                        editor.putInt("selectedBackground", 113);
                                        editor.putInt("selectedColor", 0);
                                        editor.commit();
                                    }
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                if (Utilities.loadPrefFromSD(ThemingActivity.this.getParentActivity(), xmlFile) == 4) {
//                                                    Utilities.applyWallpaper(wName); TODO Multi
                                                    Utilities.restartApp();
                                                }
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                ThemingActivity.this.showDialog(builder.create());
                            }

                            public void startDocumentSelectActivity() {
                            }
                        });
                        ThemingActivity.this.presentFragment(fragment);
                    } else if (i == ThemingActivity.this.resetThemeRow) {
                        Builder builder = new Builder(ThemingActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                        builder.setTitle(LocaleController.getString("ResetThemeSettings", R.string.ResetThemeSettings));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!ThemingActivity.this.reseting) {
                                    ThemingActivity.this.reseting = true;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            ThemingActivity.this.reseting = false;
                                            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
                                            editor.clear();
                                            editor.commit();
                                            editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                                            editor.putInt("selectedBackground", 1000001);
                                            editor.putInt("selectedColor", 0);
                                            editor.commit();
                                            File toFile = new File(ApplicationLoader.applicationContext.getFilesDir(), "wallpaper.jpg");
                                            if (toFile.exists()) {
                                                toFile.delete();
                                            }
                                            if (ThemingActivity.this.getParentActivity() != null) {
                                                Toast.makeText(ThemingActivity.this.getParentActivity(), LocaleController.getString("ResetThemeToastText", R.string.ResetThemeToastText), Toast.LENGTH_LONG).show();
                                            }
                                            Theme.updateAllColors();
                                            if (ThemingActivity.this.listAdapter != null) {
                                                ThemingActivity.this.listAdapter.notifyDataSetChanged();
                                            }
                                            if (Theme.usePlusTheme) {
                                                ThemingActivity.this.updateTheme();
                                            }
                                            ThemingActivity.this.fixLayout();
                                        }
                                    });
                                    AndroidUtilities.needRestart = true;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            if (ThemingActivity.this.getParentActivity() != null) {
                                                Toast.makeText(ThemingActivity.this.getParentActivity(), LocaleController.getString("AppWillRestart", R.string.AppWillRestart), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingActivity.this.showDialog(builder.create());
                    } else if (i == ThemingActivity.this.chatsRow) {
                        ThemingActivity.this.presentFragment(new ThemingChatsActivity());
                    } else if (i == ThemingActivity.this.chatRow) {
                        ThemingActivity.this.presentFragment(new ThemingChatActivity());
                    } else if (i == ThemingActivity.this.contactsRow) {
                        ThemingActivity.this.presentFragment(new ThemingContactsActivity());
                    } else if (i == ThemingActivity.this.drawerRow) {
                        ThemingActivity.this.presentFragment(new ThemingDrawerActivity());
                    } else if (i == ThemingActivity.this.profileRow) {
                        ThemingActivity.this.presentFragment(new ThemingProfileActivity());
                    } else if (i == ThemingActivity.this.settingsRow) {
                        ThemingActivity.this.presentFragment(new ThemingSettingsActivity());
                    } else if (i == ThemingActivity.this.usePlusThemeRow) {
                        Theme.usePlusTheme = !Theme.usePlusTheme;
                        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
                        editor.putBoolean("usePlusTheme", Theme.usePlusTheme);
                        editor.apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.usePlusTheme);
                        }
                        if (!Theme.usePlusTheme) {
                            ThemeInfo applyingTheme = null;
                            try {
                                SharedPreferences prefs = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                                String theme = prefs.getString("prevTheme", prefs.getString(AndroidUtilities.THEME_PREFS, null));
                                if (theme != null) {
                                    applyingTheme = (ThemeInfo) Theme.getThemeList().get(theme);
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            if (applyingTheme == null) {
                                applyingTheme = Theme.getDefaultTheme();
                            }
                            Theme.applyTheme(applyingTheme);
                        } else if (Theme.getCurrentTheme() != Theme.getDefaultTheme()) {
                            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                            edit.putString("prevTheme", Theme.getCurrentTheme().name);
                            edit.commit();
                            Theme.applyTheme(Theme.getDefaultTheme());
                        }
                        Theme.applyPlusTheme(true);
                        if (ThemingActivity.this.parentLayout != null) {
                            ThemingActivity.this.parentLayout.rebuildAllFragmentViews(false);
                        }
                        if (ThemingActivity.this.listView != null) {
                            ThemingActivity.this.listView.invalidateViews();
                        }
                    }
                }
            });
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (ThemingActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    if (i == ThemingActivity.this.themeColorRow) {
                        ThemingActivity.this.commitInt(AndroidUtilities.defColor);
                    } else if (i == ThemingActivity.this.dialogColorRow) {
                        ThemingActivity.this.resetPref(Theme.pkey_dialogColor);
                    }
                    return true;
                }
            });
            frameLayout.addView(this.actionBar);
        } else {
            ViewGroup parent = (ViewGroup) this.fragmentView.getParent();
            if (parent != null) {
                parent.removeView(this.fragmentView);
            }
        }
        if (Theme.usePlusTheme) {
            updateTheme();
        }
        return this.fragmentView;
    }

    private void resetPref(String key) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
        editor.remove(key);
        editor.commit();
        Theme.updateMainColors();
        refreshTheme();
    }

    private void commitInt(String key, int value) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
        editor.putInt(key, value);
        editor.commit();
        refreshTheme();
    }

    private void commitInt(int i) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        Editor editor = preferences.edit();
        editor.putInt(Theme.pkey_themeColor, i);
        AndroidUtilities.themeColor = i;
        editor.commit();
        int darkColor = AndroidUtilities.setDarkColor(i, 21);
        editor.putInt("chatsHeaderColor", i);
        editor.putInt("chatsCountBGColor", i);
        editor.putInt(Theme.pkey_chatsChecksColor, i);
        editor.putInt(Theme.pkey_chatsMemberColor, darkColor);
        editor.putInt(Theme.pkey_chatsMediaColor, preferences.getInt(Theme.pkey_chatsMemberColor, darkColor));
        editor.putInt(Theme.pkey_chatsFloatingBGColor, i);
        editor.putInt(Theme.pkey_chatHeaderColor, i);
        editor.putInt(Theme.pkey_chatRBubbleColor, AndroidUtilities.getDefBubbleColor());
        editor.putInt(Theme.pkey_chatStatusColor, AndroidUtilities.setDarkColor(i, -64));
        editor.putInt(Theme.pkey_chatRTimeColor, darkColor);
        editor.putInt(Theme.pkey_chatEmojiViewTabColor, AndroidUtilities.setDarkColor(i, -21));
        editor.putInt(Theme.pkey_chatChecksColor, i);
        editor.putInt(Theme.pkey_chatSendIconColor, i);
        editor.putInt(Theme.pkey_chatMemberColor, darkColor);
        editor.putInt("chatForwardColor", darkColor);
        editor.putInt("contactsHeaderColor", i);
        editor.putInt("contactsOnlineColor", darkColor);
        editor.putInt("prefHeaderColor", i);
        editor.putInt(Theme.pkey_dialogColor, i);
        editor.commit();
        fixLayout();
        AndroidUtilities.themeColor = i;
        refreshTheme();
    }

    private void refreshTheme() {
        if (!Theme.usePlusTheme) {
            Theme.usePlusTheme = true;
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
            editor.putBoolean("usePlusTheme", true);
            editor.commit();
        }
        Theme.applyPlusTheme();
        if (this.parentLayout != null) {
            this.parentLayout.rebuildAllFragmentViews(false);
        }
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        fixLayout();
    }

    private void updateTheme() {
        this.actionBar.setTitleColor(Theme.prefActionbarTitleColor);
        this.actionBar.setBackgroundColor(Theme.prefActionbarColor);
        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(Theme.prefActionbarIconsColor, Mode.MULTIPLY);
        this.actionBar.setBackButtonDrawable(back);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ThemingActivity.this.fragmentView != null) {
                        ThemingActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return false;
                }
            });
            this.listView.setAdapter(this.listAdapter);
        }
    }
}