package net.oschina.app.improve.main.subscription;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 新版栏目问答
 * Created by haibin
 * on 2016/10/27.
 */

class QuestionSubAdapter extends BaseGeneralRecyclerAdapter<SubBean> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {
    private OSCApplication.ReadState mReadState;

    QuestionSubAdapter(Callback callback, int mode) {
        super(callback, mode);
        mReadState = OSCApplication.getReadState("sub_list");
        setOnLoadingHeaderCallBack(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderViewHolder(mHeaderView);
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new QuestionViewHolder(mInflater.inflate(R.layout.item_list_sub_question, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, SubBean item, int position) {
        QuestionViewHolder vh = (QuestionViewHolder) holder;

        Author author = item.getAuthor();

        mCallBack.getImgLoader()
                .load(author != null ? author.getPortrait() : "")
                .asBitmap().placeholder(R.mipmap.widget_default_face)
                .into(vh.iv_question);

        vh.tv_question_title.setText(item.getTitle());
        vh.tv_question_content.setText(item.getBody());

        Resources resources = mContext.getResources();

        if (mReadState.already(item.getKey())) {
            vh.tv_question_title.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
            vh.tv_question_content.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
        } else {
            vh.tv_question_title.setTextColor(TDevice.getColor(resources, R.color.text_title_color));
            vh.tv_question_content.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
        }

        String authorName;
        if (author != null && !TextUtils.isEmpty(authorName = author.getName())) {
            vh.tv_time.setText(String.format("@%s %s",
                    (authorName.length() > 9 ? authorName.substring(0, 9) : authorName),
                    StringUtils.formatSomeAgo(item.getPubDate())));
        } else {
            vh.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        }

        vh.tv_view.setText(String.valueOf(item.getStatistics().getView()));
        vh.tv_comment_count.setText(String.valueOf(item.getStatistics().getComment()));
    }

    private static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question_title, tv_question_content, tv_time, tv_comment_count, tv_view;
        CircleImageView iv_question;

        QuestionViewHolder(View itemView) {
            super(itemView);
            tv_question_title = (TextView) itemView.findViewById(R.id.tv_question_title);
            tv_question_content = (TextView) itemView.findViewById(R.id.tv_question_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_info_comment);
            tv_view = (TextView) itemView.findViewById(R.id.tv_info_view);
            iv_question = (CircleImageView) itemView.findViewById(R.id.iv_question);
        }
    }
}
