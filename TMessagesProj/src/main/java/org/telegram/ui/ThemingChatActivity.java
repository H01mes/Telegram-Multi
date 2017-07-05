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
import android.os.Bundle;
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
import org.telegram.messenger.MessagesController;
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

public class ThemingChatActivity extends BaseFragment {
    public static final int CENTER = 0;
    private int attachBGColorRow;
    private int attachBGGradientColorRow;
    private int attachBGGradientRow;
    private int attachTextColorRow;
    private int avatarAlignTopRow;
    private int avatarMarginLeftRow;
    private int avatarRadiusRow;
    private int avatarSizeRow;
    private int bubblesRow;
    private int checksColorRow;
    private int checksRow;
    private int commandColorCheckRow;
    private int commandColorRow;
    private int contactNameColorRow;
    private int dateBubbleColorRow;
    private int dateColorRow;
    private int dateSizeRow;
    private int editTextBGColorRow;
    private int editTextBGGradientColorRow;
    private int editTextBGGradientRow;
    private int editTextColorRow;
    private int editTextIconsColorRow;
    private int editTextSizeRow;
    private int emojiViewBGColorRow;
    private int emojiViewBGGradientColorRow;
    private int emojiViewBGGradientRow;
    private int emojiViewTabColorRow;
    private int emojiViewTabIconColorRow;
    private int forwardLeftNameColorRow;
    private int forwardRightNameColorRow;
    private int gradientBGColorRow;
    private int gradientBGRow;
    private int headerAvatarRadiusRow;
    private int headerColorRow;
    private int headerGradientColorRow;
    private int headerGradientRow;
    private int headerIconsColorRow;
    private int headerSection2Row;
    private int hideStatusIndicatorCheckRow;
    private int lBubbleColorRow;
    private int lLinkColorRow;
    private int lTextColorRow;
    private int lTimeColorRow;
    private ListAdapter listAdapter;
    private ListView listView;
    private int memberColorCheckRow;
    private int memberColorRow;
    private int muteColorRow;
    private int nameColorRow;
    private int nameSizeRow;
    private int onlineColorRow;
    private int ownAvatarAlignTopRow;
    private int quickBarColorRow;
    private int quickBarNamesColorRow;
    private int rBubbleColorRow;
    private int rLinkColorRow;
    private int rTextColorRow;
    private int rTimeColorRow;
    private int rowCount;
    private int rowsSection2Row;
    private int rowsSectionRow;
    private int selectedMessageBGColorRow;
    private int sendColorRow;
    private int showContactAvatar;
    private int showOwnAvatar;
    private int showOwnAvatarGroup;
    private boolean showPrefix;
    private int showUsernameCheckRow;
    private int solidBGColorCheckRow;
    private int solidBGColorRow;
    private int statusColorRow;
    private int statusSizeRow;
    private int textSizeRow;
    private int timeSizeRow;
    private int typingColorRow;
    NumberPicker r0;
    int p;
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
            int g = AndroidUtilities.getIntDef(Theme.pkey_chatGradientBG, 0);
            if (i == ThemingChatActivity.this.headerColorRow || i == ThemingChatActivity.this.headerGradientRow || ((AndroidUtilities.getIntDef(Theme.pkey_chatHeaderGradient, 0) != 0 && i == ThemingChatActivity.this.headerGradientColorRow) || i == ThemingChatActivity.this.muteColorRow || i == ThemingChatActivity.this.headerIconsColorRow || i == ThemingChatActivity.this.headerAvatarRadiusRow || i == ThemingChatActivity.this.rBubbleColorRow || i == ThemingChatActivity.this.lBubbleColorRow || i == ThemingChatActivity.this.bubblesRow || i == ThemingChatActivity.this.checksRow || i == ThemingChatActivity.this.solidBGColorCheckRow || ((Theme.chatSolidBGColorCheck && i == ThemingChatActivity.this.solidBGColorRow) || ((Theme.chatSolidBGColorCheck && i == ThemingChatActivity.this.gradientBGRow) || ((g != 0 && i == ThemingChatActivity.this.gradientBGColorRow) || i == ThemingChatActivity.this.avatarRadiusRow || i == ThemingChatActivity.this.avatarSizeRow || i == ThemingChatActivity.this.avatarMarginLeftRow || i == ThemingChatActivity.this.avatarAlignTopRow || i == ThemingChatActivity.this.ownAvatarAlignTopRow || i == ThemingChatActivity.this.showContactAvatar || i == ThemingChatActivity.this.showOwnAvatar || i == ThemingChatActivity.this.showOwnAvatarGroup || i == ThemingChatActivity.this.hideStatusIndicatorCheckRow || i == ThemingChatActivity.this.nameColorRow || i == ThemingChatActivity.this.nameSizeRow || i == ThemingChatActivity.this.statusColorRow || i == ThemingChatActivity.this.onlineColorRow || i == ThemingChatActivity.this.typingColorRow || i == ThemingChatActivity.this.statusSizeRow || i == ThemingChatActivity.this.textSizeRow || i == ThemingChatActivity.this.timeSizeRow || ((AndroidUtilities.getBoolPref("chatCommandColorCheck") && i == ThemingChatActivity.this.commandColorRow) || i == ThemingChatActivity.this.commandColorCheckRow || i == ThemingChatActivity.this.dateColorRow || i == ThemingChatActivity.this.dateSizeRow || i == ThemingChatActivity.this.dateBubbleColorRow || i == ThemingChatActivity.this.rTextColorRow || i == ThemingChatActivity.this.rLinkColorRow || i == ThemingChatActivity.this.lTextColorRow || i == ThemingChatActivity.this.lLinkColorRow || i == ThemingChatActivity.this.rTimeColorRow || i == ThemingChatActivity.this.lTimeColorRow || i == ThemingChatActivity.this.checksColorRow || i == ThemingChatActivity.this.memberColorCheckRow || ((Theme.chatMemberColorCheck && i == ThemingChatActivity.this.memberColorRow) || i == ThemingChatActivity.this.contactNameColorRow || i == ThemingChatActivity.this.forwardRightNameColorRow || i == ThemingChatActivity.this.forwardLeftNameColorRow || i == ThemingChatActivity.this.showUsernameCheckRow || i == ThemingChatActivity.this.editTextSizeRow || i == ThemingChatActivity.this.editTextColorRow || i == ThemingChatActivity.this.editTextIconsColorRow || i == ThemingChatActivity.this.sendColorRow || i == ThemingChatActivity.this.editTextBGColorRow || i == ThemingChatActivity.this.editTextBGGradientRow || ((AndroidUtilities.getIntDef(Theme.pkey_chatEditTextBGGradient, 0) != 0 && i == ThemingChatActivity.this.editTextBGGradientColorRow) || i == ThemingChatActivity.this.attachBGColorRow || i == ThemingChatActivity.this.attachBGGradientRow || ((AndroidUtilities.getIntDef(Theme.pkey_chatAttachBGGradient, 0) != 0 && i == ThemingChatActivity.this.attachBGGradientColorRow) || i == ThemingChatActivity.this.attachTextColorRow || i == ThemingChatActivity.this.emojiViewBGColorRow || i == ThemingChatActivity.this.emojiViewBGGradientRow || ((AndroidUtilities.getIntDef(Theme.pkey_chatEmojiViewBGGradient, 0) != 0 && i == ThemingChatActivity.this.emojiViewBGGradientColorRow) || i == ThemingChatActivity.this.emojiViewTabIconColorRow || i == ThemingChatActivity.this.emojiViewTabColorRow || i == ThemingChatActivity.this.selectedMessageBGColorRow || i == ThemingChatActivity.this.quickBarColorRow || i == ThemingChatActivity.this.quickBarNamesColorRow)))))))))) {
                return true;
            }
            return false;
        }

        public int getCount() {
            return ThemingChatActivity.this.rowCount;
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
            if (ThemingChatActivity.this.showPrefix) {
                prefix = "2.";
                if (i == ThemingChatActivity.this.headerSection2Row) {
                    prefix = prefix + "1 ";
                } else if (i == ThemingChatActivity.this.rowsSection2Row) {
                    prefix = prefix + "2 ";
                } else if (i < ThemingChatActivity.this.rowsSection2Row) {
                    prefix = prefix + "1." + i + " ";
                } else {
                    prefix = prefix + "2." + (i - ThemingChatActivity.this.rowsSection2Row) + " ";
                }
            }
            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
            View shadowSectionCell;
            if (type == 0) {
                if (view == null) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                }
            } else if (type == 1) {
                if (view == null) {
                    shadowSectionCell = new HeaderCell(this.mContext);
                    shadowSectionCell.setBackgroundColor(-1);
                }
                if (i == ThemingChatActivity.this.headerSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("Header", R.string.Header));
                } else if (i == ThemingChatActivity.this.rowsSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("ChatList", R.string.ChatList));
                }
            } else if (type == 2) {
                if (view == null) {
                    shadowSectionCell = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                int size;
                if (i == ThemingChatActivity.this.headerAvatarRadiusRow) {
                    textCell.setTag(Theme.pkey_chatHeaderAvatarRadius);
                    size = themePrefs.getInt(Theme.pkey_chatHeaderAvatarRadius, AndroidUtilities.isTablet() ? 35 : 32);
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.avatarRadiusRow) {
                    textCell.setTag(Theme.pkey_chatAvatarRadius);
                    size = themePrefs.getInt(Theme.pkey_chatAvatarRadius, AndroidUtilities.isTablet() ? 35 : 32);
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarRadius", R.string.AvatarRadius), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.avatarSizeRow) {
                    textCell.setTag(Theme.pkey_chatAvatarSize);
                    size = themePrefs.getInt(Theme.pkey_chatAvatarSize, AndroidUtilities.isTablet() ? 45 : 42);
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarSize", R.string.AvatarSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.avatarMarginLeftRow) {
                    textCell.setTag(Theme.pkey_chatAvatarMarginLeft);
                    size = themePrefs.getInt(Theme.pkey_chatAvatarMarginLeft, 6);
                    textCell.setTextAndValue(prefix + LocaleController.getString("AvatarMarginLeft", R.string.AvatarMarginLeft), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.nameSizeRow) {
                    textCell.setTag(Theme.pkey_chatNameSize);
                    size = themePrefs.getInt(Theme.pkey_chatNameSize, AndroidUtilities.isTablet() ? 20 : 18);
                    textCell.setTextAndValue(prefix + LocaleController.getString("NameSize", R.string.NameSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.statusSizeRow) {
                    textCell.setTag(Theme.pkey_chatStatusSize);
                    size = themePrefs.getInt(Theme.pkey_chatStatusSize, AndroidUtilities.isTablet() ? 16 : 14);
                    textCell.setTextAndValue(prefix + LocaleController.getString("StatusSize", R.string.StatusSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.textSizeRow) {
                    textCell.setTag(Theme.pkey_chatTextSize);
                    size = themePrefs.getInt(Theme.pkey_chatTextSize, AndroidUtilities.isTablet() ? 18 : 16);
                    textCell.setTextAndValue(prefix + LocaleController.getString("TextSize", R.string.TextSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.timeSizeRow)
                        {
                    textCell.setTag(Theme.pkey_chatTimeSize);
                    size = themePrefs.getInt(Theme.pkey_chatTimeSize, AndroidUtilities.isTablet() ? 14 : 12);
                    textCell.setTextAndValue(prefix + LocaleController.getString("TimeSize", R.string.TimeSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.dateSizeRow) {
                    textCell.setTag(Theme.pkey_chatDateSize);
                    size = themePrefs.getInt(Theme.pkey_chatDateSize, AndroidUtilities.isTablet() ? 18 : MessagesController.getInstance().fontSize - 2);
                    textCell.setTextAndValue(prefix + LocaleController.getString("DateSize", R.string.DateSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.editTextSizeRow) {
                    textCell.setTag(Theme.pkey_chatEditTextSize);
                    size = themePrefs.getInt(Theme.pkey_chatEditTextSize, AndroidUtilities.isTablet() ? 20 : 18);
                    textCell.setTextAndValue(prefix + LocaleController.getString("EditTextSize", R.string.EditTextSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == ThemingChatActivity.this.bubblesRow) {
                    textCell.setTag("chatBubbleStyle");
                    Theme.chatBubbleStyle = themePrefs.getString("chatBubbleStyle", Theme.bubblesNamesArray[0]);
                    textCell.setTextAndValue(prefix + LocaleController.getString("BubbleStyle", R.string.BubbleStyle), Theme.chatBubbleStyle, true);
                } else if (i == ThemingChatActivity.this.checksRow) {
                    textCell.setTag("chatCheckStyle");
                    Theme.chatCheckStyle = themePrefs.getString("chatCheckStyle", Theme.checksNamesArray[0]);
                    textCell.setTextAndValue(prefix + LocaleController.getString("CheckStyle", R.string.CheckStyle), Theme.chatCheckStyle, true);
                }
            } else if (type == 4) {
                if (view == null) {
                    shadowSectionCell = new TextCheckCell(this.mContext);
                }
                TextCheckCell textCell2 = (TextCheckCell) view;
                if (i == ThemingChatActivity.this.solidBGColorCheckRow) {
                    textCell2.setTag("chatSolidBGColorCheck");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("SetSolidBGColor", R.string.SetSolidBGColor), Theme.chatSolidBGColorCheck, false);
                } else if (i == ThemingChatActivity.this.commandColorCheckRow) {
                    textCell2.setTag("chatCommandColorCheck");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("CommandColorCheck", R.string.CommandColorCheck), themePrefs.getBoolean("chatCommandColorCheck", false), false);
                } else if (i == ThemingChatActivity.this.memberColorCheckRow) {
                    textCell2.setTag("chatMemberColorCheck");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("SetMemberColor", R.string.SetMemberColor), Theme.chatMemberColorCheck, false);
                } else if (i == ThemingChatActivity.this.showUsernameCheckRow) {
                    textCell2.setTag("chatShowUsernameCheck");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowUsername", R.string.ShowUsername), Theme.chatShowUsernameCheck, true);
                } else if (i == ThemingChatActivity.this.avatarAlignTopRow) {
                    textCell2.setTag("chatAvatarAlignTop");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("AvatarAlignTop", R.string.AvatarAlignTop), Theme.chatAvatarAlignTop, true);
                } else if (i == ThemingChatActivity.this.ownAvatarAlignTopRow) {
                    textCell2.setTag("chatOwnAvatarAlignTop");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("OwnAvatarAlignTop", R.string.OwnAvatarAlignTop), Theme.chatOwnAvatarAlignTop, true);
                } else if (i == ThemingChatActivity.this.showContactAvatar) {
                    textCell2.setTag("chatShowContactAvatar");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowContactAvatar", R.string.ShowContactAvatar), Theme.chatShowContactAvatar, true);
                } else if (i == ThemingChatActivity.this.showOwnAvatar) {
                    textCell2.setTag("chatShowOwnAvatar");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowOwnAvatar", R.string.ShowOwnAvatar), Theme.chatShowOwnAvatar, true);
                } else if (i == ThemingChatActivity.this.showOwnAvatarGroup) {
                    textCell2.setTag("chatShowOwnAvatarGroup");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowOwnAvatarGroup", R.string.ShowOwnAvatarGroup), Theme.chatShowOwnAvatarGroup, true);
                } else if (i == ThemingChatActivity.this.hideStatusIndicatorCheckRow) {
                    textCell2.setTag("chatHideStatusIndicator");
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideStatusIndicator", R.string.HideStatusIndicator), Theme.chatHideStatusIndicator, true);
                }
            } else if (type == 3) {
                if (view == null) {
                    shadowSectionCell = new TextColorCell(this.mContext);
                }
                TextColorCell textCell3 = (TextColorCell) view;
                int defColor = themePrefs.getInt(Theme.pkey_themeColor, AndroidUtilities.defColor);
                int darkColor = AndroidUtilities.getIntDarkerColor(Theme.pkey_themeColor, 21);
                int lightColor = AndroidUtilities.getIntDarkerColor(Theme.pkey_themeColor, -64);
                if (i == ThemingChatActivity.this.headerColorRow) {
                    textCell3.setTag(Theme.pkey_chatHeaderColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("HeaderColor", R.string.HeaderColor), themePrefs.getInt(Theme.pkey_chatHeaderColor, defColor), false);
                } else if (i == ThemingChatActivity.this.headerGradientColorRow) {
                    textCell3.setTag(Theme.pkey_chatHeaderGradientColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("HeaderColor", R.string.RowGradientColor), themePrefs.getInt(Theme.pkey_chatHeaderGradient, 0) == 0 ? 0 : themePrefs.getInt(Theme.pkey_chatHeaderGradientColor, Theme.defColor), true);
                } else if (i == ThemingChatActivity.this.headerIconsColorRow) {
                    textCell3.setTag(Theme.pkey_chatHeaderIconsColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("HeaderIconsColor", R.string.HeaderIconsColor), themePrefs.getInt(Theme.pkey_chatHeaderIconsColor, -1), true);
                } else if (i == ThemingChatActivity.this.solidBGColorRow) {
                    textCell3.setTag(Theme.pkey_chatSolidBGColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("SolidBGColor", R.string.SolidBGColor), Theme.chatSolidBGColorCheck ? themePrefs.getInt(Theme.pkey_chatSolidBGColor, -1) : 0, false);
                } else if (i == ThemingChatActivity.this.gradientBGColorRow) {
                    textCell3.setTag(Theme.pkey_chatGradientBGColor);
                    String str = prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor);
                    int i2 = (themePrefs.getInt(Theme.pkey_chatGradientBG, 0) == 0 || !Theme.chatSolidBGColorCheck) ? 0 : themePrefs.getInt(Theme.pkey_chatGradientBGColor, -1);
                    textCell3.setTextAndColor(str, i2, true);
                } else if (i == ThemingChatActivity.this.memberColorRow) {
                    textCell3.setTag(Theme.pkey_chatMemberColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("MemberColor", R.string.MemberColor), themePrefs.getBoolean("chatMemberColorCheck", false) ? themePrefs.getInt(Theme.pkey_chatMemberColor, darkColor) : 0, true);
                } else if (i == ThemingChatActivity.this.contactNameColorRow) {
                    textCell3.setTag(Theme.pkey_chatContactNameColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("SharedContactNameColor", R.string.SharedContactNameColor), themePrefs.getInt(Theme.pkey_chatContactNameColor, defColor), true);
                } else if (i == ThemingChatActivity.this.forwardRightNameColorRow) {
                    textCell3.setTag(Theme.pkey_chatForwardRColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("ForwardRightNameColor", R.string.ForwardRightNameColor), themePrefs.getInt(Theme.pkey_chatForwardRColor, darkColor), true);
                } else if (i == ThemingChatActivity.this.forwardLeftNameColorRow) {
                    textCell3.setTag(Theme.pkey_chatForwardLColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("ForwardLeftNameColor", R.string.ForwardLeftNameColor), themePrefs.getInt(Theme.pkey_chatForwardLColor, darkColor), true);
                } else if (i == ThemingChatActivity.this.muteColorRow) {
                    textCell3.setTag("chatMuteColor");
                    textCell3.setTextAndColor(prefix + LocaleController.getString("MuteColor", R.string.MuteColor), themePrefs.getInt("chatMuteColor", -1), true);
                } else if (i == ThemingChatActivity.this.rBubbleColorRow) {
                    textCell3.setTag(Theme.pkey_chatRBubbleColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("RBubbleColor", R.string.RBubbleColor), themePrefs.getInt(Theme.pkey_chatRBubbleColor, AndroidUtilities.getDefBubbleColor()), true);
                } else if (i == ThemingChatActivity.this.lBubbleColorRow) {
                    textCell3.setTag(Theme.pkey_chatLBubbleColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("LBubbleColor", R.string.LBubbleColor), themePrefs.getInt(Theme.pkey_chatLBubbleColor, -1), true);
                } else if (i == ThemingChatActivity.this.rTextColorRow) {
                    textCell3.setTag(Theme.pkey_chatRTextColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("RTextColor", R.string.RTextColor), themePrefs.getInt(Theme.pkey_chatRTextColor, -16777216), true);
                } else if (i == ThemingChatActivity.this.lTextColorRow) {
                    textCell3.setTag(Theme.pkey_chatLTextColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("LTextColor", R.string.LTextColor), themePrefs.getInt(Theme.pkey_chatLTextColor, -16777216), true);
                } else if (i == ThemingChatActivity.this.selectedMessageBGColorRow) {
                    textCell3.setTag(Theme.pkey_chatSelectedMsgBGColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("SelectedMsgBGColor", R.string.SelectedMsgBGColor), themePrefs.getInt(Theme.pkey_chatSelectedMsgBGColor, Theme.SELECTED_MDG_BACKGROUND_COLOR_DEF), true);
                } else if (i == ThemingChatActivity.this.rLinkColorRow) {
                    textCell3.setTag(Theme.pkey_chatRLinkColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("RLinkColor", R.string.RLinkColor), themePrefs.getInt(Theme.pkey_chatRLinkColor, defColor), true);
                } else if (i == ThemingChatActivity.this.lLinkColorRow) {
                    textCell3.setTag(Theme.pkey_chatLLinkColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("LLinkColor", R.string.LLinkColor), themePrefs.getInt(Theme.pkey_chatLLinkColor, defColor), true);
                } else if (i == ThemingChatActivity.this.nameColorRow) {
                    textCell3.setTag(Theme.pkey_chatNameColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("NameColor", R.string.NameColor), themePrefs.getInt(Theme.pkey_chatNameColor, -1), true);
                } else if (i == ThemingChatActivity.this.statusColorRow) {
                    textCell3.setTag(Theme.pkey_chatStatusColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("StatusColor", R.string.StatusColor), themePrefs.getInt(Theme.pkey_chatStatusColor, lightColor), true);
                } else if (i == ThemingChatActivity.this.onlineColorRow) {
                    textCell3.setTag(Theme.pkey_chatOnlineColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("OnlineColor", R.string.OnlineColor), themePrefs.getInt(Theme.pkey_chatOnlineColor, themePrefs.getInt(Theme.pkey_chatStatusColor, lightColor)), true);
                } else if (i == ThemingChatActivity.this.typingColorRow) {
                    textCell3.setTag(Theme.pkey_chatTypingColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("TypingColor", R.string.TypingColor), themePrefs.getInt(Theme.pkey_chatTypingColor, themePrefs.getInt(Theme.pkey_chatStatusColor, lightColor)), false);
                } else if (i == ThemingChatActivity.this.rTimeColorRow) {
                    textCell3.setTag(Theme.pkey_chatRTimeColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("RTimeColor", R.string.RTimeColor), themePrefs.getInt(Theme.pkey_chatRTimeColor, darkColor), true);
                } else if (i == ThemingChatActivity.this.lTimeColorRow) {
                    textCell3.setTag(Theme.pkey_chatLTimeColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("LTimeColor", R.string.LTimeColor), themePrefs.getInt(Theme.pkey_chatLTimeColor, -6182221), true);
                } else if (i == ThemingChatActivity.this.checksColorRow) {
                    textCell3.setTag(Theme.pkey_chatChecksColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("ChecksColor", R.string.ChecksColor), themePrefs.getInt(Theme.pkey_chatChecksColor, defColor), true);
                } else if (i == ThemingChatActivity.this.commandColorRow) {
                    textCell3.setTag(Theme.pkey_chatCommandColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("CommandColor", R.string.CommandColor), themePrefs.getBoolean("chatCommandColorCheck", false) ? themePrefs.getInt(Theme.pkey_chatCommandColor, Theme.defColor) : 0, true);
                } else if (i == ThemingChatActivity.this.dateColorRow) {
                    textCell3.setTag(Theme.pkey_chatDateColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("DateColor", R.string.DateColor), themePrefs.getInt(Theme.pkey_chatDateColor, -1), true);
                } else if (i == ThemingChatActivity.this.dateBubbleColorRow) {
                    textCell3.setTag(Theme.pkey_chatDateBubbleColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("DateBubbleColor", R.string.DateBubbleColor), themePrefs.getInt(Theme.pkey_chatDateBubbleColor, 1719044499), true);
                } else if (i == ThemingChatActivity.this.sendColorRow) {
                    textCell3.setTag(Theme.pkey_chatSendIconColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("SendIcon", R.string.SendIcon), themePrefs.getInt(Theme.pkey_chatSendIconColor, themePrefs.getInt(Theme.pkey_chatEditTextIconsColor, defColor)), true);
                } else if (i == ThemingChatActivity.this.editTextColorRow) {
                    textCell3.setTag(Theme.pkey_chatEditTextColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("EditTextColor", R.string.EditTextColor), themePrefs.getInt(Theme.pkey_chatEditTextColor, -16777216), true);
                } else if (i == ThemingChatActivity.this.editTextBGColorRow) {
                    textCell3.setTag(Theme.pkey_chatEditTextBGColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("EditTextBGColor", R.string.EditTextBGColor), themePrefs.getInt(Theme.pkey_chatEditTextBGColor, -1), false);
                } else if (i == ThemingChatActivity.this.editTextBGGradientColorRow) {
                    textCell3.setTag(Theme.pkey_chatEditTextBGGradient);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor), themePrefs.getInt(Theme.pkey_chatEditTextBGGradient, 0) == 0 ? 0 : themePrefs.getInt(Theme.pkey_chatEditTextBGGradientColor, -1), true);
                } else if (i == ThemingChatActivity.this.attachBGColorRow) {
                    textCell3.setTag(Theme.pkey_chatAttachBGColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("AttachBGColor", R.string.AttachBGColor), themePrefs.getInt(Theme.pkey_chatAttachBGColor, -1), false);
                } else if (i == ThemingChatActivity.this.attachBGGradientColorRow) {
                    textCell3.setTag(Theme.pkey_chatAttachBGGradient);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor), themePrefs.getInt(Theme.pkey_chatAttachBGGradient, 0) == 0 ? 0 : themePrefs.getInt(Theme.pkey_chatAttachBGGradientColor, -1), true);
                } else if (i == ThemingChatActivity.this.attachTextColorRow) {
                    textCell3.setTag(Theme.pkey_chatAttachTextColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("AttachTextColor", R.string.AttachTextColor), themePrefs.getInt(Theme.pkey_chatAttachTextColor, -9079435), true);
                } else if (i == ThemingChatActivity.this.editTextIconsColorRow) {
                    textCell3.setTag(Theme.pkey_chatEditTextIconsColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("EditTextIconsColor", R.string.EditTextIconsColor), themePrefs.getInt(Theme.pkey_chatEditTextIconsColor, -5395027), true);
                } else if (i == ThemingChatActivity.this.emojiViewBGColorRow) {
                    textCell3.setTag(Theme.pkey_chatEmojiViewBGColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("EmojiViewBGColor", R.string.EmojiViewBGColor), themePrefs.getInt(Theme.pkey_chatEmojiViewBGColor, -657673), false);
                } else if (i == ThemingChatActivity.this.emojiViewBGGradientColorRow) {
                    textCell3.setTag(Theme.pkey_chatEmojiViewBGGradient);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("RowGradientColor", R.string.RowGradientColor), themePrefs.getInt(Theme.pkey_chatEmojiViewBGGradient, 0) == 0 ? 0 : themePrefs.getInt(Theme.pkey_chatEmojiViewBGGradientColor, -657673), true);
                } else if (i == ThemingChatActivity.this.emojiViewTabIconColorRow) {
                    textCell3.setTag(Theme.pkey_chatEmojiViewTabIconColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("EmojiViewTabIconColor", R.string.EmojiViewTabIconColor), themePrefs.getInt(Theme.pkey_chatEmojiViewTabIconColor, -5723992), true);
                } else if (i == ThemingChatActivity.this.emojiViewTabColorRow) {
                    textCell3.setTag(Theme.pkey_chatEmojiViewTabColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("EmojiViewTabColor", R.string.EmojiViewTabColor), themePrefs.getInt(Theme.pkey_chatEmojiViewTabColor, AndroidUtilities.getIntDarkerColor(Theme.pkey_themeColor, -21)), true);
                } else if (i == ThemingChatActivity.this.quickBarColorRow) {
                    textCell3.setTag(Theme.pkey_chatQuickBarColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("QuickBarColor", R.string.QuickBarColor), themePrefs.getInt(Theme.pkey_chatQuickBarColor, -1), true);
                } else if (i == ThemingChatActivity.this.quickBarNamesColorRow) {
                    textCell3.setTag(Theme.pkey_chatQuickBarNamesColor);
                    textCell3.setTextAndColor(prefix + LocaleController.getString("QuickBarNamesColor", R.string.QuickBarNamesColor), themePrefs.getInt(Theme.pkey_chatQuickBarNamesColor, -14606047), false);
                }
            } else if (type == 5) {
                if (view == null) {
                    shadowSectionCell = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell4 = (TextDetailSettingsCell) view;
                int value;
                if (i == ThemingChatActivity.this.gradientBGRow) {
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt(Theme.pkey_chatGradientBG, 0);
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
                } else if (i == ThemingChatActivity.this.headerGradientRow) {
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt(Theme.pkey_chatHeaderGradient, 0);
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
                } else if (i == ThemingChatActivity.this.editTextBGGradientRow) {
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt(Theme.pkey_chatEditTextBGGradient, 0);
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
                } else if (i == ThemingChatActivity.this.attachBGGradientRow) {
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt(Theme.pkey_chatAttachBGGradient, 0);
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
                } else if (i == ThemingChatActivity.this.emojiViewBGGradientRow) {
                    textCell4.setMultilineDetail(false);
                    value = themePrefs.getInt(Theme.pkey_chatEmojiViewBGGradient, 0);
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
            if (i == ThemingChatActivity.this.rowsSectionRow) {
                return 0;
            }
            if (i == ThemingChatActivity.this.headerSection2Row || i == ThemingChatActivity.this.rowsSection2Row) {
                return 1;
            }
            if (i == ThemingChatActivity.this.headerAvatarRadiusRow || i == ThemingChatActivity.this.avatarRadiusRow || i == ThemingChatActivity.this.avatarSizeRow || i == ThemingChatActivity.this.avatarMarginLeftRow || i == ThemingChatActivity.this.nameSizeRow || i == ThemingChatActivity.this.statusSizeRow || i == ThemingChatActivity.this.textSizeRow || i == ThemingChatActivity.this.timeSizeRow || i == ThemingChatActivity.this.dateSizeRow || i == ThemingChatActivity.this.editTextSizeRow || i == ThemingChatActivity.this.bubblesRow || i == ThemingChatActivity.this.checksRow) {
                return 2;
            }
            if (i == ThemingChatActivity.this.headerColorRow || i == ThemingChatActivity.this.headerGradientColorRow || i == ThemingChatActivity.this.muteColorRow || i == ThemingChatActivity.this.headerIconsColorRow || i == ThemingChatActivity.this.solidBGColorRow || i == ThemingChatActivity.this.gradientBGColorRow || i == ThemingChatActivity.this.rBubbleColorRow || i == ThemingChatActivity.this.lBubbleColorRow || i == ThemingChatActivity.this.nameColorRow || i == ThemingChatActivity.this.statusColorRow || i == ThemingChatActivity.this.onlineColorRow || i == ThemingChatActivity.this.typingColorRow || i == ThemingChatActivity.this.commandColorRow || i == ThemingChatActivity.this.dateColorRow || i == ThemingChatActivity.this.dateBubbleColorRow || i == ThemingChatActivity.this.rTextColorRow || i == ThemingChatActivity.this.rLinkColorRow || i == ThemingChatActivity.this.lTextColorRow || i == ThemingChatActivity.this.lLinkColorRow || i == ThemingChatActivity.this.rTimeColorRow || i == ThemingChatActivity.this.lTimeColorRow || i == ThemingChatActivity.this.checksColorRow || i == ThemingChatActivity.this.memberColorRow || i == ThemingChatActivity.this.contactNameColorRow || i == ThemingChatActivity.this.forwardRightNameColorRow || i == ThemingChatActivity.this.forwardLeftNameColorRow || i == ThemingChatActivity.this.sendColorRow || i == ThemingChatActivity.this.editTextColorRow || i == ThemingChatActivity.this.editTextBGColorRow || i == ThemingChatActivity.this.editTextBGGradientColorRow || i == ThemingChatActivity.this.editTextIconsColorRow || i == ThemingChatActivity.this.attachBGColorRow || i == ThemingChatActivity.this.attachBGGradientColorRow || i == ThemingChatActivity.this.attachTextColorRow || i == ThemingChatActivity.this.emojiViewBGColorRow || i == ThemingChatActivity.this.emojiViewBGGradientColorRow || i == ThemingChatActivity.this.emojiViewTabIconColorRow || i == ThemingChatActivity.this.emojiViewTabColorRow || i == ThemingChatActivity.this.selectedMessageBGColorRow || i == ThemingChatActivity.this.quickBarColorRow || i == ThemingChatActivity.this.quickBarNamesColorRow) {
                return 3;
            }
            if (i == ThemingChatActivity.this.solidBGColorCheckRow || i == ThemingChatActivity.this.commandColorCheckRow || i == ThemingChatActivity.this.memberColorCheckRow || i == ThemingChatActivity.this.showUsernameCheckRow || i == ThemingChatActivity.this.avatarAlignTopRow || i == ThemingChatActivity.this.ownAvatarAlignTopRow || i == ThemingChatActivity.this.showContactAvatar || i == ThemingChatActivity.this.showOwnAvatar || i == ThemingChatActivity.this.showOwnAvatarGroup || i == ThemingChatActivity.this.hideStatusIndicatorCheckRow) {
                return 4;
            }
            if (i == ThemingChatActivity.this.headerGradientRow || i == ThemingChatActivity.this.gradientBGRow || i == ThemingChatActivity.this.editTextBGGradientRow || i == ThemingChatActivity.this.attachBGGradientRow || i == ThemingChatActivity.this.emojiViewBGGradientRow) {
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
        this.onlineColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.typingColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rowsSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.solidBGColorCheckRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.solidBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.gradientBGRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.gradientBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showContactAvatar = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.avatarAlignTopRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showOwnAvatar = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showOwnAvatarGroup = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ownAvatarAlignTopRow = i;
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
        this.textSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rTextColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rLinkColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.lTextColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.lLinkColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.selectedMessageBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.commandColorCheckRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.commandColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.timeSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rTimeColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.lTimeColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.checksColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dateSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dateColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.bubblesRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.checksRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rBubbleColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.lBubbleColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dateBubbleColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.memberColorCheckRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.memberColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactNameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.forwardRightNameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.forwardLeftNameColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showUsernameCheckRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sendColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.editTextSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.editTextColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.editTextBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.editTextBGGradientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.editTextBGGradientColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.editTextIconsColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.attachBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.attachBGGradientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.attachBGGradientColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.attachTextColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emojiViewBGColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emojiViewBGGradientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emojiViewBGGradientColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emojiViewTabIconColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emojiViewTabColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.quickBarColorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.quickBarNamesColorRow = i;
        this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("chatShowPrefix", true);
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
            this.actionBar.setTitle(LocaleController.getString("ChatScreen", R.string.ChatScreen));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        ThemingChatActivity.this.finishFragment();
                    }
                }
            });
            this.actionBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean z;
                    ThemingChatActivity themingChatActivity = ThemingChatActivity.this;
                    if (ThemingChatActivity.this.showPrefix) {
                        z = false;
                    } else {
                        z = true;
                    }
                    themingChatActivity.showPrefix = z;
                    ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("chatShowPrefix", ThemingChatActivity.this.showPrefix).apply();
                    if (ThemingChatActivity.this.listAdapter != null) {
                        ThemingChatActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            });
            this.listAdapter = new ListAdapter(context);
            this.fragmentView = new FrameLayout(context);
            FrameLayout frameLayout = (FrameLayout) this.fragmentView; //TODO Multi
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
                    if (i == ThemingChatActivity.this.headerColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatHeaderColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatHeaderColor, color);
                                }
                            }, Theme.chatHeaderColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.headerGradientColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatHeaderGradientColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatHeaderGradientColor, Theme.defColor), 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.headerGradientRow) {
                        Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt(Theme.pkey_chatHeaderGradient, which).commit();
                                if (ThemingChatActivity.this.listView != null) {
                                    ThemingChatActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatActivity.this.editTextBGGradientRow) {
                        Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt(Theme.pkey_chatEditTextBGGradient, which).commit();
                                if (ThemingChatActivity.this.listView != null) {
                                    ThemingChatActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatActivity.this.attachBGGradientRow) {
                        Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt(Theme.pkey_chatAttachBGGradient, which).commit();
                                if (ThemingChatActivity.this.listView != null) {
                                    ThemingChatActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatActivity.this.emojiViewBGGradientRow) {
                        Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt(Theme.pkey_chatEmojiViewBGGradient, which).commit();
                                if (ThemingChatActivity.this.listView != null) {
                                    ThemingChatActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatActivity.this.commandColorCheckRow) {
                        boolean b = themePrefs.getBoolean(key, false);
//                        SharedPreferences editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, !b);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(!b);
                        }
                        if (ThemingChatActivity.this.listView != null) {
                            ThemingChatActivity.this.listView.invalidateViews();
                        }
                    } else if (i == ThemingChatActivity.this.solidBGColorCheckRow) {
                        Theme.chatSolidBGColorCheck = !Theme.chatSolidBGColorCheck;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatSolidBGColorCheck);
                        themePrefs.edit().apply();
                        Theme.reloadWallpaper();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatSolidBGColorCheck);
                        }
                        if (ThemingChatActivity.this.listView != null) {
                            ThemingChatActivity.this.listView.invalidateViews();
                        }
                    } else if (i == ThemingChatActivity.this.memberColorCheckRow) {
                        boolean b = themePrefs.getBoolean(key, false);
                        Theme.chatMemberColorCheck = !Theme.chatMemberColorCheck;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatMemberColorCheck);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatMemberColorCheck);
                        }
                        if (ThemingChatActivity.this.listView != null) {
                            ThemingChatActivity.this.listView.invalidateViews();
                        }
                    } else if (i == ThemingChatActivity.this.showUsernameCheckRow) {
                        Theme.chatShowUsernameCheck = !Theme.chatShowUsernameCheck;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatShowUsernameCheck);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatShowUsernameCheck);
                        }
                        if (ThemingChatActivity.this.listView != null) {
                            ThemingChatActivity.this.listView.invalidateViews();
                        }
                    } else if (i == ThemingChatActivity.this.avatarAlignTopRow) {
                        Theme.chatAvatarAlignTop = !Theme.chatAvatarAlignTop;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatAvatarAlignTop);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatAvatarAlignTop);
                        }
                    } else if (i == ThemingChatActivity.this.ownAvatarAlignTopRow) {
                        Theme.chatOwnAvatarAlignTop = !Theme.chatOwnAvatarAlignTop;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatOwnAvatarAlignTop);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatOwnAvatarAlignTop);
                        }
                    } else if (i == ThemingChatActivity.this.showContactAvatar) {
                        Theme.chatShowContactAvatar = !Theme.chatShowContactAvatar;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatShowContactAvatar);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatShowContactAvatar);
                        }
                    } else if (i == ThemingChatActivity.this.showOwnAvatar) {
                        Theme.chatShowOwnAvatar = !Theme.chatShowOwnAvatar;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatShowOwnAvatar);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatShowOwnAvatar);
                        }
                    } else if (i == ThemingChatActivity.this.showOwnAvatarGroup) {
                        Theme.chatShowOwnAvatarGroup = !Theme.chatShowOwnAvatarGroup;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatShowOwnAvatarGroup);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatShowOwnAvatarGroup);
                        }
                    } else if (i == ThemingChatActivity.this.hideStatusIndicatorCheckRow) {
                        Theme.chatHideStatusIndicator = !Theme.chatHideStatusIndicator;
//                        editor = themePrefs.edit();
                        themePrefs.edit().putBoolean(key, Theme.chatHideStatusIndicator);
                        themePrefs.edit().apply();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(Theme.chatHideStatusIndicator);
                        }
                    } else if (i == ThemingChatActivity.this.solidBGColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatSolidBGColor, color);
                                    Theme.reloadWallpaper();
                                }
                            }, themePrefs.getInt(Theme.pkey_chatSolidBGColor, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.gradientBGColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatGradientBGColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatGradientBGColor, -1), 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.gradientBGRow) {
                        Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
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
                                ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit().putInt(Theme.pkey_chatGradientBG, which).commit();
                                if (ThemingChatActivity.this.listView != null) {
                                    ThemingChatActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ThemingChatActivity.this.showDialog(builder.create());
                    } else if (i == ThemingChatActivity.this.memberColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatMemberColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatMemberColor, color);
                                }
                            }, Theme.chatMemberColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.contactNameColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatContactNameColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatContactNameColor, color);
                                }
                            }, Theme.chatContactNameColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.forwardRightNameColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatForwardRColor = color;
                                    ThemingChatActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatForwardRColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.forwardLeftNameColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatForwardLColor = color;
                                    ThemingChatActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatForwardLColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.muteColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(key, color);
                                }
                            }, themePrefs.getInt(key, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.rBubbleColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatRBubbleColor = color;
                                    Theme.updateChatDrawablesColor();
                                    ThemingChatActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatRBubbleColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.lBubbleColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatLBubbleColor = color;
                                    Theme.updateChatDrawablesColor();
                                    ThemingChatActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatLBubbleColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.rTextColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatRTextColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatRTextColor, color);
                                }
                            }, Theme.chatRTextColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.lTextColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatLTextColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatLTextColor, color);
                                }
                            }, Theme.chatLTextColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.selectedMessageBGColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    int alpha = Color.alpha(color);
                                    Theme.chatSelectedMsgBGColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatSelectedMsgBGColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatSelectedMsgBGColor, Theme.SELECTED_MDG_BACKGROUND_COLOR_DEF), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.rLinkColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatRLinkColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatRLinkColor, color);
                                }
                            }, Theme.chatRLinkColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.lLinkColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatLLinkColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatLLinkColor, color);
                                }
                            }, Theme.chatLLinkColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.rTimeColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatRTimeColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatRTimeColor, color);
                                }
                            }, Theme.chatRTimeColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.lTimeColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatLTimeColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatLTimeColor, color);
                                }
                            }, Theme.chatLTimeColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.dateBubbleColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatDateBubbleColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatDateBubbleColor, color);
                                }
                            }, Theme.chatDateBubbleColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.headerIconsColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatHeaderIconsColor = color;
                                    ThemingChatActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatHeaderIconsColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.nameColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatNameColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatNameColor, -1), 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.sendColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatSendIconColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatSendIconColor, AndroidUtilities.getIntColor(Theme.pkey_chatEditTextIconsColor)), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.editTextColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatEditTextColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatEditTextColor, -16777216), 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.editTextBGColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatEditTextBGColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatEditTextBGColor, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.editTextBGGradientColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatEditTextBGGradientColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatEditTextBGGradientColor, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.attachBGColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatAttachBGColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatAttachBGColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatAttachBGColor, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.attachBGGradientColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatAttachBGGradientColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatAttachBGGradientColor, -1), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.attachTextColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatAttachTextColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatAttachTextColor, -9079435), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.editTextIconsColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatEditTextIconsColor = color;
                                    ThemingChatActivity.this.commitInt(key, color);
                                }
                            }, Theme.chatEditTextIconsColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.emojiViewBGColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatEmojiViewBGColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatEmojiViewBGColor, -657673), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.emojiViewBGGradientColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatEmojiViewBGGradientColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatEmojiViewBGGradientColor, -657673), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.emojiViewTabIconColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatEmojiViewTabIconColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatEmojiViewTabIconColor, -5723992), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.emojiViewTabColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatEmojiViewTabColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatEmojiViewTabColor, Theme.darkColor), 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.statusColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatStatusColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatStatusColor, color);
                                }
                            }, Theme.chatStatusColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.onlineColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatOnlineColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatOnlineColor, color);
                                }
                            }, Theme.chatOnlineColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.typingColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatTypingColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatTypingColor, color);
                                }
                            }, Theme.chatTypingColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.commandColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatCommandColor, color);
                                }
                            }, themePrefs.getInt(Theme.pkey_chatCommandColor, Theme.defColor), 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.dateColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatDateColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatDateColor, color);
                                }
                            }, Theme.chatDateColor, 0, 0, false).show();
                        }
                    } else if (i == ThemingChatActivity.this.checksColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatChecksColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatChecksColor, color);
                                }
                            }, Theme.chatChecksColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.headerAvatarRadiusRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt(key, 32);
                            r0.setMinValue(1);
                            r0.setMaxValue(32);
                            r0.setValue(currentValue);
                            builder.setView(r0);
                            p = r0.getValue();
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (p != currentValue) {
                                        ThemingChatActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.avatarRadiusRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarRadius", R.string.AvatarRadius));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            r0.setMinValue(1);
                            r0.setMaxValue(AndroidUtilities.isTablet() ? 35 : 32);
                            r0.setValue(Theme.chatAvatarRadius);
                            builder.setView(r0);
                            p = r0.getValue();
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (p != Theme.chatAvatarRadius) {
                                        Theme.chatAvatarRadius = r0.getValue();
                                        ThemingChatActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.avatarSizeRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarSize", R.string.AvatarSize));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            r0.setMinValue(0);
                            r0.setMaxValue(56);
                            r0.setValue(Theme.chatAvatarSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatAvatarSize) {
                                        Theme.chatAvatarSize = r0.getValue();
                                        ThemingChatActivity.this.commitInt(key, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.avatarMarginLeftRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AvatarMarginLeft", R.string.AvatarMarginLeft));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            r0.setMinValue(0);
                            r0.setMaxValue(12);
                            r0.setValue(Theme.chatAvatarMarginLeft);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatAvatarMarginLeft) {
                                        Theme.chatAvatarMarginLeft = r0.getValue();
                                        ThemingChatActivity.this.commitInt(key, Theme.chatAvatarMarginLeft);
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.nameSizeRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("NameSize", R.string.NameSize));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt(Theme.pkey_chatNameSize, 18);
                            r0.setMinValue(12);
                            r0.setMaxValue(30);
                            r0.setValue(currentValue);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingChatActivity.this.commitInt(Theme.pkey_chatNameSize, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.statusSizeRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("StatusSize", R.string.StatusSize));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            r0.setMinValue(8);
                            r0.setMaxValue(22);
                            r0.setValue(Theme.chatStatusSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatStatusSize) {
                                        Theme.chatStatusSize = r0.getValue();
                                        ThemingChatActivity.this.commitInt(Theme.pkey_chatStatusSize, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.textSizeRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("TextSize", R.string.TextSize));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt(Theme.pkey_chatTextSize, 16);
                            r0.setMinValue(12);
                            r0.setMaxValue(30);
                            r0.setValue(currentValue);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingChatActivity.this.commitInt(Theme.pkey_chatTextSize, r0.getValue());
                                        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                                        editor.putInt("fons_size", r0.getValue());
                                        MessagesController.getInstance().fontSize = r0.getValue();
                                        editor.apply();
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.timeSizeRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("TimeSize", R.string.TimeSize));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            r0.setMinValue(8);
                            r0.setMaxValue(20);
                            r0.setValue(Theme.chatTimeSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatTimeSize) {
                                        Theme.chatTimeSize = r0.getValue();
                                        ThemingChatActivity.this.commitInt(Theme.pkey_chatTimeSize, Theme.chatTimeSize);
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.dateSizeRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("DateSize", R.string.DateSize));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            r0.setMinValue(8);
                            r0.setMaxValue(20);
                            r0.setValue(Theme.chatDateSize);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != Theme.chatDateSize) {
                                        Theme.chatDateSize = r0.getValue();
                                        ThemingChatActivity.this.commitInt(Theme.pkey_chatDateSize, Theme.chatDateSize);
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.editTextSizeRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ThemingChatActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("EditTextSize", R.string.EditTextSize));
                            r0 = new NumberPicker(ThemingChatActivity.this.getParentActivity());
                            currentValue = themePrefs.getInt(Theme.pkey_chatEditTextSize, 18);
                            r0.setMinValue(12);
                            r0.setMaxValue(28);
                            r0.setValue(currentValue);
                            builder.setView(r0);
//                            r1 = r0;
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (r0.getValue() != currentValue) {
                                        ThemingChatActivity.this.commitInt(Theme.pkey_chatEditTextSize, r0.getValue());
                                    }
                                }
                            });
                            ThemingChatActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ThemingChatActivity.this.bubblesRow) {
                        Bundle args = new Bundle();
                        args.putInt("array_id", 0);
                        ThemingChatActivity.this.presentFragment(new ImageListActivity(args));
                    } else if (i == ThemingChatActivity.this.checksRow) {
                        Bundle args = new Bundle();
                        args.putInt("array_id", 1);
                        ThemingChatActivity.this.presentFragment(new ImageListActivity(args));
                    } else if (i == ThemingChatActivity.this.quickBarColorRow) {
                        if (ThemingChatActivity.this.getParentActivity() != null) {
                            ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                            new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    Theme.chatQuickBarColor = color;
                                    ThemingChatActivity.this.commitInt(Theme.pkey_chatQuickBarColor, color);
                                }
                            }, Theme.chatQuickBarColor, 0, 0, true).show();
                        }
                    } else if (i == ThemingChatActivity.this.quickBarNamesColorRow && ThemingChatActivity.this.getParentActivity() != null) {
                        ((LayoutInflater) ThemingChatActivity.this.getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.colordialog, null, false);
                        new ColorSelectorDialog(ThemingChatActivity.this.getParentActivity(), new OnColorChangedListener() {
                            public void colorChanged(int color) {
                                Theme.chatQuickBarNamesColor = color;
                                ThemingChatActivity.this.commitInt(Theme.pkey_chatQuickBarNamesColor, color);
                            }
                        }, Theme.chatQuickBarNamesColor, 0, 0, true).show();
                    }
                }
            });
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (ThemingChatActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    if (i == ThemingChatActivity.this.headerColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatHeaderColor);
                    } else if (i == ThemingChatActivity.this.headerGradientRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatHeaderGradient);
                    } else if (i == ThemingChatActivity.this.headerGradientColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatHeaderGradientColor);
                    } else if (i == ThemingChatActivity.this.solidBGColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatSolidBGColor);
                        Theme.reloadWallpaper();
                    } else if (i == ThemingChatActivity.this.gradientBGColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatGradientBGColor);
                        Theme.reloadWallpaper();
                    } else if (i == ThemingChatActivity.this.gradientBGRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatGradientBG);
                    } else if (i == ThemingChatActivity.this.memberColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatMemberColor);
                    } else if (i == ThemingChatActivity.this.contactNameColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatContactNameColor);
                    } else if (i == ThemingChatActivity.this.rTextColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatRTextColor);
                    } else if (i == ThemingChatActivity.this.lTextColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatLTextColor);
                    } else if (i == ThemingChatActivity.this.selectedMessageBGColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatSelectedMsgBGColor);
                    } else if (i == ThemingChatActivity.this.nameColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatNameColor);
                    } else if (i == ThemingChatActivity.this.nameSizeRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatNameSize);
                    } else if (i == ThemingChatActivity.this.statusColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatStatusColor);
                    } else if (i == ThemingChatActivity.this.onlineColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatOnlineColor);
                    } else if (i == ThemingChatActivity.this.typingColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatTypingColor);
                    } else if (i == ThemingChatActivity.this.statusSizeRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatStatusSize);
                    } else if (i == ThemingChatActivity.this.rTimeColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatRTimeColor);
                    } else if (i == ThemingChatActivity.this.lTimeColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatLTimeColor);
                    } else if (i == ThemingChatActivity.this.commandColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatCommandColor);
                    } else if (i == ThemingChatActivity.this.dateColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatDateColor);
                    } else if (i == ThemingChatActivity.this.checksColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatChecksColor);
                    } else if (i == ThemingChatActivity.this.textSizeRow) {
                        ThemingChatActivity.this.resetInt(Theme.pkey_chatTextSize, 16);
                    } else if (i == ThemingChatActivity.this.timeSizeRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatTimeSize);
                    } else if (i == ThemingChatActivity.this.dateSizeRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatDateSize);
                    } else if (i == ThemingChatActivity.this.dateBubbleColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatDateBubbleColor);
                    } else if (i == ThemingChatActivity.this.sendColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatSendIconColor);
                    } else if (i == ThemingChatActivity.this.editTextColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEditTextColor);
                    } else if (i == ThemingChatActivity.this.editTextSizeRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEditTextSize);
                    } else if (i == ThemingChatActivity.this.editTextBGColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEditTextBGColor);
                    } else if (i == ThemingChatActivity.this.editTextBGGradientColorRow) {
                        ThemingChatActivity.this.resetPref("chatEditTextBGGradentColor");
                    } else if (i == ThemingChatActivity.this.editTextBGGradientRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEditTextBGGradient);
                    } else if (i == ThemingChatActivity.this.attachBGColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatAttachBGColor);
                    } else if (i == ThemingChatActivity.this.attachBGGradientRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatAttachBGGradient);
                    } else if (i == ThemingChatActivity.this.attachBGGradientColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatAttachBGGradientColor);
                    } else if (i == ThemingChatActivity.this.attachTextColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatAttachTextColor);
                    } else if (i == ThemingChatActivity.this.emojiViewBGColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEmojiViewBGColor);
                    } else if (i == ThemingChatActivity.this.emojiViewBGGradientRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEmojiViewBGGradient);
                    } else if (i == ThemingChatActivity.this.emojiViewBGGradientColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEmojiViewBGGradientColor);
                    } else if (i == ThemingChatActivity.this.emojiViewTabIconColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEmojiViewTabIconColor);
                    } else if (i == ThemingChatActivity.this.emojiViewTabColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatEmojiViewTabColor);
                    } else if (i == ThemingChatActivity.this.quickBarColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatQuickBarColor);
                    } else if (i == ThemingChatActivity.this.quickBarNamesColorRow) {
                        ThemingChatActivity.this.resetPref(Theme.pkey_chatQuickBarNamesColor);
                    } else if (view.getTag() != null) {
                        ThemingChatActivity.this.resetPref(view.getTag().toString());
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
        editor.apply();
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
        Theme.updateChatColors();
        refreshTheme();
    }

    private void resetInt(String key, int value) {
        Theme.setPlusColor(key, value, true);
        resetPref(key);
        if (key.equals(Theme.pkey_chatTextSize)) {
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            editor.putInt("fons_size", value);
            MessagesController.getInstance().fontSize = value;
            editor.apply();
        }
    }

    private void commitInt(String key, int value) {
        Theme.setPlusColor(key, value, false);
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
        editor.putInt(key, value);
        editor.apply();
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
                    if (ThemingChatActivity.this.fragmentView != null) {
                        ThemingChatActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return false;
                }
            });
        }
    }
}
