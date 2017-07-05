package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
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

public class ThemingProfileActivity extends BaseFragment {
    public static final int CENTER = 0;
    private int adminStarColorRow;
    private int avatarRadiusRow;
    private int creatorStarColorRow;
    private int headerAvatarRadiusRow;
    private int headerColorRow;
    private int headerGradientColorRow;
    private int headerGradientRow;
    private int headerIconsColorRow;
    private int headerSection2Row;
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
            return i == ThemingProfileActivity.this.headerColorRow || i == ThemingProfileActivity.this.headerGradientRow || ((Theme.profileActionbarGradientList > 0 && i == ThemingProfileActivity.this.headerGradientColorRow) || i == ThemingProfileActivity.this.headerIconsColorRow || i == ThemingProfileActivity.this.iconsColorRow || i == ThemingProfileActivity.this.nameColorRow || i == ThemingProfileActivity.this.nameSizeRow || i == ThemingProfileActivity.this.statusColorRow || i == ThemingProfileActivity.this.statusSizeRow || i == ThemingProfileActivity.this.rowColorRow || i == ThemingProfileActivity.this.rowGradientRow || ((Theme.profileRowGradientList != 0 && i == ThemingProfileActivity.this.rowGradientColorRow) || ((Theme.profileRowGradientList != 0 && i == ThemingProfileActivity.this.rowGradientListCheckRow) || i == ThemingProfileActivity.this.titleColorRow || i == ThemingProfileActivity.this.summaryColorRow || i == ThemingProfileActivity.this.onlineColorRow || i == ThemingProfileActivity.this.headerAvatarRadiusRow || i == ThemingProfileActivity.this.avatarRadiusRow || i == ThemingProfileActivity.this.creatorStarColorRow || i == ThemingProfileActivity.this.adminStarColorRow)));
        }

        public int getCount() {
            return ThemingProfileActivity.this.rowCount;
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
            int i2 = 0;
            int type = getItemViewType(i);
            String prefix = "";
            if (ThemingProfileActivity.this.showPrefix) {
                prefix = "5.";
                if (i == ThemingProfileActivity.this.headerSection2Row) {
                    prefix = prefix + "1 ";
                } else if (i == ThemingProfileActivity.this.rowsSection2Row) {
                    prefix = prefix + "2 ";
                } else if (i < ThemingProfileActivity.this.rowsSection2Row) {
                    prefix = prefix + "1." + i + " ";
                } else {
                    prefix = prefix + "2." + (i - ThemingProfileActivity.this.rowsSection2Row) + " ";
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
                if (i == ThemingProfileActivity.this.headerSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("Header", R.string.Header));
                } else if (i == ThemingProfileActivity.this.rowsSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("OptionsList", R.string.OptionsList));
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == ThemingProfileActivity.this.nameSizeRow) {
                    textCell.setTag("profileNameSize");
                    textCell.setTextAndValue(prefix + LocaleController.getString("NameSize", R.string.NameSize), String.format("%d", new Object[]{Integer.valueOf(Theme.profileActionbarNameSize)}), true);
                } else if (i == ThemingProfileActivity.this.statusSizeRow) {
                    textCell.setTag("profileStatusSize");
                    textCell.setTextAndValue(prefix + LocaleController.getString("StatusSize", R.string.StatusSize), String.format("%d", new Object[]{Integer.valueOf(Theme.profileActionbarStatusSize)}), true);
                } else if (i == ThemingProfileActivity.this.headerAvatarRadiusRow) {
                    textCell.setTag("profileAvatarRadius");
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(Theme.profileActionbarAvatarRadius)}), true);
                } else if (i == ThemingProfileActivity.this.avatarRadiusRow) {
                    textCell.setTag("profileRowAvatarRadius");
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(Theme.profileRowAvatarRadius)}), true);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextColorCell(this.mContext);
                }
                TextColorCell textCell2 = (TextColorCell) view;
                if (i == ThemingProfileActivity.this.headerColorRow) {
                    textCell2.setTag("profileHeaderColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderColor", R.string.HeaderColor), Theme.profileActionbarColor, false);
                } else if (i == ThemingProfileActivity.this.headerGradientColorRow) {
                    textCell2.setTag("profileHeaderGradientColor");
                    String r4 = prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor);
                    if (Theme.profileActionbarGradientList != 0) {
                        i2 = Theme.profileActionbarGradientColor;
                    }
                    textCell2.setTextAndColor(r4, i2, true);
                } else if (i == ThemingProfileActivity.this.headerIconsColorRow) {
                    textCell2.setTag("profileHeaderIconsColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderIconsColor", R.string.HeaderIconsColor), Theme.profileActionbarIconsColor, true);
                } else if (i == ThemingProfileActivity.this.iconsColorRow) {
                    textCell2.setTag("profileIconsColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("IconsColor", R.string.IconsColor), Theme.profileRowIconsColor, true);
                } else if (i == ThemingProfileActivity.this.creatorStarColorRow) {
                    textCell2.setTag("profileCreatorStarColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("CreatorStarColor", R.string.CreatorStarColor), Theme.profileRowCreatorStarColor, true);
                } else if (i == ThemingProfileActivity.this.adminStarColorRow) {
                    textCell2.setTag("profileAdminStarColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("AdminStarColor", R.string.AdminStarColor), Theme.profileRowAdminStarColor, false);
                } else if (i == ThemingProfileActivity.this.nameColorRow) {
                    textCell2.setTag("profileNameColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("NameColor", R.string.NameColor), Theme.profileActionbarNameColor, true);
                } else if (i == ThemingProfileActivity.this.statusColorRow) {
                    textCell2.setTag("profileStatusColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("StatusColor", R.string.StatusColor), Theme.profileActionbarStatusColor, false);
                } else if (i == ThemingProfileActivity.this.rowColorRow) {
                    textCell2.setTag("profileRowColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("RowColor", R.string.RowColor), Theme.profileRowColor, false);
                } else if (i == ThemingProfileActivity.this.rowGradientColorRow) {
                    textCell2.setTag("profileRowGradientColor");
                    String r4 = prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor);
                    if (Theme.profileRowGradientList != 0) {
                        i2 = Theme.profileRowGradientColor;
                    }
                    textCell2.setTextAndColor(r4, i2, true);
                } else if (i == ThemingProfileActivity.this.titleColorRow) {
                    textCell2.setTag("profileTitleColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("NameColor", R.string.NameColor), Theme.profileRowTitleColor, true);
                } else if (i == ThemingProfileActivity.this.summaryColorRow) {
                    textCell2.setTag("profileSummaryColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("StatusColor", R.string.StatusColor), Theme.profileRowStatusColor, true);
                } else if (i == ThemingProfileActivity.this.onlineColorRow) {
                    textCell2.setTag("profileOnlineColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("OnlineColor", R.string.OnlineColor), Theme.profileRowOnlineColor, true);
                }
            } else if (type == 4) {
                if (view == null) {
                    view = new TextCheckCell(this.mContext);
                }
            } else if (type == 5) {
                if (view == null) {
                    view = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell3 = (TextDetailSettingsCell) view;
                if (i == ThemingProfileActivity.this.headerGradientRow) {
                    textCell3.setTag("profileHeaderGradient");
                    textCell3.setMultilineDetail(false);
                    if (Theme.profileActionbarGradientList == 0) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled), false);
                    } else if (Theme.profileActionbarGradientList == 1) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTopBottom", R.string.RowGradientTopBottom), false);
                    } else if (Theme.profileActionbarGradientList == 2) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientLeftRight", R.string.RowGradientLeftRight), false);
                    } else if (Theme.profileActionbarGradientList == 3) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTLBR", R.string.RowGradientTLBR), false);
                    } else if (Theme.profileActionbarGradientList == 4) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientBLTR", R.string.RowGradientBLTR), false);
                    }
                } else if (i == ThemingProfileActivity.this.rowGradientRow) {
                    textCell3.setTag("profileRowGradient");
                    textCell3.setMultilineDetail(false);
                    if (Theme.profileRowGradientList == 0) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled), false);
                    } else if (Theme.profileRowGradientList == 1) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTopBottom", R.string.RowGradientTopBottom), false);
                    } else if (Theme.profileRowGradientList == 2) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientLeftRight", R.string.RowGradientLeftRight), false);
                    } else if (Theme.profileRowGradientList == 3) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTLBR", R.string.RowGradientTLBR), false);
                    } else if (Theme.profileRowGradientList == 4) {
                        textCell3.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientBLTR", R.string.RowGradientBLTR), false);
                    }
                }
            }
            if (view != null) {
                view.setBackgroundColor(Theme.usePlusTheme ? Theme.prefBGColor : Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ThemingProfileActivity.this.rowsSectionRow) {
                return 0;
            }
            if (i == ThemingProfileActivity.this.headerSection2Row || i == ThemingProfileActivity.this.rowsSection2Row) {
                return 1;
            }
            if (i == ThemingProfileActivity.this.nameSizeRow || i == ThemingProfileActivity.this.statusSizeRow || i == ThemingProfileActivity.this.headerAvatarRadiusRow || i == ThemingProfileActivity.this.avatarRadiusRow) {
                return 2;
            }
            if (i == ThemingProfileActivity.this.headerColorRow || i == ThemingProfileActivity.this.headerGradientColorRow || i == ThemingProfileActivity.this.headerIconsColorRow || i == ThemingProfileActivity.this.iconsColorRow || i == ThemingProfileActivity.this.nameColorRow || i == ThemingProfileActivity.this.statusColorRow || i == ThemingProfileActivity.this.rowColorRow || i == ThemingProfileActivity.this.rowGradientColorRow || i == ThemingProfileActivity.this.titleColorRow || i == ThemingProfileActivity.this.summaryColorRow || i == ThemingProfileActivity.this.onlineColorRow || i == ThemingProfileActivity.this.creatorStarColorRow || i == ThemingProfileActivity.this.adminStarColorRow) {
                return 3;
            }
            if (i == ThemingProfileActivity.this.rowGradientListCheckRow) {
                return 4;
            }
            if (i == ThemingProfileActivity.this.headerGradientRow || i == ThemingProfileActivity.this.rowGradientRow) {
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
        this.headerIconsColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerAvatarRadiusRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.statusSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.statusColorRow = i;
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
        this.titleColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.summaryColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.onlineColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.iconsColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.creatorStarColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.adminStarColorRow = i;
        this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("profileShowPrefix", true);
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
            this.actionBar.setTitle(LocaleController.getString("ProfileScreen", R.string.ProfileScreen));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        ThemingProfileActivity.this.finishFragment();
                    }
                }
            });
            this.actionBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean z;
                    ThemingProfileActivity themingProfileActivity = ThemingProfileActivity.this;
                    if (ThemingProfileActivity.this.showPrefix) {
                        z = false;
                    } else {
                        z = true;
                    }
                    themingProfileActivity.showPrefix = z;
                    ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("profileShowPrefix", ThemingProfileActivity.this.showPrefix).apply();
                    if (ThemingProfileActivity.this.listAdapter != null) {
                        ThemingProfileActivity.this.listAdapter.notifyDataSetChanged();
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
                    final String key = view.getTag() != null ? view.getTag().toString() : "";
                    if (i == ThemingProfileActivity.this.headerColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileActionbarColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileActionbarColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingProfileActivity.this.headerGradientRow) {
                        Builder builder = new Builder(ThemingProfileActivity.this.getParentActivity());
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
                                ThemingProfileActivity.this.commitInt("profileHeaderGradient", which);
                                Theme.profileActionbarGradientList = which;
                                if (ThemingProfileActivity.this.listView != null) {
                                    ThemingProfileActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingProfileActivity.this.showDialog(builder.create());
                    } else if (i == ThemingProfileActivity.this.rowGradientRow) {
                        Builder builder = new Builder(ThemingProfileActivity.this.getParentActivity());
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
                                ThemingProfileActivity.this.commitInt("profileRowGradient", which);
                                Theme.profileRowGradientList = which;
                                if (ThemingProfileActivity.this.listView != null) {
                                    ThemingProfileActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingProfileActivity.this.showDialog(builder.create());
                    } else if (i == ThemingProfileActivity.this.headerGradientColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileActionbarGradientColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileActionbarGradientColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingProfileActivity.this.headerIconsColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileActionbarIconsColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileActionbarIconsColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingProfileActivity.this.iconsColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileRowIconsColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileRowIconsColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingProfileActivity.this.creatorStarColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileRowCreatorStarColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileRowCreatorStarColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingProfileActivity.this.adminStarColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileRowAdminStarColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileRowAdminStarColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingProfileActivity.this.nameColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileActionbarNameColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileActionbarNameColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingProfileActivity.this.statusColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileActionbarStatusColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileActionbarStatusColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingProfileActivity.this.nameSizeRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingProfileActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("NameSize", R.string.NameSize));
                            numberPicker = new NumberPicker(ThemingProfileActivity.this.getParentActivity());
                            numberPicker.setMinValue(12);
                            numberPicker.setMaxValue(30);
                            numberPicker.setValue(Theme.profileActionbarNameSize);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.profileActionbarNameSize) {
                                        Theme.profileActionbarNameSize = numberPicker.getValue();
                                        ThemingProfileActivity.this.commitInt(key, numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingProfileActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingProfileActivity.this.statusSizeRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingProfileActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("StatusSize", R.string.StatusSize));
                            numberPicker = new NumberPicker(ThemingProfileActivity.this.getParentActivity());
                            numberPicker.setMinValue(8);
                            numberPicker.setMaxValue(22);
                            numberPicker.setValue(Theme.profileActionbarStatusSize);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.profileActionbarStatusSize) {
                                        Theme.profileActionbarStatusSize = numberPicker.getValue();
                                        ThemingProfileActivity.this.commitInt(key, numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingProfileActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingProfileActivity.this.rowColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileRowColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileRowColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingProfileActivity.this.rowGradientColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileRowGradientColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileRowGradientColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingProfileActivity.this.headerAvatarRadiusRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingProfileActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            numberPicker = new NumberPicker(ThemingProfileActivity.this.getParentActivity());
                            numberPicker.setMinValue(1);
                            numberPicker.setMaxValue(32);
                            numberPicker.setValue(Theme.profileActionbarAvatarRadius);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.profileActionbarAvatarRadius) {
                                        Theme.profileActionbarAvatarRadius = numberPicker.getValue();
                                        ThemingProfileActivity.this.commitInt("profileAvatarRadius", numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingProfileActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingProfileActivity.this.avatarRadiusRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingProfileActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            numberPicker = new NumberPicker(ThemingProfileActivity.this.getParentActivity());
                            numberPicker.setMinValue(1);
                            numberPicker.setMaxValue(32);
                            numberPicker.setValue(Theme.profileRowAvatarRadius);
                            builder.setView(numberPicker);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (numberPicker.getValue() != Theme.profileRowAvatarRadius) {
                                        Theme.profileRowAvatarRadius = numberPicker.getValue();
                                        ThemingProfileActivity.this.commitInt("profileRowAvatarRadius", numberPicker.getValue());
                                    }
                                }
                            });
                            ThemingProfileActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingProfileActivity.this.titleColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileRowTitleColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileRowTitleColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingProfileActivity.this.summaryColorRow) {
                        if (ThemingProfileActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.profileRowStatusColor = color;
                                    ThemingProfileActivity.this.commitInt(key, color);
                                }
                            }, Theme.profileRowStatusColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingProfileActivity.this.onlineColorRow && ThemingProfileActivity.this.getParentActivity() != null) {
                        ((LayoutInflater) ThemingProfileActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                        new ColorSelectorDialog(ThemingProfileActivity.this.getParentActivity(), new OnColorChangedListener() {
                            public void colorChanged(int color) {
                                Theme.profileRowOnlineColor = color;
                                ThemingProfileActivity.this.commitInt(key, color);
                            }
                        }, Theme.profileRowOnlineColor, 0, 0, false).show();
                    }
                }
            });
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (ThemingProfileActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    if (view.getTag() != null) {
                        ThemingProfileActivity.this.resetPref(view.getTag().toString());
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
        if (this.listView != null) {
            this.listView.invalidateViews();
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
                    if (ThemingProfileActivity.this.fragmentView != null) {
                        ThemingProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return false;
                }
            });
        }
    }
}
