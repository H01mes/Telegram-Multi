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

public class ThemingContactsActivity extends BaseFragment {
    public static final int CENTER = 0;
    private int avatarRadiusRow;
    private int headerColorRow;
    private int headerGradientColorRow;
    private int headerGradientRow;
    private int headerIconsColorRow;
    private int headerSection2Row;
    private int headerTitleColorRow;
    private int iconsColorRow;
    private ListAdapter listAdapter;
    private ListView listView;
    private int nameColorRow;
    private int nameSizeRow;
    private int onlineColorRow;
    private int rowColorRow;
    private int rowCount;
    private int rowGradientColorRow;
    private int rowGradientListCheckRow;
    private int rowGradientRow;
    private int rowsSection2Row;
    private int rowsSectionRow;
    private boolean showPrefix;
    private int statusColorRow;
    private int statusSizeRow;
    NumberPicker numberPicker;
    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            int g = AndroidUtilities.getIntDef("contactsRowGradient", 0);
            if (i == ThemingContactsActivity.this.headerColorRow || i == ThemingContactsActivity.this.headerGradientRow || ((AndroidUtilities.getIntDef("contactsHeaderGradient", 0) != 0 && i == ThemingContactsActivity.this.headerGradientColorRow) || i == ThemingContactsActivity.this.headerTitleColorRow || i == ThemingContactsActivity.this.headerIconsColorRow || i == ThemingContactsActivity.this.iconsColorRow || i == ThemingContactsActivity.this.rowColorRow || i == ThemingContactsActivity.this.rowGradientRow || ((g != 0 && i == ThemingContactsActivity.this.rowGradientColorRow) || ((g != 0 && i == ThemingContactsActivity.this.rowGradientListCheckRow) || i == ThemingContactsActivity.this.avatarRadiusRow || i == ThemingContactsActivity.this.nameColorRow || i == ThemingContactsActivity.this.nameSizeRow || i == ThemingContactsActivity.this.statusColorRow || i == ThemingContactsActivity.this.statusSizeRow || i == ThemingContactsActivity.this.onlineColorRow)))) {
                return true;
            }
            return false;
        }

        public int getCount() {
            return ThemingContactsActivity.this.rowCount;
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
            boolean z = false;
            int type = getItemViewType(i);
            String prefix = "";
            if (ThemingContactsActivity.this.showPrefix) {
                prefix = "3.";
                if (i == ThemingContactsActivity.this.headerSection2Row) {
                    prefix = prefix + "1 ";
                } else if (i == ThemingContactsActivity.this.rowsSection2Row) {
                    prefix = prefix + "2 ";
                } else if (i < ThemingContactsActivity.this.rowsSection2Row) {
                    prefix = prefix + "1." + i + " ";
                } else {
                    prefix = prefix + "2." + (i - ThemingContactsActivity.this.rowsSection2Row) + " ";
                }
            }
            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
            if (type == 0) {
                if (view == null) {
                    view = new ShadowSectionCell(this.mContext);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                if (i == ThemingContactsActivity.this.headerSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("Header", R.string.Header));
                } else if (i == ThemingContactsActivity.this.rowsSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("ContactsList", R.string.ContactsList));
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == ThemingContactsActivity.this.avatarRadiusRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(Theme.contactsAvatarRadius)}), true);
                } else if (i == ThemingContactsActivity.this.nameSizeRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("NameSize", R.string.NameSize), String.format("%d", new Object[]{Integer.valueOf(Theme.contactsNameSize)}), true);
                } else if (i == ThemingContactsActivity.this.statusSizeRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("StatusSize", R.string.StatusSize), String.format("%d", new Object[]{Integer.valueOf(Theme.contactsStatusSize)}), true);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextColorCell(this.mContext);
                }
                TextColorCell textCell2 = (TextColorCell) view;
                int r5 = 0;
                if (i == ThemingContactsActivity.this.headerColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderColor", R.string.HeaderColor), Theme.contactsHeaderColor, false);
                } else if (i == ThemingContactsActivity.this.headerGradientColorRow) {
                    String r6 = prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor);
                    if (themePrefs.getInt("contactsHeaderGradient", 0) != 0) {
                        r5 = themePrefs.getInt("contactsHeaderGradientColor", Theme.defColor);
                    }
                    textCell2.setTextAndColor(r6, r5, true);
                } else if (i == ThemingContactsActivity.this.headerTitleColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderTitleColor", R.string.HeaderTitleColor), Theme.contactsHeaderTitleColor, true);
                } else if (i == ThemingContactsActivity.this.headerIconsColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderIconsColor", R.string.HeaderIconsColor), Theme.contactsHeaderIconsColor, false);
                } else if (i == ThemingContactsActivity.this.iconsColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("IconsColor", R.string.IconsColor), Theme.contactsIconsColor, true);
                } else if (i == ThemingContactsActivity.this.rowColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("RowColor", R.string.RowColor), Theme.contactsRowColor, false);
                } else if (i == ThemingContactsActivity.this.rowGradientColorRow) {
                    String r6 = prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor);
                    if (themePrefs.getInt("contactsRowGradient", 0) != 0) {
                        r5 = themePrefs.getInt("contactsRowGradientColor", -1);
                    }
                    textCell2.setTextAndColor(r6, r5, true);
                } else if (i == ThemingContactsActivity.this.nameColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("NameColor", R.string.NameColor), Theme.contactsNameColor, true);
                } else if (i == ThemingContactsActivity.this.statusColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("StatusColor", R.string.StatusColor), Theme.contactsStatusColor, true);
                } else if (i == ThemingContactsActivity.this.onlineColorRow) {
                    textCell2.setTextAndColor(prefix + LocaleController.getString("OnlineColor", R.string.OnlineColor), Theme.contactsOnlineColor, false);
                }
            } else if (type == 4) {
                if (view == null) {
                    view = new TextCheckCell(this.mContext);
                }
                TextCheckCell textCell3 = (TextCheckCell) view;
                if (i == ThemingContactsActivity.this.rowGradientListCheckRow) {
                    textCell3.setTag("contactsRowGradientListCheck");
                    int value = AndroidUtilities.getIntDef("contactsRowGradient", 0);
                    String r6 = prefix + LocaleController.getString("RowGradientList", R.string.RowGradientList);
                    if (value != 0) {
                        z = themePrefs.getBoolean("contactsRowGradientListCheck", false);
                    }
                    textCell3.setTextAndCheck(r6, z, true);
                }
            } else if (type == 5) {
                if (view == null) {
                    view = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell4 = (TextDetailSettingsCell) view;
                if (i == ThemingContactsActivity.this.headerGradientRow) {
                    textCell4.setTag("contactsHeaderGradient");
                    textCell4.setMultilineDetail(false);
                    int value = themePrefs.getInt("contactsHeaderGradient", 0);
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
                } else if (i == ThemingContactsActivity.this.rowGradientRow) {
                    textCell4.setTag("contactsRowGradient");
                    textCell4.setMultilineDetail(false);
                    int value = themePrefs.getInt("contactsRowGradient", 0);
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
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ThemingContactsActivity.this.rowsSectionRow) {
                return 0;
            }
            if (i == ThemingContactsActivity.this.headerSection2Row || i == ThemingContactsActivity.this.rowsSection2Row) {
                return 1;
            }
            if (i == ThemingContactsActivity.this.avatarRadiusRow || i == ThemingContactsActivity.this.nameSizeRow || i == ThemingContactsActivity.this.statusSizeRow) {
                return 2;
            }
            if (i == ThemingContactsActivity.this.headerColorRow || i == ThemingContactsActivity.this.headerGradientColorRow || i == ThemingContactsActivity.this.headerTitleColorRow || i == ThemingContactsActivity.this.headerIconsColorRow || i == ThemingContactsActivity.this.iconsColorRow || i == ThemingContactsActivity.this.rowColorRow || i == ThemingContactsActivity.this.rowGradientColorRow || i == ThemingContactsActivity.this.nameColorRow || i == ThemingContactsActivity.this.statusColorRow || i == ThemingContactsActivity.this.onlineColorRow) {
                return 3;
            }
            if (i == ThemingContactsActivity.this.rowGradientListCheckRow) {
                return 4;
            }
            if (i == ThemingContactsActivity.this.headerGradientRow || i == ThemingContactsActivity.this.rowGradientRow) {
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
        this.headerColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerGradientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerGradientColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerTitleColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerIconsColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowsSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowGradientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowGradientColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarRadiusRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.iconsColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.statusColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.statusSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.onlineColorRow = i;
        this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("contactsShowPrefix", true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        if (this.fragmentView == null) {
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            if (AndroidUtilities.isTablet()) {
                this.actionBar.setOccupyStatusBar(false);
            }
            this.actionBar.setTitle(LocaleController.getString("ContactsScreen", R.string.ContactsScreen));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        ThemingContactsActivity.this.finishFragment();
                    }
                }
            });
            this.actionBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean z;
                    ThemingContactsActivity themingContactsActivity = ThemingContactsActivity.this;
                    if (ThemingContactsActivity.this.showPrefix) {
                        z = false;
                    } else {
                        z = true;
                    }
                    themingContactsActivity.showPrefix = z;
                    ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("contactsShowPrefix", ThemingContactsActivity.this.showPrefix).apply();
                    if (ThemingContactsActivity.this.listAdapter != null) {
                        ThemingContactsActivity.this.listAdapter.notifyDataSetChanged();
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
                    if (i == ThemingContactsActivity.this.headerColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsHeaderColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsHeaderColor", color);
                                }
                            }, Theme.contactsHeaderColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingContactsActivity.this.headerGradientColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingContactsActivity.this.commitInt("contactsHeaderGradientColor", color);
                                }
                            }, themePrefs.getInt("contactsHeaderGradientColor", Theme.defColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingContactsActivity.this.headerTitleColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsHeaderTitleColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsHeaderTitleColor", color);
                                }
                            }, Theme.contactsHeaderTitleColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingContactsActivity.this.headerIconsColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsHeaderIconsColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsHeaderIconsColor", color);
                                }
                            }, Theme.contactsHeaderIconsColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingContactsActivity.this.iconsColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsIconsColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsIconsColor", color);
                                }
                            }, Theme.contactsIconsColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingContactsActivity.this.rowColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsRowColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsRowColor", color);
                                }
                            }, Theme.contactsRowColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingContactsActivity.this.rowGradientColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingContactsActivity.this.commitInt("contactsRowGradientColor", color);
                                }
                            }, themePrefs.getInt("contactsRowGradientColor", -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingContactsActivity.this.rowGradientListCheckRow) {
                        boolean b = themePrefs.getBoolean("contactsRowGradientListCheck", false);
                        Editor editor = themePrefs.edit();
                        editor.putBoolean("contactsRowGradientListCheck", !b);
                        editor.commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(!b);
                        }
                    } else if (i == ThemingContactsActivity.this.headerGradientRow) {
                        Builder builder = new Builder(ThemingContactsActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt("contactsHeaderGradient", which).commit();
                                if (ThemingContactsActivity.this.listView != null) {
                                    ThemingContactsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingContactsActivity.this.showDialog(builder.create());
                    } else if (i == ThemingContactsActivity.this.rowGradientRow) {
                        Builder builder = new Builder(ThemingContactsActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt("contactsRowGradient", which).commit();
                                if (ThemingContactsActivity.this.listView != null) {
                                    ThemingContactsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingContactsActivity.this.showDialog(builder.create());
                    } else if (i == ThemingContactsActivity.this.nameColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsRowColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsNameColor", color);
                                }
                            }, Theme.contactsRowColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingContactsActivity.this.statusColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsStatusColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsStatusColor", color);
                                }
                            }, Theme.contactsStatusColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingContactsActivity.this.onlineColorRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingContactsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingContactsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.contactsOnlineColor = color;
                                    ThemingContactsActivity.this.commitInt("contactsOnlineColor", color);
                                }
                            }, Theme.contactsOnlineColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingContactsActivity.this.avatarRadiusRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingContactsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            numberPicker = new NumberPicker(ThemingContactsActivity.this.getParentActivity());
                            numberPicker.setMinValue(1);
                            numberPicker.setMaxValue(32);
                            numberPicker.setValue(Theme.contactsAvatarRadius);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.contactsAvatarRadius) {
                                        Theme.contactsAvatarRadius = numberPicker.getValue();
                                        ThemingContactsActivity.this.commitInt("contactsAvatarRadius", numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingContactsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingContactsActivity.this.nameSizeRow) {
                        if (ThemingContactsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingContactsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("NameSize", R.string.NameSize));
                            numberPicker = new NumberPicker(ThemingContactsActivity.this.getParentActivity());
                            numberPicker.setMinValue(12);
                            numberPicker.setMaxValue(30);
                            numberPicker.setValue(Theme.contactsNameSize);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.contactsNameSize) {
                                        Theme.contactsNameSize = numberPicker.getValue();
                                        ThemingContactsActivity.this.commitInt("contactsNameSize", numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingContactsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingContactsActivity.this.statusSizeRow && ThemingContactsActivity.this.getParentActivity() != null) {
                        Builder builder = new Builder(ThemingContactsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("StatusSize", R.string.StatusSize));
                        numberPicker = new NumberPicker(ThemingContactsActivity.this.getParentActivity());
                        numberPicker.setMinValue(10);
                        numberPicker.setMaxValue(20);
                        numberPicker.setValue(Theme.contactsStatusSize);
                        builder.setView(numberPicker);
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (numberPicker.getValue() != Theme.contactsStatusSize) {
                                    Theme.contactsStatusSize = numberPicker.getValue();
                                    ThemingContactsActivity.this.commitInt("contactsStatusSize", numberPicker.getValue());
                                }
                            }
                        });
                        ThemingContactsActivity.this.showDialog(builder.create());
                    }
                }
            });
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (ThemingContactsActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    if (i == ThemingContactsActivity.this.headerColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsHeaderColor");
                    } else if (i == ThemingContactsActivity.this.headerGradientColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsHeaderGradientColor");
                    } else if (i == ThemingContactsActivity.this.headerTitleColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsHeaderTitleColor");
                    } else if (i == ThemingContactsActivity.this.headerIconsColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsHeaderIconsColor");
                    } else if (i == ThemingContactsActivity.this.iconsColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsIconsColor");
                    } else if (i == ThemingContactsActivity.this.rowColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsRowColor");
                    } else if (i == ThemingContactsActivity.this.rowGradientColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsRowGradientColor");
                    } else if (i == ThemingContactsActivity.this.headerGradientRow) {
                        ThemingContactsActivity.this.resetInt("contactsHeaderGradient");
                    } else if (i == ThemingContactsActivity.this.rowGradientRow) {
                        ThemingContactsActivity.this.resetInt("contactsRowGradient");
                    } else if (i == ThemingContactsActivity.this.avatarRadiusRow) {
                        ThemingContactsActivity.this.resetInt("contactsAvatarRadius");
                    } else if (i == ThemingContactsActivity.this.nameColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsNameColor");
                    } else if (i == ThemingContactsActivity.this.nameSizeRow) {
                        ThemingContactsActivity.this.resetInt("contactsNameSize");
                    } else if (i == ThemingContactsActivity.this.statusColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsStatusColor");
                    } else if (i == ThemingContactsActivity.this.statusSizeRow) {
                        ThemingContactsActivity.this.resetInt("contactsStatusSize");
                    } else if (i == ThemingContactsActivity.this.onlineColorRow) {
                        ThemingContactsActivity.this.resetInt("contactsOnlineColor");
                    } else if (view.getTag() != null) {
                        ThemingContactsActivity.this.resetPref(view.getTag().toString());
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
        if (key != null) {
            editor.remove(key);
        }
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
                    if (ThemingContactsActivity.this.fragmentView != null) {
                        ThemingContactsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return false;
                }
            });
        }
    }
}
