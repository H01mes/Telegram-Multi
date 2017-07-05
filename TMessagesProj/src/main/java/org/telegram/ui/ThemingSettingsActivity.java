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
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ColorSelectorDialog;
import org.telegram.ui.Components.ColorSelectorDialog.OnColorChangedListener;
import org.telegram.ui.Components.NumberPicker;

public class ThemingSettingsActivity extends BaseFragment {
    public static final int CENTER = 0;
    private int avatarColorRow;
    private int avatarRadiusRow;
    private int avatarSizeRow;
    private int backgroundColorRow;
    private int dividerColorRow;
    private int headerColorRow;
    private int headerIconsColorRow;
    private int headerSection2Row;
    private int headerStatusColorRow;
    private int headerTitleColorRow;
    private ListAdapter listAdapter;
    private ListView listView;
    private int rowCount;
    private int rowsSection2Row;
    private int rowsSectionRow;
    private int sectionColorRow;
    private int shadowColorRow;
    private boolean showPrefix;
    private int summaryColorRow;
    private int titleColorRow;
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
            return i == ThemingSettingsActivity.this.headerColorRow || i == ThemingSettingsActivity.this.headerTitleColorRow || i == ThemingSettingsActivity.this.headerStatusColorRow || i == ThemingSettingsActivity.this.headerIconsColorRow || i == ThemingSettingsActivity.this.avatarColorRow || i == ThemingSettingsActivity.this.avatarRadiusRow || i == ThemingSettingsActivity.this.avatarSizeRow || i == ThemingSettingsActivity.this.backgroundColorRow || i == ThemingSettingsActivity.this.shadowColorRow || i == ThemingSettingsActivity.this.sectionColorRow || i == ThemingSettingsActivity.this.titleColorRow || i == ThemingSettingsActivity.this.summaryColorRow || i == ThemingSettingsActivity.this.dividerColorRow;
        }

        public int getCount() {
            return ThemingSettingsActivity.this.rowCount;
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
            if (ThemingSettingsActivity.this.showPrefix) {
                prefix = "6.";
                if (i == ThemingSettingsActivity.this.headerSection2Row) {
                    prefix = prefix + "1 ";
                } else if (i == ThemingSettingsActivity.this.rowsSection2Row) {
                    prefix = prefix + "2 ";
                } else if (i < ThemingSettingsActivity.this.rowsSection2Row) {
                    prefix = prefix + "1." + i + " ";
                } else {
                    prefix = prefix + "2." + (i - ThemingSettingsActivity.this.rowsSection2Row) + " ";
                }
            }
            if (type == 0) {
                if (view == null) {
                    view = new ShadowSectionCell(this.mContext);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                if (i == ThemingSettingsActivity.this.headerSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("Header", R.string.Header));
                } else if (i == ThemingSettingsActivity.this.rowsSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("OptionsList", R.string.OptionsList));
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextColorCell(this.mContext);
                }
                TextColorCell textCell = (TextColorCell) view;
                if (i == ThemingSettingsActivity.this.headerColorRow) {
                    textCell.setTag(Theme.pkey_prefActionbarColor);
                    textCell.setTextAndColor(prefix + LocaleController.getString("HeaderColor", R.string.HeaderColor), Theme.prefActionbarColor, true);
                } else if (i == ThemingSettingsActivity.this.headerTitleColorRow) {
                    textCell.setTag(Theme.pkey_prefActionbarTitleColor);
                    textCell.setTextAndColor(prefix + LocaleController.getString("HeaderTitleColor", R.string.HeaderTitleColor), Theme.prefActionbarTitleColor, true);
                } else if (i == ThemingSettingsActivity.this.headerStatusColorRow) {
                    textCell.setTag(Theme.pkey_prefActionbarStatusColor);
                    textCell.setTextAndColor(prefix + LocaleController.getString("StatusColor", R.string.StatusColor), Theme.prefActionbarStatusColor, true);
                } else if (i == ThemingSettingsActivity.this.headerIconsColorRow) {
                    textCell.setTag("prefActionbarIconsColor");
                    textCell.setTextAndColor(prefix + LocaleController.getString("HeaderIconsColor", R.string.HeaderIconsColor), Theme.prefActionbarIconsColor, true);
                } else if (i == ThemingSettingsActivity.this.avatarColorRow) {
                    textCell.setTag(Theme.pkey_prefAvatarColor);
                    textCell.setTextAndColor(prefix + LocaleController.getString("AvatarColor", R.string.AvatarColor), Theme.prefAvatarColor, true);
                } else if (i == ThemingSettingsActivity.this.backgroundColorRow) {
                    textCell.setTag(Theme.pkey_prefBGColor);
                    textCell.setTextAndColor(prefix + LocaleController.getString("BackgroundColor", R.string.BackgroundColor), Theme.prefBGColor, true);
                } else if (i == ThemingSettingsActivity.this.shadowColorRow) {
                    textCell.setTag("prefShadowColor");
                    textCell.setTextAndColor(prefix + LocaleController.getString("ShadowColor", R.string.ShadowColor), Theme.prefShadowColor, true);
                } else if (i == ThemingSettingsActivity.this.sectionColorRow) {
                    textCell.setTag("prefSectionColor");
                    textCell.setTextAndColor(prefix + LocaleController.getString("SectionColor", R.string.SectionColor), Theme.prefSectionColor, true);
                } else if (i == ThemingSettingsActivity.this.titleColorRow) {
                    textCell.setTag("prefTitleColor");
                    textCell.setTextAndColor(prefix + LocaleController.getString("TitleColor", R.string.TitleColor), Theme.prefTitleColor, true);
                } else if (i == ThemingSettingsActivity.this.summaryColorRow) {
                    textCell.setTag("prefSummaryColor");
                    textCell.setTextAndColor(prefix + LocaleController.getString("SummaryColor", R.string.SummaryColor), Theme.prefSummaryColor, true);
                } else if (i == ThemingSettingsActivity.this.dividerColorRow) {
                    textCell.setTag("prefDividerColor");
                    textCell.setTextAndColor(prefix + LocaleController.getString("DividerColor", R.string.DividerColor), Theme.prefDividerColor, true);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell2 = (TextSettingsCell) view;
                SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
                int size;
                if (i == ThemingSettingsActivity.this.avatarRadiusRow) {
                    textCell2.setTag("prefAvatarRadius");
                    size = themePrefs.getInt("prefAvatarRadius", AndroidUtilities.isTablet() ? 35 : 32);
                    textCell2.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingSettingsActivity.this.avatarSizeRow) {
                    textCell2.setTag("prefAvatarSize");
                    size = themePrefs.getInt("prefAvatarSize", AndroidUtilities.isTablet() ? 45 : 42);
                    textCell2.setTextAndValue(prefix + LocaleController.getString("AvatarSize", R.string.AvatarSize), String.format("%d", new Object[]{Integer.valueOf(size)}), false);
                }
            }
            if (view != null) {
                view.setBackgroundColor(Theme.usePlusTheme ? Theme.prefBGColor : Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ThemingSettingsActivity.this.avatarRadiusRow || i == ThemingSettingsActivity.this.avatarSizeRow) {
                return 3;
            }
            if (i == ThemingSettingsActivity.this.headerColorRow || i == ThemingSettingsActivity.this.headerTitleColorRow || i == ThemingSettingsActivity.this.headerStatusColorRow || i == ThemingSettingsActivity.this.headerIconsColorRow || i == ThemingSettingsActivity.this.avatarColorRow || i == ThemingSettingsActivity.this.backgroundColorRow || i == ThemingSettingsActivity.this.shadowColorRow || i == ThemingSettingsActivity.this.sectionColorRow || i == ThemingSettingsActivity.this.titleColorRow || i == ThemingSettingsActivity.this.summaryColorRow || i == ThemingSettingsActivity.this.dividerColorRow) {
                return 2;
            }
            if (i == ThemingSettingsActivity.this.headerSection2Row || i == ThemingSettingsActivity.this.rowsSection2Row) {
                return 1;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 4;
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
        this.headerTitleColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerStatusColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerIconsColorRow = i;
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
        this.rowsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowsSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.backgroundColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shadowColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sectionColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.titleColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.summaryColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dividerColorRow = i;
        this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("prefShowPrefix", true);
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
            this.actionBar.setTitle(LocaleController.getString("SettingsScreen", R.string.SettingsScreen));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        ThemingSettingsActivity.this.finishFragment();
                    }
                }
            });
            this.actionBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean z;
                    ThemingSettingsActivity themingSettingsActivity = ThemingSettingsActivity.this;
                    if (ThemingSettingsActivity.this.showPrefix) {
                        z = false;
                    } else {
                        z = true;
                    }
                    themingSettingsActivity.showPrefix = z;
                    ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("prefShowPrefix", ThemingSettingsActivity.this.showPrefix).apply();
                    if (ThemingSettingsActivity.this.listAdapter != null) {
                        ThemingSettingsActivity.this.listAdapter.notifyDataSetChanged();
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
                    if (i == ThemingSettingsActivity.this.headerColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefActionbarColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefHeaderColor", color);
                                }
                            }, Theme.prefActionbarColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.headerTitleColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefActionbarTitleColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefHeaderTitleColor", color);
                                }
                            }, Theme.prefActionbarTitleColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.headerStatusColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefActionbarStatusColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefHeaderStatusColor", color);
                                }
                            }, Theme.prefActionbarStatusColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.headerIconsColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefActionbarIconsColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefHeaderIconsColor", color);
                                }
                            }, Theme.prefActionbarIconsColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.avatarColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefAvatarColor = color;
                                    ThemingSettingsActivity.this.commitInt(Theme.pkey_prefAvatarColor, color);
                                }
                            }, Theme.prefAvatarColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.avatarRadiusRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingSettingsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            numberPicker = new NumberPicker(ThemingSettingsActivity.this.getParentActivity());
                            numberPicker.setMinValue(1);
                            numberPicker.setMaxValue(32);
                            numberPicker.setValue(Theme.prefAvatarRadius);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.prefAvatarRadius) {
                                        Theme.prefAvatarRadius = numberPicker.getValue();
                                        ThemingSettingsActivity.this.commitInt("prefAvatarRadius", numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingSettingsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingSettingsActivity.this.avatarSizeRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingSettingsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarSize", R.string.AvatarSize));
                            numberPicker = new NumberPicker(ThemingSettingsActivity.this.getParentActivity());
                            numberPicker.setMinValue(0);
                            numberPicker.setMaxValue(48);
                            numberPicker.setValue(Theme.prefAvatarSize);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.prefAvatarSize) {
                                        Theme.prefAvatarSize = numberPicker.getValue();
                                        ThemingSettingsActivity.this.commitInt("prefAvatarSize", numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingSettingsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingSettingsActivity.this.backgroundColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefBGColor = color;
                                    ThemingSettingsActivity.this.commitInt(Theme.pkey_prefBGColor, color);
                                }
                            }, Theme.prefBGColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.shadowColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefShadowColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefShadowColor", color);
                                }
                            }, Theme.prefShadowColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.sectionColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefSectionColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefSectionColor", color);
                                }
                            }, Theme.prefSectionColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.titleColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefTitleColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefTitleColor", color);
                                }
                            }, Theme.prefTitleColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.summaryColorRow) {
                        if (ThemingSettingsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.prefSummaryColor = color;
                                    ThemingSettingsActivity.this.commitInt("prefSummaryColor", color);
                                }
                            }, Theme.prefSummaryColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingSettingsActivity.this.dividerColorRow && ThemingSettingsActivity.this.getParentActivity() != null) {
                        ((LayoutInflater) ThemingSettingsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                        new ColorSelectorDialog(ThemingSettingsActivity.this.getParentActivity(), new OnColorChangedListener() {
                            public void colorChanged(int color) {
                                Theme.prefDividerColor = color;
                                ThemingSettingsActivity.this.commitInt("prefDividerColor", color);
                            }
                        }, Theme.prefDividerColor, 0, 0, true).show();
                    }
                }
            });
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (ThemingSettingsActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    if (i == ThemingSettingsActivity.this.headerColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefHeaderColor");
                    } else if (i == ThemingSettingsActivity.this.headerTitleColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefHeaderTitleColor");
                    } else if (i == ThemingSettingsActivity.this.headerStatusColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefHeaderStatusColor");
                    } else if (i == ThemingSettingsActivity.this.headerIconsColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefHeaderIconsColor");
                    } else if (i == ThemingSettingsActivity.this.avatarColorRow) {
                        ThemingSettingsActivity.this.resetInt(Theme.pkey_prefAvatarColor);
                    } else if (i == ThemingSettingsActivity.this.avatarRadiusRow) {
                        ThemingSettingsActivity.this.resetInt("prefAvatarRadius");
                    } else if (i == ThemingSettingsActivity.this.avatarSizeRow) {
                        ThemingSettingsActivity.this.resetInt("prefAvatarSize");
                    } else if (i == ThemingSettingsActivity.this.backgroundColorRow) {
                        ThemingSettingsActivity.this.resetInt(Theme.pkey_prefBGColor);
                    } else if (i == ThemingSettingsActivity.this.shadowColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefShadowColor");
                    } else if (i == ThemingSettingsActivity.this.sectionColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefSectionColor");
                    } else if (i == ThemingSettingsActivity.this.titleColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefTitleColor");
                    } else if (i == ThemingSettingsActivity.this.summaryColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefSummaryColor");
                    } else if (i == ThemingSettingsActivity.this.dividerColorRow) {
                        ThemingSettingsActivity.this.resetInt("prefDividerColor");
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

    private void resetInt(String key) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
        editor.remove(key);
        editor.commit();
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
        if (Theme.usePlusTheme) {
            updateTheme();
        }
        refreshTheme();
    }

    private void commitInt(String key, int value) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
        editor.putInt(key, value);
        editor.commit();
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
        if (Theme.usePlusTheme) {
            updateTheme();
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
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ThemingSettingsActivity.this.fragmentView != null) {
                        ThemingSettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return false;
                }
            });
        }
    }
}
