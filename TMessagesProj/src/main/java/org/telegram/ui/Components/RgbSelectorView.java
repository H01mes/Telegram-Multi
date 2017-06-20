package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.telegram.messenger.R;

public class RgbSelectorView extends LinearLayout {
    private ImageView imgPreview;
    private OnColorChangedListener listener;
    private SeekBar seekAlpha;
    private TextView seekAlphaValue;
    private SeekBar seekBlue;
    private TextView seekBlueValue;
    private SeekBar seekGreen;
    private TextView seekGreenValue;
    private SeekBar seekRed;
    private TextView seekRedValue;

    public interface OnColorChangedListener {
        void colorChanged(int i);
    }

    public RgbSelectorView(Context context) {
        super(context);
        init();
    }

    public RgbSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View rgbView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.color_rgbview, null);
        addView(rgbView, new LayoutParams(-1, -1));
        OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getTag() != null) {
                    if (seekBar.getTag().equals("R")) {
                        RgbSelectorView.this.seekRedValue.setText(String.valueOf(progress));
                    }
                    if (seekBar.getTag().equals("G")) {
                        RgbSelectorView.this.seekGreenValue.setText(String.valueOf(progress));
                    }
                    if (seekBar.getTag().equals("B")) {
                        RgbSelectorView.this.seekBlueValue.setText(String.valueOf(progress));
                    }
                    if (seekBar.getTag().equals("A")) {
                        RgbSelectorView.this.seekAlphaValue.setText(String.valueOf(progress));
                    }
                }
                RgbSelectorView.this.setPreviewImage();
                RgbSelectorView.this.onColorChanged();
            }
        };
        this.seekRed = (SeekBar) rgbView.findViewById(R.id.color_rgb_seekRed);
        this.seekRed.setTag("R");
        this.seekRedValue = (TextView) findViewById(R.id.color_rgb_tvRed_value);
        this.seekRed.setOnSeekBarChangeListener(listener);
        this.seekGreen = (SeekBar) rgbView.findViewById(R.id.color_rgb_seekGreen);
        this.seekGreen.setTag("G");
        this.seekGreenValue = (TextView) findViewById(R.id.color_rgb_tvGreen_value);
        this.seekGreen.setOnSeekBarChangeListener(listener);
        this.seekBlue = (SeekBar) rgbView.findViewById(R.id.color_rgb_seekBlue);
        this.seekBlue.setTag("B");
        this.seekBlueValue = (TextView) findViewById(R.id.color_rgb_tvBlue_value);
        this.seekBlue.setOnSeekBarChangeListener(listener);
        this.seekAlpha = (SeekBar) rgbView.findViewById(R.id.color_rgb_seekAlpha);
        this.seekAlpha.setTag("A");
        this.seekAlphaValue = (TextView) findViewById(R.id.color_rgb_tvAlpha_value);
        this.seekAlpha.setOnSeekBarChangeListener(listener);
        this.imgPreview = (ImageView) rgbView.findViewById(R.id.color_rgb_imgpreview);
        setColor(-16777216);
    }

    private void setPreviewImage() {
        Bitmap preview = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        preview.setPixel(0, 0, getColor());
        this.imgPreview.setImageBitmap(preview);
    }

    public int getColor() {
        return Color.argb(this.seekAlpha.getProgress(), this.seekRed.getProgress(), this.seekGreen.getProgress(), this.seekBlue.getProgress());
    }

    public void setColor(int color) {
        this.seekAlpha.setProgress(Color.alpha(color));
        this.seekRed.setProgress(Color.red(color));
        this.seekGreen.setProgress(Color.green(color));
        this.seekBlue.setProgress(Color.blue(color));
        setPreviewImage();
    }

    private void onColorChanged() {
        if (this.listener != null) {
            this.listener.colorChanged(getColor());
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }
}
