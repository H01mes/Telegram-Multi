package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;

public class TextDrawable extends ShapeDrawable {
    private static final float SHADE_FACTOR = 0.9f;
    private final Paint borderPaint;
    private final int borderThickness;
    private final int color;
    private final int fontSize;
    private final int height;
    private final float radius;
    private final RectShape shape;
    private final String text;
    private final Paint textPaint;
    private final int width;

    public interface IBuilder {
        TextDrawable build(String str, int i);
    }

    public interface IConfigBuilder {
        IConfigBuilder bold();

        IShapeBuilder endConfig();

        IConfigBuilder fontSize(int i);

        IConfigBuilder height(int i);

        IConfigBuilder textColor(int i);

        IConfigBuilder toUpperCase();

        IConfigBuilder useFont(Typeface typeface);

        IConfigBuilder width(int i);

        IConfigBuilder withBorder(int i);
    }

    public interface IShapeBuilder {
        IConfigBuilder beginConfig();

        TextDrawable buildRect(String str, int i);

        TextDrawable buildRound(String str, int i);

        TextDrawable buildRoundRect(String str, int i, int i2);

        IBuilder rect();

        IBuilder round();

        IBuilder roundRect(int i);
    }

    public static class Builder implements IConfigBuilder, IShapeBuilder, IBuilder {
        private int borderThickness;
        private int color;
        private Typeface font;
        private int fontSize;
        private int height;
        private boolean isBold;
        public float radius;
        private RectShape shape;
        private String text;
        public int textColor;
        private boolean toUpperCase;
        private int width;

        private Builder() {
            this.text = "";
            this.color = -7829368;
            this.textColor = -1;
            this.borderThickness = 0;
            this.width = -1;
            this.height = -1;
            this.shape = new RectShape();
            this.font = Typeface.create("sans-serif-light", 0);
            this.fontSize = -1;
            this.isBold = false;
            this.toUpperCase = false;
        }

        public IConfigBuilder width(int width) {
            this.width = width;
            return this;
        }

        public IConfigBuilder height(int height) {
            this.height = height;
            return this;
        }

        public IConfigBuilder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public IConfigBuilder withBorder(int thickness) {
            this.borderThickness = thickness;
            return this;
        }

        public IConfigBuilder useFont(Typeface font) {
            this.font = font;
            return this;
        }

        public IConfigBuilder fontSize(int size) {
            this.fontSize = size;
            return this;
        }

        public IConfigBuilder bold() {
            this.isBold = true;
            return this;
        }

        public IConfigBuilder toUpperCase() {
            this.toUpperCase = true;
            return this;
        }

        public IConfigBuilder beginConfig() {
            return this;
        }

        public IShapeBuilder endConfig() {
            return this;
        }

        public IBuilder rect() {
            this.shape = new RectShape();
            return this;
        }

        public IBuilder round() {
            this.shape = new OvalShape();
            return this;
        }

        public IBuilder roundRect(int radius) {
            this.radius = (float) radius;
            this.shape = new RoundRectShape(new float[]{(float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius}, null, null);
            return this;
        }

        public TextDrawable buildRect(String text, int color) {
            rect();
            return build(text, color);
        }

        public TextDrawable buildRoundRect(String text, int color, int radius) {
            roundRect(radius);
            return build(text, color);
        }

        public TextDrawable buildRound(String text, int color) {
            round();
            return build(text, color);
        }

        public TextDrawable build(String text, int color) {
            this.color = color;
            this.text = text;
            Builder builder = new Builder();
            builder.color = color;
            builder.text = text;
            return new TextDrawable(builder);
        }
    }

    private TextDrawable(Builder builder) {
        super(builder.shape);
        this.shape = builder.shape;
        this.height = builder.height;
        this.width = builder.width;
        this.radius = builder.radius;
        this.text = builder.toUpperCase ? builder.text.toUpperCase() : builder.text;
        this.color = builder.color;
        this.fontSize = builder.fontSize;
        this.textPaint = new Paint();
        this.textPaint.setColor(builder.textColor);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setFakeBoldText(builder.isBold);
        this.textPaint.setStyle(Style.FILL);
        this.textPaint.setTypeface(builder.font);
        this.textPaint.setTextAlign(Align.CENTER);
        this.textPaint.setStrokeWidth((float) builder.borderThickness);
        this.borderThickness = builder.borderThickness;
        this.borderPaint = new Paint();
        this.borderPaint.setColor(getDarkerShade(this.color));
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setStrokeWidth((float) this.borderThickness);
        getPaint().setColor(this.color);
    }

    private int getDarkerShade(int color) {
        return Color.rgb((int) (((float) Color.red(color)) * SHADE_FACTOR), (int) (((float) Color.green(color)) * SHADE_FACTOR), (int) (((float) Color.blue(color)) * SHADE_FACTOR));
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect r = getBounds();
        if (this.borderThickness > 0) {
            drawBorder(canvas);
        }
        int count = canvas.save();
        canvas.translate((float) r.left, (float) r.top);
        int width = this.width < 0 ? r.width() : this.width;
        int height = this.height < 0 ? r.height() : this.height;
        this.textPaint.setTextSize((float) (this.fontSize < 0 ? Math.min(width, height) / 2 : this.fontSize));
        canvas.drawText(this.text, (float) (width / 2), ((float) (height / 2)) - ((this.textPaint.descent() + this.textPaint.ascent()) / 2.0f), this.textPaint);
        canvas.restoreToCount(count);
    }

    private void drawBorder(Canvas canvas) {
        RectF rect = new RectF(getBounds());
        rect.inset((float) (this.borderThickness / 2), (float) (this.borderThickness / 2));
        if (this.shape instanceof OvalShape) {
            canvas.drawOval(rect, this.borderPaint);
        } else if (this.shape instanceof RoundRectShape) {
            canvas.drawRoundRect(rect, this.radius, this.radius, this.borderPaint);
        } else {
            canvas.drawRect(rect, this.borderPaint);
        }
    }

    public void setAlpha(int alpha) {
        this.textPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.textPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public int getIntrinsicWidth() {
        return this.width;
    }

    public int getIntrinsicHeight() {
        return this.height;
    }

    public static IShapeBuilder builder() {
        return new Builder();
    }
}
