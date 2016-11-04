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
 * 自适应字体大小的TextView
 * @author thanatosx
 */
public class AutoSizeTextView extends TextView {
    private int mContentWidth;
    private float mDefaultTextSize;
    private float mMinSize;

    public AutoSizeTextView(Context context) {
        this(context, null);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        mDefaultTextSize = getTextSize();
        // TODO 可配置的mMinSize
        mMinSize = 1;
        mContentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private void resize(CharSequence charSequence) {
        // TODO 支持多行
        String text = charSequence.toString();
        if (TextUtils.isEmpty(text) || mContentWidth <= 0) return;

        TextPaint paint = new TextPaint(getPaint());
        paint.setTextSize(mDefaultTextSize);

        float ts = mDefaultTextSize;
        for (float mw = paint.measureText(text); ts > mMinSize && mw > mContentWidth; ) {
            paint.setTextSize(--ts);
            mw = paint.measureText(text);
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, ts);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentWidth = w - getPaddingLeft() - getPaddingRight();
        resize(getText());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        resize(text);
    }
}
