package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import net.oschina.app.R;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.TDevice;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class IdentityView extends AppCompatTextView {
    private static final int STROKE_SIZE = 2;
    private int mColor = 0xff24CF5F;
    private boolean mWipeOffBorder = false;
    private Author.Identity mIdentity;
    private GradientDrawable mDrawable;

    public IdentityView(Context context) {
        super(context);
        init(null, 0);
    }

    public IdentityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public IdentityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        Context context = getContext();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IdentityView, defStyleAttr, 0);
            mColor = a.getColor(R.styleable.IdentityView_oscColor, mColor);
            mWipeOffBorder = a.getBoolean(R.styleable.IdentityView_oscWipeOffBorder, mWipeOffBorder);
            a.recycle();
        }

        setVisibility(GONE);
        setTextSize(10);
        setGravity(Gravity.CENTER);
        setSingleLine(true);
        setLines(1);
        setColor(mColor);
        setText(R.string.identity_officialMember);

        final int padding = (int) TDevice.dipToPx(getResources(), 2);
        setPadding(padding + padding, padding, padding + padding, padding);

        if (isInEditMode()) {
            Author.Identity identity = new Author.Identity();
            identity.officialMember = true;
            setup(identity);
        }
    }

    public void setColor(int color) {
        mColor = color;
        final GradientDrawable drawable = mDrawable;
        if (drawable != null) {
            drawable.setStroke(STROKE_SIZE, color);
        }
        setTextColor(color);
        invalidate();
    }

    public void setup(Author author) {
        if (author == null)
            setup((Author.Identity) null);
        else
            setup(author.getIdentity());
    }

    public void setup(Author.Identity identity) {
        this.mIdentity = identity;

        if (identity == null) {
            setVisibility(GONE);
            return;
        }

        setVisibility(identity.officialMember ? VISIBLE : GONE);
        initBorder();
    }

    private void initBorder() {
        if (mWipeOffBorder || mIdentity == null || !mIdentity.officialMember) {
            mDrawable = null;
            setBackground(null);
            return;
        }

        if (mDrawable == null) {
            float radius = getHeight() / 2f;
            if (radius <= 0)
                radius = TDevice.dipToPx(getResources(), 4);

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setDither(true);
            gradientDrawable.setStroke(STROKE_SIZE, mColor);
            gradientDrawable.setCornerRadius(radius);

            mDrawable = gradientDrawable;
        } else {
            mDrawable.setStroke(STROKE_SIZE, mColor);
        }

        setBackground(mDrawable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final GradientDrawable drawable = mDrawable;
        if (drawable != null) {
            drawable.setCornerRadius(h / 2f);
        }
    }
}
