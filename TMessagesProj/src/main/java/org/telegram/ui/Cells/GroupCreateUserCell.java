/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateUserCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private GroupCreateCheckBox checkBox;
    private AvatarDrawable avatarDrawable;
    private TLRPC.User currentUser;
    private CharSequence currentName;
    private CharSequence currentStatus;
    private int nameColor = this.themePrefs.getInt("contactsNameColor", -14606047);
    private SimpleTextView nameTextView;
    private int onlineColor = this.themePrefs.getInt("contactsOnlineColor", Theme.darkColor);
    private int statusColor = this.themePrefs.getInt("contactsStatusColor", -5723992);
    private SimpleTextView statusTextView;
    private SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);


    private String lastName;
    private int lastStatus;
    private TLRPC.FileLocation lastAvatar;

    public GroupCreateUserCell(Context context, boolean needCheck) {

        super(context);
        int i;
        int i2;
        int i3 = 5;
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(Theme.usePlusTheme ? (float) this.themePrefs.getInt("contactsAvatarRadius", 32) : 24.0f));
        View view = this.avatarImageView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(50, 50.0f, i | 48, LocaleController.isRTL ? 0.0f : 11.0f, 11.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextColor(Theme.usePlusTheme ? this.nameColor : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(Theme.usePlusTheme ? this.themePrefs.getInt("contactsNameSize", 17) : 17);
        SimpleTextView simpleTextView = this.nameTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        simpleTextView.setGravity(i2 | 48);
        view = this.nameTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i | 48, LocaleController.isRTL ? 28.0f : 72.0f, 14.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(Theme.usePlusTheme ? this.themePrefs.getInt("contactsStatusSize", 16) : 16);
        simpleTextView = this.statusTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        simpleTextView.setGravity(i2 | 48);
        view = this.statusTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i | 48, LocaleController.isRTL ? 28.0f : 72.0f, 39.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        if (needCheck) {
            this.checkBox = new GroupCreateCheckBox(context);
            this.checkBox.setVisibility(0);
            View view2 = this.checkBox;
            if (!LocaleController.isRTL) {
                i3 = 3;
            }
            addView(view2, LayoutHelper.createFrame(24, 24.0f, i3 | 48, LocaleController.isRTL ? 0.0f : 41.0f, 41.0f, LocaleController.isRTL ? 41.0f : 0.0f, 0.0f));
        }
    }

    public void setUser(TLRPC.User user, CharSequence name, CharSequence status) {
        currentUser = user;
        currentStatus = status;
        currentName = name;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        checkBox.setChecked(checked, animated);
    }

    public TLRPC.User getUser() {
        return currentUser;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72), MeasureSpec.EXACTLY));
    }

    public void recycle() {
        avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int mask) {
        if (currentUser == null) {
            return;
        }
        TLRPC.FileLocation photo = null;
        String newName = null;
        if (currentUser.photo != null) {
            photo = currentUser.photo.photo_small;
        }

        if (mask != 0) {
            boolean continueUpdate = false;
            if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                if (lastAvatar != null && photo == null || lastAvatar == null && photo != null && lastAvatar != null && photo != null && (lastAvatar.volume_id != photo.volume_id || lastAvatar.local_id != photo.local_id)) {
                    continueUpdate = true;
                }
            }
            if (currentUser != null && currentStatus == null && !continueUpdate && (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {
                int newStatus = 0;
                if (currentUser.status != null) {
                    newStatus = currentUser.status.expires;
                }
                if (newStatus != lastStatus) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate && currentName == null && lastName != null && (mask & MessagesController.UPDATE_MASK_NAME) != 0) {
                newName = UserObject.getUserName(currentUser);
                if (!newName.equals(lastName)) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate) {
                return;
            }
        }

        avatarDrawable.setInfo(currentUser);
        lastStatus = currentUser.status != null ? currentUser.status.expires : 0;

        if (currentName != null) {
            lastName = null;
            nameTextView.setText(currentName, true);
        } else {
            lastName = newName == null ? UserObject.getUserName(currentUser) : newName;
            nameTextView.setText(lastName);
        }
        int i;
        if (currentStatus != null) {
            statusTextView.setText(currentStatus, true);
            statusTextView.setTag(Theme.key_groupcreate_offlineText);
            SimpleTextView simpleTextView = this.statusTextView;
            if (Theme.usePlusTheme) {
                i = this.statusColor;
            } else {
                i = Theme.getColor(Theme.key_groupcreate_offlineText);
            }
            simpleTextView.setTextColor(i);
        } else {
            if (currentUser.bot) {
                statusTextView.setTag(Theme.key_groupcreate_offlineText);
                this.statusTextView.setTextColor(Theme.usePlusTheme ? this.statusColor : Theme.getColor(Theme.key_groupcreate_offlineText));
                statusTextView.setText(LocaleController.getString("Bot", R.string.Bot));
            } else {
                if (currentUser.id == UserConfig.getClientUserId() || currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance().getCurrentTime() || MessagesController.getInstance().onlinePrivacy.containsKey(currentUser.id)) {
                    statusTextView.setTag(Theme.key_groupcreate_offlineText);
                    this.statusTextView.setTextColor(Theme.usePlusTheme ? this.onlineColor : Theme.getColor(Theme.key_groupcreate_onlineText));
                    statusTextView.setText(LocaleController.getString("Online", R.string.Online));
                } else {
                    statusTextView.setTag(Theme.key_groupcreate_offlineText);
                    this.statusTextView.setTextColor(Theme.usePlusTheme ? this.statusColor : Theme.getColor(Theme.key_groupcreate_offlineText));
                    statusTextView.setText(LocaleController.formatUserStatus(currentUser));
                }
            }
        }

        avatarImageView.setImage(photo, "50_50", avatarDrawable);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
