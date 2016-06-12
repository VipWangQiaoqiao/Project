package net.oschina.app.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import net.oschina.app.bean.blog.BlogDetail;

import java.util.List;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class GeneralDetailFooterView extends LinearLayout {

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

    }

    public void addComment(List<BlogDetail.Comment> comments) {

    }
}
