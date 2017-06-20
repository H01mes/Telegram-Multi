/*
 * This is the source code of Telegram for Android v. 3.x.x
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class MentionCell extends LinearLayout {

    private BackupImageView imageView;
    private TextView nameTextView;
    private TextView usernameTextView;
    private AvatarDrawable avatarDrawable;
    private int editTextColor;
    private SharedPreferences themePrefs;

    public MentionCell(Context context) {
        super(context);

        setOrientation(HORIZONTAL);

        avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12));

        imageView = new BackupImageView(context);
        imageView.setRoundRadius(AndroidUtilities.dp(14));
        this.themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        this.editTextColor = this.themePrefs.getInt(Theme.pkey_chatEditTextColor, -16777216);
        addView(imageView, LayoutHelper.createLinear(28, 28, 12, 4, 0, 0));

        nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.usePlusTheme ? this.editTextColor : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        nameTextView.setSingleLine(true);
        nameTextView.setGravity(Gravity.LEFT);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(nameTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 12, 0, 0, 0));

        usernameTextView = new TextView(context);
        this.usernameTextView.setTextColor(Theme.usePlusTheme ? Theme.chatEditTextIconsColor : Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));        usernameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        usernameTextView.setSingleLine(true);
        usernameTextView.setGravity(Gravity.LEFT);
        usernameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(usernameTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 12, 0, 8, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36), MeasureSpec.EXACTLY));
    }

    public void setUser(TLRPC.User user) {
        if (user == null) {
            nameTextView.setText("");
            usernameTextView.setText("");
            imageView.setImageDrawable(null);
            return;
        }
        avatarDrawable.setInfo(user);
        if (user.photo != null && user.photo.photo_small != null) {
            imageView.setImage(user.photo.photo_small, "50_50", avatarDrawable);
        } else {
            imageView.setImageDrawable(avatarDrawable);
        }
        nameTextView.setText(UserObject.getUserName(user));
        if (user.username != null) {
            usernameTextView.setText("@" + user.username);
        } else {
            usernameTextView.setText("");
        }
        imageView.setVisibility(VISIBLE);
        usernameTextView.setVisibility(VISIBLE);
    }

    public void setText(String text) {
        imageView.setVisibility(INVISIBLE);
        usernameTextView.setVisibility(INVISIBLE);
        nameTextView.setText(text);
    }

    public void setBotCommand(String command, String help, TLRPC.User user) {
        if (user != null) {
            imageView.setVisibility(VISIBLE);
            avatarDrawable.setInfo(user);
            if (user.photo != null && user.photo.photo_small != null) {
                imageView.setImage(user.photo.photo_small, "50_50", avatarDrawable);
            } else {
                imageView.setImageDrawable(avatarDrawable);
            }
        } else {
            imageView.setVisibility(INVISIBLE);
        }
        usernameTextView.setVisibility(VISIBLE);
        nameTextView.setText(command);
        usernameTextView.setText(Emoji.replaceEmoji(help, usernameTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20), false));
    }

    public void setIsDarkTheme(boolean isDarkTheme) {
        if (isDarkTheme) {
            this.nameTextView.setTextColor(Theme.usePlusTheme ? this.editTextColor : -1);
            this.usernameTextView.setTextColor(Theme.usePlusTheme ? Theme.chatEditTextIconsColor : Color.CYAN); //TODO Multi color
            return;
        }
        this.nameTextView.setTextColor(Theme.usePlusTheme ? this.editTextColor : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setTextColor(Theme.usePlusTheme ? Theme.chatEditTextIconsColor : Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
    }
}
