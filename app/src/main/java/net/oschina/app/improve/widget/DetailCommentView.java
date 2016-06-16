package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.bean.BlogDetail;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.shape.BorderShape;

import java.util.List;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class DetailCommentView extends LinearLayout {
    private LinearLayout mLayComments;

    public DetailCommentView(Context context) {
        super(context);
        init();
    }

    public DetailCommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailCommentView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lay_detail_comment_layout, this, true);

        mLayComments = (LinearLayout) findViewById(R.id.lay_blog_detail_comment);
    }

    public interface OnCommentClickListener {
        void onClick(View view, BlogDetail.Comment about);
    }

    public void setComment(List<BlogDetail.Comment> comments, int commentTotal, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener, OnClickListener seeMoreListener) {
        if (comments != null && comments.size() > 0) {

            if (comments.size() < commentTotal) {
                findViewById(R.id.tv_see_comment).setVisibility(View.GONE);
                mLayComments.findViewById(R.id.tv_see_comment).setOnClickListener(seeMoreListener);
            } else {
                findViewById(R.id.tv_see_comment).setVisibility(View.GONE);
            }

            final Resources resources = getResources();
            final LayoutInflater inflater = LayoutInflater.from(getContext());

            boolean clearLine = true;
            for (final BlogDetail.Comment comment : comments) {
                if (comment == null)
                    continue;

                @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_blog_detail_comment, null, false);
                imageLoader.load(comment.authorPortrait).error(R.drawable.widget_dface)
                        .into(((ImageView) lay.findViewById(R.id.iv_avatar)));

                ((TextView) lay.findViewById(R.id.tv_name)).setText(comment.author);

                if (clearLine) {
                    clearLine = false;
                    lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }

                TweetTextView content = ((TweetTextView) lay.findViewById(R.id.tv_content));
                formatHtml(resources, content, comment.content);

                if (comment.refer != null) {
                    // 最多5层
                    View view = getReferLayout(comment.refer, inflater, 5);
                    lay.addView(view, lay.indexOfChild(content));
                }

                ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                        StringUtils.friendly_time(comment.pubDate));

                lay.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCommentClickListener.onClick(v, comment);
                    }
                });

                mLayComments.addView(lay, 0);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("deprecation")
    private View getReferLayout(BlogDetail.Refer refer, LayoutInflater inflater, int count) {
        final Context context = getContext();

        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_blog_detail_comment_refer, null, false);
        ShapeDrawable drawable = new ShapeDrawable(new BorderShape(new RectF(Ui.dipToPx(getContext(), 1), 0, 0, 0)));
        drawable.getPaint().setColor(0xffd7d6da);
        lay.findViewById(R.id.lay_blog_detail_comment_refer).setBackgroundDrawable(drawable);

        TextView textView = ((TextView) lay.findViewById(R.id.tv_blog_detail_comment_refer));
        drawable = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, 1)));
        drawable.getPaint().setColor(0xffd7d6da);
        textView.setBackgroundDrawable(drawable);

        formatHtml(context.getResources(), textView, refer.author + ":<br>" + refer.content);


        if (refer.refer != null && (--count) > 0) {
            View view = getReferLayout(refer.refer, inflater, count);
            lay.addView(view, lay.indexOfChild(textView));
        }

        return lay;
    }


    private static void formatHtml(Resources resources, TextView textView, String str) {
        textView.setMovementMethod(MyLinkMovementMethod.a());
        textView.setFocusable(false);
        textView.setLongClickable(false);

        if (textView instanceof TweetTextView) {
            ((TweetTextView) textView).setDispatchToParent(true);
        }

        str = TweetTextView.modifyPath(str);
        Spanned span = Html.fromHtml(str);
        span = InputHelper.displayEmoji(resources, span.toString());
        textView.setText(span);
        MyURLSpan.parseLinkText(textView, span);
    }
}
