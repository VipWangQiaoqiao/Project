package net.oschina.app.adapter.general;

import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.Post;
import net.oschina.app.widget.CircleImageView;

/**
 * Created by fei on 2016/5/24.
 * desc:
 */
public class PostAdapter extends BaseListAdapter<Post> {

    public PostAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Post item, int position) {

        CircleImageView face = vh.getView(R.id.iv_ques_item_icon);
        face.setImageResource(R.drawable.widget_dface);
        TextView title = vh.getView(R.id.tv_ques_item_title);
        title.setText(item.getTitle());
        TextView content = vh.getView(R.id.tv_ques_item_content);
        content.setText(item.getPortrait());
        TextView history = vh.getView(R.id.tv_ques_item_history);
        history.setText(item.getPubDate());
        TextView see = vh.getView(R.id.tv_ques_item_see);
        see.setText(item.getViewCount() + "");
        TextView answer = vh.getView(R.id.tv_ques_item_answer);
        answer.setText(item.getAnswerCount() + "");

    }

    @Override
    protected int getLayoutId(int position, Post item) {
        return R.layout.fragment_item_question;
    }

}
