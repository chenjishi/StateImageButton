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

    private final Matrix mMatrix = new Matrix();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final PorterDuffXfermode mXferMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    private String mText;

    private float mTextSize;

    private float mTextMarginTop;

    private float mIndicatorRadius;

    private int mIndicatorColor;

    private int mIndicatorMarginTop;

    private int mIndicatorMarginRight;

    private int mIndicatorHorizontalPadding;

    private int mIndicatorVerticalPadding;

    private boolean mIndicatorAlignImage;

    private boolean mShowIndicator;

    private int mHighlightColor;

    private int mImageMarginTop;

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
            mTextSize = a.getDimension(R.styleable.StateImageButton_text_size, 0);

            int textColor = a.getColor(R.styleable.StateImageButton_text_color, Color.TRANSPARENT);
            mTextColors.put(STATE_NORMAL, textColor);

            textColor = a.getColor(R.styleable.StateImageButton_text_color_selected, textColor);
            mTextColors.put(STATE_SELECTED, textColor);

            textColor = a.getColor(R.styleable.StateImageButton_text_color_disabled, textColor);
            mTextColors.put(STATE_DISABLED, textColor);

            mTextMarginTop = a.getDimension(R.styleable.StateImageButton_text_margin_top, 0);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(mTextSize);

            mIndicatorColor = a.getColor(R.styleable.StateImageButton_indicator_color, INDICATOR_COLOR);
            mIndicatorRadius = a.getDimension(R.styleable.StateImageButton_indicator_radius, INDICATOR_RADIUS * density);

            mHighlightColor = a.getColor(R.styleable.StateImageButton_highlight_color, 0);

            mIndicatorMarginTop = (int) a.getDimension(R.styleable.StateImageButton_indicator_margin_top, 0);
            mIndicatorMarginRight = (int) a.getDimension(R.styleable.StateImageButton_indicator_margin_right, 0);
            mIndicatorHorizontalPadding = (int) a.getDimension(R.styleable.StateImageButton_indicator_horizontal_padding, 0);
            mIndicatorVerticalPadding = (int) a.getDimension(R.styleable.StateImageButton_indicator_vertical_padding, 0);
            mIndicatorAlignImage = a.getBoolean(R.styleable.StateImageButton_indicator_align_image, false);

            mImageMarginTop = (int) a.getDimension(R.styleable.StateImageButton_image_margin_top, 0);

            a.recycle();
        }
    }

    public void setSelectedDyeingColor(int color) {
        mColors.put(STATE_SELECTED, color);
        invalidate();
    }

    public void setDisabledDyeingColor(int color) {
        mColors.put(STATE_DISABLED, color);
        invalidate();
    }

    public void setImageResourceId(int resId) {
        mIconIds.put(STATE_NORMAL, resId);
        decodeStatusIcon(STATE_NORMAL);
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

    public void setImageMarginTop(int margin) {
        mImageMarginTop = margin;
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
        invalidate();
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
     * @param margin
     */
    public void setTextMarginTop(int margin) {
        mTextMarginTop = margin;
        invalidate();
    }

    /**
     * set whole view's highlight color when pressed
     * @param color
     */
    public void setHighlightColor(int color) {
        mHighlightColor = color;
        invalidate();
    }

    /**
     * set indicator's background color, call {@link #showIndicator(boolean)} first
     * then the indicator will show at view's top and right corner
     * @param color
     */
    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        invalidate();
    }

    /**
     * set indicator circle's radius
     * @param radius
     */
    public void setIndicatorRadius(float radius) {
        mIndicatorRadius = radius;
        invalidate();
    }

    /**
     * set margin to view's top and right, if {@link #mIndicatorAlignImage}
     * set is true, this margin have no effect
     * @param top
     * @param right
     */
    public void setIndicatorMargin(int top, int right) {
        mIndicatorMarginTop = top;
        mIndicatorMarginRight = right;
        invalidate();
    }

    /**
     * set padding between indicator and image, just work for
     * {@link #mIndicatorAlignImage} is true
     * @param horizontal
     * @param vertical
     */
    public void setIndicatorWithImagePadding(int horizontal, int vertical) {
        mIndicatorHorizontalPadding = horizontal;
        mIndicatorVerticalPadding = vertical;
        invalidate();
    }

    /**
     * @param b if true indicator will align with image's top and right corner,
     *          call {@link #setIndicatorWithImagePadding(int, int)} to set padding,
     *          if false indicator will aligh with whole view's top and right,
     *          call {@link #setIndicatorMargin(int, int)} to set margins
     */
    public void setIndicatorAlignImage(boolean b) {
        mIndicatorAlignImage = b;
        invalidate();
    }

    private Bitmap decodeStatusIcon(int status) {
        int iconId = mIconIds.get(status);
        if (iconId <= 0) return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), iconId, options);
        mBitmaps.put(status, bmp);

        return bmp;
    }

    public void showIndicator(boolean b) {
        mShowIndicator = b;
        invalidate();
    }

    public void setImageBitmap(Bitmap bm) {
        mBitmaps.put(STATE_NORMAL, bm);
        requestLayout();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        Bitmap bmp = mBitmaps.size() > 0 ? mBitmaps.get(0) : null;

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
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
                height += Math.abs(metrics.top) + metrics.bottom;
                height += mTextMarginTop;
            }
            height += mImageMarginTop;
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

        float x = (w - bw) / 2f;
        float y = getPaddingTop() + mImageMarginTop;
        if (mColors.size() > 0) {
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
            int color = mTextColors.get(STATE_NORMAL);
            if (!isEnabled()) color = mTextColors.get(STATE_DISABLED);
            if (isPressed() || isSelected()) color = mTextColors.get(STATE_SELECTED);
            mPaint.setColor(color);
            x = w / 2f;
            y = getPaddingTop() + mImageMarginTop + bh + mTextMarginTop + Math.abs(mPaint.getFontMetrics().top);
            canvas.drawText(mText, x, y, mPaint);
        }

        if (mShowIndicator) {
            mPaint.setColor(mIndicatorColor);
            mPaint.setStyle(Paint.Style.FILL);
            if (mIndicatorAlignImage) {
                x = (w + bw) / 2 + mIndicatorHorizontalPadding + mIndicatorRadius;
                y = getPaddingTop() + mImageMarginTop - mIndicatorVerticalPadding - mIndicatorRadius;
            } else {
                x = w - getPaddingRight() - mIndicatorMarginRight - mIndicatorRadius;
                y = getPaddingTop() + mIndicatorMarginTop + mIndicatorRadius;
            }
            canvas.drawCircle(x, y, mIndicatorRadius, mPaint);
        }
    }

    private void drawBitmap(Canvas c, Bitmap bmp, float x, float y) {
        mMatrix.reset();
        mMatrix.setTranslate(x, y);
        c.drawBitmap(bmp, mMatrix, null);
    }

    private void colorOverlay(Canvas c, int color, float x, float y, int w, int h) {
        mPaint.setXfermode(mXferMode);
        mPaint.setColor(color);
        c.drawRect(x, y, x + w, y + h, mPaint);
        mPaint.setXfermode(null);
    }
}
