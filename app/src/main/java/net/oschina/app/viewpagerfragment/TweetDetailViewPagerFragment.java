package net.oschina.app.viewpagerfragment;

import android.os.Bundle;
import android.widget.Toast;

import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.bean.TweetLikeList;
import net.oschina.app.fragment.TweetLikeUsersFragment;
import net.oschina.app.improve.fragments.tweet.ListTweetCommentFragment;
import net.oschina.app.improve.fragments.tweet.ListTweetLikeUsersFragment;
import net.oschina.app.util.UIHelper;

/**
 * 赞 | 评论
 * Created by thanatos on 16/6/12.
 */
public class TweetDetailViewPagerFragment extends BaseViewPagerFragment{

    public static final String BUNDLE_KEY_TWEET_ID = "BUNDLE_KEY_TWEET_ID";
    public static final String BUNDLE_KEY_TWEET_UP_COUNT = "BUNDLE_KEY_TWEET_UP_COUNT";
    public static final String BUNDLE_KEY_TWEET_COMMENT_COUNT = "BUNDLE_KEY_TWEET_COMMENT_COUNT";

    private int tid;
    private int upCount;
    private int cmmCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tid = getArguments().getInt(BUNDLE_KEY_TWEET_ID, 0);
        upCount = getArguments().getInt(BUNDLE_KEY_TWEET_UP_COUNT, 0);
        cmmCount = getArguments().getInt(BUNDLE_KEY_TWEET_COMMENT_COUNT, 0);
        if (tid<=0){
            Toast.makeText(getContext(), "参数获取异常", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        Bundle b1 = new Bundle();
        b1.putInt(ListTweetLikeUsersFragment.BUNDLE_KEY_TWEET_ID, tid);

        Bundle b2 = new Bundle();
        b2.putInt(ListTweetCommentFragment.BUNDLE_KEY_TWEET_ID, tid);

        adapter.addTab(String.format("赞(%s)", upCount), "up", ListTweetLikeUsersFragment.class, b1);
        adapter.addTab(String.format("评论(%s)", cmmCount), "comment", ListTweetCommentFragment.class, b2);
    }
}
