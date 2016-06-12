package net.oschina.app.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.bean.blog.BlogDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class GeneralDetailFooterView extends LinearLayout {
    private List<Object> objects = new ArrayList<>();
    private int mIndexOfAbout = -1;
    private int mIndexOfComments = -1;

    public GeneralDetailFooterView(Context context) {
        super(context);
    }

    public GeneralDetailFooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GeneralDetailFooterView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addAbout(List<BlogDetail.About> abouts) {
        if (mIndexOfAbout == -1) {
            objects.add("相关推荐");
            objects.addAll(abouts);
            mIndexOfAbout = abouts.size();
        } else {
            objects.addAll(mIndexOfAbout, abouts);
            mIndexOfAbout += abouts.size();
        }
        if (mIndexOfComments != -1) {
            mIndexOfComments = objects.size() - 1;
        }
    }

    public void addComment(List<BlogDetail.Comment> comments) {
        if (mIndexOfComments == -1) {
            objects.add("评论");
            objects.addAll(comments);
        } else {
            objects.addAll(mIndexOfComments, comments);
        }
        mIndexOfComments = objects.size() - 1;
    }
}
