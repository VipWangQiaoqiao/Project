package net.oschina.app.improve.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.bean.comment.Refer;
import net.oschina.app.improve.bean.comment.Reply;
import net.oschina.app.improve.utils.parser.StringParser;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;
import net.oschina.common.widget.drawable.shape.BorderShape;

/**
 * Created by JuQiu
 * on 16/6/21.
 */

public final class CommentsUtil {

    @SuppressWarnings("deprecation")
    static View getReferLayout(LayoutInflater inflater, Refer[] refer, int count) {
        Context context = inflater.getContext();
        @SuppressLint("InflateParams")
        ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_comment_item_refer, null, false);
        ShapeDrawable drawable = new ShapeDrawable(new BorderShape(new RectF(TDevice.dipToPx(context.getResources(), 1), 0, 0, 0)));
        drawable.getPaint().setColor(0xffd7d6da);
        lay.findViewById(R.id.lay_blog_detail_comment_refer).setBackgroundDrawable(drawable);

        TextView textView = ((TextView) lay.findViewById(R.id.tv_blog_detail_comment_refer));
        drawable = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, 1)));
        drawable.getPaint().setColor(0xffd7d6da);
        textView.setBackgroundDrawable(drawable);
        formatHtml(context.getResources(), textView, refer[refer.length - 1 - count].getAuthor() + ":<br>" + refer[refer.length - 1 - count].getContent());
        if (count < (refer.length < 5 ? refer.length - 1 : 4)) {
            count++;
            View view = getReferLayout(inflater, refer, count);
            lay.addView(view, lay.indexOfChild(textView));
        }

        return lay;
    }

    @SuppressWarnings("deprecation")
    static View getReplyLayout(LayoutInflater inflater, Reply[] replies, int count) {
        Context context = inflater.getContext();
        @SuppressLint("InflateParams")
        ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_comment_item_refer, null, false);
        ShapeDrawable drawable = new ShapeDrawable(new BorderShape(new RectF(TDevice.dipToPx(context.getResources(), 1), 0, 0, 0)));
        drawable.getPaint().setColor(0xffd7d6da);
        lay.findViewById(R.id.lay_blog_detail_comment_refer).setBackgroundDrawable(drawable);

        TextView textView = ((TextView) lay.findViewById(R.id.tv_blog_detail_comment_refer));
        drawable = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, 1)));
        drawable.getPaint().setColor(0xffd7d6da);
        textView.setBackgroundDrawable(drawable);

        formatHtml(context.getResources(), textView, replies[replies.length - 1 - count].getAuthor().getName() + ":<br>" + replies[replies.length - 1 - count].getContent());

        if (count < (replies.length < 5 ? replies.length - 1 : 4)) {
            count++;
            View view = getReplyLayout(inflater, replies, count);
            lay.addView(view, lay.indexOfChild(textView));
        }

        return lay;
    }

    @SuppressWarnings("deprecation")
    public static void formatHtml(Resources resources, TextView textView, String str) {
        str = str.trim();

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

    @SuppressWarnings({"unused", "deprecation"})
    public static void formatHtml(Resources resources, TextView textView, String str, boolean isShare) {
        str = str.trim();

        textView.setMovementMethod(MyLinkMovementMethod.a());
        textView.setFocusable(false);
        textView.setLongClickable(false);

        if (textView instanceof TweetTextView) {
            ((TweetTextView) textView).setDispatchToParent(true);
        }

        Spanned span = StringParser.getInstance().parse(textView.getContext(),str);

        if(isShare){
            Drawable drawableLeft = resources.getDrawable(R.mipmap.ic_quote_left);
            drawableLeft.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
            ImageSpan imageSpanLeft = new ImageSpan(drawableLeft, ImageSpan.ALIGN_BASELINE);

            Drawable drawableRight = resources.getDrawable(R.mipmap.ic_quote_right);
            drawableRight.setBounds(0, 0, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight());
            ImageSpan imageSpanRight = new ImageSpan(drawableRight, ImageSpan.ALIGN_BASELINE);

            span = InputHelper.displayEmoji(resources, span.toString());
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append("[icon]  ");
            sb.append(span);
            sb.append("  [icon]");
            sb.setSpan(imageSpanLeft, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sb.setSpan(imageSpanRight, sb.length() - 6, sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            textView.setText(sb);
            textView.setTextSize(26.0f);
            textView.setText(sb);
            //MyURLSpan.parseLinkText(textView, span);
        }else {
            span = InputHelper.displayEmoji(resources, span.toString());
            textView.setText(span);
            textView.setTextSize(14.0f);
            textView.setText(span);
            //MyURLSpan.parseLinkText(textView, span);
        }
    }
}
