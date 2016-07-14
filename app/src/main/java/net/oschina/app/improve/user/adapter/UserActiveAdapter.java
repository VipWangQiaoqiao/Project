package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.bean.Active;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.ui.OSCPhotosActivity;
import net.oschina.app.util.BitmapHelper;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.utils.DensityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos on 16/7/14.
 */
public class UserActiveAdapter extends BaseRecyclerAdapter<Active> {

    private final static String AT_HOST_PRE = "http://my.oschina.net";
    private final static String MAIN_HOST = "http://www.oschina.net";
    private Bitmap recordBitmap;
    private int rectSize;

    public UserActiveAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_cell_active, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Active item, int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.name.setText(item.getAuthor());

        vh.action.setText(UIHelper.parseActiveAction(item.getObjectType(),
                item.getObjectCatalog(), item.getObjectTitle()));

        if (TextUtils.isEmpty(item.getMessage())) {
            vh.body.setVisibility(View.GONE);
        } else {
            vh.body.setMovementMethod(MyLinkMovementMethod.a());
            vh.body.setFocusable(false);
            vh.body.setDispatchToParent(true);
            vh.body.setLongClickable(false);

            Spanned span = Html.fromHtml(modifyPath(item.getMessage()));

            if (!StringUtils.isEmpty(item.getTweetattach())) {
                if (recordBitmap == null) {
                    initRecordImg(mContext);
                }
                ImageSpan recordImg = new ImageSpan(mContext, recordBitmap);
                SpannableString str = new SpannableString("c");
                str.setSpan(recordImg, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                vh.body.setText(str);
                span = InputHelper.displayEmoji(mContext.getResources(), span);
                vh.body.append(span);
            } else {
                span = InputHelper.displayEmoji(mContext.getResources(), span);
                vh.body.setText(span);
            }
            MyURLSpan.parseLinkText(vh.body, span);
        }

        Active.ObjectReply reply = item.getObjectReply();
        if (reply != null) {
            vh.reply.setMovementMethod(MyLinkMovementMethod.a());
            vh.reply.setFocusable(false);
            vh.reply.setDispatchToParent(true);
            vh.reply.setLongClickable(false);
            Spanned span = UIHelper.parseActiveReply(reply.objectName,
                    reply.objectBody);
            vh.reply.setText(span);//
            MyURLSpan.parseLinkText(vh.reply, span);
            vh.lyReply.setVisibility(TextView.VISIBLE);
        } else {
            vh.reply.setText("");
            vh.lyReply.setVisibility(TextView.GONE);
        }

        vh.time.setText(StringUtils.friendly_time(item.getPubDate()));

        PlatfromUtil.setPlatFromString(vh.from, item.getAppClient());

        vh.commentCount.setText(item.getCommentCount() + "");

        vh.avatar.setUserInfo(item.getAuthorId(), item.getAuthor());
        vh.avatar.setAvatarUrl(item.getPortrait());

        if (!TextUtils.isEmpty(item.getTweetimage())) {
            setTweetImage(null, vh, item);
        } else {
            vh.pic.setVisibility(View.GONE);
            vh.pic.setImageBitmap(null);
        }
    }

    /**
     * 动态设置图片显示样式
     */
    private void setTweetImage(final ViewGroup parent, final ViewHolder vh,
                               final Active item) {
        vh.pic.setVisibility(View.VISIBLE);

        new Core.Builder().url(item.getTweetimage()).view(vh.pic).loadBitmapRes(R.drawable
                .pic_bg).size(rectSize, rectSize).bitmapCallBack(new BitmapCallBack() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                super.onSuccess(bitmap);
                if (bitmap != null) {
                    bitmap = BitmapHelper.scaleWithXY(bitmap, rectSize / bitmap.getHeight());
                    vh.pic.setImageBitmap(bitmap);
                }
            }
        }).doTask();

        vh.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OSCPhotosActivity.showImagePreview(mContext, getOriginalUrl(item.getTweetimage()));
            }
        });
    }

    private String modifyPath(String message) {
        message = message.replaceAll("(<a[^>]+href=\")/([\\S]+)\"", "$1"
                + AT_HOST_PRE + "/$2\"");
        message = message.replaceAll(
                "(<a[^>]+href=\")http://m.oschina.net([\\S]+)\"", "$1"
                        + MAIN_HOST + "$2\"");
        return message;
    }

    private void initRecordImg(Context cxt) {
        recordBitmap = BitmapFactory.decodeResource(cxt.getResources(),
                R.drawable.audio3);
        recordBitmap = ImageUtils.zoomBitmap(recordBitmap,
                DensityUtils.dip2px(cxt, 20f), DensityUtils.dip2px(cxt, 20f));
    }

    private void initImageSize(Context cxt) {
        if (cxt != null && rectSize == 0) {
            rectSize = (int) cxt.getResources().getDimension(R.dimen.space_100);
        } else {
            rectSize = 300;
        }
    }

    private String getOriginalUrl(String url) {
        return url.replaceAll("_thumb", "");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView name;
        @Bind(R.id.tv_from)
        TextView from;
        @Bind(R.id.tv_time)
        TextView time;
        @Bind(R.id.tv_action)
        TextView action;
        @Bind(R.id.tv_action_name)
        TextView actionName;
        @Bind(R.id.tv_comment_count)
        TextView commentCount;
        @Bind(R.id.tv_body)
        TweetTextView body;
        @Bind(R.id.tv_reply)
        TweetTextView reply;
        @Bind(R.id.iv_pic)
        ImageView pic;
        @Bind(R.id.ly_reply)
        View lyReply;
        @Bind(R.id.iv_avatar)
        AvatarView avatar;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
