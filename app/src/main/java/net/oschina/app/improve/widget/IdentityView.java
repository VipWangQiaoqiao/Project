package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.TDevice;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class IdentityView extends AppCompatTextView {
    private Author.Identity mIdentity;
    private GradientDrawable mDrawable;

    public IdentityView(Context context) {
        super(context);
        init();
    }

    public IdentityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IdentityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVisibility(GONE);
        final int padding = (int) TDevice.dipToPx(getResources(), 2);
        setPadding(padding + padding, padding, padding + padding, padding);
        setTextSize(10);
        setGravity(Gravity.CENTER);
        setSingleLine(true);
        setLines(1);
        setText("官方人员");

        if (isInEditMode()) {
            Author.Identity identity = new Author.Identity();
            identity.officialMember = true;
            setup(identity);
        }
    }

    public void setup(Author author) {
        if (author == null)
            setup((Author.Identity) null);
        else
            setup(author.getIdentity());
    }

    public void setup(Author author, int color) {
        if (author == null)
            setup((Author.Identity) null, color);
        else
            setup(author.getIdentity(), color);
    }

    public void setup(Author.Identity identity) {
        setup(identity, 0xff24CF5F);
    }

    public void setup(Author.Identity identity, int color) {
        if (identity == null) {
            setVisibility(GONE);
            return;
        }
        this.mIdentity = identity;
        setVisibility(identity.officialMember ? VISIBLE : GONE);
        initView(color);
    }

    private void initView(int color) {
        if (!mIdentity.officialMember)
            return;

        if (mDrawable == null) {
            float border = getHeight() / 2f;
            if (border <= 0)
                border = TDevice.dipToPx(getResources(), 4);

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setDither(true);
            gradientDrawable.setStroke(2, color);
            gradientDrawable.setCornerRadius(border);

            mDrawable = gradientDrawable;
        } else {
            mDrawable.setStroke(2, color);
        }

        setBackground(mDrawable);
        setTextColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDrawable != null) {
            mDrawable.setCornerRadius(4);
        }
    }
}
