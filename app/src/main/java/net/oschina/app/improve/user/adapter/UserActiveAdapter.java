package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Active;
import net.oschina.app.widget.TweetTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thanatos on 16/7/14.
 */
public class UserActiveAdapter extends BaseRecyclerAdapter<Active> {

    private final static String AT_HOST_PRE = "http://my.oschina.net";
    private final static String MAIN_HOST = "http://www.oschina.net";

    private RequestManager reqManager;
    private View.OnClickListener mPreviewImageCallback;

    public UserActiveAdapter(Context context, int mode) {
        super(context, mode);
        reqManager = Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_active, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Active item, int position) {
        ViewHolder vh = (ViewHolder) holder;
        /*vh.nick.setText(item.getAuthor().getName());

        vh.action.setText(UIUtil.parseActiveAction(item.getObjectType(), item.getObjectCatalog(), item.getObjectTitle()));

        if (TextUtils.isEmpty(item.getMessage())) {
            vh.content.setVisibility(View.GONE);
        } else {
            vh.content.setMovementMethod(MyLinkMovementMethod.a());
            vh.content.setFocusable(false);
            vh.content.setDispatchToParent(true);
            vh.content.setLongClickable(false);

            Spanned span = Html.fromHtml(modifyPath(item.getMessage()));

            if (!StringUtils.isEmpty(item.getTweetattach())) {
                if (mRecordBitmap == null) {
                    initRecordImg(mContext);
                }
                ImageSpan recordImg = new ImageSpan(mContext, mRecordBitmap);
                SpannableString str = new SpannableString("c");
                str.setSpan(recordImg, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                vh.content.setText(str);
                span = InputHelper.displayEmoji(mContext.getResources(), span);
                vh.content.append(span);
            } else {
                span = InputHelper.displayEmoji(mContext.getResources(), span);
                vh.content.setText(span);
            }
            MyURLSpan.parseLinkText(vh.content, span);
        }

        Active.ObjectReply reply = item.getObjectReply();
        if (reply != null) {
            vh.replyContent.setMovementMethod(MyLinkMovementMethod.a());
            vh.replyContent.setFocusable(false);
            vh.replyContent.setDispatchToParent(true);
            vh.replyContent.setLongClickable(false);
            Spanned span = UIUtil.parseActiveReply(reply.objectName,
                    reply.objectBody);
            vh.replyContent.setText(span);
            MyURLSpan.parseLinkText(vh.replyContent, span);
            vh.replyContent.setVisibility(TextView.VISIBLE);
        } else {
            vh.replyContent.setText("");
            vh.replyContent.setVisibility(TextView.GONE);
        }

        vh.time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        vh.commentCount.setText(String.valueOf(item.getCommentCount()));
        reqManager.load(item.getPortrait())
                .asBitmap()
                .placeholder(R.mipmap.widget_dface)
                .error(R.mipmap.widget_dface)
                .into(vh.portrait);

        if (!TextUtils.isEmpty(item.getTweetimage())) {
            vh.pic.setVisibility(View.VISIBLE);
            vh.pic.setTag(R.mipmap.widget_dface, item.getTweetimage());
            vh.pic.setOnClickListener(getPreviewImageCallback());
            reqManager.load(item.getTweetimage()).placeholder(R.color.grey_200).into(vh.pic);
        } else {
            vh.pic.setVisibility(View.GONE);
            vh.pic.setImageBitmap(null);
        }*/
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_nick)
        TextView nick;
        @Bind(R.id.tv_time)
        TextView time;
        @Bind(R.id.tv_action)
        TextView action;
        @Bind(R.id.tv_comment_count)
        TextView commentCount;
        @Bind(R.id.tv_content)
        TweetTextView content;
        @Bind(R.id.tv_reply)
        TweetTextView replyContent;
        @Bind(R.id.iv_pic)
        ImageView pic;
        @Bind(R.id.iv_portrait)
        CircleImageView portrait;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
