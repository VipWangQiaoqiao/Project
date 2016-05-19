package net.oschina.app.fragment.general;

import net.oschina.app.adapter.PostAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Post;

/**
 * 技术问答界面
 */
public class QuestionFragment extends BaseListFragment<Post> {
    @Override
    protected ListBaseAdapter<Post> getListAdapter() {
        return new PostAdapter();
    }
}
