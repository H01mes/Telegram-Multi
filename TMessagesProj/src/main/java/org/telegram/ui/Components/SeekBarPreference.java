package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
    private static final String ANDROIDNS = "http://schemas.android.com/apk/res/android";
    private static final int DEFAULT_VALUE = 50;
    private static final String ROBOBUNNYNS = "http://robobunny.com";
    private final String TAG = getClass().getName();
    private RelativeLayout layout = null;
    private int mCurrentValue;
    private int mInterval = 1;
    private int mMaxValue = 100;
    private int mMinValue = 0;
    private SeekBar mSeekBar;
    private TextView mStatusText;
    private String mUnitsLeft = "";
    private String mUnitsRight = "";

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreference(context, attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPreference(context, attrs);
    }

    private void initPreference(Context context, AttributeSet attrs) {
        setValuesFromXml(attrs);
        this.mSeekBar = new SeekBar(context, attrs);
        this.mSeekBar.setMax(this.mMaxValue - this.mMinValue);
        this.mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void setValuesFromXml(AttributeSet attrs) {
        this.mMaxValue = attrs.getAttributeIntValue(ANDROIDNS, "max", 100);
        this.mMinValue = attrs.getAttributeIntValue(ROBOBUNNYNS, "min", 0);
        this.mUnitsLeft = getAttributeStringValue(attrs, ROBOBUNNYNS, "unitsLeft", "");
        this.mUnitsRight = getAttributeStringValue(attrs, ROBOBUNNYNS, "unitsRight", getAttributeStringValue(attrs, ROBOBUNNYNS, "units", ""));
        try {
            String newInterval = attrs.getAttributeValue(ROBOBUNNYNS, "interval");
            if (newInterval != null) {
                this.mInterval = Integer.parseInt(newInterval);
            }
        } catch (Exception e) {
            Log.e(this.TAG, "Invalid interval value", e);
        }
    }

    private String getAttributeStringValue(AttributeSet attrs, String namespace, String name, String defaultValue) {
        String value = attrs.getAttributeValue(namespace, name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @SuppressLint("MissingSuperCall")
    protected View onCreateView(ViewGroup parent) {
        try {
            this.layout = (RelativeLayout) ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.seek_bar_preference, parent, false);
        } catch (Exception e) {
            Log.e(this.TAG, "Error creating seek bar preference", e);
        }
        return this.layout;
    }

    public void onBindView(View view) {
        try {
            super.onBindView(view);
            try {
                ViewParent oldContainer = this.mSeekBar.getParent();
                ViewParent newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);
                if (oldContainer != newContainer) {
                    if (oldContainer != null) {
                        ((ViewGroup) oldContainer).removeView(this.mSeekBar);
                    }
                    newContainer.removeAllViews();
                    newContainer.addView(this.mSeekBar, -1, -2);
                }
            } catch (Exception ex) {
                Log.e(this.TAG, "Error binding view: " + ex.toString());
            }
            if (!(this.layout.isEnabled() || this.layout == null)) {
                this.mSeekBar.setEnabled(false);
            }
            updateView(view);
        } catch (NullPointerException e) {
        }
    }

    protected void updateView(View view) {
        try {
            RelativeLayout layout = (RelativeLayout) view;
            this.mStatusText = (TextView) layout.findViewById(R.id.seekBarPrefValue);
            this.mStatusText.setText(String.valueOf(this.mCurrentValue));
            this.mStatusText.setMinimumWidth(30);
            this.mSeekBar.setProgress(this.mCurrentValue - this.mMinValue);
            ((TextView) layout.findViewById(R.id.seekBarPrefUnitsRight)).setText(this.mUnitsRight);
            ((TextView) layout.findViewById(R.id.seekBarPrefUnitsLeft)).setText(this.mUnitsLeft);
        } catch (Exception e) {
            Log.e(this.TAG, "Error updating seek bar preference", e);
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            int newValue = progress + this.mMinValue;
            if (newValue > this.mMaxValue) {
                newValue = this.mMaxValue;
            } else if (newValue < this.mMinValue) {
                newValue = this.mMinValue;
            } else if (!(this.mInterval == 1 || newValue % this.mInterval == 0)) {
                newValue = Math.round(((float) newValue) / ((float) this.mInterval)) * this.mInterval;
            }
            if (callChangeListener(Integer.valueOf(newValue))) {
                this.mCurrentValue = newValue;
                this.mStatusText.setText(String.valueOf(newValue));
                persistInt(newValue);
                return;
            }
            seekBar.setProgress(this.mCurrentValue - this.mMinValue);
        } catch (NullPointerException e) {
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mSeekBar.setEnabled(enabled);
    }

    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        boolean z = true;
        super.onDependencyChanged(dependency, disableDependent);
        if (this.layout != null) {
            boolean z2;
            SeekBar seekBar = this.mSeekBar;
            if (disableDependent) {
                z2 = false;
            } else {
                z2 = true;
            }
            seekBar.setEnabled(z2);
            TextView textView = this.mStatusText;
            if (disableDependent) {
                z = false;
            }
            textView.setEnabled(z);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
        if (this.mUnitsRight.contains("r") || this.mUnitsRight.contains("Mb")) {
            Editor e = this.mSeekBar.getContext().getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).edit();
            e.putBoolean("need_reboot", true);
            e.commit();
        }
    }

    protected Object onGetDefaultValue(TypedArray ta, int index) {
        return Integer.valueOf(ta.getInt(index, 50));
    }

    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            this.mCurrentValue = getPersistedInt(this.mCurrentValue);
            return;
        }
        int temp = 0;
        try {
            temp = ((Integer) defaultValue).intValue();
        } catch (Exception e) {
            Log.e(this.TAG, "Invalid default value: " + defaultValue.toString());
        }
        persistInt(temp);
        this.mCurrentValue = temp;
    }
}
