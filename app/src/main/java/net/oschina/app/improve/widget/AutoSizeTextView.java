package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import net.oschina.app.R;

/**
 * 自适应字体大小TextView
 * qiujuer@live.cn
 */
public class AutoSizeTextView extends TextView {
    private int mContentWidth;
    private float mDefSize;
    private float mMinSize;

    public AutoSizeTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AutoSizeTextView, defStyle, 0);

        mDefSize = getTextSize();
        mMinSize = a.getDimension(
                R.styleable.AutoSizeTextView_textMinSize,
                mDefSize);
        mContentWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        a.recycle();
    }

    private void resize(CharSequence charSequence) {
        String text = charSequence.toString();
        if (!TextUtils.isEmpty(text) && mContentWidth > 0) {
            TextPaint paint = getPaint();
            final float fixedSize = paint.getTextSize();
            float textSize = paint.getTextSize();
            float measureWidth = paint.measureText(text);

            // resize to low
            while ((textSize > mMinSize) && (measureWidth >= mContentWidth)) {
                float backSize = paint.getTextSize();
                textSize = backSize - 1;
                if (textSize < mMinSize) {
                    textSize = mMinSize;
                }

                paint.setTextSize(textSize);
                measureWidth = paint.measureText(text);
            }

            // resize to high
            while ((textSize < mDefSize) && (measureWidth < mContentWidth)) {
                float backSize = paint.getTextSize();
                textSize = backSize + 1;
                if (textSize > mDefSize) {
                    textSize = mDefSize;
                }

                paint.setTextSize(textSize);
                measureWidth = paint.measureText(text);
                if (measureWidth >= mContentWidth) {
                    textSize = backSize;
                    break;
                }
            }

            if (fixedSize != textSize) {
                paint.setTextSize(fixedSize);
                // refresh
                if (!isInEditMode()) {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }
            }
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Resize on view size change
        if (w != oldw) {
            mContentWidth = w - getPaddingLeft() - getPaddingRight();
            resize(getText());
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        resize(text);
    }
}
