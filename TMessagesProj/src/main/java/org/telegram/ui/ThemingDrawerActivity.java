package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ColorSelectorDialog;
import org.telegram.ui.Components.ColorSelectorDialog.OnColorChangedListener;
import org.telegram.ui.Components.NumberPicker;

import java.util.ArrayList;

public class ThemingDrawerActivity extends BaseFragment {
    public static final int CENTER = 0;
    private int avatarColorRow;
    private int avatarRadiusRow;
    private int avatarSizeRow;
    private int centerAvatarRow;
    private boolean drawer = false;
    private int headerBackgroundCheckRow;
    private int headerColorRow;
    private int headerGradientColorRow;
    private int headerGradientRow;
    private int headerSection2Row;
    private int hideBackgroundShadowRow;
    private int iconColorRow;
    private ListAdapter listAdapter;
    private int listColorRow;
    private int listDividerColorRow;
    private ListView listView;
    private int nameColorRow;
    private int nameSizeRow;
    private int optionColorRow;
    private int optionSizeRow;
    private int phoneColorRow;
    private int phoneSizeRow;
    private boolean player = false;
    private int rowCount;
    private int rowGradientColorRow;
    private int rowGradientListCheckRow;
    private int rowGradientRow;
    private int rowsSection2Row;
    private int rowsSectionRow;
    private boolean showPrefix;
    private int versionColorRow;
    private int versionSizeRow;
    NumberPicker r0;
    int currentValue;
    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            int h = AndroidUtilities.getIntDef("drawerHeaderGradient", 0);
            int g = AndroidUtilities.getIntDef("drawerRowGradient", 0);
            if (i == ThemingDrawerActivity.this.headerColorRow || i == ThemingDrawerActivity.this.headerGradientRow || ((h > 0 && i == ThemingDrawerActivity.this.headerGradientColorRow) || i == ThemingDrawerActivity.this.headerBackgroundCheckRow || i == ThemingDrawerActivity.this.hideBackgroundShadowRow || i == ThemingDrawerActivity.this.centerAvatarRow || i == ThemingDrawerActivity.this.listColorRow || i == ThemingDrawerActivity.this.rowGradientRow || ((g != 0 && i == ThemingDrawerActivity.this.rowGradientColorRow) || ((g != 0 && i == ThemingDrawerActivity.this.rowGradientListCheckRow) || i == ThemingDrawerActivity.this.listDividerColorRow || i == ThemingDrawerActivity.this.iconColorRow || i == ThemingDrawerActivity.this.optionColorRow || i == ThemingDrawerActivity.this.optionSizeRow || i == ThemingDrawerActivity.this.avatarColorRow || i == ThemingDrawerActivity.this.avatarRadiusRow || i == ThemingDrawerActivity.this.nameColorRow || i == ThemingDrawerActivity.this.avatarSizeRow || i == ThemingDrawerActivity.this.nameSizeRow || i == ThemingDrawerActivity.this.phoneColorRow || i == ThemingDrawerActivity.this.phoneSizeRow || i == ThemingDrawerActivity.this.versionColorRow || i == ThemingDrawerActivity.this.versionSizeRow)))) {
                return true;
            }
            return false;
        }

        public int getCount() {
            return ThemingDrawerActivity.this.rowCount;
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
            String prefix = "";
            if (ThemingDrawerActivity.this.showPrefix) {
                prefix = "4.";
                if (i == ThemingDrawerActivity.this.headerSection2Row) {
                    prefix = prefix + "1 ";
                } else if (i == ThemingDrawerActivity.this.rowsSection2Row) {
                    prefix = prefix + "2 ";
                } else if (i < ThemingDrawerActivity.this.rowsSection2Row) {
                    prefix = prefix + "1." + i + " ";
                } else {
                    prefix = prefix + "2." + (i - ThemingDrawerActivity.this.rowsSection2Row) + " ";
                }
            }
            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
            int defColor = themePrefs.getInt(Theme.pkey_themeColor, AndroidUtilities.defColor);
            if (type == 0) {
                if (view == null) {
                    view = new ShadowSectionCell(this.mContext);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                if (i == ThemingDrawerActivity.this.headerSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("Header", R.string.Header));
                } else if (i == ThemingDrawerActivity.this.rowsSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("OptionsList", R.string.OptionsList));
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                int size;
                if (i == ThemingDrawerActivity.this.avatarRadiusRow) {
                    size = themePrefs.getInt("drawerAvatarRadius", AndroidUtilities.isTablet() ? 35 : 32);
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingDrawerActivity.this.avatarSizeRow) {
                    size = themePrefs.getInt("drawerAvatarSize", AndroidUtilities.isTablet() ? 68 : 64);
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarSize", R.string.AvatarSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingDrawerActivity.this.nameSizeRow) {
                    size = themePrefs.getInt("drawerNameSize", AndroidUtilities.isTablet() ? 17 : 15);
                    textCell.setTextAndValue(prefix + LocaleController.getString("OwnNameSize", R.string.OwnNameSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingDrawerActivity.this.optionSizeRow) {
                    size = themePrefs.getInt("drawerOptionSize", AndroidUtilities.isTablet() ? 17 : 15);
                    textCell.setTextAndValue(prefix + LocaleController.getString("OptionSize", R.string.OptionSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingDrawerActivity.this.phoneSizeRow) {
                    size = themePrefs.getInt("drawerPhoneSize", AndroidUtilities.isTablet() ? 15 : 13);
                    textCell.setTextAndValue(prefix + LocaleController.getString("PhoneSize", R.string.PhoneSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingDrawerActivity.this.versionSizeRow) {
                    size = themePrefs.getInt("drawerVersionSize", AndroidUtilities.isTablet() ? 15 : 13);
                    textCell.setTextAndValue(prefix + LocaleController.getString("VersionSize", R.string.VersionSize), String.format("%d", new Object[]{Integer.valueOf(size)}), false);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextColorCell(this.mContext);
                }
                TextColorCell textCell2 = (TextColorCell) view;
                if (i == ThemingDrawerActivity.this.headerColorRow) {
                    textCell2.setTag("drawerHeaderColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderColor", R.string.HeaderColor), Theme.drawerHeaderColor, false);
                } else if (i == ThemingDrawerActivity.this.headerGradientColorRow) {
                    textCell2.setTag("drawerHeaderGradientColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor), themePrefs.getInt("drawerHeaderGradient", 0) == 0 ? 0 : themePrefs.getInt("drawerHeaderGradientColor", defColor), true);
                } else if (i == ThemingDrawerActivity.this.listColorRow) {
                    textCell2.setTag("drawerListColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("ListColor", R.string.ListColor), themePrefs.getInt("drawerListColor", -1), false);
                } else if (i == ThemingDrawerActivity.this.rowGradientColorRow) {
                    textCell2.setTag("drawerRowGradientColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor), themePrefs.getInt("drawerRowGradient", 0) == 0 ? 0 : themePrefs.getInt("drawerRowGradientColor", -1), true);
                } else if (i == ThemingDrawerActivity.this.listDividerColorRow) {
                    textCell2.setTag("drawerListDividerColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("ListDividerColor", R.string.ListDividerColor), themePrefs.getInt("drawerListDividerColor", -2500135), true);
                } else if (i == ThemingDrawerActivity.this.iconColorRow) {
                    textCell2.setTag(Theme.pkey_drawerIconColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("IconColor", R.string.IconColor), Theme.drawerIconColor, true);
                } else if (i == ThemingDrawerActivity.this.optionColorRow) {
                    textCell2.setTag(Theme.pkey_drawerOptionColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("OptionColor", R.string.OptionColor), Theme.drawerOptionColor, true);
                } else if (i == ThemingDrawerActivity.this.versionColorRow) {
                    textCell2.setTag("drawerVersionColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("VersionColor", R.string.VersionColor), themePrefs.getInt("drawerVersionColor", -6052957), true);
                } else if (i == ThemingDrawerActivity.this.avatarColorRow) {
                    textCell2.setTag("drawerAvatarColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("AvatarColor", R.string.AvatarColor), themePrefs.getInt("drawerAvatarColor", Theme.darkColor), true);
                } else if (i == ThemingDrawerActivity.this.nameColorRow) {
                    textCell2.setTag(Theme.pkey_drawerNameColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("NameColor", R.string.NameColor), Theme.drawerNameColor, true);
                } else if (i == ThemingDrawerActivity.this.phoneColorRow) {
                    textCell2.setTag(Theme.pkey_drawerPhoneColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("PhoneColor", R.string.PhoneColor), themePrefs.getInt(Theme.pkey_drawerPhoneColor, AndroidUtilities.getIntDarkerColor(Theme.pkey_themeColor, -64)), true);
                }
            } else if (type == 4) {
                if (view == null) {
                    view = new TextCheckCell(this.mContext);
                }
                TextCheckCell textCell3 = (TextCheckCell) view;
                if (i == ThemingDrawerActivity.this.headerBackgroundCheckRow) {
                    textCell3.setTag("drawerHeaderBGCheck");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("HideBackground", R.string.HideBackground), Theme.drawerHeaderBGCheck, true);
                } else if (i == ThemingDrawerActivity.this.hideBackgroundShadowRow) {
                    textCell3.setTag("drawerHideBGShadowCheck");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("HideBackgroundShadow", R.string.HideBackgroundShadow), Theme.drawerHideBGShadowCheck, true);
                } else if (i == ThemingDrawerActivity.this.centerAvatarRow) {
                    textCell3.setTag("drawerCenterAvatarCheck");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("CenterAvatar", R.string.CenterAvatar), Theme.drawerCenterAvatarCheck, false);
                } else if (i == ThemingDrawerActivity.this.rowGradientListCheckRow) {
                    textCell3.setTag("drawerRowGradientListCheck");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("RowGradientList", R.string.RowGradientList), AndroidUtilities.getIntDef("drawerRowGradient", 0) == 0 ? false : themePrefs.getBoolean("drawerRowGradientListCheck", false), true);
                }
            } else if (type == 5) {
                if (view == null) {
                    view = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell4 = (TextDetailSettingsCell) view;
                int value;
                if (i == ThemingDrawerActivity.this.headerGradientRow) {
                    textCell4.setTag("drawerHeaderGradient");
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt("drawerHeaderGradient", 0);
                    if (value == 0) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled), false);
                    } else if (value == 1) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTopBottom", R.string.RowGradientTopBottom), false);
                    } else if (value == 2) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientLeftRight", R.string.RowGradientLeftRight), false);
                    } else if (value == 3) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTLBR", R.string.RowGradientTLBR), false);
                    } else if (value == 4) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientBLTR", R.string.RowGradientBLTR), false);
                    }
                } else if (i == ThemingDrawerActivity.this.rowGradientRow) {
                    textCell4.setTag("drawerRowGradient");
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt("drawerRowGradient", 0);
                    if (value == 0) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled), false);
                    } else if (value == 1) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTopBottom", R.string.RowGradientTopBottom), false);
                    } else if (value == 2) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientLeftRight", R.string.RowGradientLeftRight), false);
                    } else if (value == 3) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTLBR", R.string.RowGradientTLBR), false);
                    } else if (value == 4) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientBLTR", R.string.RowGradientBLTR), false);
                    }
                }
            }
            if (view != null) {
                view.setBackgroundColor(Theme.usePlusTheme ? Theme.prefBGColor : Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            if (view != null) {
                view.setBackgroundColor(Theme.usePlusTheme ? Theme.prefBGColor : Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ThemingDrawerActivity.this.rowsSectionRow) {
                return 0;
            }
            if (i == ThemingDrawerActivity.this.headerSection2Row || i == ThemingDrawerActivity.this.rowsSection2Row) {
                return 1;
            }
            if (i == ThemingDrawerActivity.this.avatarRadiusRow || i == ThemingDrawerActivity.this.avatarSizeRow || i == ThemingDrawerActivity.this.nameSizeRow || i == ThemingDrawerActivity.this.phoneSizeRow || i == ThemingDrawerActivity.this.optionSizeRow || i == ThemingDrawerActivity.this.versionSizeRow) {
                return 2;
            }
            if (i == ThemingDrawerActivity.this.headerColorRow || i == ThemingDrawerActivity.this.headerGradientColorRow || i == ThemingDrawerActivity.this.listColorRow || i == ThemingDrawerActivity.this.rowGradientColorRow || i == ThemingDrawerActivity.this.listDividerColorRow || i == ThemingDrawerActivity.this.iconColorRow || i == ThemingDrawerActivity.this.optionColorRow || i == ThemingDrawerActivity.this.versionColorRow || i == ThemingDrawerActivity.this.avatarColorRow || i == ThemingDrawerActivity.this.nameColorRow || i == ThemingDrawerActivity.this.phoneColorRow) {
                return 3;
            }
            if (i == ThemingDrawerActivity.this.headerBackgroundCheckRow || i == ThemingDrawerActivity.this.hideBackgroundShadowRow || i == ThemingDrawerActivity.this.centerAvatarRow || i == ThemingDrawerActivity.this.rowGradientListCheckRow) {
                return 4;
            }
            if (i == ThemingDrawerActivity.this.headerGradientRow || i == ThemingDrawerActivity.this.rowGradientRow) {
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
        this.headerSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerBackgroundCheckRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.hideBackgroundShadowRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerGradientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerGradientColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarRadiusRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.phoneColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.phoneSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.centerAvatarRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowsSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.listColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowGradientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowGradientColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.listDividerColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.iconColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.optionColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.optionSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.versionColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.versionSizeRow = i;
        this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("drawerShowPrefix", true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.player && MediaController.getInstance().getPlayingMessageObject() != null) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, Integer.valueOf(MediaController.getInstance().getPlayingMessageObject().getId()));
        }
        if (this.drawer) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        }
    }

    public View createView(Context context) {
        if (this.fragmentView == null) {
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            if (AndroidUtilities.isTablet()) {
                this.actionBar.setOccupyStatusBar(false);
            }
            this.actionBar.setTitle(LocaleController.getString("NavigationDrawer", R.string.NavigationDrawer));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        ThemingDrawerActivity.this.finishFragment();
                    }
                }
            });
            this.actionBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean z;
                    ThemingDrawerActivity themingDrawerActivity = ThemingDrawerActivity.this;
                    if (ThemingDrawerActivity.this.showPrefix) {
                        z = false;
                    } else {
                        z = true;
                    }
                    themingDrawerActivity.showPrefix = z;
                    ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("drawerShowPrefix", ThemingDrawerActivity.this.showPrefix).apply();
                    if (ThemingDrawerActivity.this.listAdapter != null) {
                        ThemingDrawerActivity.this.listAdapter.notifyDataSetChanged();
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
                    SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
                    String key = view.getTag() != null ? view.getTag().toString() : "";
                    int defColor = themePrefs.getInt(Theme.pkey_themeColor, AndroidUtilities.defColor);
                    if (i == ThemingDrawerActivity.this.headerColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.drawerHeaderColor = color;
                                    ThemingDrawerActivity.this.commitInt("drawerHeaderColor", color);
                                }
                            }, themePrefs.getInt("drawerHeaderColor", defColor), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.headerGradientRow) {
                        Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("RowGradient", R.string.RowGradient));
                        ArrayList array = new ArrayList();
                        array.add(LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled));
                        array.add(LocaleController.getString("RowGradientTopBottom", R.string.RowGradientTopBottom));
                        array.add(LocaleController.getString("RowGradientLeftRight", R.string.RowGradientLeftRight));
                        array.add(LocaleController.getString("RowGradientTLBR", R.string.RowGradientTLBR));
                        array.add(LocaleController.getString("RowGradientBLTR", R.string.RowGradientBLTR));
                        String[] simpleArray = new String[array.size()];
                        array.toArray(new String[array.size()]);
                        builder.setItems((CharSequence[]) array.toArray(simpleArray), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt("drawerHeaderGradient", which).commit();
                                if (ThemingDrawerActivity.this.listView != null) {
                                    ThemingDrawerActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingDrawerActivity.this.showDialog(builder.create());
                    } else if (i == ThemingDrawerActivity.this.headerGradientColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingDrawerActivity.this.commitInt("drawerHeaderGradientColor", color);
                                }
                            }, themePrefs.getInt("drawerHeaderGradientColor", defColor), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.headerBackgroundCheckRow) {
                        Theme.drawerHeaderBGCheck = !Theme.drawerHeaderBGCheck;
                        //editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.drawerHeaderBGCheck);
                        themePrefs.edit().commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.drawerHeaderBGCheck);
                        }
                    } else if (i == ThemingDrawerActivity.this.hideBackgroundShadowRow) {
                        Theme.drawerHideBGShadowCheck = !Theme.drawerHideBGShadowCheck;
                        //editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.drawerHideBGShadowCheck);
                        themePrefs.edit().commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.drawerHideBGShadowCheck);
                        }
                    } else if (i == ThemingDrawerActivity.this.centerAvatarRow) {
                        Theme.drawerCenterAvatarCheck = !Theme.drawerCenterAvatarCheck;
                        //editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.drawerCenterAvatarCheck);
                        themePrefs.edit().commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.drawerCenterAvatarCheck);
                        }
                    } else if (i == ThemingDrawerActivity.this.listColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingDrawerActivity.this.commitInt("drawerListColor", color);
                                    ThemingDrawerActivity.this.player = true;
                                }
                            }, themePrefs.getInt("drawerListColor", -1), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.rowGradientColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingDrawerActivity.this.commitInt("drawerRowGradientColor", color);
                                }
                            }, themePrefs.getInt("drawerRowGradientColor", -1), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.rowGradientListCheckRow) {
                        boolean b = themePrefs.getBoolean("drawerRowGradientListCheck", false);
                        //editor = themePrefs.edit();
                        themePrefs.edit().putBoolean("drawerRowGradientListCheck", !b);
                        themePrefs.edit().commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(!b);
                        }
                    } else if (i == ThemingDrawerActivity.this.rowGradientRow) {
                        Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("RowGradient", R.string.RowGradient));
                        ArrayList array = new ArrayList();
                        array.add(LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled));
                        array.add(LocaleController.getString("RowGradientTopBottom", R.string.RowGradientTopBottom));
                        array.add(LocaleController.getString("RowGradientLeftRight", R.string.RowGradientLeftRight));
                        array.add(LocaleController.getString("RowGradientTLBR", R.string.RowGradientTLBR));
                        array.add(LocaleController.getString("RowGradientBLTR", R.string.RowGradientBLTR));
                        String[] simpleArray = new String[array.size()];
                        array.toArray(new String[array.size()]);
                        builder.setItems((CharSequence[]) array.toArray(simpleArray), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt("drawerRowGradient", which).commit();
                                if (ThemingDrawerActivity.this.listView != null) {
                                    ThemingDrawerActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingDrawerActivity.this.showDialog(builder.create());
                    } else if (i == ThemingDrawerActivity.this.listDividerColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingDrawerActivity.this.commitInt("drawerListDividerColor", color);
                                    ThemingDrawerActivity.this.player = true;
                                }
                            }, themePrefs.getInt("drawerListDividerColor", -2500135), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.iconColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.drawerIconColor = color;
                                    ThemingDrawerActivity.this.commitInt(Theme.pkey_drawerIconColor, color);
                                    ThemingDrawerActivity.this.player = true;
                                }
                            }, themePrefs.getInt(Theme.pkey_drawerIconColor, -9211021), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.optionColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.drawerOptionColor = color;
                                    ThemingDrawerActivity.this.commitInt(Theme.pkey_drawerOptionColor, color);
                                    ThemingDrawerActivity.this.player = true;
                                }
                            }, themePrefs.getInt(Theme.pkey_drawerOptionColor, -12303292), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.versionColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingDrawerActivity.this.commitInt("drawerVersionColor", color);
                                }
                            }, themePrefs.getInt("drawerVersionColor", -6052957), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.avatarColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingDrawerActivity.this.commitInt("drawerAvatarColor", color);
                                }
                            }, themePrefs.getInt("drawerAvatarColor", Theme.darkColor), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.nameColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.drawerNameColor = color;
                                    ThemingDrawerActivity.this.commitInt(Theme.pkey_drawerNameColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_drawerNameColor, -1), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.phoneColorRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingDrawerActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingDrawerActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.drawerPhoneColor = color;
                                    ThemingDrawerActivity.this.commitInt(Theme.pkey_drawerPhoneColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_drawerPhoneColor, AndroidUtilities.getIntDarkerColor(Theme.pkey_themeColor, -64)), 0, 0, true).show();
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.avatarRadiusRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            r0 = new NumberPicker(ThemingDrawerActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt("drawerAvatarRadius", 32);
                            r0.setMinValue(1);
                            r0.setMaxValue(32);
                            r0.setValue(currentValue);
                            builder.setView(r0);
                          //  r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingDrawerActivity.this.commitInt("drawerAvatarRadius", r0.getValue());
                                    }
                                }
                            });
                            ThemingDrawerActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.avatarSizeRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarSize", R.string.AvatarSize));
                            r0 = new NumberPicker(ThemingDrawerActivity.this.getParentActivity());
                            r0.setMinValue(0);
                            r0.setMaxValue(75);
                            r0.setValue(Theme.drawerAvatarSize);
                            builder.setView(r0);
                          //  r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.drawerAvatarSize) {
                                        Theme.drawerAvatarSize = r0.getValue();
                                        ThemingDrawerActivity.this.commitInt("drawerAvatarSize", r0.getValue());
                                    }
                                }
                            });
                            ThemingDrawerActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.nameSizeRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("OwnNameSize", R.string.OwnNameSize));
                            r0 = new NumberPicker(ThemingDrawerActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt("drawerNameSize", 15);
                            r0.setMinValue(10);
                            r0.setMaxValue(20);
                            r0.setValue(currentValue);
                            builder.setView(r0);
                          //  r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingDrawerActivity.this.commitInt("drawerNameSize", r0.getValue());
                                    }
                                }
                            });
                            ThemingDrawerActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.phoneSizeRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("PhoneSize", R.string.StatusSize));
                            r0 = new NumberPicker(ThemingDrawerActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt("drawerPhoneSize", 13);
                            r0.setMinValue(8);
                            r0.setMaxValue(18);
                            r0.setValue(currentValue);
                            builder.setView(r0);
                          //  r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingDrawerActivity.this.commitInt("drawerPhoneSize", r0.getValue());
                                    }
                                }
                            });
                            ThemingDrawerActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.optionSizeRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("OptionSize", R.string.OptionSize));
                            r0 = new NumberPicker(ThemingDrawerActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt("drawerOptionSize", 15);
                            r0.setMinValue(10);
                            r0.setMaxValue(20);
                            r0.setValue(currentValue);
                            builder.setView(r0);
                          //  r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingDrawerActivity.this.commitInt("drawerOptionSize", r0.getValue());
                                    }
                                }
                            });
                            ThemingDrawerActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    } else if (i == ThemingDrawerActivity.this.versionSizeRow) {
                        if (ThemingDrawerActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingDrawerActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("VersionSize", R.string.VersionSize));
                            r0 = new NumberPicker(ThemingDrawerActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt("drawerVersionSize", 13);
                            r0.setMinValue(10);
                            r0.setMaxValue(20);
                            r0.setValue(currentValue);
                            builder.setView(r0);
                          //  r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingDrawerActivity.this.commitInt("drawerVersionSize", r0.getValue());
                                    }
                                }
                            });
                            ThemingDrawerActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    }
                    ThemingDrawerActivity.this.drawer = true;
                }
            });
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (ThemingDrawerActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    if (i == ThemingDrawerActivity.this.headerColorRow) {
                        ThemingDrawerActivity.this.resetInt("drawerHeaderColor");
                    } else if (i == ThemingDrawerActivity.this.headerGradientRow) {
                        ThemingDrawerActivity.this.resetInt("drawerHeaderGradient");
                    } else if (i == ThemingDrawerActivity.this.headerGradientColorRow) {
                        ThemingDrawerActivity.this.resetInt("drawerHeaderGradientColor");
                    } else if (i == ThemingDrawerActivity.this.listColorRow) {
                        ThemingDrawerActivity.this.resetInt("drawerListColor");
                        ThemingDrawerActivity.this.player = true;
                    } else if (i == ThemingDrawerActivity.this.rowGradientColorRow) {
                        ThemingDrawerActivity.this.resetInt("drawerRowGradientColor");
                        ThemingDrawerActivity.this.player = true;
                    } else if (i == ThemingDrawerActivity.this.rowGradientRow) {
                        ThemingDrawerActivity.this.resetInt("drawerRowGradient");
                        ThemingDrawerActivity.this.player = true;
                    } else if (i == ThemingDrawerActivity.this.rowGradientListCheckRow) {
                        ThemingDrawerActivity.this.resetInt("drawerRowGradientListCheck");
                        ThemingDrawerActivity.this.player = true;
                    } else if (i == ThemingDrawerActivity.this.listDividerColorRow) {
                        ThemingDrawerActivity.this.resetInt("drawerListDividerColor");
                    } else if (i == ThemingDrawerActivity.this.avatarColorRow) {
                        ThemingDrawerActivity.this.resetInt("drawerAvatarColor");
                    } else if (i == ThemingDrawerActivity.this.avatarRadiusRow) {
                        ThemingDrawerActivity.this.resetInt("drawerAvatarRadius");
                    } else if (i == ThemingDrawerActivity.this.nameColorRow) {
                        ThemingDrawerActivity.this.resetInt(Theme.pkey_drawerNameColor);
                    } else if (i == ThemingDrawerActivity.this.avatarSizeRow) {
                        ThemingDrawerActivity.this.resetInt("drawerAvatarSize");
                    } else if (i == ThemingDrawerActivity.this.nameSizeRow) {
                        ThemingDrawerActivity.this.resetInt("drawerNameSize");
                    } else if (i == ThemingDrawerActivity.this.phoneColorRow) {
                        ThemingDrawerActivity.this.resetInt(Theme.pkey_drawerPhoneColor);
                    } else if (i == ThemingDrawerActivity.this.phoneSizeRow) {
                        ThemingDrawerActivity.this.resetInt("drawerPhoneSize");
                    } else if (i == ThemingDrawerActivity.this.iconColorRow) {
                        ThemingDrawerActivity.this.resetInt(Theme.pkey_drawerIconColor);
                        ThemingDrawerActivity.this.player = true;
                    } else if (i == ThemingDrawerActivity.this.optionColorRow) {
                        ThemingDrawerActivity.this.resetInt(Theme.pkey_drawerOptionColor);
                        ThemingDrawerActivity.this.player = true;
                    } else if (i == ThemingDrawerActivity.this.optionSizeRow) {
                        ThemingDrawerActivity.this.resetInt("drawerOptionSize");
                    } else if (i == ThemingDrawerActivity.this.versionColorRow) {
                        ThemingDrawerActivity.this.resetInt("drawerVersionColor");
                    } else if (i == ThemingDrawerActivity.this.versionSizeRow) {
                        ThemingDrawerActivity.this.resetInt("drawerVersionSize");
                    } else if (view.getTag() != null) {
                        ThemingDrawerActivity.this.resetPref(view.getTag().toString());
                    }
                    ThemingDrawerActivity.this.drawer = true;
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
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
        refreshTheme();
    }

    private void resetInt(String key) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
        editor.remove(key);
        editor.commit();
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    private void commitInt(String key, int value) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
        editor.putInt(key, value);
        editor.commit();
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
        refreshTheme();
    }

    private void refreshTheme() {
        Theme.applyPlusTheme();
        if (this.parentLayout != null) {
            this.parentLayout.rebuildAllFragmentViews(false);
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
        this.actionBar.setBackgroundColor(Theme.prefActionbarColor);
        this.actionBar.setTitleColor(Theme.prefActionbarTitleColor);
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
                    if (ThemingDrawerActivity.this.fragmentView != null) {
                        ThemingDrawerActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return false;
                }
            });
        }
    }
}
