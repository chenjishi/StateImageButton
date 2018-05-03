package com.miscell.stateimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;

import static android.text.TextUtils.isEmpty;

public class StateImageButton extends View {
    private final static int STATE_NORMAL = 0;
    private final static int STATE_SELECTED = 1;
    private final static int STATE_DISABLED = 2;

    private final static int INDICATOR_COLOR = 0xFFE14127;

    private final static int INDICATOR_RADIUS = 2;

    private final SparseIntArray mColors = new SparseIntArray();

    private final SparseIntArray mIconIds = new SparseIntArray();

    private final SparseIntArray mTextColors = new SparseIntArray();

    private final SparseArray<Bitmap> mBitmaps = new SparseArray<>();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Matrix mMatrix = new Matrix();

    private final PorterDuffXfermode mXferMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    private String mText;

    private float mTextSize;

    private int mTextImageGap;

    private int mImageOffset;

    private float mIndicatorRadius;

    private int mIndicatorColor;

    private boolean mShowIndicator;

    private int mHighlightColor;

    private int mAlpha = 255;

    private int mImageWidth, mImageHeight;

    private float mIndicatorTopPercent, mIndicatorRightPercent;

    public StateImageButton(Context context) {
        this(context, null);
    }

    public StateImageButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateImageButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        final float density = getResources().getDisplayMetrics().density;

        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateImageButton);

            int color = a.getColor(R.styleable.StateImageButton_color_selected, Color.TRANSPARENT);
            if (color != 0) mColors.put(STATE_SELECTED, color);

            color = a.getColor(R.styleable.StateImageButton_color_disabled, Color.TRANSPARENT);
            if (color != 0) mColors.put(STATE_DISABLED, color);

            int iconId = a.getResourceId(R.styleable.StateImageButton_image_normal, -1);
            if (-1 != iconId) {
                mIconIds.put(STATE_NORMAL, iconId);
                decodeStatusIcon(STATE_NORMAL);
            }

            iconId = a.getResourceId(R.styleable.StateImageButton_image_selected, -1);
            if (-1 != iconId) mIconIds.put(STATE_SELECTED, iconId);

            iconId = a.getResourceId(R.styleable.StateImageButton_image_disabled, -1);
            if (-1 != iconId) mIconIds.put(STATE_DISABLED, iconId);

            mText = a.getString(R.styleable.StateImageButton_text);
            mTextSize = a.getDimension(R.styleable.StateImageButton_state_text_size, 0);

            int textColor = a.getColor(R.styleable.StateImageButton_state_text_color, Color.TRANSPARENT);
            mTextColors.put(STATE_NORMAL, textColor);

            textColor = a.getColor(R.styleable.StateImageButton_text_color_selected, textColor);
            mTextColors.put(STATE_SELECTED, textColor);

            textColor = a.getColor(R.styleable.StateImageButton_text_color_disabled, textColor);
            mTextColors.put(STATE_DISABLED, textColor);

            mTextImageGap = (int) a.getDimension(R.styleable.StateImageButton_text_image_gap, 0);
            mPaint.setTextSize(mTextSize);

            mIndicatorColor = a.getColor(R.styleable.StateImageButton_indicator_color, INDICATOR_COLOR);
            mIndicatorRadius = a.getDimension(R.styleable.StateImageButton_indicator_radius, INDICATOR_RADIUS * density);

            mHighlightColor = a.getColor(R.styleable.StateImageButton_highlight_color, 0);

            mImageWidth = (int) a.getDimension(R.styleable.StateImageButton_image_width, 0);
            mImageHeight = (int) a.getDimension(R.styleable.StateImageButton_image_height, 0);

            mIndicatorTopPercent = a.getFloat(R.styleable.StateImageButton_indicator_top_percent, 0);
            mIndicatorRightPercent = a.getFloat(R.styleable.StateImageButton_indicator_right_percent, 0);
            mImageOffset = (int) a.getDimension(R.styleable.StateImageButton_image_offset, 0);

            a.recycle();
        }
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAlpha(mAlpha);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
    }

    public void setPaintAlpha(int alpha) {
        mAlpha = alpha;
        invalidate();
    }

    public void setSelectedColor(int color) {
        mColors.put(STATE_SELECTED, color);
        invalidate();
    }

    public void setDisabledColor(int color) {
        mColors.put(STATE_DISABLED, color);
        invalidate();
    }

    public void setImageResourceId(int resId) {
        mIconIds.put(STATE_NORMAL, resId);
        decodeStatusIcon(STATE_NORMAL);
        requestLayout();
        invalidate();
    }

    public void setImageDisabledResourceId(int resId) {
        mIconIds.put(STATE_DISABLED, resId);
        invalidate();
    }

    public void setImageSelectedResourceId(int resId) {
        mIconIds.put(STATE_SELECTED, resId);
        invalidate();
    }

    public void setText(int resId) {
        setText(getContext().getString(resId));
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setTextSize(float size) {
        mTextSize = size;
        mPaint.setTextSize(size);
        mPaint.setTextAlign(Paint.Align.CENTER);
        requestLayout();
    }

    public void setTextColor(int color) {
        mTextColors.put(STATE_NORMAL, color);
        invalidate();
    }

    public void setTextColorDisabled(int color) {
        mTextColors.put(STATE_DISABLED, color);
        invalidate();
    }

    public void setTextColorSelected(int color) {
        mTextColors.put(STATE_SELECTED, color);
        invalidate();
    }

    /**
     * margin between image and text
     *
     * @param margin
     */
    public void setTextImageGap(int margin) {
        mTextImageGap = margin;
        requestLayout();
        invalidate();
    }

    /**
     * set whole view's highlight color when pressed
     *
     * @param color
     */
    public void setHighlightColor(int color) {
        mHighlightColor = color;
        invalidate();
    }

    /**
     * set indicator's background color, call {@link #showIndicator(boolean)} first
     * then the indicator will show at view's top and right corner
     *
     * @param color
     */
    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        invalidate();
    }

    /**
     * set indicator circle's radius
     *
     * @param radius
     */
    public void setIndicatorRadius(float radius) {
        mIndicatorRadius = radius;
        invalidate();
    }

    private Bitmap decodeStatusIcon(int status) {
        int iconId = mIconIds.get(status);
        if (iconId <= 0) return null;

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), iconId);
        mBitmaps.put(status, bmp);

        return bmp;
    }

    public void setImageSize(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
        requestLayout();
        invalidate();
    }

    public void showIndicator(boolean b) {
        mShowIndicator = b;
        invalidate();
    }

    public void setImageBitmap(Bitmap bm) {
        mBitmaps.put(STATE_NORMAL, bm);
        requestLayout();
        invalidate();
    }

    public void setIndicatorMarginPercent(float top, float right) {
        mIndicatorTopPercent = top;
        mIndicatorRightPercent = right;
    }

    public void setImageOffset(int offset) {
        mImageOffset = offset;
        requestLayout();
        invalidate();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        Bitmap bmp = mBitmaps.size() > 0 ? mBitmaps.get(0) : null;
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = getPaddingLeft() + getPaddingRight();
            if (null != bmp) {
                int bw = bmp.getWidth();
                int tw = 0;
                if (!isEmpty(mText)) tw = Math.round(mPaint.measureText(mText));
                width += Math.max(bw, tw);
            }
            if (mode == MeasureSpec.AT_MOST) {
                width = Math.min(size, width);
            }
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = getPaddingTop() + getPaddingBottom();
            if (null != bmp) height += bmp.getHeight();
            if (!isEmpty(mText)) {
                Paint.FontMetrics metrics = mPaint.getFontMetrics();
                height += metrics.bottom - metrics.top;
                height += mTextImageGap;
            }
            height += mImageOffset;
            if (mode == MeasureSpec.AT_MOST) {
                height = Math.min(size, height);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0 || mBitmaps.size() == 0) return;

        if (isPressed() && mHighlightColor != 0) canvas.drawColor(mHighlightColor);
        Bitmap bmp = mBitmaps.get(STATE_NORMAL);
        int bh = bmp.getHeight();
        int bw = bmp.getWidth();

        if (mImageWidth > 0 && mImageHeight > 0) {
            bw = mImageWidth;
            bh = mImageHeight;
        }
        float x = (w - bw) / 2f;
        float y = getPaddingTop() + mImageOffset;
        mPaint.setAlpha(mAlpha);
        if (mColors.size() > 0) {
            if (isEmpty(mText)) y = (h - bh) / 2f;
            drawBitmap(canvas, bmp, x, y);
            if (!isEnabled()) colorOverlay(canvas, mColors.get(STATE_DISABLED), x, y, bw, bh);
            if (isPressed() || isSelected()) {
                colorOverlay(canvas, mColors.get(STATE_SELECTED), x, y, bw, bh);
            }
        } else {
            if (!isEnabled()) {
                bmp = mBitmaps.get(STATE_DISABLED);
                if (null == bmp) bmp = decodeStatusIcon(STATE_DISABLED);
            }

            if (isPressed() || isSelected()) {
                bmp = mBitmaps.get(STATE_SELECTED);
                if (null == bmp) bmp = decodeStatusIcon(STATE_SELECTED);
            }

            if (null == bmp) bmp = mBitmaps.get(STATE_NORMAL);
            drawBitmap(canvas, bmp, x, y);
        }

        if (!isEmpty(mText)) {
            int color = 0;
            if (!isEnabled()) color = mTextColors.get(STATE_DISABLED);
            if (isPressed() || isSelected()) color = mTextColors.get(STATE_SELECTED);
            if (color == 0) color = mTextColors.get(STATE_NORMAL);
            mPaint.setColor(color);
            mPaint.setAlpha(mAlpha);
            x = w / 2f;
            y = getPaddingTop() + mImageOffset + bh + mTextImageGap - mPaint.getFontMetrics().top;
            canvas.drawText(mText, x, y, mPaint);
        }

        if (mShowIndicator) {
            mPaint.setColor(mIndicatorColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAlpha(mAlpha);

            if (mIndicatorTopPercent > 0 || mIndicatorRightPercent > 0) {
                x = w / 2f + (bw / 2f - bw * mIndicatorRightPercent - mIndicatorRadius + 1);
                y = getPaddingTop() + bh * mIndicatorTopPercent + mIndicatorRadius;
            } else {
                x = w - getPaddingRight() - mIndicatorRadius;
                y = getPaddingTop() + mIndicatorRadius;
            }
            canvas.drawCircle(x, y, mIndicatorRadius, mPaint);
        }
    }

    private final RectF src = new RectF(), dst = new RectF();

    private void drawBitmap(Canvas c, Bitmap bmp, float x, float y) {
        mMatrix.reset();
        if (mImageWidth > 0 && mImageHeight > 0) {
            src.set(0, 0, bmp.getWidth(), bmp.getHeight());
            dst.set(x, y, x + mImageWidth, y + mImageHeight);
            mMatrix.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);
        } else {
            mMatrix.setTranslate(x, y);
        }
        c.drawBitmap(bmp, mMatrix, mPaint);
    }

    private void colorOverlay(Canvas c, int color, float x, float y, int w, int h) {
        mPaint.setXfermode(mXferMode);
        mPaint.setColor(color);
        c.drawRect(x, y, x + w, y + h, mPaint);
        mPaint.setXfermode(null);
    }
}
