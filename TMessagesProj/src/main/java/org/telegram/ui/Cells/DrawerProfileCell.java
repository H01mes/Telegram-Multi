/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui.Cells;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class DrawerProfileCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private TextView nameTextView;
    private TextView phoneTextView;
    private ImageView shadowView;
    private CloudView cloudView;
    private Rect srcRect = new Rect();
    private Rect destRect = new Rect();
    private Paint paint = new Paint();
    private Integer currentColor;
    private Drawable cloudDrawable;
    private int lastCloudColor;

    private class CloudView extends View {

        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public CloudView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!Theme.isCustomTheme() || Theme.getCachedWallpaper() == null) {
                this.paint.setColor(Theme.usePlusTheme ? Theme.darkColor : Theme.getColor(Theme.key_chats_menuCloudBackgroundCats));
            } else {
                this.paint.setColor(Theme.usePlusTheme ? Theme.darkColor : Theme.getServiceMessageColor());
            }
            int newColor = Theme.getColor(Theme.key_chats_menuCloud);
            if (lastCloudColor != newColor) {
                cloudDrawable.setColorFilter(new PorterDuffColorFilter(lastCloudColor = Theme.getColor(Theme.key_chats_menuCloud), PorterDuff.Mode.MULTIPLY));
            }
            canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, AndroidUtilities.dp(34) / 2.0f, paint);
            int l = (getMeasuredWidth() - AndroidUtilities.dp(33)) / 2;
            int t = (getMeasuredHeight() - AndroidUtilities.dp(33)) / 2;
            cloudDrawable.setBounds(l, t, l + AndroidUtilities.dp(33), t + AndroidUtilities.dp(33));
            cloudDrawable.draw(canvas);
        }
    }

    public DrawerProfileCell(Context context) {
        super(context);
        float f;
        this.cloudDrawable = context.getResources().getDrawable(R.drawable.cloud);
        Drawable drawable = this.cloudDrawable;
        int color = Theme.getColor(Theme.key_chats_menuCloud);
        this.lastCloudColor = color;
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        this.shadowView = new ImageView(context);
        this.shadowView.setVisibility(INVISIBLE);
        this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.shadowView.setImageResource(R.drawable.bottom_shadow);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        int aSize = Theme.usePlusTheme ? Theme.drawerAvatarSize : 64;
        View view = this.avatarImageView;
        float f2 = (float) aSize;
        int i = Theme.drawerCenterAvatarCheck ? 81 : 83;
        if (Theme.drawerCenterAvatarCheck) {
            f = 0.0f;
        } else {
            f = 16.0f;
        }
        addView(view, LayoutHelper.createFrame(aSize, f2, i, f, 0.0f, 0.0f, 67.0f));
        final Activity activity = (Activity) context;
        this.avatarImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TLRPC.User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (user.photo != null && user.photo.photo_big != null) {
                    PhotoViewer.getInstance().setParentActivity(activity);
                    PhotoViewer.getInstance().openPhoto(user.photo.photo_big, (PhotoViewer.PhotoViewerProvider) DrawerProfileCell.this); //TODO Multi error in open profile
                }
            }
        });
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextSize(1, 15.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        if (Theme.drawerCenterAvatarCheck) {
            this.nameTextView.setGravity(17);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 28.0f));
        } else {
            this.nameTextView.setGravity(3);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 28.0f));
        }
        this.phoneTextView = new TextView(context);
        this.phoneTextView.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        if (Theme.drawerCenterAvatarCheck) {
            this.phoneTextView.setGravity(17);
            addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 9.0f));
        } else {
            this.phoneTextView.setGravity(3);
            addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 9.0f));
        }
        this.cloudView = new CloudView(context);
        addView(this.cloudView, LayoutHelper.createFrame(61, 61, 85));
    }

    public void refreshAvatar(int size, int radius) {
        removeView(this.avatarImageView);
        removeView(this.nameTextView);
        removeView(this.phoneTextView);
        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp((float) radius));
        if (Theme.drawerCenterAvatarCheck) {
            addView(this.avatarImageView, LayoutHelper.createFrame(size, (float) size, 81, 0.0f, 0.0f, 0.0f, 67.0f));
            this.nameTextView.setGravity(17);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 28.0f));
            this.phoneTextView.setGravity(17);
            addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 9.0f));
            return;
        }
        addView(this.avatarImageView, LayoutHelper.createFrame(size, (float) size, 83, 16.0f, 0.0f, 0.0f, 67.0f));
        this.nameTextView.setGravity(3);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 16.0f, 28.0f));
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 16.0f, 9.0f));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, MeasureSpec.EXACTLY));
        } else {
            try {
                super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), MeasureSpec.EXACTLY));
            } catch (Throwable e) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148.0f));
                FileLog.e(e);
            }
        }
        if (!Theme.plusHideMobile || Theme.plusShowUsername) {
            this.phoneTextView.setVisibility(0);
        } else {
            this.phoneTextView.setVisibility(8);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int color;
        Drawable backgroundDrawable = Theme.getCachedWallpaper();
        if (Theme.hasThemeKey(Theme.key_chats_menuTopShadow)) {
            color = Theme.getColor(Theme.key_chats_menuTopShadow);
        } else {
            color = Theme.getServiceMessageColor() | -16777216;
        }
        if (this.currentColor == null || this.currentColor.intValue() != color) {
            this.currentColor = Integer.valueOf(color);
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_chats_menuName));
        if (!Theme.isCustomTheme() || backgroundDrawable == null || (Theme.usePlusTheme && (!Theme.usePlusTheme || Theme.drawerHeaderBGCheck))) {
            this.shadowView.setVisibility(INVISIBLE);
            this.phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhoneCats));
            super.onDraw(canvas);
        } else {
            this.phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhone));
            ImageView imageView = this.shadowView;
            int i = (Theme.usePlusTheme && Theme.drawerHideBGShadowCheck) ? 4 : 0;
            if(i == 4)
            imageView.setVisibility(INVISIBLE);
            if(i == 0)
                imageView.setVisibility(VISIBLE);

            if (backgroundDrawable instanceof ColorDrawable) {
                backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                backgroundDrawable.draw(canvas);
            } else if (backgroundDrawable instanceof BitmapDrawable) {
                float scale;
                Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
                float scaleX = ((float) getMeasuredWidth()) / ((float) bitmap.getWidth());
                float scaleY = ((float) getMeasuredHeight()) / ((float) bitmap.getHeight());
                if (scaleX < scaleY) {
                    scale = scaleY;
                } else {
                    scale = scaleX;
                }
                int width = (int) (((float) getMeasuredWidth()) / scale);
                int height = (int) (((float) getMeasuredHeight()) / scale);
                int x = (bitmap.getWidth() - width) / 2;
                int y = (bitmap.getHeight() - height) / 2;
                this.srcRect.set(x, y, x + width, y + height);
                this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                try {
                    canvas.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
        if (Theme.usePlusTheme) {
            updateTheme();
        }
    }

    public void setUser(TLRPC.User user) {
        String value;
        if (user == null) {
            return;
        }
        TLRPC.FileLocation photo = null;
        if (user.photo != null) {
            photo = user.photo.photo_small;
        }
        nameTextView.setText(UserObject.getUserName(user));
        if (!Theme.plusShowUsername) {
            value = PhoneFormat.getInstance().format("+" + user.phone);
        } else if (user.username == null || user.username.length() == 0) {
            value = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
        } else {
            value = "@" + user.username;
        }
        this.phoneTextView.setText(value);
        phoneTextView.setText(PhoneFormat.getInstance().format("+" + user.phone));
        AvatarDrawable avatarDrawable = new AvatarDrawable(user);
        avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        avatarImageView.setImage(photo, "50_50", avatarDrawable);
        if (Theme.usePlusTheme) {
            updateTheme();
        }
    }

    public void updatePhotoAtIndex(int index) {
    }

    public boolean allowCaption() {
        return false;
    }

    public boolean scaleToFill() {
        return false;
    }

    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        if (fileLocation == null) {
            return null;
        }
        TLRPC.User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (user == null || user.photo == null || user.photo.photo_big == null) {
            return null;
        }
        TLRPC.FileLocation photoBig = user.photo.photo_big;
        if (photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
            return null;
        }
        int[] coords = new int[2];
        this.avatarImageView.getLocationInWindow(coords);
        PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
        object.viewX = coords[0];
        object.viewY = coords[1] - AndroidUtilities.statusBarHeight;
        object.parentView = this.avatarImageView;
        object.imageReceiver = this.avatarImageView.getImageReceiver();
        object.thumb = object.imageReceiver.getBitmap();
        object.size = -1;
        object.radius = this.avatarImageView.getImageReceiver().getRoundRadius();
        return object;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
    }

    public void willHidePhotoViewer() {
        this.avatarImageView.getImageReceiver().setVisible(true, true);
    }

    public boolean isPhotoChecked(int index) {
        return false;
    }

    public void setPhotoChecked(int index) {
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
    }

    public int getSelectedCount() {
        return 0;
    }

    private void updateTheme() {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        int tColor = themePrefs.getInt(Theme.pkey_themeColor, AndroidUtilities.defColor);
        setBackgroundColor(themePrefs.getInt("drawerHeaderColor", tColor));
        int value = themePrefs.getInt("drawerHeaderGradient", 0);
        if (value > 0) {
            GradientDrawable.Orientation go;
            switch (value) {
                case 2:
                    go = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
                case 3:
                    go = GradientDrawable.Orientation.TL_BR;
                    break;
                case 4:
                    go = GradientDrawable.Orientation.BL_TR;
                    break;
                default:
                    go = GradientDrawable.Orientation.TOP_BOTTOM;
                    break;
            }
            int gradColor = themePrefs.getInt("drawerHeaderGradientColor", tColor);
            int hColor = Color.BLACK;//TODO Multi colors
            setBackgroundDrawable(new GradientDrawable(go, new int[]{hColor, gradColor}));
        }
        this.nameTextView.setTextColor(themePrefs.getInt(Theme.pkey_drawerNameColor, -1));
        this.nameTextView.setTextSize(1, (float) themePrefs.getInt("drawerNameSize", 15));
        this.phoneTextView.setTextColor(themePrefs.getInt(Theme.pkey_drawerPhoneColor, Theme.lightColor));
        this.phoneTextView.setTextSize(1, (float) themePrefs.getInt("drawerPhoneSize", 13));
        if (!Theme.plusHideMobile || Theme.plusShowUsername) {
            this.phoneTextView.setVisibility(VISIBLE);
        } else {
            this.phoneTextView.setVisibility(VISIBLE);
        }
        TLRPC.User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        TLObject photo = null;
        if (!(user == null || user.photo == null || user.photo.photo_small == null)) {
            photo = user.photo.photo_small;
        }
        AvatarDrawable avatarDrawable = new AvatarDrawable(user);
        avatarDrawable.setColor(themePrefs.getInt("drawerAvatarColor", Theme.darkColor));
        int radius = AndroidUtilities.dp((float) themePrefs.getInt("drawerAvatarRadius", 32));
        avatarDrawable.setRadius(radius);
        this.avatarImageView.getImageReceiver().setRoundRadius(radius);
        this.avatarImageView.setImage(photo, "50_50", avatarDrawable);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cloudView.invalidate();
    }
}
