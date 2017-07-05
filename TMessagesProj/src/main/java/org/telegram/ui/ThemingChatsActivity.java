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
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.User;
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

public class ThemingChatsActivity extends BaseFragment {
    public static final int CENTER = 0;
    private int avatarMarginLeftRow;
    private int avatarRadiusRow;
    private int avatarSizeRow;
    private int checksColorRow;
    private int countBGColorRow;
    private int countColorRow;
    private int countSilentBGColorRow;
    private int countSizeRow;
    private int dividerColorRow;
    private int favIndicatorColorRow;
    private int floatingBGColorRow;
    private int floatingPencilColorRow;
    private int groupIconColorRow;
    private int groupNameColorRow;
    private int groupNameSizeRow;
    private int headerColorRow;
    private int headerGradientColorRow;
    private int headerGradientRow;
    private int headerIconsColorRow;
    private int headerSection2Row;
    private int headerTabCounterBGColorRow;
    private int headerTabCounterColorRow;
    private int headerTabCounterSilentBGColorRow;
    private int headerTabIconColorRow;
    private int headerTabUnselectedIconColorRow;
    private int headerTitleColorRow;
    private int headerTitleRow;
    private int hideHeaderShadow;
    private int hideStatusIndicatorCheckRow;
    private int highlightSearchColorRow;
    private ListAdapter listAdapter;
    private ListView listView;
    private int mediaColorRow;
    private int memberColorRow;
    private int messageColorRow;
    private int messageSizeRow;
    private int muteColorRow;
    private int nameColorRow;
    private int nameSizeRow;
    private int pinnedMsgBGColorRow;
    private int rowColorRow;
    private int rowCount;
    private int rowGradientColorRow;
    private int rowGradientListCheckRow;
    private int rowGradientRow;
    private int rowsSection2Row;
    private int rowsSectionRow;
    private boolean showPrefix;
    private int tabsBGColorRow;
    private int tabsCounterSizeRow;
    private int tabsTextModeRow;
    private int tabsTextSizeRow;
    private int tabsToBottomRow;
    private int timeColorRow;
    private int timeSizeRow;
    private int typingColorRow;
    private int unknownNameColorRow;
    int size;
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
            return i == ThemingChatsActivity.this.headerColorRow || i == ThemingChatsActivity.this.headerGradientRow || ((Theme.chatsHeaderGradient != 0 && i == ThemingChatsActivity.this.headerGradientColorRow) || i == ThemingChatsActivity.this.headerTitleColorRow || i == ThemingChatsActivity.this.headerIconsColorRow || i == ThemingChatsActivity.this.headerTabIconColorRow || i == ThemingChatsActivity.this.headerTabUnselectedIconColorRow || i == ThemingChatsActivity.this.headerTitleRow || i == ThemingChatsActivity.this.headerTabCounterColorRow || i == ThemingChatsActivity.this.headerTabCounterBGColorRow || i == ThemingChatsActivity.this.headerTabCounterSilentBGColorRow || i == ThemingChatsActivity.this.tabsCounterSizeRow || i == ThemingChatsActivity.this.rowColorRow || i == ThemingChatsActivity.this.rowGradientRow || ((Theme.chatsRowGradient != 0 && i == ThemingChatsActivity.this.rowGradientColorRow) || ((Theme.chatsRowGradient != 0 && i == ThemingChatsActivity.this.rowGradientListCheckRow) || i == ThemingChatsActivity.this.dividerColorRow || i == ThemingChatsActivity.this.avatarRadiusRow || i == ThemingChatsActivity.this.avatarSizeRow || i == ThemingChatsActivity.this.avatarMarginLeftRow || i == ThemingChatsActivity.this.hideHeaderShadow || i == ThemingChatsActivity.this.hideStatusIndicatorCheckRow || i == ThemingChatsActivity.this.nameColorRow || i == ThemingChatsActivity.this.groupNameColorRow || i == ThemingChatsActivity.this.unknownNameColorRow || i == ThemingChatsActivity.this.groupIconColorRow || i == ThemingChatsActivity.this.muteColorRow || i == ThemingChatsActivity.this.checksColorRow || i == ThemingChatsActivity.this.nameSizeRow || i == ThemingChatsActivity.this.groupNameSizeRow || i == ThemingChatsActivity.this.messageColorRow || i == ThemingChatsActivity.this.highlightSearchColorRow || i == ThemingChatsActivity.this.memberColorRow || i == ThemingChatsActivity.this.mediaColorRow || i == ThemingChatsActivity.this.typingColorRow || i == ThemingChatsActivity.this.messageSizeRow || i == ThemingChatsActivity.this.timeColorRow || i == ThemingChatsActivity.this.timeSizeRow || i == ThemingChatsActivity.this.countColorRow || i == ThemingChatsActivity.this.countSizeRow || i == ThemingChatsActivity.this.countBGColorRow || i == ThemingChatsActivity.this.countSilentBGColorRow || i == ThemingChatsActivity.this.floatingPencilColorRow || i == ThemingChatsActivity.this.floatingBGColorRow || i == ThemingChatsActivity.this.tabsBGColorRow || i == ThemingChatsActivity.this.favIndicatorColorRow || i == ThemingChatsActivity.this.tabsToBottomRow || i == ThemingChatsActivity.this.tabsTextModeRow || i == ThemingChatsActivity.this.tabsTextSizeRow || i == ThemingChatsActivity.this.pinnedMsgBGColorRow)));
        }

        public int getCount() {
            return ThemingChatsActivity.this.rowCount;
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
            if (ThemingChatsActivity.this.showPrefix) {
                prefix = "1.";
                if (i == ThemingChatsActivity.this.headerSection2Row) {
                    prefix = prefix + "1 ";
                } else if (i == ThemingChatsActivity.this.rowsSection2Row) {
                    prefix = prefix + "2 ";
                } else if (i < ThemingChatsActivity.this.rowsSection2Row) {
                    prefix = prefix + "1." + i + " ";
                } else {
                    prefix = prefix + "2." + (i - ThemingChatsActivity.this.rowsSection2Row) + " ";
                }
            }
            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
            View shadowSectionCell = new ShadowSectionCell(this.mContext);
            if (type == 0) {
            } else if (type == 1) {
                if (view == null) {
                    shadowSectionCell = new HeaderCell(this.mContext);
                    shadowSectionCell.setBackgroundColor(-1);
                }
                if (i == ThemingChatsActivity.this.headerSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("Header", R.string.Header));
                } else if (i == ThemingChatsActivity.this.rowsSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("ChatsList", R.string.ChatsList));
                }
            } else if (type == 2) {
                if (view == null) {
                    shadowSectionCell = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == ThemingChatsActivity.this.avatarRadiusRow) {
                    textCell.setTag("chatsAvatarRadius");
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(Theme.chatsAvatarRadius)}), true);
                } else if (i == ThemingChatsActivity.this.tabsCounterSizeRow) {
                    textCell.setTag("chatsHeaderTabCounterSize");
                    textCell.setTextAndValue(prefix + LocaleController.getString("CountSize", R.string.CountSize), String.format("%d", new Object[]{Integer.valueOf(Theme.chatsTabCounterSize)}), true);
                } else if (i == ThemingChatsActivity.this.avatarSizeRow) {
                    textCell.setTag("chatsAvatarSize");
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarSize", R.string.AvatarSize), String.format("%d", new Object[]{Integer.valueOf(Theme.chatsAvatarSize)}), true);
                } else if (i == ThemingChatsActivity.this.avatarMarginLeftRow) {
                    textCell.setTag("chatsAvatarMarginLeft");
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarMarginLeft", R.string.AvatarMarginLeft), String.format("%d", new Object[]{Integer.valueOf(Theme.chatsAvatarMarginLeft)}), true);
                } else if (i == ThemingChatsActivity.this.nameSizeRow) {
                    textCell.setTag("chatsNameSize");
                    textCell.setTextAndValue(prefix + LocaleController.getString("NameSize", R.string.NameSize), String.format("%d", new Object[]{Integer.valueOf(Theme.chatsNameSize)}), true);
                } else if (i == ThemingChatsActivity.this.groupNameSizeRow) {
                    textCell.setTag("chatsGroupNameSize");
                    size = themePrefs.getInt("chatsGroupNameSize", Theme.chatsNameSize);
                    textCell.setTextAndValue(prefix + LocaleController.getString("GroupNameSize", R.string.GroupNameSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatsActivity.this.messageSizeRow) {
                    textCell.setTag("chatsMessageSize");
                    size = themePrefs.getInt("chatsMessageSize", AndroidUtilities.isTablet() ? 18 : 16);
                    textCell.setTextAndValue(prefix + LocaleController.getString("MessageSize", R.string.MessageSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatsActivity.this.timeSizeRow) {
                    textCell.setTag("chatsTimeSize");
                    size = themePrefs.getInt("chatsTimeSize", AndroidUtilities.isTablet() ? 15 : 13);
                    textCell.setTextAndValue(prefix + LocaleController.getString("TimeDateSize", R.string.TimeDateSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatsActivity.this.countSizeRow) {
                    textCell.setTag("chatsCountSize");
                    textCell.setTextAndValue(prefix + LocaleController.getString("CountSize", R.string.CountSize), String.format("%d", new Object[]{Integer.valueOf(Theme.chatsCountSize)}), true);
                } else if (i == ThemingChatsActivity.this.tabsTextSizeRow) {
                    textCell.setTag("chatsTabsTextSize");
                    textCell.setTextAndValue(prefix + LocaleController.getString("TabsTextSize", R.string.TabsTextSize), String.format("%d", new Object[]{Integer.valueOf(Theme.chatsTabsTextSize)}), false);
                }
            } else if (type == 3) {
                if (view == null) {
                    shadowSectionCell = new TextColorCell(this.mContext);
                }
                shadowSectionCell = new TextColorCell(this.mContext);
                TextColorCell textCell2 = (TextColorCell) view;
                if (i == ThemingChatsActivity.this.headerColorRow) {
                    textCell2.setTag("chatsHeaderColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderColor", R.string.HeaderColor), Theme.chatsHeaderColor, false);
                } else if (i == ThemingChatsActivity.this.headerGradientColorRow) {
                    textCell2.setTag("chatsHeaderGradientColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor), Theme.chatsHeaderGradientColor, true);
                } else if (i == ThemingChatsActivity.this.headerTitleColorRow) {
                    textCell2.setTag(Theme.pkey_chatsHeaderTitleColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderTitleColor", R.string.HeaderTitleColor), Theme.chatsHeaderTitleColor, true);
                } else if (i == ThemingChatsActivity.this.headerIconsColorRow) {
                    textCell2.setTag("chatsHeaderIconsColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderIconsColor", R.string.HeaderIconsColor), Theme.chatsHeaderIconsColor, true);
                } else if (i == ThemingChatsActivity.this.headerTabIconColorRow) {
                    Theme.chatsHeaderTabIconColor = themePrefs.getInt("chatsHeaderTabIconColor", Theme.chatsHeaderIconsColor);
                    textCell2.setTag("chatsHeaderTabIconColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderTabIconColor", R.string.HeaderTabIconColor), Theme.chatsHeaderTabIconColor, true);
                } else if (i == ThemingChatsActivity.this.headerTabUnselectedIconColorRow) {
                    Theme.chatsHeaderTabUnselectedIconColor = themePrefs.getInt("chatsHeaderTabUnselectedIconColor", AndroidUtilities.getIntAlphaColor("chatsHeaderTabIconColor", Theme.chatsHeaderIconsColor, 0.35f));
                    textCell2.setTag("chatsHeaderTabUnselectedIconColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderTabUnselectedIconColor", R.string.HeaderTabUnselectedIconColor), Theme.chatsHeaderTabUnselectedIconColor, true);
                } else if (i == ThemingChatsActivity.this.headerTabCounterColorRow) {
                    textCell2.setTag("chatsHeaderTabCounterColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderTabCounterColor", R.string.HeaderTabCounterColor), Theme.chatsTabCounterColor, true);
                } else if (i == ThemingChatsActivity.this.headerTabCounterBGColorRow) {
                    textCell2.setTag("chatsHeaderTabCounterBGColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HeaderTabCounterBGColor", R.string.HeaderTabCounterBGColor), Theme.chatsTabCounterBGColor, true);
                } else if (i == ThemingChatsActivity.this.headerTabCounterSilentBGColorRow) {
                    textCell2.setTag("chatsHeaderTabCounterSilentBGColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("CountSilentBGColor", R.string.CountSilentBGColor), Theme.chatsTabCounterSilentBGColor, false);
                } else if (i == ThemingChatsActivity.this.rowColorRow) {
                    textCell2.setTag("chatsRowColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("RowColor", R.string.RowColor), Theme.chatsRowColor, false);
                } else if (i == ThemingChatsActivity.this.tabsBGColorRow) {
                    textCell2.setTag("chatsTabsBGColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("TabsBGColor", R.string.TabsBGColor), themePrefs.getInt("chatsTabsBGColor", Theme.defColor), true);
                } else if (i == ThemingChatsActivity.this.favIndicatorColorRow) {
                    textCell2.setTag("chatsFavIndicatorColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("FavIndicatorColor", R.string.FavIndicatorColor), themePrefs.getInt("chatsFavIndicatorColor", Theme.FAV_INDICATOR_COLOR_DEF), false);
                } else if (i == ThemingChatsActivity.this.rowGradientColorRow) {
                    textCell2.setTag("chatsRowGradientColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor), Theme.chatsRowGradient == 0 ? 0 : Theme.chatsRowGradientColor, true);
                } else if (i == ThemingChatsActivity.this.pinnedMsgBGColorRow) {
                    textCell2.setTag("chatsPinnedMsgBGColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("PinnedMsgBgColor", R.string.PinnedMsgBgColor), themePrefs.getInt("chatsPinnedMsgBGColor", Theme.chatsRowColor), true);
                } else if (i == ThemingChatsActivity.this.dividerColorRow) {
                    textCell2.setTag("chatsDividerColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("DividerColor", R.string.DividerColor), Theme.chatsDividerColor, true);
                } else if (i == ThemingChatsActivity.this.nameColorRow) {
                    textCell2.setTag(Theme.pkey_chatsNameColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("NameColor", R.string.NameColor), Theme.chatsNameColor, true);
                } else if (i == ThemingChatsActivity.this.groupNameColorRow) {
                    textCell2.setTag(Theme.pkey_chatsGroupNameColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("GroupNameColor", R.string.GroupNameColor), themePrefs.getInt(Theme.pkey_chatsGroupNameColor, Theme.chatsNameColor), true);
                } else if (i == ThemingChatsActivity.this.unknownNameColorRow) {
                    textCell2.setTag(Theme.pkey_chatsUnknownNameColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("UnknownNameColor", R.string.UnknownNameColor), themePrefs.getInt(Theme.pkey_chatsUnknownNameColor, Theme.chatsNameColor), true);
                } else if (i == ThemingChatsActivity.this.groupIconColorRow) {
                    textCell2.setTag("chatsGroupIconColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("GroupIconColor", R.string.GroupIconColor), themePrefs.getInt("chatsGroupIconColor", themePrefs.getInt(Theme.pkey_chatsGroupNameColor, -16777216)), true);
                } else if (i == ThemingChatsActivity.this.muteColorRow) {
                    textCell2.setTag("chatsMuteColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("MuteColor", R.string.MuteColor), themePrefs.getInt("chatsMuteColor", -5723992), true);
                } else if (i == ThemingChatsActivity.this.checksColorRow) {
                    textCell2.setTag(Theme.pkey_chatsChecksColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("ChecksColor", R.string.ChecksColor), Theme.chatsChecksColor, true);
                } else if (i == ThemingChatsActivity.this.messageColorRow) {
                    textCell2.setTag(Theme.pkey_chatsMessageColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("MessageColor", R.string.MessageColor), Theme.chatsMessageColor, true);
                } else if (i == ThemingChatsActivity.this.memberColorRow) {
                    textCell2.setTag(Theme.pkey_chatsMemberColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("MemberColor", R.string.MemberColor), Theme.chatsMemberColor, true);
                } else if (i == ThemingChatsActivity.this.mediaColorRow) {
                    textCell2.setTag(Theme.pkey_chatsMediaColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("MediaColor", R.string.MediaColor), themePrefs.getInt(Theme.pkey_chatsMediaColor, Theme.chatsMemberColor), true);
                } else if (i == ThemingChatsActivity.this.typingColorRow) {
                    textCell2.setTag("chatsTypingColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("TypingColor", R.string.TypingColor), themePrefs.getInt(textCell2.getTag().toString(), Theme.defColor), true);
                } else if (i == ThemingChatsActivity.this.timeColorRow) {
                    textCell2.setTag("chatsTimeColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("TimeDateColor", R.string.TimeDateColor), themePrefs.getInt("chatsTimeColor", -6710887), true);
                } else if (i == ThemingChatsActivity.this.countColorRow) {
                    textCell2.setTag("chatsCountColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("CountColor", R.string.CountColor), themePrefs.getInt("chatsCountColor", -1), true);
                } else if (i == ThemingChatsActivity.this.countBGColorRow) {
                    textCell2.setTag("chatsCountBGColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("CountBGColor", R.string.CountBGColor), themePrefs.getInt("chatsCountBGColor", Theme.defColor), true);
                } else if (i == ThemingChatsActivity.this.countSilentBGColorRow) {
                    textCell2.setTag("chatsCountSilentBGColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("CountSilentBGColor", R.string.CountSilentBGColor), themePrefs.getInt("chatsCountSilentBGColor", themePrefs.getInt("chatsCountBGColor", -4605511)), true);
                } else if (i == ThemingChatsActivity.this.floatingPencilColorRow) {
                    textCell2.setTag(Theme.pkey_chatsFloatingPencilColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("FloatingPencilColor", R.string.FloatingPencilColor), Theme.chatsFloatingPencilColor, true);
                } else if (i == ThemingChatsActivity.this.floatingBGColorRow) {
                    textCell2.setTag(Theme.pkey_chatsFloatingBGColor);
                    textCell2.setTextAndColor(prefix + LocaleController.getString("FloatingBGColor", R.string.FloatingBGColor), Theme.chatsFloatingBGColor, true);
                } else if (i == ThemingChatsActivity.this.highlightSearchColorRow) {
                    textCell2.setTag("chatsHighlightSearchColor");
                    textCell2.setTextAndColor(prefix + LocaleController.getString("HighlightSearchColor", R.string.HighlightSearchColor), themePrefs.getInt("chatsHighlightSearchColor", Theme.lightColor), false);
                }
            } else if (type == 4) {
                if (view == null) {
                    shadowSectionCell = new TextCheckCell(this.mContext);
                }
                shadowSectionCell = new TextCheckCell(this.mContext);
                TextCheckCell textCell3 = (TextCheckCell) view;
                if (i == ThemingChatsActivity.this.rowGradientListCheckRow) {
                    textCell3.setTag("chatsRowGradientListCheck");
                    String str = prefix + LocaleController.getString("RowGradientList", R.string.RowGradientList);
                    boolean z = Theme.chatsRowGradient != 0 && themePrefs.getBoolean("chatsRowGradientListCheck", false);
                    textCell3.setTextAndCheck(str, z, true);
                } else if (i == ThemingChatsActivity.this.hideHeaderShadow) {
                    textCell3.setTag("chatsHideHeaderShadow");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("HideHeaderShadow", R.string.HideHeaderShadow), Theme.chatsHideHeaderShadow, true);
                } else if (i == ThemingChatsActivity.this.hideStatusIndicatorCheckRow) {
                    textCell3.setTag("chatsHideStatusIndicator");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("HideStatusIndicator", R.string.HideStatusIndicator), Theme.chatsHideStatusIndicator, true);
                } else if (i == ThemingChatsActivity.this.tabsToBottomRow) {
                    textCell3.setTag("chatsTabsToBottom");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("TabsToBottom", R.string.TabsToBottom), Theme.chatsTabsToBottom, true);
                } else if (i == ThemingChatsActivity.this.tabsTextModeRow) {
                    textCell3.setTag("chatsTabTitlesMode");
                    textCell3.setTextAndCheck(prefix + LocaleController.getString("ShowTabTitle", R.string.ShowTabTitle), Theme.chatsTabTitlesMode, true);
                }
            } else if (type == 5) {
                if (view == null) {
                    shadowSectionCell = new TextDetailSettingsCell(this.mContext);
                }
                shadowSectionCell = new TextDetailSettingsCell(this.mContext);
                TextDetailSettingsCell textCell4 = (TextDetailSettingsCell) view;
                int value;
                if (i == ThemingChatsActivity.this.headerTitleRow) {
                    String text;
                    textCell4.setTag("chatsHeaderTitle");
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt("chatsHeaderTitle", 0);
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                    if (user == null || user.username == null || user.username.length() == 0) {
                        text = "-";
                    } else {
                        text = "@" + user.username;
                    }
                    if (value == 0) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("HeaderTitle", R.string.HeaderTitle), LocaleController.getString("AppName", R.string.AppName), true);
                    } else if (value == 1) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("HeaderTitle", R.string.HeaderTitle), LocaleController.getString("ShortAppName", R.string.ShortAppName), true);
                    } else if (value == 2) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("HeaderTitle", R.string.HeaderTitle), ContactsController.formatName(user.first_name, user.last_name), true);
                    } else if (value == 3) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("HeaderTitle", R.string.HeaderTitle), text, true);
                    } else if (value == 4) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("HeaderTitle", R.string.HeaderTitle), "", true);
                    }
                } else if (i == ThemingChatsActivity.this.headerGradientRow) {
                    textCell4.setTag("chatsHeaderGradient");
                    textCell4.setMultilineDetail(false);
                    value = Theme.chatsHeaderGradient;
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
                } else if (i == ThemingChatsActivity.this.rowGradientRow) {
                    textCell4.setTag("chatsRowGradient");
                    textCell4.setMultilineDetail(false);
                    if (Theme.chatsRowGradient == 0) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled), false);
                    } else if (Theme.chatsRowGradient == 1) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTopBottom", R.string.RowGradientTopBottom), false);
                    } else if (Theme.chatsRowGradient == 2) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientLeftRight", R.string.RowGradientLeftRight), false);
                    } else if (Theme.chatsRowGradient == 3) {
                        textCell4.setTextAndValue(prefix + LocaleController.getString("RowGradient", R.string.RowGradient), LocaleController.getString("RowGradientTLBR", R.string.RowGradientTLBR), false);
                    } else if (Theme.chatsRowGradient == 4) {
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
            if (i == ThemingChatsActivity.this.rowsSectionRow) {
                return 0;
            }
            if (i == ThemingChatsActivity.this.headerSection2Row || i == ThemingChatsActivity.this.rowsSection2Row) {
                return 1;
            }
            if (i == ThemingChatsActivity.this.avatarRadiusRow || i == ThemingChatsActivity.this.avatarSizeRow || i == ThemingChatsActivity.this.avatarMarginLeftRow || i == ThemingChatsActivity.this.nameSizeRow || i == ThemingChatsActivity.this.groupNameSizeRow || i == ThemingChatsActivity.this.messageSizeRow || i == ThemingChatsActivity.this.timeSizeRow || i == ThemingChatsActivity.this.countSizeRow || i == ThemingChatsActivity.this.tabsCounterSizeRow || i == ThemingChatsActivity.this.tabsTextSizeRow) {
                return 2;
            }
            if (i == ThemingChatsActivity.this.headerColorRow || i == ThemingChatsActivity.this.headerGradientColorRow || i == ThemingChatsActivity.this.headerTitleColorRow || i == ThemingChatsActivity.this.headerIconsColorRow || i == ThemingChatsActivity.this.headerTabIconColorRow || i == ThemingChatsActivity.this.headerTabUnselectedIconColorRow || i == ThemingChatsActivity.this.headerTabCounterColorRow || i == ThemingChatsActivity.this.headerTabCounterBGColorRow || i == ThemingChatsActivity.this.headerTabCounterSilentBGColorRow || i == ThemingChatsActivity.this.rowColorRow || i == ThemingChatsActivity.this.rowGradientColorRow || i == ThemingChatsActivity.this.dividerColorRow || i == ThemingChatsActivity.this.nameColorRow || i == ThemingChatsActivity.this.groupNameColorRow || i == ThemingChatsActivity.this.unknownNameColorRow || i == ThemingChatsActivity.this.groupIconColorRow || i == ThemingChatsActivity.this.muteColorRow || i == ThemingChatsActivity.this.checksColorRow || i == ThemingChatsActivity.this.messageColorRow || i == ThemingChatsActivity.this.highlightSearchColorRow || i == ThemingChatsActivity.this.memberColorRow || i == ThemingChatsActivity.this.mediaColorRow || i == ThemingChatsActivity.this.typingColorRow || i == ThemingChatsActivity.this.timeColorRow || i == ThemingChatsActivity.this.countColorRow || i == ThemingChatsActivity.this.countBGColorRow || i == ThemingChatsActivity.this.countSilentBGColorRow || i == ThemingChatsActivity.this.floatingPencilColorRow || i == ThemingChatsActivity.this.floatingBGColorRow || i == ThemingChatsActivity.this.tabsBGColorRow || i == ThemingChatsActivity.this.favIndicatorColorRow || i == ThemingChatsActivity.this.pinnedMsgBGColorRow) {
                return 3;
            }
            if (i == ThemingChatsActivity.this.rowGradientListCheckRow || i == ThemingChatsActivity.this.hideHeaderShadow || i == ThemingChatsActivity.this.hideStatusIndicatorCheckRow || i == ThemingChatsActivity.this.tabsToBottomRow || i == ThemingChatsActivity.this.tabsTextModeRow) {
                return 4;
            }
            if (i == ThemingChatsActivity.this.headerTitleRow || i == ThemingChatsActivity.this.headerGradientRow || i == ThemingChatsActivity.this.rowGradientRow) {
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
        this.headerTitleRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerIconsColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.hideHeaderShadow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.tabsBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerTabIconColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerTabUnselectedIconColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerTabCounterColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.tabsCounterSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerTabCounterBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.headerTabCounterSilentBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.tabsToBottomRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.tabsTextModeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.tabsTextSizeRow = i;
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
        this.pinnedMsgBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dividerColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarRadiusRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarMarginLeftRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.hideStatusIndicatorCheckRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.favIndicatorColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.unknownNameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nameSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupNameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupNameSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupIconColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.muteColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.checksColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.memberColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mediaColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.typingColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.timeColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.timeSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.countColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.countSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.countBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.countSilentBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.floatingPencilColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.floatingBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.highlightSearchColorRow = i;
        this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("chatsShowPrefix", true);
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
            this.actionBar.setTitle(LocaleController.getString("MainScreen", R.string.MainScreen));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        ThemingChatsActivity.this.finishFragment();
                    }
                }
            });
            this.actionBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean z;
                    ThemingChatsActivity themingChatsActivity = ThemingChatsActivity.this;
                    if (ThemingChatsActivity.this.showPrefix) {
                        z = false;
                    } else {
                        z = true;
                    }
                    themingChatsActivity.showPrefix = z;
                    ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("chatsShowPrefix", ThemingChatsActivity.this.showPrefix).apply();
                    if (ThemingChatsActivity.this.listAdapter != null) {
                        ThemingChatsActivity.this.listAdapter.notifyDataSetChanged();
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
                    final String key = view.getTag() != null ? view.getTag().toString() : "";
                    if (i == ThemingChatsActivity.this.headerColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsHeaderColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsHeaderColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerGradientColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsHeaderGradientColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsHeaderGradientColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerTitleColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsHeaderTitleColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, themePrefs.getInt(key, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerIconsColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsHeaderIconsColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, themePrefs.getInt(key, Theme.chatsHeaderIconsColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerTabIconColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsHeaderTabIconColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    Theme.chatsHeaderTabUnselectedIconColor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).getInt("chatsHeaderTabUnselectedIconColor", AndroidUtilities.getIntAlphaColor("chatsHeaderTabIconColor", Theme.chatsHeaderIconsColor, 0.35f));
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsHeaderTabIconColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerTabUnselectedIconColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsHeaderTabUnselectedIconColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsHeaderTabUnselectedIconColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerTabCounterColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsTabCounterColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsTabCounterColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerTabCounterBGColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsTabCounterBGColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsTabCounterBGColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerTabCounterSilentBGColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsTabCounterSilentBGColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsTabCounterSilentBGColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.rowColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsRowColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(2));
                                }
                            }, Theme.chatsRowColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.tabsBGColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsTabsBGColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsTabsBGColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.favIndicatorColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsFavIndicatorColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatsFavIndicatorColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.rowGradientColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsRowGradientColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(2));
                                }
                            }, Theme.chatsRowGradientColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.rowGradientListCheckRow) {
                        boolean b = themePrefs.getBoolean(key, false);
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, !b);
                        themePrefs.edit().commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(!b);
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(2));
                    } else if (i == ThemingChatsActivity.this.pinnedMsgBGColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsPinnedMsgBGColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, Theme.chatsPinnedMsgBGColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.dividerColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsDividerColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, -2302756), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.headerTitleRow) {
                        Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("HeaderTitle", R.string.HeaderTitle));
                        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                        ArrayList array = new ArrayList();
                        array.add(LocaleController.getString("AppName", R.string.AppName));
                        array.add(LocaleController.getString("ShortAppName", R.string.ShortAppName));
                        String usr = "";
                        if (!(user == null || (user.first_name == null && user.last_name == null))) {
                            array.add(ContactsController.formatName(user.first_name, user.last_name));
                        }
                        if (!(user == null || user.username == null || user.username.length() == 0)) {
                            array.add("@" + user.username);
                        }
                        array.add("");
                        String[] simpleArray = new String[array.size()];
                        array.toArray(new String[array.size()]);
                        builder.setItems((CharSequence[]) array.toArray(simpleArray), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt("chatsHeaderTitle", which).commit();
                                if (ThemingChatsActivity.this.listView != null) {
                                    ThemingChatsActivity.this.listView.invalidateViews();
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(11));
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatsActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatsActivity.this.headerGradientRow) {
                        Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt("chatsHeaderGradient", which).commit();
                                Theme.chatsHeaderGradient = which;
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                if (ThemingChatsActivity.this.listView != null) {
                                    ThemingChatsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatsActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatsActivity.this.rowGradientRow) {
                        Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
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
                                SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
                                Theme.chatsRowGradient = which;
                                themePrefs.edit().putInt("chatsRowGradient", Theme.chatsRowGradient).commit();
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(2));
                                if (ThemingChatsActivity.this.listView != null) {
                                    ThemingChatsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatsActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatsActivity.this.nameColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsNameColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatsNameColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.groupNameColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, Theme.chatsNameColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.unknownNameColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, Theme.chatsNameColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.groupIconColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, themePrefs.getInt(Theme.pkey_chatsGroupNameColor, -16777216)), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.muteColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, -5723992), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.checksColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsChecksColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, Theme.defColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.messageColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsMessageColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, -8355712), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.highlightSearchColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, Theme.lightColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.memberColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsMemberColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, Theme.darkColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.mediaColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsMediaColor = color;
                                    ThemingChatsActivity.this.commitInt(Theme.pkey_chatsMediaColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatsMediaColor, themePrefs.getInt(Theme.pkey_chatsMemberColor, Theme.darkColor)), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.typingColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, Theme.defColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.timeColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, -6710887), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.countColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.countBGColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.dialogs_countPaint.setColor(color);
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, Theme.defColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.countSilentBGColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.dialogs_countGrayPaint.setColor(color);
                                    ThemingChatsActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, themePrefs.getInt("chatsCountBGColor", -4605511)), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.avatarRadiusRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            r0.setMinValue(1);
                            r0.setMaxValue(32);
                            r0.setValue(Theme.chatsAvatarRadius);
                            builder.setView(r0);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatsAvatarRadius) {
                                        Theme.chatsAvatarRadius = r0.getValue();
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.tabsTextSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("TabsTextSize", R.string.TabsTextSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            r0.setMinValue(8);
                            r0.setMaxValue(18);
                            r0.setValue(Theme.chatsTabsTextSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatsTabsTextSize) {
                                        int value = r0.getValue();
                                        Theme.plusTabsTextSize = value;
                                        Theme.chatsTabsTextSize = value;
                                        ThemingChatsActivity.this.commitInt(key, Theme.chatsTabsTextSize);
                                        Editor editorPlus = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                        editorPlus.putInt("tabsTextSize", Theme.chatsTabsTextSize);
                                        editorPlus.apply();
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(15));
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.tabsCounterSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("CountSize", R.string.CountSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            r0.setMinValue(8);
                            r0.setMaxValue(14);
                            r0.setValue(Theme.chatsTabCounterSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatsTabCounterSize) {
                                        Theme.chatsTabCounterSize = r0.getValue();
                                        ThemingChatsActivity.this.commitInt(key, Theme.chatsTabCounterSize);
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(15));
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.avatarSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarSize", R.string.AvatarSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            r0.setMinValue(0);
                            r0.setMaxValue(72);
                            r0.setValue(Theme.chatsAvatarSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatsAvatarSize) {
                                        Theme.chatsAvatarSize = r0.getValue();
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.avatarMarginLeftRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarMarginLeft", R.string.AvatarMarginLeft));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            r0.setMinValue(0);
                            r0.setMaxValue(18);
                            r0.setValue(Theme.chatsAvatarMarginLeft);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatsAvatarMarginLeft) {
                                        Theme.chatsAvatarMarginLeft = r0.getValue();
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.nameSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("NameSize", R.string.NameSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            r0.setMinValue(12);
                            r0.setMaxValue(30);
                            r0.setValue(Theme.chatsNameSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatsNameSize) {
                                        Theme.chatsNameSize = r0.getValue();
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            }).create();
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.groupNameSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("GroupNameSize", R.string.GroupNameSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt(key, Theme.chatsNameSize);
                            r0.setMinValue(12);
                            r0.setMaxValue(30);
                            r0.setValue(currentValue);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            }).create();
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.messageSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("MessageSize", R.string.MessageSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt(key, 16);
                            r0.setMinValue(12);
                            r0.setMaxValue(30);
                            r0.setValue(currentValue);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.timeSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("TimeDateSize", R.string.TimeDateSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt(key, 13);
                            r0.setMinValue(5);
                            r0.setMaxValue(25);
                            r0.setValue(currentValue);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.countSizeRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("CountSize", R.string.CountSize));
                            r0 = new NumberPicker(ThemingChatsActivity.this.getParentActivity());
                            r0.setMinValue(8);
                            r0.setMaxValue(20);
                            r0.setValue(Theme.chatsCountSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatsCountSize) {
                                        Theme.chatsCountSize = r0.getValue();
                                        ThemingChatsActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatsActivity.this.floatingPencilColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsFloatingPencilColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, themePrefs.getInt(key, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.floatingBGColorRow) {
                        if (ThemingChatsActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatsActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatsActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatsFloatingBGColor = color;
                                    ThemingChatsActivity.this.commitInt(key, color);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                                }
                            }, themePrefs.getInt(key, Theme.defColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatsActivity.this.hideStatusIndicatorCheckRow) {
                        Theme.chatsHideStatusIndicator = !Theme.chatsHideStatusIndicator;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatsHideStatusIndicator);
                        themePrefs.edit().commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatsHideStatusIndicator);
                        }
                    } else if (i == ThemingChatsActivity.this.tabsToBottomRow) {
                        boolean r3 = !Theme.chatsTabsToBottom;
                        Theme.plusTabsToBottom = r3;
                        Theme.chatsTabsToBottom = r3;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatsTabsToBottom);
                        themePrefs.edit().apply();
                        SharedPreferences.Editor editorPlus = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                        editorPlus.putBoolean("tabsToBottom", Theme.chatsTabsToBottom);
                        editorPlus.apply();
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(14));
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatsTabsToBottom);
                        }
                    } else if (i == ThemingChatsActivity.this.tabsTextModeRow) {
                        boolean r3 = !Theme.chatsTabTitlesMode;
                        Theme.plusTabTitlesMode = r3;
                        Theme.chatsTabTitlesMode = r3;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatsTabTitlesMode);
                        themePrefs.edit().apply();
                        SharedPreferences.Editor editorPlus = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                        editorPlus.putBoolean("tabTitlesMode", Theme.chatsTabTitlesMode);
                        editorPlus.apply();
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(15));
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatsTabTitlesMode);
                        }
                    } else if (i == ThemingChatsActivity.this.hideHeaderShadow) {
                        Theme.chatsHideHeaderShadow = !Theme.chatsHideHeaderShadow;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatsHideHeaderShadow);
                        themePrefs.edit().commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatsHideHeaderShadow);
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateDialogsTheme, Integer.valueOf(1));
                    }
                }
            });
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (ThemingChatsActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    ThemingChatsActivity.this.resetPref(view.getTag().toString());
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
        Theme.updateChatsColors();
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
                    if (ThemingChatsActivity.this.fragmentView != null) {
                        ThemingChatsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return false;
                }
            });
        }
    }
}
