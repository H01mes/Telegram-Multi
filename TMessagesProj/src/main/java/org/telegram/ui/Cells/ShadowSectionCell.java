/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class ShadowSectionCell extends View {
    boolean bTheme;
    private int size = 12;

    public ShadowSectionCell(Context context) {
        super(context);
        setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        this.bTheme = true;
    }

    public void setSize(int value) {
        size = value;
    }

    public ShadowSectionCell(Context context, boolean theme) {
        super(context);
        setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        this.bTheme = theme;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(size), MeasureSpec.EXACTLY));
        if (Theme.usePlusTheme && this.bTheme) {
            setTheme();
        }
    }

    private void setTheme() {
        if (Theme.prefShadowColor == -986896) { //TODO Multi color
            setBackgroundResource(R.drawable.greydivider);
        } else {
            setBackgroundColor(Theme.prefShadowColor);
        }
    }
}
