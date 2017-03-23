package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
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
    private ShapeDrawable mShapeDrawable;

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
        setPadding(padding, padding, padding, padding);
        setTextSize(11);
        setGravity(Gravity.CENTER);
        setSingleLine(true);
        setLines(1);
        setText("开源中国官方人员");

        if (isInEditMode()) {
            Author.Identity identity = new Author.Identity();
            identity.officialMember = true;
            setup(identity);
        }
    }

    public void setup(Author.Identity identity) {
        setup(identity, 0xff24CF5F);
    }

    public void setup(Author.Identity identity, int color) {
        if (identity == null) {
            setVisibility(GONE);
            return;
        }

        identity.officialMember = true;

        this.mIdentity = identity;
        setVisibility(identity.officialMember ? VISIBLE : GONE);
        initView(color);
    }

    private void initView(int color) {
        if (!mIdentity.officialMember)
            return;

        if (mShapeDrawable == null) {
            final float border = TDevice.dipToPx(getResources(), 2);
            final float[] innerRadii = new float[]{border, border, border, border, border, border, border, border};
            RoundRectShape shape = new RoundRectShape(innerRadii, null, null);
            ShapeDrawable drawable = new ShapeDrawable(shape);
            final Paint paint = drawable.getPaint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(color);
            paint.setDither(true);
            paint.setAntiAlias(true);
            mShapeDrawable = drawable;
        } else {
            mShapeDrawable.getPaint().setColor(color);
        }

        setBackground(mShapeDrawable);
        setTextColor(color);
    }
}
