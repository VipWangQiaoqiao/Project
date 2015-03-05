package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;

import net.oschina.app.R;
import net.oschina.app.adapter.CommentAdapter.OnOperationListener;
import net.oschina.app.base.BeseHaveHeaderListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.team.bean.TeamActiveDetail;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 周报详情<br>
 * 逻辑介绍：用Listview来显示评论内容，在ListView的HeadView中添加本周报的详细内容与周报列表的item。
 * 周报的详细内容通过动态添加addView的方式
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class TeamDiaryDetail extends
        BeseHaveHeaderListFragment<Comment, TeamActiveDetail> implements
        EmojiTextListener, EmojiFragmentControl, OnOperationListener,
        OnItemClickListener, OnItemLongClickListener {

    private static final String CACHE_KEY_PREFIX = "team_diary_detail_";

    @Override
    protected View initHeaderView() {
        View headView = View.inflate(aty, R.layout.frag_dynamic_detail, null);
        return headView;
    }

    @Override
    protected String getDetailCacheKey() {
        return null;
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeamActiveDetail detailBean) {

    }

    @Override
    protected TeamActiveDetail getDetailBean(ByteArrayInputStream is) {
        return null;
    }

    @Override
    protected ListBaseAdapter<Comment> getListAdapter() {
        return null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        return false;
    }

    @Override
    public void onMoreClick(Comment comment) {

    }

    @Override
    public void onSendClick(String text) {

    }

    @Override
    protected void requestDetailData(boolean isRefresh) {

    }
}
