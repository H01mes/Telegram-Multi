package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;

import java.io.File;
import java.util.ArrayList;

public class PlusSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chatAlwaysBackToMainRow;
    private int chatCenterQuickBarBtnRow;
    private int chatDirectShareFavsFirst;
    private int chatDirectShareReplies;
    private int chatDirectShareToMenu;
    private int chatDoNotCloseQuickBarRow;
    private int chatDoNotHideStickersTabRow;
    private int chatDrawSingleBigEmojiRow;
    private int chatHideBotKeyboardRow;
    private int chatHideInstantCameraRow;
    private int chatHideJoinedGroupRow;
    private int chatHideLeftGroupRow;
    private int chatHideQuickBarOnScrollRow;
    private int chatMarkdownRow;
    private int chatPhotoQualityRow;
    private int chatPhotoViewerHideStatusBarRow;
    private int chatSaveToCloudQuoteRow;
    private int chatSearchUserOnTwitterRow;
    private int chatShowDateToastRow;
    private int chatShowDirectShareBtn;
    private int chatShowEditedMarkRow;
    private int chatShowMembersQuickBarRow;
    private int chatShowPhotoQualityBarRow;
    private int chatShowQuickBarRow;
    private int chatSwipeToReplyRow;
    private int chatVerticalQuickBarRow;
    private int chatsToLoadRow;
    private int dialogsDisableTabsAnimationCheckRow;
    private int dialogsDisableTabsScrollingRow;
    private int dialogsDoNotChangeHeaderTitleRow;
    private int dialogsExpandTabsRow;
    private int dialogsGroupPicClickRow;
    private int dialogsHideSelectedTabIndicator;
    private int dialogsHideTabsCheckRow;
    private int dialogsHideTabsCounters;
    private int dialogsInfiniteTabsSwipe;
    private int dialogsLimitTabsCountersRow;
    private int dialogsManageTabsRow;
    private int dialogsPicClickRow;
    private int dialogsSectionRow;
    private int dialogsSectionRow2;
    private int dialogsTabsCountersCountChats;
    private int dialogsTabsCountersCountNotMuted;
    private int dialogsTabsHeightRow;
    private int dialogsTabsRow;
    private int dialogsTabsTextModeRow;
    private int dialogsTabsTextSizeRow;
    private int dialogsTabsToBottomRow;
    private int disableAudioStopRow;
    private int disableMessageClickRow;
    private int drawerSectionRow;
    private int drawerSectionRow2;
    private int emojiPopupSize;
    private int enableDirectReplyRow;
    private WebPage foundWebPage;
    private int hideMobileNumberRow;
    private int hideNotificationsIfPlayingRow;
    private int keepOriginalFilenameDetailRow;
    private int keepOriginalFilenameRow;
    private int linkSearchRequestId;
    private ListAdapter listAdapter;
    private ListView listView;
    private int mediaDownloadSection;
    private int mediaDownloadSection2;
    private int messagesSectionRow;
    private int messagesSectionRow2;
    private int moveVersionToSettingsRow;
    private int notificationInvertMessagesOrderRow;
    private int notificationSection2Row;
    private int notificationSectionRow;
    private int pass;
    private int plusSettingsSectionRow;
    private int plusSettingsSectionRow2;
    private int privacySectionRow;
    private int privacySectionRow2;
    private int profileEnableGoToMsgRow;
    private int profileSectionRow;
    private int profileSectionRow2;
    private int profileSharedOptionsRow;
    private int resetPlusSettingsRow;
    private boolean reseting = false;
    private int restorePlusSettingsRow;
    private int rowCount;
    private int savePlusSettingsRow;
    private boolean saving = false;
    private int settingsSectionRow2;
    private int showAndroidEmojiRow;
    private int showMySettingsRow;
    private int showOfflineToastNotificationRow;
    private int showOnlineToastNotificationDetailRow;
    private int showOnlineToastNotificationRow;
    private boolean showPrefix;
    private int showToastOnlyIfContactFavRow;
    private int showTypingToastNotificationRow;
    private int showUsernameRow;
    private int toastNotificationPaddingRow;
    private int toastNotificationPositionRow;
    private int toastNotificationSection2Row;
    private int toastNotificationSectionRow;
    private int toastNotificationSizeRow;
    private int toastNotificationToBottomRow;
    private int useDeviceFontRow;
    private String userAbout;
    TextCheckCell r42;
    boolean r35;
    
    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == PlusSettingsActivity.this.showAndroidEmojiRow || i == PlusSettingsActivity.this.useDeviceFontRow || i == PlusSettingsActivity.this.emojiPopupSize || i == PlusSettingsActivity.this.dialogsTabsTextSizeRow || i == PlusSettingsActivity.this.dialogsTabsHeightRow || i == PlusSettingsActivity.this.dialogsTabsRow || i == PlusSettingsActivity.this.dialogsManageTabsRow || i == PlusSettingsActivity.this.chatShowDirectShareBtn || i == PlusSettingsActivity.this.profileSharedOptionsRow || i == PlusSettingsActivity.this.disableAudioStopRow || i == PlusSettingsActivity.this.disableMessageClickRow || i == PlusSettingsActivity.this.chatDirectShareToMenu || i == PlusSettingsActivity.this.chatDirectShareReplies || i == PlusSettingsActivity.this.chatDirectShareFavsFirst || i == PlusSettingsActivity.this.chatShowEditedMarkRow || i == PlusSettingsActivity.this.chatShowDateToastRow || i == PlusSettingsActivity.this.chatHideLeftGroupRow || i == PlusSettingsActivity.this.chatHideJoinedGroupRow || i == PlusSettingsActivity.this.chatHideBotKeyboardRow || i == PlusSettingsActivity.this.dialogsHideTabsCheckRow || i == PlusSettingsActivity.this.dialogsDisableTabsAnimationCheckRow || i == PlusSettingsActivity.this.dialogsInfiniteTabsSwipe || i == PlusSettingsActivity.this.dialogsHideTabsCounters || i == PlusSettingsActivity.this.dialogsTabsCountersCountChats || i == PlusSettingsActivity.this.dialogsTabsCountersCountNotMuted || i == PlusSettingsActivity.this.chatSearchUserOnTwitterRow || i == PlusSettingsActivity.this.keepOriginalFilenameRow || i == PlusSettingsActivity.this.dialogsPicClickRow || i == PlusSettingsActivity.this.dialogsGroupPicClickRow || i == PlusSettingsActivity.this.hideMobileNumberRow || i == PlusSettingsActivity.this.showUsernameRow || i == PlusSettingsActivity.this.notificationInvertMessagesOrderRow || i == PlusSettingsActivity.this.savePlusSettingsRow || i == PlusSettingsActivity.this.restorePlusSettingsRow || i == PlusSettingsActivity.this.resetPlusSettingsRow || i == PlusSettingsActivity.this.chatPhotoQualityRow || i == PlusSettingsActivity.this.chatShowPhotoQualityBarRow || i == PlusSettingsActivity.this.dialogsTabsTextModeRow || i == PlusSettingsActivity.this.dialogsExpandTabsRow || i == PlusSettingsActivity.this.dialogsDisableTabsScrollingRow || i == PlusSettingsActivity.this.dialogsDoNotChangeHeaderTitleRow || i == PlusSettingsActivity.this.dialogsTabsToBottomRow || i == PlusSettingsActivity.this.dialogsHideSelectedTabIndicator || i == PlusSettingsActivity.this.showMySettingsRow || i == PlusSettingsActivity.this.showTypingToastNotificationRow || i == PlusSettingsActivity.this.toastNotificationSizeRow || i == PlusSettingsActivity.this.toastNotificationPaddingRow || i == PlusSettingsActivity.this.toastNotificationToBottomRow || i == PlusSettingsActivity.this.toastNotificationPositionRow || i == PlusSettingsActivity.this.showOnlineToastNotificationRow || i == PlusSettingsActivity.this.showOfflineToastNotificationRow || i == PlusSettingsActivity.this.showToastOnlyIfContactFavRow || i == PlusSettingsActivity.this.enableDirectReplyRow || i == PlusSettingsActivity.this.chatShowQuickBarRow || i == PlusSettingsActivity.this.chatVerticalQuickBarRow || i == PlusSettingsActivity.this.chatAlwaysBackToMainRow || i == PlusSettingsActivity.this.chatDoNotCloseQuickBarRow || i == PlusSettingsActivity.this.chatHideQuickBarOnScrollRow || i == PlusSettingsActivity.this.chatCenterQuickBarBtnRow || i == PlusSettingsActivity.this.chatShowMembersQuickBarRow || i == PlusSettingsActivity.this.chatSaveToCloudQuoteRow || i == PlusSettingsActivity.this.chatSwipeToReplyRow || i == PlusSettingsActivity.this.hideNotificationsIfPlayingRow || i == PlusSettingsActivity.this.chatHideInstantCameraRow || i == PlusSettingsActivity.this.chatDoNotHideStickersTabRow || i == PlusSettingsActivity.this.chatPhotoViewerHideStatusBarRow || i == PlusSettingsActivity.this.chatsToLoadRow || i == PlusSettingsActivity.this.profileEnableGoToMsgRow || i == PlusSettingsActivity.this.chatDrawSingleBigEmojiRow || i == PlusSettingsActivity.this.dialogsLimitTabsCountersRow || i == PlusSettingsActivity.this.chatMarkdownRow || i == PlusSettingsActivity.this.moveVersionToSettingsRow;
        }

        public int getCount() {
            return PlusSettingsActivity.this.rowCount;
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
            if (PlusSettingsActivity.this.showPrefix) {
                prefix = "P";
                if (i == PlusSettingsActivity.this.settingsSectionRow2) {
                    prefix = prefix + "0 ";
                } else if (i < PlusSettingsActivity.this.dialogsSectionRow2) {
                    prefix = prefix + "0." + i + " ";
                } else if (i == PlusSettingsActivity.this.dialogsSectionRow2) {
                    prefix = prefix + "1 ";
                } else if (i > PlusSettingsActivity.this.dialogsSectionRow2 && i < PlusSettingsActivity.this.messagesSectionRow2) {
                    prefix = prefix + "1." + (i - PlusSettingsActivity.this.dialogsSectionRow2) + " ";
                } else if (i == PlusSettingsActivity.this.messagesSectionRow2) {
                    prefix = prefix + "2 ";
                } else if (i > PlusSettingsActivity.this.messagesSectionRow2 && i < PlusSettingsActivity.this.drawerSectionRow2) {
                    prefix = prefix + "2." + (i - PlusSettingsActivity.this.messagesSectionRow2) + " ";
                } else if (i == PlusSettingsActivity.this.drawerSectionRow2) {
                    prefix = prefix + "3 ";
                } else if (i > PlusSettingsActivity.this.drawerSectionRow2 && i < PlusSettingsActivity.this.profileSectionRow2) {
                    prefix = prefix + "3." + (i - PlusSettingsActivity.this.drawerSectionRow2) + " ";
                } else if (i == PlusSettingsActivity.this.profileSectionRow2) {
                    prefix = prefix + "4 ";
                } else if (i > PlusSettingsActivity.this.profileSectionRow2 && i < PlusSettingsActivity.this.notificationSection2Row) {
                    prefix = prefix + "4." + (i - PlusSettingsActivity.this.profileSectionRow2) + " ";
                } else if (i == PlusSettingsActivity.this.notificationSection2Row) {
                    prefix = prefix + "5 ";
                } else if (i > PlusSettingsActivity.this.notificationSection2Row && i < PlusSettingsActivity.this.toastNotificationSection2Row) {
                    prefix = prefix + "5." + (i - PlusSettingsActivity.this.notificationSection2Row) + " ";
                } else if (i == PlusSettingsActivity.this.toastNotificationSection2Row) {
                    prefix = prefix + "6 ";
                } else if (i > PlusSettingsActivity.this.toastNotificationSection2Row && i < PlusSettingsActivity.this.privacySectionRow2) {
                    prefix = prefix + "6." + ((i - PlusSettingsActivity.this.toastNotificationSection2Row) - (i <= PlusSettingsActivity.this.showOnlineToastNotificationDetailRow ? 0 : 1)) + " ";
                } else if (i == PlusSettingsActivity.this.privacySectionRow2) {
                    prefix = prefix + "7 ";
                } else if (i > PlusSettingsActivity.this.privacySectionRow2 && i < PlusSettingsActivity.this.mediaDownloadSection2) {
                    prefix = prefix + "7." + (i - PlusSettingsActivity.this.privacySectionRow2) + " ";
                } else if (i == PlusSettingsActivity.this.mediaDownloadSection2) {
                    prefix = prefix + "8 ";
                } else if (i > PlusSettingsActivity.this.mediaDownloadSection2 && i < PlusSettingsActivity.this.plusSettingsSectionRow2) {
                    prefix = prefix + "8." + (i - PlusSettingsActivity.this.mediaDownloadSection2) + " ";
                }
            }
            View shadowSectionCell;
            if (type == 0) {
                if (view == null) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                }
            } else if (type == 1) {
                if (view == null) {
                    shadowSectionCell = new HeaderCell(this.mContext);
                }
                if (i == PlusSettingsActivity.this.settingsSectionRow2) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("General", R.string.General));
                } else if (i == PlusSettingsActivity.this.messagesSectionRow2) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("MessagesSettings", R.string.MessagesSettings));
                } else if (i == PlusSettingsActivity.this.profileSectionRow2) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("ProfileScreen", R.string.ProfileScreen));
                } else if (i == PlusSettingsActivity.this.drawerSectionRow2) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("NavigationDrawer", R.string.NavigationDrawer));
                } else if (i == PlusSettingsActivity.this.privacySectionRow2) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("PrivacySettings", R.string.PrivacySettings));
                } else if (i == PlusSettingsActivity.this.mediaDownloadSection2) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("SharedMedia", R.string.SharedMedia));
                } else if (i == PlusSettingsActivity.this.dialogsSectionRow2) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("DialogsSettings", R.string.DialogsSettings));
                } else if (i == PlusSettingsActivity.this.notificationSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("Notifications", R.string.Notifications));
                } else if (i == PlusSettingsActivity.this.toastNotificationSection2Row) {
                    ((HeaderCell) view).setText(prefix + LocaleController.getString("ToastNotification", R.string.ToastNotification));
                } else if (i == PlusSettingsActivity.this.plusSettingsSectionRow2) {
                    ((HeaderCell) view).setText(LocaleController.getString("PlusSettings", R.string.PlusSettings));
                }
            } else if (type == 2) {
                if (view == null) {
                    shadowSectionCell = new TextSettingsCell(this.mContext);
                }
                String value;
                TextSettingsCell textCell = (TextSettingsCell) view;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                if (i == PlusSettingsActivity.this.emojiPopupSize) {
                    int size = preferences.getInt("emojiPopupSize", AndroidUtilities.isTablet() ? 65 : 60);
                    textCell.setTextAndValue(prefix + LocaleController.getString("EmojiPopupSize", R.string.EmojiPopupSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                } else if (i == PlusSettingsActivity.this.chatPhotoQualityRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("PhotoQuality", R.string.PhotoQuality), String.format("%d", new Object[]{Integer.valueOf(Theme.plusPhotoQuality)}), true);
                } else if (i == PlusSettingsActivity.this.dialogsTabsTextSizeRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("TabsTextSize", R.string.TabsTextSize), String.format("%d", new Object[]{Integer.valueOf(Theme.plusTabsTextSize)}), true);
                } else if (i == PlusSettingsActivity.this.dialogsTabsHeightRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("TabsHeight", R.string.TabsHeight), String.format("%d", new Object[]{Integer.valueOf(Theme.plusTabsHeight)}), true);
                } else if (i == PlusSettingsActivity.this.dialogsPicClickRow) {
                    int sort = preferences.getInt("dialogsClickOnPic", 0);
                    if (sort == 0) {
                        value = LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled);
                    } else if (sort == 1) {
                        value = LocaleController.getString("ShowPics", R.string.ShowPics);
                    } else {
                        value = LocaleController.getString("ShowProfile", R.string.ShowProfile);
                    }
                    textCell.setTextAndValue(prefix + LocaleController.getString("ClickOnContactPic", R.string.ClickOnContactPic), value, true);
                } else if (i == PlusSettingsActivity.this.dialogsGroupPicClickRow) {
                    int sort = preferences.getInt("dialogsClickOnGroupPic", 0);
                    if (sort == 0) {
                        value = LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled);
                    } else if (sort == 1) {
                        value = LocaleController.getString("ShowPics", R.string.ShowPics);
                    } else {
                        value = LocaleController.getString("ShowProfile", R.string.ShowProfile);
                    }
                    textCell.setTextAndValue(prefix + LocaleController.getString("ClickOnGroupPic", R.string.ClickOnGroupPic), value, true);
                } else if (i == PlusSettingsActivity.this.toastNotificationSizeRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("ToastNotificationSize", R.string.ToastNotificationSize), String.format("%d", new Object[]{Integer.valueOf(Theme.plusToastNotificationSize)}), true);
                } else if (i == PlusSettingsActivity.this.toastNotificationPaddingRow) {
                    textCell.setTextAndValue(prefix + LocaleController.getString("ToastNotificationPadding", R.string.ToastNotificationPadding), String.format("%d", new Object[]{Integer.valueOf(Theme.plusToastNotificationPadding)}), true);
                } else if (i == PlusSettingsActivity.this.toastNotificationPositionRow) {
                    int sort = Theme.plusToastNotificationPosition;
                    if (sort == 0) {
                        value = LocaleController.getString("Left", R.string.Left);
                    } else if (sort == 1) {
                        value = LocaleController.getString("Center", R.string.Center);
                    } else {
                        value = LocaleController.getString("Right", R.string.Right);
                    }
                    textCell.setTextAndValue(prefix + LocaleController.getString("ToastNotificationPosition", R.string.ToastNotificationPosition), value, true);
                } else if (i == PlusSettingsActivity.this.chatsToLoadRow) {
                    String title = "Chats to load";
                    int value2 = preferences.getInt("chatsToLoad", 100);
                    if (value2 == 50) {
                        textCell.setTextAndValue(title, "50", true);
                    } else if (value2 == 100) {
                        textCell.setTextAndValue(title, "100", true);
                    } else if (value2 == 200) {
                        textCell.setTextAndValue(title, "200", true);
                    } else if (value2 == 300) {
                        textCell.setTextAndValue(title, "300", true);
                    } else if (value2 == 400) {
                        textCell.setTextAndValue(title, "400", true);
                    } else if (value2 == 500) {
                        textCell.setTextAndValue(title, "500", true);
                    } else if (value2 == 750) {
                        textCell.setTextAndValue(title, "750", true);
                    } else if (value2 == 1000) {
                        textCell.setTextAndValue(title, "1000", true);
                    } else if (value2 == 1500) {
                        textCell.setTextAndValue(title, "1500", true);
                    } else if (value2 == 2000) {
                        textCell.setTextAndValue(title, "2000", true);
                    } else if (value2 == 1000000) {
                        textCell.setTextAndValue(title, "All", true);
                    }
                }
                if (i == PlusSettingsActivity.this.dialogsManageTabsRow) {
                    textCell.setText(prefix + LocaleController.getString("SortTabs", R.string.SortTabs), true);
                }
            } else if (type == 3) {
                if (view == null) {
                    shadowSectionCell = new TextCheckCell(this.mContext);
                }
                TextCheckCell textCell2 = (TextCheckCell) view;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                if (i == PlusSettingsActivity.this.disableAudioStopRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DisableAudioStop", R.string.DisableAudioStop), preferences.getBoolean("disableAudioStop", false), true);
                } else if (i == PlusSettingsActivity.this.disableMessageClickRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DisableMessageClick", R.string.DisableMessageClick), preferences.getBoolean("disableMessageClick", false), true);
                } else if (i == PlusSettingsActivity.this.chatDirectShareReplies) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DirectShareReplies", R.string.DirectShareReplies), preferences.getBoolean("directShareReplies", false), true);
                } else if (i == PlusSettingsActivity.this.chatDirectShareToMenu) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DirectShareToMenu", R.string.DirectShareToMenu), preferences.getBoolean("directShareToMenu", false), true);
                } else if (i == PlusSettingsActivity.this.chatDirectShareFavsFirst) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DirectShareShowFavsFirst", R.string.DirectShareShowFavsFirst), preferences.getBoolean("directShareFavsFirst", false), true);
                } else if (i == PlusSettingsActivity.this.chatShowEditedMarkRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowEditedMark", R.string.ShowEditedMark), preferences.getBoolean("showEditedMark", true), true);
                } else if (i == PlusSettingsActivity.this.chatShowDateToastRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowDateToast", R.string.ShowDateToast), preferences.getBoolean("showDateToast", true), true);
                } else if (i == PlusSettingsActivity.this.chatHideLeftGroupRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideLeftGroup", R.string.HideLeftGroup), preferences.getBoolean("hideLeftGroup", false), true);
                } else if (i == PlusSettingsActivity.this.chatHideJoinedGroupRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideJoinedGroup", R.string.HideJoinedGroup), preferences.getBoolean("hideJoinedGroup", false), true);
                } else if (i == PlusSettingsActivity.this.chatHideBotKeyboardRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideBotKeyboard", R.string.HideBotKeyboard), preferences.getBoolean("hideBotKeyboard", false), true);
                } else if (i == PlusSettingsActivity.this.keepOriginalFilenameRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("KeepOriginalFilename", R.string.KeepOriginalFilename), preferences.getBoolean("keepOriginalFilename", false), false);
                } else if (i == PlusSettingsActivity.this.showAndroidEmojiRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowAndroidEmoji", R.string.ShowAndroidEmoji), preferences.getBoolean("showAndroidEmoji", false), true);
                } else if (i == PlusSettingsActivity.this.useDeviceFontRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("UseDeviceFont", R.string.UseDeviceFont), preferences.getBoolean("useDeviceFont", false), true);
                } else if (i == PlusSettingsActivity.this.dialogsHideTabsCheckRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideTabs", R.string.HideTabs), Theme.plusHideTabs, true);
                } else if (i == PlusSettingsActivity.this.dialogsDisableTabsAnimationCheckRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DisableTabsAnimation", R.string.DisableTabsAnimation), Theme.plusDisableTabsAnimation, true);
                } else if (i == PlusSettingsActivity.this.dialogsTabsTextModeRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowTabTitle", R.string.ShowTabTitle), Theme.plusTabTitlesMode, true);
                } else if (i == PlusSettingsActivity.this.dialogsExpandTabsRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("FitTabsToScreen", R.string.FitTabsToScreen), Theme.plusTabsShouldExpand, true);
                } else if (i == PlusSettingsActivity.this.dialogsTabsToBottomRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("TabsToBottom", R.string.TabsToBottom), preferences.getBoolean("tabsToBottom", false), true);
                } else if (i == PlusSettingsActivity.this.dialogsDisableTabsScrollingRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DisableTabsScrolling", R.string.DisableTabsScrolling), Theme.plusDisableTabsScrolling, true);
                } else if (i == PlusSettingsActivity.this.dialogsHideSelectedTabIndicator) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideSelectedTabIndicator", R.string.HideSelectedTabIndicator), preferences.getBoolean("hideSelectedTabIndicator", false), true);
                } else if (i == PlusSettingsActivity.this.dialogsInfiniteTabsSwipe) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("InfiniteSwipe", R.string.InfiniteSwipe), Theme.plusInfiniteTabsSwipe, true);
                } else if (i == PlusSettingsActivity.this.dialogsHideTabsCounters) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideTabsCounters", R.string.HideTabsCounters), Theme.plusHideTabsCounters, true);
                } else if (i == PlusSettingsActivity.this.dialogsTabsCountersCountChats) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HeaderTabCounterCountChats", R.string.HeaderTabCounterCountChats), Theme.plusTabsCountersCountChats, true);
                } else if (i == PlusSettingsActivity.this.dialogsTabsCountersCountNotMuted) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HeaderTabCounterCountNotMuted", R.string.HeaderTabCounterCountNotMuted), Theme.plusTabsCountersCountNotMuted, true);
                } else if (i == PlusSettingsActivity.this.hideMobileNumberRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideMobile", R.string.HideMobile), Theme.plusHideMobile, true);
                } else if (i == PlusSettingsActivity.this.showUsernameRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowUsernameInMenu", R.string.ShowUsernameInMenu), Theme.plusShowUsername, true);
                } else if (i == PlusSettingsActivity.this.notificationInvertMessagesOrderRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("InvertMessageOrder", R.string.InvertMessageOrder), preferences.getBoolean("invertMessagesOrder", false), false);
                } else if (i == PlusSettingsActivity.this.chatSearchUserOnTwitterRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("SearchUserOnTwitter", R.string.SearchUserOnTwitter), preferences.getBoolean("searchOnTwitter", true), true);
                } else if (i == PlusSettingsActivity.this.chatShowPhotoQualityBarRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowPhotoQualityBar", R.string.ShowPhotoQualityBar), Theme.plusShowPhotoQualityBar, false);
                } else if (i == PlusSettingsActivity.this.showTypingToastNotificationRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowTypingToast", R.string.ShowTypingToast), Theme.plusShowTypingToast, false);
                } else if (i == PlusSettingsActivity.this.toastNotificationToBottomRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ToastNotificationToBottom", R.string.ToastNotificationToBottom), Theme.plusToastNotificationToBottom, true);
                } else if (i == PlusSettingsActivity.this.showOnlineToastNotificationRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowOnlineToast", R.string.ShowOnlineToast), Theme.plusShowOnlineToast, true);
                } else if (i == PlusSettingsActivity.this.showToastOnlyIfContactFavRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowOnlyIfContactFav", R.string.ShowOnlyIfContactFav), Theme.plusShowOnlyIfContactFav, true);
                } else if (i == PlusSettingsActivity.this.showOfflineToastNotificationRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowOfflineToast", R.string.ShowOfflineToast), Theme.plusShowOfflineToast, true);
                } else if (i == PlusSettingsActivity.this.hideNotificationsIfPlayingRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("OverrideNotificationsIfPlaying", R.string.OverrideNotificationsIfPlaying), Theme.plusHideNotificationsIfPlaying, true);
                } else if (i == PlusSettingsActivity.this.enableDirectReplyRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("EnableDirectReply", R.string.EnableDirectReply), Theme.plusEnableDirectReply, true);
                } else if (i == PlusSettingsActivity.this.chatShowQuickBarRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowQuickBar", R.string.ShowQuickBar), Theme.plusShowQuickBar, false);
                } else if (i == PlusSettingsActivity.this.chatVerticalQuickBarRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("VerticalQuickBar", R.string.VerticalQuickBar), Theme.plusVerticalQuickBar, false);
                } else if (i == PlusSettingsActivity.this.chatAlwaysBackToMainRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("AlwaysBackToMain", R.string.AlwaysBackToMain), Theme.plusAlwaysBackToMain, false);
                } else if (i == PlusSettingsActivity.this.chatDoNotCloseQuickBarRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DoNotCloseQuickBar", R.string.DoNotCloseQuickBar), Theme.plusDoNotCloseQuickBar, false);
                } else if (i == PlusSettingsActivity.this.chatHideQuickBarOnScrollRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideQuickBarOnScroll", R.string.HideQuickBarOnScroll), Theme.plusHideQuickBarOnScroll, false);
                } else if (i == PlusSettingsActivity.this.chatCenterQuickBarBtnRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("CenterQuickBarButton", R.string.CenterQuickBarButton), Theme.plusCenterQuickBarBtn, false);
                } else if (i == PlusSettingsActivity.this.chatShowMembersQuickBarRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("ShowMembersOnQuickBar", R.string.ShowMembersOnQuickBar), Theme.plusQuickBarShowMembers, true);
                } else if (i == PlusSettingsActivity.this.chatSaveToCloudQuoteRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("SaveToCloudQuote", R.string.SaveToCloudQuote), Theme.plusSaveToCloudQuote, true);
                } else if (i == PlusSettingsActivity.this.chatSwipeToReplyRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("SwipeToReply", R.string.SwipeToReply), Theme.plusSwipeToReply, true);
                } else if (i == PlusSettingsActivity.this.chatHideInstantCameraRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideInstantCamera", R.string.HideInstantCamera), Theme.plusHideInstantCamera, true);
                } else if (i == PlusSettingsActivity.this.chatDoNotHideStickersTabRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DoNotHideStickersTab", R.string.DoNotHideStickersTab), Theme.plusDoNotHideStickersTab, true);
                } else if (i == PlusSettingsActivity.this.chatPhotoViewerHideStatusBarRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("HideStatusBar", R.string.HideStatusBar), Theme.plusPhotoViewerHideStatusBar, false);
                } else if (i == PlusSettingsActivity.this.profileEnableGoToMsgRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("EnableGoToMessage", R.string.EnableGoToMessage), Theme.plusProfileEnableGoToMsg, true);
                } else if (i == PlusSettingsActivity.this.dialogsDoNotChangeHeaderTitleRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("DoNotChangeHeaderTitle", R.string.DoNotChangeHeaderTitle), Theme.plusDoNotChangeHeaderTitle, true);
                } else if (i == PlusSettingsActivity.this.chatDrawSingleBigEmojiRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("EmojiBigSize", R.string.EmojiBigSize), Theme.plusDrawSingleBigEmoji, false);
                } else if (i == PlusSettingsActivity.this.dialogsLimitTabsCountersRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("LimitTabsCounter", R.string.LimitTabsCounter), Theme.plusLimitTabsCounters, true);
                } else if (i == PlusSettingsActivity.this.chatMarkdownRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("Markdown", R.string.Markdown), Theme.plusEnableMarkdown, true);
                } else if (i == PlusSettingsActivity.this.moveVersionToSettingsRow) {
                    textCell2.setTextAndCheck(prefix + LocaleController.getString("MoveVersionToSettings", R.string.MoveVersionToSettings), Theme.plusMoveVersionToSettings, true);
                }
            } else if (type == 6) {
                if (view == null) {
                    shadowSectionCell = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell3 = (TextDetailSettingsCell) view;
                String text;
                String value;
                if (i == PlusSettingsActivity.this.dialogsTabsRow) {
                    value = prefix + LocaleController.getString("HideShowTabs", R.string.HideShowTabs);
                    text = "";
                    if (!Theme.plusHideAllTab) {
                        text = text + LocaleController.getString("All", R.string.All);
                    }
                    if (!Theme.plusHideUsersTab) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Users", R.string.Users);
                    }
                    if (!Theme.plusHideGroupsTab) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Groups", R.string.Groups);
                    }
                    if (!Theme.plusHideSuperGroupsTab) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("SuperGroups", R.string.SuperGroups);
                    }
                    if (!Theme.plusHideChannelsTab) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Channels", R.string.Channels);
                    }
                    if (!Theme.plusHideBotsTab) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Bots", R.string.Bots);
                    }
                    if (!Theme.plusHideFavsTab) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Favorites", R.string.Favorites);
                    }
                    if (text.length() == 0) {
                        text = "";
                    }
                    textCell3.setTextAndValue(value, text, true);
                } else if (i == PlusSettingsActivity.this.chatShowDirectShareBtn) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean showDSBtnUsers = preferences.getBoolean("showDSBtnUsers", false);
                    boolean showDSBtnGroups = preferences.getBoolean("showDSBtnGroups", true);
                    boolean showDSBtnSGroups = preferences.getBoolean("showDSBtnSGroups", true);
                    boolean showDSBtnChannels = preferences.getBoolean("showDSBtnChannels", true);
                    boolean showDSBtnBots = preferences.getBoolean("showDSBtnBots", true);
                    value = prefix + LocaleController.getString("ShowDirectShareButton", R.string.ShowDirectShareButton);
                    text = "";
                    if (showDSBtnUsers) {
                        text = text + LocaleController.getString("Users", R.string.Users);
                    }
                    if (showDSBtnGroups) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Groups", R.string.Groups);
                    }
                    if (showDSBtnSGroups) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("SuperGroups", R.string.SuperGroups);
                    }
                    if (showDSBtnChannels) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Channels", R.string.Channels);
                    }
                    if (showDSBtnBots) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Bots", R.string.Bots);
                    }
                    if (text.length() == 0) {
                        text = LocaleController.getString("Channels", R.string.UsernameEmpty);
                    }
                    textCell3.setTextAndValue(value, text, true);
                } else if (i == PlusSettingsActivity.this.profileSharedOptionsRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean hideMedia = preferences.getBoolean("hideSharedMedia", false);
                    boolean hideFiles = preferences.getBoolean("hideSharedFiles", false);
                    boolean hideMusic = preferences.getBoolean("hideSharedMusic", false);
                    boolean hideLinks = preferences.getBoolean("hideSharedLinks", false);
                    value = prefix + LocaleController.getString("SharedMedia", R.string.SharedMedia);
                    text = "";
                    if (!hideMedia) {
                        text = text + LocaleController.getString("SharedMediaTitle", R.string.SharedMediaTitle);
                    }
                    if (!hideFiles) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("DocumentsTitle", R.string.DocumentsTitle);
                    }
                    if (!hideMusic) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("AudioTitle", R.string.AudioTitle);
                    }
                    if (!hideLinks) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("LinksTitle", R.string.LinksTitle);
                    }
                    if (text.length() == 0) {
                        text = "";
                    }
                    textCell3.setTextAndValue(value, text, true);
                } else if (i == PlusSettingsActivity.this.showMySettingsRow) {
                    int FLAGS = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getInt("showMySettings", 0);
                    boolean showVersion = (FLAGS & 1) == 1;
                    boolean showLanguage = (FLAGS & 2) == 2;
                    value = prefix + LocaleController.getString("ShowMySettings", R.string.ShowMySettings);
                    text = "";
                    if (showVersion) {
                        text = text + LocaleController.getString("PlusVersion", R.string.PlusVersion);
                    }
                    if (showLanguage) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("Language", R.string.Language);
                    }
                    if (text.length() == 0) {
                        text = "";
                    }
                    textCell3.setTextAndValue(value, text, true);
                } else if (i == PlusSettingsActivity.this.savePlusSettingsRow) {
                    textCell3.setMultilineDetail(true);
                    textCell3.setTextAndValue(LocaleController.getString("SaveSettings", R.string.SaveSettings), LocaleController.getString("SaveSettingsSum", R.string.SaveSettingsSum) + " (" + LocaleController.getString("AlsoFavorites", R.string.AlsoFavorites) + ")", true);
                } else if (i == PlusSettingsActivity.this.restorePlusSettingsRow) {
                    textCell3.setMultilineDetail(true);
                    textCell3.setTextAndValue(LocaleController.getString("RestoreSettings", R.string.RestoreSettings), LocaleController.getString("RestoreSettingsSum", R.string.RestoreSettingsSum), true);
                } else if (i == PlusSettingsActivity.this.resetPlusSettingsRow) {
                    textCell3.setMultilineDetail(true);
                    textCell3.setTextAndValue(LocaleController.getString("ResetSettings", R.string.ResetSettings), LocaleController.getString("ResetSettingsSum", R.string.ResetSettingsSum), false);
                }
            } else if (type == 7) {
                if (view == null) {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == PlusSettingsActivity.this.keepOriginalFilenameDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("KeepOriginalFilenameHelp", R.string.KeepOriginalFilenameHelp));
                    view.setBackgroundResource(R.drawable.greydivider);
                } else if (i == PlusSettingsActivity.this.showOnlineToastNotificationDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("OnlineToastHelp", R.string.OnlineToastHelp));
                    view.setBackgroundResource(R.drawable.greydivider);
                }
            }
            if (view != null) {
                view.setBackgroundColor(Theme.usePlusTheme ? Theme.prefBGColor : Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == PlusSettingsActivity.this.messagesSectionRow || i == PlusSettingsActivity.this.profileSectionRow || i == PlusSettingsActivity.this.drawerSectionRow || i == PlusSettingsActivity.this.privacySectionRow || i == PlusSettingsActivity.this.mediaDownloadSection || i == PlusSettingsActivity.this.dialogsSectionRow || i == PlusSettingsActivity.this.notificationSectionRow || i == PlusSettingsActivity.this.toastNotificationSectionRow || i == PlusSettingsActivity.this.plusSettingsSectionRow) {
                return 0;
            }
            if (i == PlusSettingsActivity.this.settingsSectionRow2 || i == PlusSettingsActivity.this.messagesSectionRow2 || i == PlusSettingsActivity.this.profileSectionRow2 || i == PlusSettingsActivity.this.drawerSectionRow2 || i == PlusSettingsActivity.this.privacySectionRow2 || i == PlusSettingsActivity.this.mediaDownloadSection2 || i == PlusSettingsActivity.this.dialogsSectionRow2 || i == PlusSettingsActivity.this.notificationSection2Row || i == PlusSettingsActivity.this.toastNotificationSection2Row || i == PlusSettingsActivity.this.plusSettingsSectionRow2) {
                return 1;
            }
            if (i == PlusSettingsActivity.this.disableAudioStopRow || i == PlusSettingsActivity.this.disableMessageClickRow || i == PlusSettingsActivity.this.dialogsHideTabsCheckRow || i == PlusSettingsActivity.this.dialogsDisableTabsAnimationCheckRow || i == PlusSettingsActivity.this.dialogsInfiniteTabsSwipe || i == PlusSettingsActivity.this.dialogsHideTabsCounters || i == PlusSettingsActivity.this.dialogsTabsCountersCountChats || i == PlusSettingsActivity.this.dialogsTabsCountersCountNotMuted || i == PlusSettingsActivity.this.showAndroidEmojiRow || i == PlusSettingsActivity.this.useDeviceFontRow || i == PlusSettingsActivity.this.keepOriginalFilenameRow || i == PlusSettingsActivity.this.hideMobileNumberRow || i == PlusSettingsActivity.this.showUsernameRow || i == PlusSettingsActivity.this.chatDirectShareToMenu || i == PlusSettingsActivity.this.chatDirectShareReplies || i == PlusSettingsActivity.this.chatDirectShareFavsFirst || i == PlusSettingsActivity.this.chatShowEditedMarkRow || i == PlusSettingsActivity.this.chatShowDateToastRow || i == PlusSettingsActivity.this.chatHideLeftGroupRow || i == PlusSettingsActivity.this.chatHideJoinedGroupRow || i == PlusSettingsActivity.this.chatHideBotKeyboardRow || i == PlusSettingsActivity.this.notificationInvertMessagesOrderRow || i == PlusSettingsActivity.this.chatSearchUserOnTwitterRow || i == PlusSettingsActivity.this.chatShowPhotoQualityBarRow || i == PlusSettingsActivity.this.dialogsTabsTextModeRow || i == PlusSettingsActivity.this.dialogsExpandTabsRow || i == PlusSettingsActivity.this.dialogsDisableTabsScrollingRow || i == PlusSettingsActivity.this.dialogsTabsToBottomRow || i == PlusSettingsActivity.this.dialogsHideSelectedTabIndicator || i == PlusSettingsActivity.this.showTypingToastNotificationRow || i == PlusSettingsActivity.this.toastNotificationToBottomRow || i == PlusSettingsActivity.this.showOnlineToastNotificationRow || i == PlusSettingsActivity.this.showOfflineToastNotificationRow || i == PlusSettingsActivity.this.showToastOnlyIfContactFavRow || i == PlusSettingsActivity.this.enableDirectReplyRow || i == PlusSettingsActivity.this.chatShowQuickBarRow || i == PlusSettingsActivity.this.chatVerticalQuickBarRow || i == PlusSettingsActivity.this.chatAlwaysBackToMainRow || i == PlusSettingsActivity.this.chatDoNotCloseQuickBarRow || i == PlusSettingsActivity.this.chatHideQuickBarOnScrollRow || i == PlusSettingsActivity.this.chatCenterQuickBarBtnRow || i == PlusSettingsActivity.this.chatShowMembersQuickBarRow || i == PlusSettingsActivity.this.chatSaveToCloudQuoteRow || i == PlusSettingsActivity.this.chatSwipeToReplyRow || i == PlusSettingsActivity.this.hideNotificationsIfPlayingRow || i == PlusSettingsActivity.this.chatHideInstantCameraRow || i == PlusSettingsActivity.this.chatDoNotHideStickersTabRow || i == PlusSettingsActivity.this.chatPhotoViewerHideStatusBarRow || i == PlusSettingsActivity.this.profileEnableGoToMsgRow || i == PlusSettingsActivity.this.dialogsDoNotChangeHeaderTitleRow || i == PlusSettingsActivity.this.chatDrawSingleBigEmojiRow || i == PlusSettingsActivity.this.dialogsLimitTabsCountersRow || i == PlusSettingsActivity.this.chatMarkdownRow || i == PlusSettingsActivity.this.moveVersionToSettingsRow) {
                return 3;
            }
            if (i == PlusSettingsActivity.this.emojiPopupSize || i == PlusSettingsActivity.this.dialogsTabsTextSizeRow || i == PlusSettingsActivity.this.dialogsTabsHeightRow || i == PlusSettingsActivity.this.dialogsPicClickRow || i == PlusSettingsActivity.this.dialogsGroupPicClickRow || i == PlusSettingsActivity.this.chatPhotoQualityRow || i == PlusSettingsActivity.this.toastNotificationSizeRow || i == PlusSettingsActivity.this.toastNotificationPaddingRow || i == PlusSettingsActivity.this.toastNotificationPositionRow || i == PlusSettingsActivity.this.chatsToLoadRow || i == PlusSettingsActivity.this.dialogsManageTabsRow) {
                return 2;
            }
            if (i == PlusSettingsActivity.this.dialogsTabsRow || i == PlusSettingsActivity.this.chatShowDirectShareBtn || i == PlusSettingsActivity.this.profileSharedOptionsRow || i == PlusSettingsActivity.this.savePlusSettingsRow || i == PlusSettingsActivity.this.restorePlusSettingsRow || i == PlusSettingsActivity.this.resetPlusSettingsRow || i == PlusSettingsActivity.this.showMySettingsRow) {
                return 6;
            }
            if (i == PlusSettingsActivity.this.keepOriginalFilenameDetailRow || i == PlusSettingsActivity.this.showOnlineToastNotificationDetailRow) {
                return 7;
            }
            return 2;
        }

        public int getViewTypeCount() {
            return 8;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.refreshTabs);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSectionRow2 = i;
        if (VERSION.SDK_INT >= 19) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.showAndroidEmojiRow = i;
        } else {
            this.showAndroidEmojiRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.useDeviceFontRow = i;
        this.chatsToLoadRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsHideTabsCheckRow = i;
        this.dialogsTabsRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsManageTabsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsTabsHeightRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsTabsTextModeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsTabsTextSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsExpandTabsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsTabsToBottomRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsDisableTabsScrollingRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsDisableTabsAnimationCheckRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsInfiniteTabsSwipe = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsHideTabsCounters = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsTabsCountersCountNotMuted = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsTabsCountersCountChats = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsLimitTabsCountersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsHideSelectedTabIndicator = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsDoNotChangeHeaderTitleRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsPicClickRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dialogsGroupPicClickRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emojiPopupSize = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.disableAudioStopRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.disableMessageClickRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatShowDirectShareBtn = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatDirectShareReplies = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatDirectShareToMenu = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatDirectShareFavsFirst = i;
        this.chatShowEditedMarkRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatHideLeftGroupRow = i;
        this.chatHideJoinedGroupRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatHideBotKeyboardRow = i;
        this.chatShowDateToastRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatSearchUserOnTwitterRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatShowPhotoQualityBarRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatPhotoQualityRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatShowQuickBarRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatVerticalQuickBarRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatAlwaysBackToMainRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatDoNotCloseQuickBarRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatHideQuickBarOnScrollRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatCenterQuickBarBtnRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatShowMembersQuickBarRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatSaveToCloudQuoteRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatSwipeToReplyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatHideInstantCameraRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatDoNotHideStickersTabRow = i;
        this.chatMarkdownRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatDrawSingleBigEmojiRow = i;
        this.chatPhotoViewerHideStatusBarRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.drawerSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.drawerSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showUsernameRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.moveVersionToSettingsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.profileSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.profileSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.profileSharedOptionsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.profileEnableGoToMsgRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.hideNotificationsIfPlayingRow = i;
        this.notificationInvertMessagesOrderRow = -1;
        this.enableDirectReplyRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.toastNotificationSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.toastNotificationSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showTypingToastNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showOnlineToastNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.showOnlineToastNotificationDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.toastNotificationToBottomRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.toastNotificationPositionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.toastNotificationSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.toastNotificationPaddingRow = i;
        this.showToastOnlyIfContactFavRow = -1;
        this.showOfflineToastNotificationRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.privacySectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.privacySectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.hideMobileNumberRow = i;
        if (UserConfig.getCurrentUser().username == null) {
            this.showMySettingsRow = -1;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.mediaDownloadSection = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.mediaDownloadSection2 = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.keepOriginalFilenameRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.keepOriginalFilenameDetailRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.plusSettingsSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.plusSettingsSectionRow2 = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.savePlusSettingsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.restorePlusSettingsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.resetPlusSettingsRow = i;
            this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("plusShowPrefix", true);
            MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
        } else {
            this.showMySettingsRow = -1;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.mediaDownloadSection = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.mediaDownloadSection2 = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.keepOriginalFilenameRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.keepOriginalFilenameDetailRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.plusSettingsSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.plusSettingsSectionRow2 = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.savePlusSettingsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.restorePlusSettingsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.resetPlusSettingsRow = i;
            this.showPrefix = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getBoolean("plusShowPrefix", true);
            MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.refreshTabs);
    }

    public View createView(Context context) {

        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setTitle(LocaleController.getString("PlusSettings", R.string.PlusSettings));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PlusSettingsActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean z;
                PlusSettingsActivity plusSettingsActivity = PlusSettingsActivity.this;
                if (PlusSettingsActivity.this.showPrefix) {
                    z = false;
                } else {
                    z = true;
                }
                plusSettingsActivity.showPrefix = z;
                ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putBoolean("plusShowPrefix", PlusSettingsActivity.this.showPrefix).apply();
                if (PlusSettingsActivity.this.listAdapter != null) {
                    PlusSettingsActivity.this.listAdapter.notifyDataSetChanged();
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
        this.listView.setAdapter(this.listAdapter);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, Theme.prefActionbarColor);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Builder builder;
                NumberPicker numberPicker;
                final NumberPicker view2;
                if (i == PlusSettingsActivity.this.emojiPopupSize) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("EmojiPopupSize", R.string.EmojiPopupSize));
                        numberPicker = new NumberPicker(PlusSettingsActivity.this.getParentActivity());
                        numberPicker.setMinValue(60);
                        numberPicker.setMaxValue(100);
                        numberPicker.setValue(ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getInt("emojiPopupSize", AndroidUtilities.isTablet() ? 65 : 60));
                        builder.setView(numberPicker);
                        view2 = numberPicker;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("emojiPopupSize", view2.getValue());
                                editor.apply();
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.chatPhotoQualityRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("PhotoQuality", R.string.PhotoQuality));
                        numberPicker = new NumberPicker(PlusSettingsActivity.this.getParentActivity());
                        numberPicker.setMinValue(1);
                        numberPicker.setMaxValue(100);
                        numberPicker.setValue(Theme.plusPhotoQuality);
                        builder.setView(numberPicker);
                        view2 = numberPicker;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("photoQuality", view2.getValue());
                                editor.apply();
                                Theme.plusPhotoQuality = view2.getValue();
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.showAndroidEmojiRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
//                    //editor = preferences.edit();
                    boolean enabled = preferences.getBoolean("showAndroidEmoji", false);
                    preferences.edit().putBoolean("showAndroidEmoji", !enabled);
                    ApplicationLoader.SHOW_ANDROID_EMOJI = !enabled;
                    if (ApplicationLoader.SHOW_ANDROID_EMOJI && Theme.plusDrawSingleBigEmoji) {
                        Theme.plusDrawSingleBigEmoji = false;
                        preferences.edit().putBoolean("drawSingleBigEmoji", false);
                    }
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        r42 = (TextCheckCell) view;
                        if (enabled) {
                            r35 = false;
                        } else {
                            r35 = true;
                        }
                        r42.setChecked(r35);
                    }
                } else if (i == PlusSettingsActivity.this.useDeviceFontRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
//                    //editor = preferences.edit();
                    boolean enabled = preferences.getBoolean("useDeviceFont", false);
                    preferences.edit().putBoolean("useDeviceFont", !enabled);
                    preferences.edit().apply();
                    ApplicationLoader.USE_DEVICE_FONT = !enabled;
                    AndroidUtilities.needRestart = true;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (PlusSettingsActivity.this.getParentActivity() != null) {
                                Toast.makeText(PlusSettingsActivity.this.getParentActivity(), LocaleController.getString("AppWillRestart", R.string.AppWillRestart), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    if (view instanceof TextCheckCell) {
                        r42 = (TextCheckCell) view;
                        if (enabled) {
                            r35 = false;
                        } else {
                            r35 = true;
                        }
                        r42.setChecked(r35);
                    }
                } else if (i == PlusSettingsActivity.this.disableAudioStopRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean send = preferences.getBoolean("disableAudioStop", false);
//                    //editor = preferences.edit();
                    preferences.edit().putBoolean("disableAudioStop", !send);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!send);
                    }
                } else if (i == PlusSettingsActivity.this.disableMessageClickRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean send = preferences.getBoolean("disableMessageClick", false);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("disableMessageClick", !send);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!send);
                    }
                } else if (i == PlusSettingsActivity.this.chatDirectShareReplies) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean send = preferences.getBoolean("directShareReplies", false);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("directShareReplies", !send);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!send);
                    }
                } else if (i == PlusSettingsActivity.this.chatDirectShareToMenu) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean send = preferences.getBoolean("directShareToMenu", false);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("directShareToMenu", !send);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!send);
                    }
                } else if (i == PlusSettingsActivity.this.chatDirectShareFavsFirst) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean send = preferences.getBoolean("directShareFavsFirst", false);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("directShareFavsFirst", !send);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!send);
                    }
                } else if (i == PlusSettingsActivity.this.chatShowEditedMarkRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean send = preferences.getBoolean("showEditedMark", true);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showEditedMark", !send);
                    preferences.edit().apply();
                    Theme.plusShowEditedMark = !send;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!send);
                    }
                } else if (i == PlusSettingsActivity.this.chatShowDateToastRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean show = preferences.getBoolean("showDateToast", true);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showDateToast", !show);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!show);
                    }
                } else if (i == PlusSettingsActivity.this.chatHideLeftGroupRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean hide = preferences.getBoolean("hideLeftGroup", false);
                    MessagesController.getInstance().hideLeftGroup = !hide;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideLeftGroup", !hide);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        r42 = (TextCheckCell) view;
                        if (hide) {
                            r35 = false;
                        } else {
                            r35 = true;
                        }
                        r42.setChecked(r35);
                    }
                } else if (i == PlusSettingsActivity.this.chatHideJoinedGroupRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean hide = preferences.getBoolean("hideJoinedGroup", false);
                    MessagesController.getInstance().hideJoinedGroup = !hide;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideJoinedGroup", !hide);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        r42 = (TextCheckCell) view;
                        if (hide) {
                            r35 = false;
                        } else {
                            r35 = true;
                        }
                        r42.setChecked(r35);
                    }
                } else if (i == PlusSettingsActivity.this.chatHideBotKeyboardRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean hide = preferences.getBoolean("hideBotKeyboard", false);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideBotKeyboard", !hide);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!hide);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsHideTabsCheckRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusHideTabs = !Theme.plusHideTabs;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideTabs", Theme.plusHideTabs);
                    preferences.edit().apply();
                    if (Theme.plusHideUsersTab && Theme.plusHideGroupsTab && Theme.plusHideSuperGroupsTab && Theme.plusHideChannelsTab && Theme.plusHideBotsTab && Theme.plusHideFavsTab && PlusSettingsActivity.this.listView != null) {
                        PlusSettingsActivity.this.listView.invalidateViews();
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(10));
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusHideTabs);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsTabsTextModeRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    r35 = !Theme.plusTabTitlesMode;
                    Theme.chatsTabTitlesMode = r35;
                    Theme.plusTabTitlesMode = r35;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("tabTitlesMode", Theme.plusTabTitlesMode);
                    preferences.edit().apply();
                    SharedPreferences.Editor editorTheme = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
                    editorTheme.putBoolean("chatsTabTitlesMode", Theme.plusTabTitlesMode);
                    editorTheme.apply();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(15));
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusTabTitlesMode);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsExpandTabsRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusTabsShouldExpand = !Theme.plusTabsShouldExpand;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("tabsShouldExpand", Theme.plusTabsShouldExpand);
                    preferences.edit().apply();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(15));
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusTabsShouldExpand);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsDoNotChangeHeaderTitleRow) {
                    Theme.plusDoNotChangeHeaderTitle = !Theme.plusDoNotChangeHeaderTitle;
                    SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                    editor.putBoolean("doNotChangeHeaderTitle", Theme.plusDoNotChangeHeaderTitle);
                    editor.apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusDoNotChangeHeaderTitle);
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(11));
                } else if (i == PlusSettingsActivity.this.dialogsDisableTabsScrollingRow) {
                    Theme.plusDisableTabsScrolling = !Theme.plusDisableTabsScrolling;
                    SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                    editor.putBoolean("disableTabsScrolling", Theme.plusDisableTabsScrolling);
                    editor.apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusDisableTabsScrolling);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsTabsToBottomRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    r35 = !Theme.plusTabsToBottom;
                    Theme.chatsTabsToBottom = r35;
                    Theme.plusTabsToBottom = r35;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("tabsToBottom", Theme.plusTabsToBottom);
                    preferences.edit().apply();
                    SharedPreferences.Editor editorTheme = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
                    editorTheme.putBoolean("chatsTabsToBottom", Theme.plusTabsToBottom);
                    editorTheme.apply();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(14));
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusTabsToBottom);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsHideSelectedTabIndicator) {
                    Theme.plusHideTabsSelector = !Theme.plusHideTabsSelector;
                    SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                    editor.putBoolean("hideSelectedTabIndicator", Theme.plusHideTabsSelector);
                    editor.apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusHideTabsSelector);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsDisableTabsAnimationCheckRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusDisableTabsAnimation = !Theme.plusDisableTabsAnimation;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("disableTabsAnimation", Theme.plusDisableTabsAnimation);
                    preferences.edit().apply();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(11));
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusDisableTabsAnimation);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsInfiniteTabsSwipe) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusInfiniteTabsSwipe = !Theme.plusInfiniteTabsSwipe;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("infiniteTabsSwipe", Theme.plusInfiniteTabsSwipe);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusInfiniteTabsSwipe);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsHideTabsCounters) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusHideTabsCounters = !Theme.plusHideTabsCounters;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideTabsCounters", Theme.plusHideTabsCounters);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusHideTabsCounters);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsLimitTabsCountersRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusLimitTabsCounters = !Theme.plusLimitTabsCounters;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("limitTabsCounters", Theme.plusLimitTabsCounters);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusLimitTabsCounters);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsTabsCountersCountChats) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusTabsCountersCountChats = !Theme.plusTabsCountersCountChats;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("tabsCountersCountChats", Theme.plusTabsCountersCountChats);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusTabsCountersCountChats);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsTabsCountersCountNotMuted) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusTabsCountersCountNotMuted = !Theme.plusTabsCountersCountNotMuted;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("tabsCountersCountNotMuted", Theme.plusTabsCountersCountNotMuted);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusTabsCountersCountNotMuted);
                    }
                } else if (i == PlusSettingsActivity.this.showUsernameRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusShowUsername = !Theme.plusShowUsername;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showUsername", Theme.plusShowUsername);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusShowUsername);
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                } else if (i == PlusSettingsActivity.this.moveVersionToSettingsRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusMoveVersionToSettings = !Theme.plusMoveVersionToSettings;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("moveVersionToSettings", Theme.plusMoveVersionToSettings);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusMoveVersionToSettings);
                    }
                } else if (i == PlusSettingsActivity.this.profileEnableGoToMsgRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusProfileEnableGoToMsg = !Theme.plusProfileEnableGoToMsg;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("profileEnableGoToMsg", Theme.plusProfileEnableGoToMsg);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusProfileEnableGoToMsg);
                    }
                } else if (i == PlusSettingsActivity.this.hideMobileNumberRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusHideMobile = !Theme.plusHideMobile;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideMobile", Theme.plusHideMobile);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusHideMobile);
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                } else if (i == PlusSettingsActivity.this.keepOriginalFilenameRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean keep = preferences.getBoolean("keepOriginalFilename", false);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("keepOriginalFilename", !keep);
                    preferences.edit().apply();
                    ApplicationLoader.KEEP_ORIGINAL_FILENAME = !keep;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!keep);
                    }
                } else if (i == PlusSettingsActivity.this.dialogsPicClickRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("ClickOnContactPic", R.string.ClickOnContactPic));
                        builder.setItems(new CharSequence[]{LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled), LocaleController.getString("ShowPics", R.string.ShowPics), LocaleController.getString("ShowProfile", R.string.ShowProfile)}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("dialogsClickOnPic", which);
                                editor.apply();
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.dialogsGroupPicClickRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("ClickOnGroupPic", R.string.ClickOnGroupPic));
                        builder.setItems(new CharSequence[]{LocaleController.getString("RowGradientDisabled", R.string.RowGradientDisabled), LocaleController.getString("ShowPics", R.string.ShowPics), LocaleController.getString("ShowProfile", R.string.ShowProfile)}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("dialogsClickOnGroupPic", which);
                                editor.apply();
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.dialogsTabsTextSizeRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("TabsTextSize", R.string.TabsTextSize));
                        numberPicker = new NumberPicker(PlusSettingsActivity.this.getParentActivity());
                        numberPicker.setMinValue(8);
                        numberPicker.setMaxValue(18);
                        numberPicker.setValue(Theme.plusTabsTextSize);
                        builder.setView(numberPicker);
                        view2 = numberPicker;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int value = view2.getValue();
                                Theme.chatsTabsTextSize = value;
                                Theme.plusTabsTextSize = value;
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("tabsTextSize", Theme.plusTabsTextSize);
                                editor.apply();
                                Editor editorTheme = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
                                editorTheme.putInt("chatsTabsTextSize", Theme.plusTabsTextSize);
                                editorTheme.apply();
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(15));
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.dialogsTabsHeightRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("TabsHeight", R.string.TabsHeight));
                        numberPicker = new NumberPicker(PlusSettingsActivity.this.getParentActivity());
                        numberPicker.setMinValue(30);
                        numberPicker.setMaxValue(48);
                        numberPicker.setValue(Theme.plusTabsHeight);
                        builder.setView(numberPicker);
                        view2 = numberPicker;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                Theme.plusTabsHeight = view2.getValue();
                                editor.putInt("tabsHeight", Theme.plusTabsHeight);
                                editor.apply();
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(12));
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.dialogsManageTabsRow) {
                    PlusSettingsActivity.this.presentFragment(new PlusManageTabsActivity());
                } else if (i == PlusSettingsActivity.this.dialogsTabsRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        PlusSettingsActivity.this.createTabsDialog(builder);
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(13));
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.profileSharedOptionsRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        PlusSettingsActivity.this.createSharedOptions(builder);
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.showMySettingsRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        PlusSettingsActivity.this.createMySettingsOptions(builder);
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                        final int FLAGS = preferences.getInt("showMySettings", 0);
//                        final SharedPreferences = preferences;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);

                                if (FLAGS != preferences.getInt("showMySettings", 0)) {
                                    PlusSettingsActivity.this.getUserAbout();
                                }
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.chatShowDirectShareBtn) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        PlusSettingsActivity.this.createDialog(builder, PlusSettingsActivity.this.chatShowDirectShareBtn);
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.notificationInvertMessagesOrderRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean scr = preferences.getBoolean("invertMessagesOrder", false);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("invertMessagesOrder", !scr);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!scr);
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                } else if (i == PlusSettingsActivity.this.hideNotificationsIfPlayingRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusHideNotificationsIfPlaying = !Theme.plusHideNotificationsIfPlaying;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideNotificationsIfPlaying", Theme.plusHideNotificationsIfPlaying);
                    preferences.edit().apply();
                    AndroidUtilities.playingAGame = false;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusHideNotificationsIfPlaying);
                    }
                } else if (i == PlusSettingsActivity.this.enableDirectReplyRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusEnableDirectReply = !Theme.plusEnableDirectReply;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("enableDirectReply", Theme.plusEnableDirectReply);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusEnableDirectReply);
                    }
                } else if (i == PlusSettingsActivity.this.chatShowQuickBarRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusShowQuickBar;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showQuickBar", !bol);
                    preferences.edit().apply();
                    Theme.plusShowQuickBar = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                    if (PlusSettingsActivity.this.listView != null) {
                        PlusSettingsActivity.this.listView.invalidateViews();
                    }
                } else if (i == PlusSettingsActivity.this.chatPhotoViewerHideStatusBarRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusPhotoViewerHideStatusBar = !Theme.plusPhotoViewerHideStatusBar;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("photoViewerHideStatusBar", Theme.plusPhotoViewerHideStatusBar);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusPhotoViewerHideStatusBar);
                    }
                    if (PlusSettingsActivity.this.listView != null) {
                        PlusSettingsActivity.this.listView.invalidateViews();
                    }
                } else if (i == PlusSettingsActivity.this.chatDrawSingleBigEmojiRow) {
                    if ((ApplicationLoader.SHOW_ANDROID_EMOJI || MessagesController.getInstance().useSystemEmoji) && !Theme.plusDrawSingleBigEmoji) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (PlusSettingsActivity.this.getParentActivity() != null) {
                                    Toast.makeText(PlusSettingsActivity.this.getParentActivity(), LocaleController.getString("EmojiBigSizeInfo", R.string.EmojiBigSizeInfo), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return;
                    }
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusDrawSingleBigEmoji = !Theme.plusDrawSingleBigEmoji;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("drawSingleBigEmoji", Theme.plusDrawSingleBigEmoji);
                    preferences.edit().apply();
                    MessagesController.getInstance().allowBigEmoji = Theme.plusDrawSingleBigEmoji;
                    SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                    editor.putBoolean("allowBigEmoji", Theme.plusDrawSingleBigEmoji);
                    editor.apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusDrawSingleBigEmoji);
                    }
                } else if (i == PlusSettingsActivity.this.chatMarkdownRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusEnableMarkdown = !Theme.plusEnableMarkdown;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("enableMarkdown", Theme.plusEnableMarkdown);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusEnableMarkdown);
                    }
                } else if (i == PlusSettingsActivity.this.chatDoNotHideStickersTabRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusDoNotHideStickersTab = !Theme.plusDoNotHideStickersTab;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("doNotHideStickersTab", Theme.plusDoNotHideStickersTab);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusDoNotHideStickersTab);
                    }
                    if (PlusSettingsActivity.this.listView != null) {
                        PlusSettingsActivity.this.listView.invalidateViews();
                    }
                } else if (i == PlusSettingsActivity.this.chatHideInstantCameraRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusHideInstantCamera = !Theme.plusHideInstantCamera;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideInstantCamera", Theme.plusHideInstantCamera);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusHideInstantCamera);
                    }
                    if (PlusSettingsActivity.this.listView != null) {
                        PlusSettingsActivity.this.listView.invalidateViews();
                    }
                } else if (i == PlusSettingsActivity.this.chatSwipeToReplyRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusSwipeToReply = !Theme.plusSwipeToReply;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("plusSwipeToReply", Theme.plusSwipeToReply);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusSwipeToReply);
                    }
                    if (PlusSettingsActivity.this.listView != null) {
                        PlusSettingsActivity.this.listView.invalidateViews();
                    }
                } else if (i == PlusSettingsActivity.this.chatVerticalQuickBarRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusVerticalQuickBar;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("verticalQuickBar", !bol);
                    preferences.edit().apply();
                    Theme.plusVerticalQuickBar = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                } else if (i == PlusSettingsActivity.this.chatAlwaysBackToMainRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusAlwaysBackToMain;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("alwaysBackToMain", !bol);
                    preferences.edit().apply();
                    Theme.plusAlwaysBackToMain = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                } else if (i == PlusSettingsActivity.this.chatDoNotCloseQuickBarRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusDoNotCloseQuickBar;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("doNotCloseQuickBar", !bol);
                    preferences.edit().apply();
                    Theme.plusDoNotCloseQuickBar = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                } else if (i == PlusSettingsActivity.this.chatHideQuickBarOnScrollRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusHideQuickBarOnScroll;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("hideQuickBarOnScroll", !bol);
                    preferences.edit().apply();
                    Theme.plusHideQuickBarOnScroll = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                } else if (i == PlusSettingsActivity.this.chatCenterQuickBarBtnRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusCenterQuickBarBtn;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("centerQuickBarBtn", !bol);
                    preferences.edit().apply();
                    Theme.plusCenterQuickBarBtn = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                } else if (i == PlusSettingsActivity.this.chatShowMembersQuickBarRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusQuickBarShowMembers;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("quickBarShowMembers", !bol);
                    preferences.edit().apply();
                    Theme.plusQuickBarShowMembers = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                } else if (i == PlusSettingsActivity.this.chatSearchUserOnTwitterRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean hide = preferences.getBoolean("searchOnTwitter", true);
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("searchOnTwitter", !hide);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!hide);
                    }
                } else if (i == PlusSettingsActivity.this.chatShowPhotoQualityBarRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    Theme.plusShowPhotoQualityBar = !Theme.plusShowPhotoQualityBar;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showPhotoQualityBar", Theme.plusShowPhotoQualityBar);
                    preferences.edit().apply();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(Theme.plusShowPhotoQualityBar);
                    }
                } else if (i == PlusSettingsActivity.this.chatSaveToCloudQuoteRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean bol = Theme.plusSaveToCloudQuote;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("saveToCloudQuote", !bol);
                    preferences.edit().apply();
                    Theme.plusSaveToCloudQuote = !bol;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!bol);
                    }
                } else if (i == PlusSettingsActivity.this.savePlusSettingsRow) {
                    View promptsView = LayoutInflater.from(PlusSettingsActivity.this.getParentActivity()).inflate(R.layout.editbox_dialog, null);
                    builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                    builder.setView(promptsView);
                    EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                    userInput.setHint(LocaleController.getString("EnterName", R.string.EnterName));
                    userInput.setHintTextColor(Color.BLACK); //TODO Multi Color
                    SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
                    userInput.getBackground().setColorFilter(themePrefs.getInt(Theme.pkey_dialogColor, themePrefs.getInt(Theme.pkey_themeColor, AndroidUtilities.defColor)), Mode.SRC_IN);
                    AndroidUtilities.clearCursorDrawable(userInput);
                    builder.setTitle(LocaleController.getString("SaveSettings", R.string.SaveSettings));
                    final EditText editText = userInput;
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!PlusSettingsActivity.this.saving) {
                                final String pName = editText.getText().toString();
                                if (pName.length() < 1) {
                                    Toast.makeText(PlusSettingsActivity.this.getParentActivity(), LocaleController.getString("NameTooShort", R.string.NameTooShort), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                PlusSettingsActivity.this.saving = true;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        PlusSettingsActivity.this.saving = false;
                                        if (PlusSettingsActivity.this.getParentActivity() != null) {
                                            String path = "/Telegram";
                                            Utilities.savePreferencesToSD(PlusSettingsActivity.this.getParentActivity(), path, "plusconfig.xml", pName + ".xml", true);
                                            Utilities.saveDBToSD(PlusSettingsActivity.this.getParentActivity(), path, "favourites", "favorites.db", true);
                                        }
                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    PlusSettingsActivity.this.showDialog(builder.create());
                } else if (i == PlusSettingsActivity.this.restorePlusSettingsRow) {
                    DocumentSelectActivity fragment = new DocumentSelectActivity();
//                    fragment.fileFilter = ".xml"; //TODO Multi
//                    fragment.arrayFilter = new String[]{".db"};
                    fragment.setDelegate(new DocumentSelectActivityDelegate() {
                        public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files) {
                            PlusSettingsActivity.this.restoreSettings((String) files.get(0));
                        }

                        public void startDocumentSelectActivity() {
                        }
                    });
                    PlusSettingsActivity.this.presentFragment(fragment);
                } else if (i == PlusSettingsActivity.this.resetPlusSettingsRow) {
                    builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                    builder.setTitle(LocaleController.getString("ResetSettings", R.string.ResetSettings));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!PlusSettingsActivity.this.reseting) {
                                PlusSettingsActivity.this.reseting = true;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        PlusSettingsActivity.this.reseting = false;
                                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                        editor.clear();
                                        editor.apply();
                                        if (PlusSettingsActivity.this.listView != null) {
                                            PlusSettingsActivity.this.listView.invalidateViews();
                                            PlusSettingsActivity.this.fixLayout();
                                        }
                                    }
                                });
                                AndroidUtilities.needRestart = true;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (PlusSettingsActivity.this.getParentActivity() != null) {
                                            Toast.makeText(PlusSettingsActivity.this.getParentActivity(), LocaleController.getString("AppWillRestart", R.string.AppWillRestart), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    PlusSettingsActivity.this.showDialog(builder.create());
                } else if (i == PlusSettingsActivity.this.showTypingToastNotificationRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean disable = Theme.plusShowTypingToast;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showTypingToast", !disable);
                    preferences.edit().apply();
                    Theme.plusShowTypingToast = !disable;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!disable);
                    }
                } else if (i == PlusSettingsActivity.this.showOnlineToastNotificationRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean disable = Theme.plusShowOnlineToast;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showOnlineToast", !disable);
                    preferences.edit().apply();
                    Theme.plusShowOnlineToast = !disable;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!disable);
                    }
                } else if (i == PlusSettingsActivity.this.showToastOnlyIfContactFavRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean disable = Theme.plusShowOnlyIfContactFav;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showOnlyIfContactFav", !disable);
                    preferences.edit().apply();
                    Theme.plusShowOnlyIfContactFav = !disable;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!disable);
                    }
                } else if (i == PlusSettingsActivity.this.showOfflineToastNotificationRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean disable = Theme.plusShowOfflineToast;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("showOfflineToast", !disable);
                    preferences.edit().apply();
                    Theme.plusShowOfflineToast = !disable;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!disable);
                    }
                } else if (i == PlusSettingsActivity.this.toastNotificationSizeRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("ToastNotificationSize", R.string.ToastNotificationSize));
                        numberPicker = new NumberPicker(PlusSettingsActivity.this.getParentActivity());
                        numberPicker.setMinValue(10);
                        numberPicker.setMaxValue(20);
                        numberPicker.setValue(Theme.plusToastNotificationSize);
                        builder.setView(numberPicker);
                        view2 = numberPicker;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("toastNotificationSize", view2.getValue());
                                editor.apply();
                                Theme.plusToastNotificationSize = view2.getValue();
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.showStatusNotifications, null, Boolean.valueOf(true));
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.toastNotificationPaddingRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("ToastNotificationPadding", R.string.ToastNotificationPadding));
                        numberPicker = new NumberPicker(PlusSettingsActivity.this.getParentActivity());
                        numberPicker.setMinValue(0);
                        numberPicker.setMaxValue(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                        numberPicker.setValue(Theme.plusToastNotificationPadding);
                        builder.setView(numberPicker);
                        view2 = numberPicker;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("toastNotificationPadding", view2.getValue());
                                editor.apply();
                                Theme.plusToastNotificationPadding = view2.getValue();
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.showStatusNotifications, null, Boolean.valueOf(true));
                            }
                        });
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.toastNotificationToBottomRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                    boolean disable = Theme.plusToastNotificationToBottom;
                    //editor = preferences.edit();
                    preferences.edit().putBoolean("toastNotificationToBottom", !disable);
                    preferences.edit().apply();
                    Theme.plusToastNotificationToBottom = !disable;
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!disable);
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.showStatusNotifications, null, Boolean.valueOf(true));
                } else if (i == PlusSettingsActivity.this.toastNotificationPositionRow) {
                    if (PlusSettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("ToastNotificationPosition", R.string.ToastNotificationPosition));
                        builder.setItems(new CharSequence[]{LocaleController.getString("Left", R.string.Left), LocaleController.getString("Center", R.string.Center), LocaleController.getString("Right", R.string.Right)}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                                editor.putInt("toastNotificationPosition", which);
                                editor.apply();
                                Theme.plusToastNotificationPosition = which;
                                if (PlusSettingsActivity.this.listView != null) {
                                    PlusSettingsActivity.this.listView.invalidateViews();
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.showStatusNotifications, null, Boolean.valueOf(true));
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        PlusSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == PlusSettingsActivity.this.chatsToLoadRow) {
                    builder = new Builder(PlusSettingsActivity.this.getParentActivity());
                    builder.setTitle("Chats to load");
                    builder.setItems(new CharSequence[]{"50", "100", "200", "300", "400", "500", "750", "1000", "1500", "2000", "All"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                            String param = "chatsToLoad";
                            int value = 100;
                            if (which == 0) {
                                value = 50;
                            } else if (which == 1) {
                                value = 100;
                            } else if (which == 2) {
                                value = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                            } else if (which == 3) {
                                value = 300;
                            } else if (which == 4) {
                                value = 400;
                            } else if (which == 5) {
                                value = 500;
                            } else if (which == 6) {
                                value = 750;
                            } else if (which == 7) {
                                value = 1000;
                            } else if (which == 8) {
                                value = ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED;
                            } else if (which == 9) {
                                value = 2000;
                            } else if (which == 10) {
                                value = 1000000;
                            }
                            Theme.plusChatsToLoad = value;
                            editor.putInt(param, value);
                            editor.commit();
                            if (PlusSettingsActivity.this.listView != null) {
                                PlusSettingsActivity.this.listView.invalidateViews();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    PlusSettingsActivity.this.showDialog(builder.create());
                }
            }
        });
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    private void restoreSettings(String xmlFile) {
        File file = new File(xmlFile);
        File favFile = null;
        final String favFilePath = file.getParentFile().toString() + "/favorites.db";
        if (!file.getName().contains("favorites.db")) {
            favFile = new File(favFilePath);
        }
        final boolean favExists = favFile != null && favFile.exists();
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("RestoreSettings", R.string.RestoreSettings));
        final String name = file.getName();
        builder.setMessage(file.getName());
        final String str = xmlFile;
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        String ext = name.split("\\.")[1];
                        if (ext == null) {
                            return;
                        }
                        if (ext.contains("xml")) {
                            if (Utilities.loadPrefFromSD(PlusSettingsActivity.this.getParentActivity(), str, "plusconfig") != 4) {
                                return;
                            }
                            if (favExists) {
                                PlusSettingsActivity.this.restoreSettings(favFilePath);
                            } else {
                                Utilities.restartApp();
                            }
                        } else if (ext.contains("db") && Utilities.loadDBFromSD(PlusSettingsActivity.this.getParentActivity(), str, "favourites") == 4) {
                            Utilities.restartApp();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private Builder createTabsDialog(Builder builder) {
        boolean z = true;
        builder.setTitle(LocaleController.getString("HideShowTabs", R.string.HideShowTabs));
        CharSequence[] charSequenceArr = new CharSequence[]{LocaleController.getString("All", R.string.All), LocaleController.getString("Users", R.string.Users), LocaleController.getString("Groups", R.string.Groups), LocaleController.getString("SuperGroups", R.string.SuperGroups), LocaleController.getString("Channels", R.string.Channels), LocaleController.getString("Bots", R.string.Bots), LocaleController.getString("Favorites", R.string.Favorites)};
        boolean[] zArr = new boolean[7];
        zArr[0] = !Theme.plusHideAllTab;
        zArr[1] = !Theme.plusHideUsersTab;
        zArr[2] = !Theme.plusHideGroupsTab;
        zArr[3] = !Theme.plusHideSuperGroupsTab;
        zArr[4] = !Theme.plusHideChannelsTab;
        zArr[5] = !Theme.plusHideBotsTab;
        if (Theme.plusHideFavsTab) {
            z = false;
        }
        zArr[6] = z;
        builder.setMultiChoiceItems(charSequenceArr, zArr, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(which));
            }
        });
        return builder;
    }

    private Builder createSharedOptions(Builder builder) {
        boolean z;
        boolean z2 = true;
        builder.setTitle(LocaleController.getString("SharedMedia", R.string.SharedMedia));
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
        boolean hideMedia = preferences.getBoolean("hideSharedMedia", false);
        boolean hideFiles = preferences.getBoolean("hideSharedFiles", false);
        boolean hideMusic = preferences.getBoolean("hideSharedMusic", false);
        boolean hideLinks = preferences.getBoolean("hideSharedLinks", false);
        CharSequence[] cs = new CharSequence[]{LocaleController.getString("SharedMediaTitle", R.string.SharedMediaTitle), LocaleController.getString("DocumentsTitle", R.string.DocumentsTitle), LocaleController.getString("AudioTitle", R.string.AudioTitle), LocaleController.getString("LinksTitle", R.string.LinksTitle)};
        boolean[] b = new boolean[4];
        if (hideMedia) {
            z = false;
        } else {
            z = true;
        }
        b[0] = z;
        b[1] = !hideFiles;
        b[2] = !hideMusic;
        if (hideLinks) {
            z2 = false;
        }
        b[3] = z2;
        builder.setMultiChoiceItems(cs, b, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                boolean z = true;
                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                String str;
                if (which == 0) {
                    str = "hideSharedMedia";
                    if (isChecked) {
                        z = false;
                    }
                    editor.putBoolean(str, z);
                } else if (which == 1) {
                    str = "hideSharedFiles";
                    if (isChecked) {
                        z = false;
                    }
                    editor.putBoolean(str, z);
                } else if (which == 2) {
                    str = "hideSharedMusic";
                    if (isChecked) {
                        z = false;
                    }
                    editor.putBoolean(str, z);
                } else if (which == 3) {
                    str = "hideSharedLinks";
                    if (isChecked) {
                        z = false;
                    }
                    editor.putBoolean(str, z);
                }
                editor.apply();
            }
        });
        return builder;
    }

    private Builder createMySettingsOptions(Builder builder) {
        boolean showVersion;
        boolean showLanguage;
        builder.setTitle(LocaleController.getString("ShowMySettings", R.string.ShowMySettings));
        int FLAGS = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getInt("showMySettings", 0);
        if ((FLAGS & 1) == 1) {
            showVersion = true;
        } else {
            showVersion = false;
        }
        if ((FLAGS & 2) == 2) {
            showLanguage = true;
        } else {
            showLanguage = false;
        }
        builder.setMultiChoiceItems(new CharSequence[]{LocaleController.getString("PlusVersion", R.string.PlusVersion), LocaleController.getString("Language", R.string.Language)}, new boolean[]{showVersion, showLanguage}, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
                int FLAGS = preferences.getInt("showMySettings", 0);
                int val = 0;
                if (which == 0) {
                    val = 1;
                } else if (which == 1) {
                    val = 2;
                }
                FLAGS = isChecked ? FLAGS + val : FLAGS - val;
                Editor editor = preferences.edit();
                editor.putInt("showMySettings", FLAGS);
                preferences.edit().apply();
            }
        });
        return builder;
    }

    public void getUserAbout() {
        String link = String.format("https://telegram.me/%s", new Object[]{UserConfig.getCurrentUser().username});
        this.userAbout = null;
        TL_messages_getWebPagePreview req = new TL_messages_getWebPagePreview();
        req.message = link;
        this.linkSearchRequestId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        PlusSettingsActivity.this.linkSearchRequestId = 0;
                        if (error == null && (response instanceof TL_messageMediaWebPage)) {
                            PlusSettingsActivity.this.foundWebPage = ((TL_messageMediaWebPage) response).webpage;
                            if (PlusSettingsActivity.this.foundWebPage.description != null) {
                                PlusSettingsActivity.this.userAbout = PlusSettingsActivity.this.foundWebPage.description;
                                PlusSettingsActivity.this.setUserAbout();
                            } else if (PlusSettingsActivity.this.pass != 1) {
                                PlusSettingsActivity.this.pass = 1;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        PlusSettingsActivity.this.getUserAbout();
                                    }
                                }, 500);
                            }
                        }
                    }
                });
            }
        });
        ConnectionsManager.getInstance().bindRequestToGuid(this.linkSearchRequestId, this.classGuid);
    }

    public void setUserAbout() {
        int startIndex = this.userAbout.lastIndexOf("\n");
        String result = null;
        if (!(startIndex == -1 || startIndex == this.userAbout.length())) {
            result = this.userAbout.substring(startIndex + 1);
        }
        int FLAGS = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getInt("showMySettings", 0);
        boolean showVersion = (FLAGS & 1) == 1;
        boolean showLanguage = (FLAGS & 2) == 2;
        String version = AndroidUtilities.getVersion();
        String status = null;
        if (showVersion) {
            status = version;
        }
        String lang = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getString("language", null);
        if (lang != null && showLanguage) {
            status = (status != null ? status + " " : "") + lang.toUpperCase();
        }
        if (status != null) {
            if (result == null || !status.equals(result)) {
                TL_account_updateProfile req = new TL_account_updateProfile();
                req.flags |= 4;
                req.about = (result == null ? this.userAbout : this.userAbout.substring(0, this.userAbout.lastIndexOf("\n"))) + "\n" + status;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error != null) {
                            if (response != null) {
                                MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), PlusSettingsActivity.this.classGuid, true);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        UserConfig.saveConfig(true);
                                    }
                                });
                            }
                        } else if (response != null) {
                            MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), PlusSettingsActivity.this.classGuid, true);
//                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */); //Todo Multi
                        }
                    }
                });
            }
        }
    }

    private Builder createDialog(Builder builder, int i) {
        if (i == this.chatShowDirectShareBtn) {
            builder.setTitle(LocaleController.getString("ShowDirectShareButton", R.string.ShowDirectShareButton));
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
            boolean showDSBtnUsers = preferences.getBoolean("showDSBtnUsers", false);
            boolean showDSBtnGroups = preferences.getBoolean("showDSBtnGroups", true);
            boolean showDSBtnSGroups = preferences.getBoolean("showDSBtnSGroups", true);
            boolean showDSBtnChannels = preferences.getBoolean("showDSBtnChannels", true);
            boolean showDSBtnBots = preferences.getBoolean("showDSBtnBots", true);
            builder.setMultiChoiceItems(new CharSequence[]{LocaleController.getString("Users", R.string.Users), LocaleController.getString("Groups", R.string.Groups), LocaleController.getString("SuperGroups", R.string.SuperGroups), LocaleController.getString("Channels", R.string.Channels), LocaleController.getString("Bots", R.string.Bots)}, new boolean[]{showDSBtnUsers, showDSBtnGroups, showDSBtnSGroups, showDSBtnChannels, showDSBtnBots}, new OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
                    if (which == 0) {
                        editor.putBoolean("showDSBtnUsers", isChecked);
                        Theme.plusShowDSBtnUsers = isChecked;
                    } else if (which == 1) {
                        editor.putBoolean("showDSBtnGroups", isChecked);
                        Theme.plusShowDSBtnGroups = isChecked;
                    } else if (which == 2) {
                        editor.putBoolean("showDSBtnSGroups", isChecked);
                        Theme.plusShowDSBtnSGroups = isChecked;
                    } else if (which == 3) {
                        editor.putBoolean("showDSBtnChannels", isChecked);
                        Theme.plusShowDSBtnChannels = isChecked;
                    } else if (which == 4) {
                        editor.putBoolean("showDSBtnBots", isChecked);
                        Theme.plusShowDSBtnBots = isChecked;
                    }
                    editor.apply();
                }
            });
        }
        return builder;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (Theme.usePlusTheme) {
            updateTheme();
        }
        fixLayout();
    }

    private void updateTheme() {
        this.actionBar.setBackgroundColor(Theme.prefActionbarColor);
        this.actionBar.setTitleColor(Theme.prefActionbarTitleColor);
        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(Theme.prefActionbarIconsColor, Mode.MULTIPLY);
        this.actionBar.setBackButtonDrawable(back);
        getParentActivity().getResources().getDrawable(R.drawable.ic_ab_other).setColorFilter(Theme.prefActionbarIconsColor, Mode.MULTIPLY);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (PlusSettingsActivity.this.fragmentView != null) {
                        PlusSettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.refreshTabs && ((Integer) args[0]).intValue() == 15 && this.listView != null) {
            this.listView.invalidateViews();
        }
    }
}
