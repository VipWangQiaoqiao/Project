package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class DetailAboutView extends LinearLayout {
    private int mDefaultType;
    private LinearLayout mLayAbouts;
    private TextView mTitle;

    public DetailAboutView(Context context) {
        super(context);
        init();
    }

    public DetailAboutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailAboutView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View inflate = inflater.inflate(R.layout.lay_detail_about_layout, this, true);

        mTitle = (TextView) inflate.findViewById(R.id.tv_blog_detail_about);
        mLayAbouts = (LinearLayout) findViewById(R.id.lay_blog_detail_about);
    }

    /**
     * set title
     *
     * @param title string
     */
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setAbout(List<About> abouts, int defaultType) {
        mLayAbouts.removeAllViews();
        this.mDefaultType = defaultType;
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        if (abouts != null && abouts.size() > 0) {
            int size = abouts.size();
            for (final About about : abouts) {
                if (about == null)
                    continue;
                @SuppressLint("InflateParams")
                View lay = inflater.inflate(R.layout.lay_blog_detail_about, null, false);
                ((TextView) lay.findViewById(R.id.tv_title)).setText(about.getTitle());

                View layInfo = lay.findViewById(R.id.lay_info_view_comment);
                layInfo.findViewById(R.id.iv_info_view).setVisibility(GONE);
                ((TextView) layInfo.findViewById(R.id.tv_info_view)).setVisibility(GONE);//setText(String.valueOf(about.getViewCount()));
                ((TextView) layInfo.findViewById(R.id.tv_info_comment)).setText(String.valueOf(about.getCommentCount()));

                if (--size == 0) {
                    lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }

                lay.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int type = mDefaultType;
                        if (about.getType() != 0) {
                            type = about.getType();
                        }
                        UIHelper.showDetail(v.getContext(), type, about.getId(), null);
                    }
                });

                mLayAbouts.addView(lay);
            }
        } else {
            setVisibility(View.GONE);
        }
    }
}
