package net.oschina.app.improve.main.subscription;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 新版栏目问答
 * Created by haibin
 * on 2016/10/27.
 */

public class QuestionSubAdapter extends BaseGeneralRecyclerAdapter<SubBean> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack{

    public QuestionSubAdapter(Callback callback,int mode) {
        super(callback, mode);
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
                .asBitmap().placeholder(R.mipmap.widget_dface)
                .into(vh.iv_question);

        vh.tv_question_title.setText(item.getTitle());
        vh.tv_question_content.setText(item.getBody());

//        if (AppContext.isOnReadedPostList(cacheName, String.valueOf(item.getId()))) {
//            vh.tv_question_title.setTextColor(mContext.getResources().getColor(R.color.count_text_color_light));
//            vh.tv_question_content.setTextColor(mContext.getResources().getColor(R.color.count_text_color_light));
//        } else {
//            vh.tv_question_title.setTextColor(mContext.getResources().getColor(R.color.blog_title_text_color_light));
//            vh.tv_question_content.setTextColor(mContext.getResources().getColor(R.color.ques_bt_text_color_dark));
//        }

        if (author != null) {
            vh.tv_time.setText((author.getName().length() > 9 ? author.getName().substring(0, 9) : author.getName().trim()) + "  " + StringUtils.formatSomeAgo(item.getPubDate().trim()));
        }
        vh.tv_view.setText(String.valueOf(item.getStatistics().getView()));
        vh.tv_view.setText(String.valueOf(item.getStatistics().getComment()));
    }

    private static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question_title, tv_question_content, tv_time, tv_comment_count, tv_view;
        CircleImageView iv_question;

        public QuestionViewHolder(View itemView) {
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
