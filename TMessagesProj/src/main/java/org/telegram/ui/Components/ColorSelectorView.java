package org.telegram.ui.Components;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import org.telegram.messenger.R;

public class ColorSelectorView extends LinearLayout {
    private static final String HEX_TAG = "HEX";
    private static final String HSV_TAG = "HSV";
    private static final String RGB_TAG = "RGB";
    private int color;
    private HexSelectorView hexSelector;
    private HsvSelectorView hsvSelector;
    private OnColorChangedListener listener;
    private int maxHeight = 0;
    private int maxWidth = 0;
    private RgbSelectorView rgbSelector;
    private TabHost tabs;

    class ColorTabContentFactory implements TabContentFactory {
        ColorTabContentFactory() {
        }

        public View createTabContent(String tag) {
            if (ColorSelectorView.HSV_TAG.equals(tag)) {
                return ColorSelectorView.this.hsvSelector;
            }
            if (ColorSelectorView.RGB_TAG.equals(tag)) {
                return ColorSelectorView.this.rgbSelector;
            }
            if (ColorSelectorView.HEX_TAG.equals(tag)) {
                return ColorSelectorView.this.hexSelector;
            }
            return null;
        }
    }

    public interface OnColorChangedListener {
        void colorChanged(int i);
    }

    public ColorSelectorView(Context context) {
        super(context);
        init();
    }

    public ColorSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setColor(int color) {
        setColor(color, null);
    }

    public void setDialog(Dialog d) {
        this.hexSelector.setDialog(d);
    }

    private void setColor(int color, View sender) {
        if (this.color != color) {
            this.color = color;
            if (sender != this.hsvSelector) {
                this.hsvSelector.setColor(color);
            }
            if (sender != this.rgbSelector) {
                this.rgbSelector.setColor(color);
            }
            if (sender != this.hexSelector) {
                this.hexSelector.setColor(color);
            }
            onColorChanged();
        }
    }

    public int getColor() {
        return this.color;
    }

    private void init() {
        View contentView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.color_colorselectview, null);
        addView(contentView, new LayoutParams(-1, -1));
        this.hsvSelector = new HsvSelectorView(getContext());
        this.hsvSelector.setLayoutParams(new LayoutParams(-1, -1));
        this.hsvSelector.setOnColorChangedListener(new org.telegram.ui.Components.HsvSelectorView.OnColorChangedListener() {
            public void colorChanged(int color) {
                ColorSelectorView.this.setColor(color);
            }
        });
        this.rgbSelector = new RgbSelectorView(getContext());
        this.rgbSelector.setLayoutParams(new LayoutParams(-1, -1));
        this.rgbSelector.setOnColorChangedListener(new org.telegram.ui.Components.RgbSelectorView.OnColorChangedListener() {
            public void colorChanged(int color) {
                ColorSelectorView.this.setColor(color);
            }
        });
        this.hexSelector = new HexSelectorView(getContext());
        this.hexSelector.setLayoutParams(new LayoutParams(-1, -1));
        this.hexSelector.setOnColorChangedListener(new org.telegram.ui.Components.HexSelectorView.OnColorChangedListener() {
            public void colorChanged(int color) {
                ColorSelectorView.this.setColor(color);
            }
        });
        this.tabs = (TabHost) contentView.findViewById(R.id.colorview_tabColors);
        this.tabs.setup();
        ColorTabContentFactory factory = new ColorTabContentFactory();
        TabSpec hsvTab = this.tabs.newTabSpec(HSV_TAG).setIndicator(createTabView(this.tabs.getContext(), HSV_TAG)).setContent(factory);
        TabSpec rgbTab = this.tabs.newTabSpec(RGB_TAG).setIndicator(createTabView(this.tabs.getContext(), RGB_TAG)).setContent(factory);
        TabSpec hexTab = this.tabs.newTabSpec(HEX_TAG).setIndicator(createTabView(this.tabs.getContext(), HEX_TAG)).setContent(factory);
        this.tabs.addTab(hsvTab);
        this.tabs.addTab(rgbTab);
        this.tabs.addTab(hexTab);
    }

    private View createTabView(Context context, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        ((TextView) view.findViewById(R.id.tabsText)).setText(text);
        return view;
    }

    private void onColorChanged() {
        if (this.listener != null) {
            this.listener.colorChanged(getColor());
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (HSV_TAG.equals(this.tabs.getCurrentTabTag())) {
            this.maxHeight = getMeasuredHeight();
            this.maxWidth = getMeasuredWidth();
        }
        setMeasuredDimension(this.maxWidth, this.maxHeight);
    }
}
