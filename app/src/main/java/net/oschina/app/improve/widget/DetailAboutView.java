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

import java.util.List;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class DetailAboutView extends LinearLayout {
    private LinearLayout mLayAbouts;

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
        inflater.inflate(R.layout.lay_detail_about_layout, this, true);

        mLayAbouts = (LinearLayout) findViewById(R.id.lay_blog_detail_about);
    }

    public void setAbout(List<About> abouts, final OnAboutClickListener onAboutClickListener) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        if (abouts != null && abouts.size() > 0) {
            boolean clearLine = true;
            for (final About about : abouts) {
                if (about == null)
                    continue;
                @SuppressLint("InflateParams") View lay = inflater.inflate(R.layout.lay_blog_detail_about, null, false);
                ((TextView) lay.findViewById(R.id.tv_title)).setText(about.getTitle());

                View layInfo = lay.findViewById(R.id.lay_info_view_comment);
                ((TextView) layInfo.findViewById(R.id.tv_info_view)).setText(String.valueOf(about.getViewCount()));
                ((TextView) layInfo.findViewById(R.id.tv_info_comment)).setText(String.valueOf(about.getCommentCount()));

                if (clearLine) {
                    clearLine = false;
                    lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }

                lay.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAboutClickListener.onClick(v, about);
                    }
                });

                mLayAbouts.addView(lay, 0);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    public interface OnAboutClickListener {
        void onClick(View view, About about);
    }
}
