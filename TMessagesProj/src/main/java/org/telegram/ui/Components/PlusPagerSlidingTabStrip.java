package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;

public class PlusPagerSlidingTabStrip extends HorizontalScrollView {
    private static final String TAG = "PlusPagerSlidingTab";
    private int btnBgRes;
    private int currentPage = 0;
    private int currentPosition = 0;
    private float currentPositionOffset = 0.0f;
    private LayoutParams defaultTabLayoutParams;
    private PlusScrollSlidingTabStripDelegate delegate;
    public OnPageChangeListener delegatePageListener;
    private int dividerColor = 436207616;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private Paint dividerPaint;
    private int dividerWidth = AndroidUtilities.dp(1.0f);
    private LayoutParams expandedTabLayoutParams;
    private int indicatorColor = -10066330;
    private int indicatorHeight = AndroidUtilities.dp(8.0f);
    private int lastScrollX = 0;
    private int layoutWidth;
    private final PageListener pageListener = new PageListener();
    private ViewPager pager;
    private Paint rectPaint;
    private int scrollOffset = AndroidUtilities.dp(20.0f);
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(15.0f);
    private int tabTextColor = Theme.chatsHeaderTabUnselectedIconColor;
    private int tabTextIconSelectedColor;
    private int tabTextIconUnselectedColor;
    private int tabTextSelectedColor = Theme.chatsHeaderTabIconColor;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = 1;
    private LinearLayout tabsContainer;
    private boolean textAllCaps = true;
    private int underlineColor = 436207616;
    private int underlineHeight = AndroidUtilities.dp(2.0f);

    public interface IconTabProvider {
        int getPageIconResId(int i);

        String getPageTitle(int i);
    }

    public interface PlusScrollSlidingTabStripDelegate {
        void onTabClick();

        void onTabLongClick(int i);

        void onTabsUpdated();
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentPosition = in.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.currentPosition);
        }
    }

    private class PageListener implements OnPageChangeListener {
        private PageListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            PlusPagerSlidingTabStrip.this.currentPosition = position;
            PlusPagerSlidingTabStrip.this.currentPositionOffset = positionOffset;
            if (Theme.plusTabTitlesMode) {
                PlusPagerSlidingTabStrip.this.scrollToChild2(position, positionOffset);
            } else {
                PlusPagerSlidingTabStrip.this.scrollToChild(position, (int) (((float) PlusPagerSlidingTabStrip.this.tabsContainer.getChildAt(position).getWidth()) * positionOffset));
            }
            PlusPagerSlidingTabStrip.this.invalidate();
            if (PlusPagerSlidingTabStrip.this.delegatePageListener != null) {
                PlusPagerSlidingTabStrip.this.delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                if (Theme.plusTabTitlesMode) {
                    PlusPagerSlidingTabStrip.this.scrollToChild2(PlusPagerSlidingTabStrip.this.pager.getCurrentItem(), 0.0f);
                } else {
                    PlusPagerSlidingTabStrip.this.scrollToChild(PlusPagerSlidingTabStrip.this.pager.getCurrentItem(), 0);
                }
            }
            if (PlusPagerSlidingTabStrip.this.delegatePageListener != null) {
                PlusPagerSlidingTabStrip.this.delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        public void onPageSelected(int position) {
            if (PlusPagerSlidingTabStrip.this.delegatePageListener != null) {
                PlusPagerSlidingTabStrip.this.delegatePageListener.onPageSelected(position);
            }
            PlusPagerSlidingTabStrip.this.changeTabsColor(position);
            PlusPagerSlidingTabStrip.this.currentPage = position;
        }
    }

    public void setDelegate(PlusScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public PlusPagerSlidingTabStrip(Context context) {
        super(context);
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.dividerPaint = new Paint();
        this.dividerPaint.setAntiAlias(true);
        this.dividerPaint.setStrokeWidth((float) this.dividerWidth);
        this.defaultTabLayoutParams = new LayoutParams(-2, -1);
        this.expandedTabLayoutParams = new LayoutParams(-2, -1, (int) 1.0f);
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(VERSION.SDK_INT >= 21 ? 16843868 : 16843534, outValue, true);
        this.btnBgRes = outValue.resourceId;
        this.layoutWidth = AndroidUtilities.displaySize.x;
        if (Theme.plusTabTitlesMode) {
            this.scrollOffset = this.layoutWidth / 2;
        }
        this.tabTextIconUnselectedColor = Theme.usePlusTheme ? Theme.chatsHeaderTabUnselectedIconColor : AndroidUtilities.getIntAlphaColor(Theme.getColor(Theme.key_actionBarDefaultIcon), 0.35f);
        this.tabTextIconSelectedColor = Theme.usePlusTheme ? Theme.chatsHeaderTabIconColor : Theme.getColor(Theme.key_actionBarDefaultIcon);
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(this.pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        if (this.tabCount >= 2) {
            for (int i = 0; i < this.tabCount; i++) {
                if (Theme.plusTabTitlesMode) {
                    addTextTabWithCounter(i, ((IconTabProvider) this.pager.getAdapter()).getPageTitle(i));
                } else {
                    addIconTabWithCounter(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconResId(i));
                }
            }
            updateTabStyles();
            getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (VERSION.SDK_INT < 16) {
                        PlusPagerSlidingTabStrip.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        PlusPagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    PlusPagerSlidingTabStrip.this.currentPosition = PlusPagerSlidingTabStrip.this.pager.getCurrentItem();
                    PlusPagerSlidingTabStrip.this.currentPage = PlusPagerSlidingTabStrip.this.currentPosition;
                    if (Theme.plusTabTitlesMode) {
                        PlusPagerSlidingTabStrip.this.scrollToChild2(PlusPagerSlidingTabStrip.this.currentPosition, 0.0f);
                    } else {
                        PlusPagerSlidingTabStrip.this.scrollToChild(PlusPagerSlidingTabStrip.this.currentPosition, 0);
                    }
                }
            });
        }
    }

    private void addTextTabWithCounter(int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setTypeface(Typeface.DEFAULT_BOLD);
        tab.setTextSize(1, (float) Theme.plusTabsTextSize);
        tab.setGravity(17);
        tab.setSingleLine();
        tab.setTextColor(position == this.pager.getCurrentItem() ? this.tabTextIconSelectedColor : this.tabTextIconUnselectedColor);
        if (this.textAllCaps && VERSION.SDK_INT >= 14) {
            tab.setAllCaps(true);
        }
        addTabWithCounter(position, tab);
    }

    private void addIconTabWithCounter(int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        tab.setColorFilter(position == this.pager.getCurrentItem() ? this.tabTextIconSelectedColor : this.tabTextIconUnselectedColor, Mode.SRC_IN);
        tab.setScaleType(ScaleType.CENTER);
        addTabWithCounter(position, tab);
    }

    public void addTabWithCounter(final int position, View view) {
        float f;
        float f2 = 4.0f;
        RelativeLayout tab = new RelativeLayout(getContext());
        tab.setFocusable(true);
        this.tabsContainer.addView(tab, Theme.plusTabsShouldExpand ? this.expandedTabLayoutParams : this.defaultTabLayoutParams);
        view.setBackgroundResource(this.btnBgRes);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (position == PlusPagerSlidingTabStrip.this.pager.getCurrentItem()) {
                    if (PlusPagerSlidingTabStrip.this.delegate != null) {
                        PlusPagerSlidingTabStrip.this.delegate.onTabClick();
                    }
                } else if (PlusPagerSlidingTabStrip.this.pager != null) {
                    PlusPagerSlidingTabStrip.this.pager.setCurrentItem(position);
                }
            }
        });
        view.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (PlusPagerSlidingTabStrip.this.delegate != null) {
                    PlusPagerSlidingTabStrip.this.delegate.onTabLongClick(position);
                }
                return true;
            }
        });
        tab.addView(view, LayoutHelper.createFrame(-1, -1.0f));
        tab.setSelected(position == this.currentPosition);
        TextView textView = new TextView(getContext());
        textView.setTextSize(1, (float) Theme.chatsTabCounterSize);
        textView.setTextColor(Theme.chatsTabCounterColor);
        textView.setGravity(17);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius((float) AndroidUtilities.dp(32.0f));
        textView.setBackgroundDrawable(shape);
        textView.setMinWidth(AndroidUtilities.dp(18.0f));
        if (Theme.chatsTabCounterSize > 10) {
            f = (float) (Theme.chatsTabCounterSize - 7);
        } else {
            f = 4.0f;
        }
        int dp = AndroidUtilities.dp(f);
        if (Theme.chatsTabCounterSize > 10) {
            f2 = (float) (Theme.chatsTabCounterSize - 7);
        }
        textView.setPadding(dp, 0, AndroidUtilities.dp(f2), 0);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.setMargins(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(5.0f));
        params.addRule(11);
        params.addRule(12);
        tab.addView(textView, params);
    }

    public void changeTabsColor(int position) {
        RelativeLayout frame = (RelativeLayout) this.tabsContainer.getChildAt(this.currentPage);
        if (frame != null) {
            try {
                View view = ((RelativeLayout) this.tabsContainer.getChildAt(position)).getChildAt(0);
                if (view instanceof ImageButton) {
                    ((ImageButton) frame.getChildAt(0)).setColorFilter(this.tabTextIconUnselectedColor, Mode.SRC_IN);
                    ((ImageButton) view).setColorFilter(this.tabTextIconSelectedColor, Mode.SRC_IN);
                } else if (view instanceof TextView) {
                    ((TextView) frame.getChildAt(0)).setTextColor(this.tabTextIconUnselectedColor);
                    ((TextView) view).setTextColor(this.tabTextIconSelectedColor);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public void updateCounter(int position, int count, boolean allMuted, boolean force) {
        float f = 4.0f;
        RelativeLayout frame = (RelativeLayout) this.tabsContainer.getChildAt(position);
        if (frame != null && frame.getChildCount() > 1) {
            TextView tv = (TextView) frame.getChildAt(1);
            if (tv != null) {
                int color;
                if (count <= 0 || Theme.plusHideTabsCounters) {
                    tv.setVisibility(INVISIBLE);
                } else {
                    tv.setVisibility(VISIBLE);
                    CharSequence format = (count < 10000 || !Theme.plusLimitTabsCounters) ? String.format("%d", new Object[]{Integer.valueOf(count)}) : "+9999";
                    tv.setText(format);
                    Drawable background = tv.getBackground();
                    color = allMuted ? Theme.usePlusTheme ? Theme.chatsTabCounterSilentBGColor : Theme.getColor(Theme.key_chats_unreadCounterMuted) : Theme.usePlusTheme ? Theme.chatsTabCounterBGColor : Theme.getColor(Theme.key_chats_unreadCounter);
                    background.setColorFilter(color, Mode.SRC_IN);
                }
                if (force) {
                    float f2;
                    tv.setTextSize(1, (float) Theme.chatsTabCounterSize);
                    tv.setTextColor(Theme.usePlusTheme ? Theme.chatsTabCounterColor : Theme.getColor(Theme.key_chats_unreadCounterText));
                    if (Theme.chatsTabCounterSize > 10) {
                        f2 = (float) (Theme.chatsTabCounterSize - 7);
                    } else {
                        f2 = 4.0f;
                    }
                    color = AndroidUtilities.dp(f2);
                    if (Theme.chatsTabCounterSize > 10) {
                        f = (float) (Theme.chatsTabCounterSize - 7);
                    }
                    tv.setPadding(color, 0, AndroidUtilities.dp(f), 0);
                }
            }
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View tab = this.tabsContainer.getChildAt(i);
            tab.setPadding(0, 0, 0, 0);
            if (!Theme.plusTabsShouldExpand) {
                if (tab.getLayoutParams() != this.defaultTabLayoutParams) {
                    tab.setLayoutParams(this.defaultTabLayoutParams);
                }
                View view = ((RelativeLayout) this.tabsContainer.getChildAt(i)).getChildAt(0);
                if (view != null) {
                    view.setPadding(this.tabPadding, 0, this.tabPadding, 0);
                }
            } else if (tab.getLayoutParams() != this.expandedTabLayoutParams) {
                tab.setLayoutParams(this.expandedTabLayoutParams);
            }
        }
        if (this.delegate != null) {
            this.delegate.onTabsUpdated();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (Theme.plusTabsShouldExpand && MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) { //TODO Multi exactly?
            this.tabsContainer.measure(1073741824 | getMeasuredWidth(), heightMeasureSpec);
        }
    }

    private void scrollToChild(int position, int offset) {
        if (this.tabCount != 0 && position < this.tabsContainer.getChildCount()) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft() + offset;
            if (position > 0 || offset > 0) {
                newScrollX -= this.scrollOffset;
            }
            if (newScrollX != this.lastScrollX) {
                this.lastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    private void scrollToChild2(int position, float offset) {
        if (this.tabCount != 0 && position < this.tabsContainer.getChildCount()) {
            int cellWidth = this.tabsContainer.getChildAt(position).getWidth();
            int newScrollX = this.lastScrollX;
            if (((double) offset) >= 0.01d || ((double) offset) <= -0.01d) {
                if (position + 1 <= this.tabCount - 1) {
                    newScrollX = (int) ((((float) this.tabsContainer.getChildAt(position).getLeft()) + ((((float) cellWidth) * (1.0f - offset)) / 2.0f)) + (((float) ((this.tabsContainer.getChildAt(position + 1).getWidth() / 2) + cellWidth)) * offset));
                } else {
                    newScrollX = (int) ((((float) this.tabsContainer.getChildAt(position).getLeft()) + ((((float) cellWidth) * (1.0f - offset)) / 2.0f)) + ((((float) (this.tabsContainer.getChildAt(position).getWidth() + cellWidth)) * offset) / 2.0f));
                }
            } else if (position + 1 <= this.tabCount - 1) {
                newScrollX = (int) (((float) (this.tabsContainer.getChildAt(position).getLeft() + (cellWidth / 2))) + (((float) ((this.tabsContainer.getChildAt(position + 1).getWidth() / 2) + cellWidth)) * offset));
            } else {
                newScrollX = (int) (((float) (this.tabsContainer.getChildAt(position).getLeft() + (cellWidth / 2))) + ((((float) (this.tabsContainer.getChildAt(position).getWidth() + cellWidth)) * offset) / 2.0f));
            }
            if (position >= 0 || ((double) offset) > 0.01d) {
                newScrollX -= this.scrollOffset;
            }
            if (newScrollX != this.lastScrollX) {
                this.lastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    private void enableShouldExpand() {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
        Theme.plusTabsShouldExpand = true;
        editor.putBoolean("tabsShouldExpand", true);
        editor.apply();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0 && this.currentPosition < this.tabCount) {
            if (Theme.plusTabsShouldExpand || !Theme.plusTabTitlesMode || this.tabsContainer.getChildAt(0).getWidth() <= this.tabsContainer.getWidth() / 2) {
                int i;
                int height = getHeight();
                Paint paint = this.rectPaint;
                if (Theme.usePlusTheme) {
                    i = this.underlineColor;
                } else {
                    i = Theme.getColor(Theme.key_actionBarDefaultIcon);
                }
                paint.setColor(i);
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
                View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
                float lineLeft = (float) currentTab.getLeft();
                float lineRight = (float) currentTab.getRight();
                if (this.currentPositionOffset > 0.0f && this.currentPosition < this.tabCount - 1) {
                    View nextTab = this.tabsContainer.getChildAt(this.currentPosition + 1);
                    lineLeft = (this.currentPositionOffset * ((float) nextTab.getLeft())) + ((1.0f - this.currentPositionOffset) * lineLeft);
                    lineRight = (this.currentPositionOffset * ((float) nextTab.getRight())) + ((1.0f - this.currentPositionOffset) * lineRight);
                }
                paint = this.rectPaint;
                i = Theme.usePlusTheme ? Theme.plusHideTabsSelector ? 0 : Theme.chatsHeaderTabIconColor : Theme.getColor(Theme.key_actionBarDefaultIcon);
                paint.setColor(i);
                canvas.drawRect(lineLeft, (float) (height - this.indicatorHeight), lineRight, (float) height, this.rectPaint);
                this.dividerPaint.setColor(this.dividerColor);
                for (int i2 = 0; i2 < this.tabCount - 1; i2++) {
                    View tab = this.tabsContainer.getChildAt(i2);
                    canvas.drawLine((float) tab.getRight(), (float) this.dividerPadding, (float) tab.getRight(), (float) (height - this.dividerPadding), this.dividerPaint);
                }
                return;
            }
            enableShouldExpand();
            notifyDataSetChanged();
        }
    }

    public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        if (!Theme.plusTabsShouldExpand) {
            post(new Runnable() {
                public void run() {
                    PlusPagerSlidingTabStrip.this.notifyDataSetChanged();
                }
            });
        }
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return this.dividerColor;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        if (Theme.plusTabsShouldExpand != shouldExpand) {
            Theme.plusTabsShouldExpand = shouldExpand;
            requestLayout();
        }
    }

    public boolean isTextAllCaps() {
        return this.textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        if (Theme.plusTabsTextSize != textSizePx) {
            Theme.plusTabsTextSize = textSizePx;
            updateTabStyles();
        }
    }

    public int getTextSize() {
        return Theme.plusTabsTextSize;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.currentPosition = savedState.currentPosition;
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPosition = this.currentPosition;
        return savedState;
    }
}
