package org.telegram.ui.Components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import org.telegram.messenger.R;

//import android.view.View.OnClickListener;

public class ColorSelectorDialog extends Dialog implements org.telegram.ui.Components.ColorPickerView.OnColorChangedListener, View.OnClickListener {//TODO Multi View or Dialog??
    public static final int BOTTOM = 1;
    public static final int CENTER = 0;
    public static final int LEFT = 4;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    private boolean alpha;
    private Button btnNew;
    private Button btnOld;
    private int color;
    private ColorSelectorView content;
    private HistorySelectorView history;
    private int initColor;
    private OnColorChangedListener listener;
    private int offset;
    private int side;

    public interface OnColorChangedListener {
        void colorChanged(int i);
    }

    public int getColor() {
        return this.color;
    }

    public ColorSelectorDialog(Context context, OnColorChangedListener listener, int initColor, int side, int offset, boolean alpha) {
        super(context, R.style.myBackgroundStyle);
        this.listener = listener;
        this.initColor = initColor;
        this.side = side;
        this.offset = offset;
        this.alpha = alpha;
    }

    public ColorSelectorDialog(Context context, int initColor, int side) {
        super(context, R.style.myBackgroundStyle);
        this.initColor = initColor;
        this.side = side;
        this.offset = this.offset;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colordialog);
        if (this.side == 2) {
            getWindow().setGravity(5);
            LayoutParams p = getWindow().getAttributes();
            p.x = this.offset;
            getWindow().setAttributes(p);
        } else if (this.side == 1) {
        }
        this.btnOld = (Button) findViewById(R.id.button_old);
        this.btnOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSelectorDialog.this.dismiss();
            }
        });
        ColorSelectorDialog.this.dismiss();

        this.btnNew = (Button) findViewById(R.id.button_new);
        this.btnNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ColorSelectorDialog.this.listener != null) {
                    ColorSelectorDialog.this.listener.colorChanged(ColorSelectorDialog.this.color);
                }
                ColorSelectorDialog.this.history.selectColor(ColorSelectorDialog.this.color);
                ColorSelectorDialog.this.dismiss();
            }
        });
        this.content = (ColorSelectorView) findViewById(R.id.content);
        this.content.setDialog(this);
        this.content.setOnColorChangedListener(new org.telegram.ui.Components.ColorSelectorView.OnColorChangedListener() {
            public void colorChanged(int color) {
                ColorSelectorDialog.this.colorChangedInternal(color);
            }
        });
        this.history = (HistorySelectorView) findViewById(R.id.historyselector);
        this.history.setOnColorChangedListener(new org.telegram.ui.Components.HistorySelectorView.OnColorChangedListener() {
            public void colorChanged(int color) {
                ColorSelectorDialog.this.colorChangedInternal(color);
                ColorSelectorDialog.this.content.setColor(color);
            }
        });
        this.btnOld.setBackgroundColor(this.initColor);
        this.btnOld.setTextColor((this.initColor ^ -1) | -16777216);
        this.content.setColor(this.initColor);
    }

    private void colorChangedInternal(int color) {
        this.btnNew.setBackgroundColor(color);
        this.btnNew.setTextColor((color ^ -1) | -16777216);
        this.color = adjustAlpha(color, this.alpha);
    }

    private int adjustAlpha(int color, boolean b) {
        return Color.argb(b ? Color.alpha(color) : 255, Color.red(color), Color.green(color), Color.blue(color));
    }

    public void setColor(int color) {
        this.content.setColor(color);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 4) {
            System.out.println("TOuch outside the dialog ******************** ");
            dismiss();
        }
        return super.onTouchEvent(event);
    }

    public void setOnColorChangedListener(OnColorChangedListener mlistener) {
        this.listener = mlistener;
    }

    public void onClick(View v) {
        if (this.listener != null) {
            this.listener.colorChanged(this.color);
        }
        this.history.selectColor(this.color);
        dismiss();
    }

    public void onColorChanged(int color) {
        if (this.listener != null) {
            this.listener.colorChanged(getColor());
        }
    }
}
