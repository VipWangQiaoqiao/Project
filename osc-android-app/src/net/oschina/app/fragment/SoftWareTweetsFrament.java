package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.CommentAdapter.OnOperationListener;
import net.oschina.app.adapter.TweetAdapter;
import net.oschina.app.api.OperationResponseHandler;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetsList;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public class SoftWareTweetsFrament extends BaseListFragment<Tweet> implements
        OnOperationListener, EmojiTextListener, OnItemLongClickListener {

    public static final String BUNDLE_KEY_ID = "BUNDLE_KEY_ID";
    protected static final String TAG = SoftWareTweetsFrament.class
            .getSimpleName();
    private static final String CACHE_KEY_PREFIX = "software_tweet_list";

    private int mId;

    private EmojiFragment mEmojiFragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BaseActivity act = ((BaseActivity) activity);
        FragmentTransaction trans = act.getSupportFragmentManager()
                .beginTransaction();
        mEmojiFragment = new EmojiFragment();
        mEmojiFragment.setEmojiTextListener(this);
        trans.replace(R.id.emoji_container, mEmojiFragment);
        trans.commit();
        try {
            activity.findViewById(R.id.emoji_container).setVisibility(
                    View.VISIBLE);
        } catch (NullPointerException e) {
        }
    }

    protected int getLayoutRes() {
        return R.layout.fragment_pull_refresh_listview;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getInt(BUNDLE_KEY_ID, 0);
        }

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);
    }

    @Override
    protected TweetAdapter getListAdapter() {
        return new TweetAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return new StringBuilder(CACHE_KEY_PREFIX).append("_").append(mId)
                .toString();
    }

    @Override
    protected TweetsList parseList(InputStream is) throws Exception {
        return XmlUtils.toBean(TweetsList.class, is);
    }

    @Override
    protected TweetsList readList(Serializable seri) {
        return ((TweetsList) seri);
    }

    @Override
    public boolean onBackPressed() {
        if (mEmojiFragment != null) {
            return mEmojiFragment.onBackPressed();
        }
        return super.onBackPressed();
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getSoftTweetList(mId, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        final Tweet tweet = (Tweet) mAdapter.getItem(position);
        if (tweet == null) {
            return;
        }
        UIHelper.showTweetDetail(parent.getContext(), tweet, tweet.getId());
    }

    private void handleComment(String text) {
        Tweet tweet = new Tweet();
        tweet.setAuthorid(AppContext.getInstance().getLoginUid());
        tweet.setBody(text);
        ServerTaskUtils.pubSoftWareTweet(getActivity(), tweet, mId);
        mEmojiFragment.reset();
    }

    @Override
    public void onSendClick(String text) {
	if (mId == 0) {
	    AppContext.showToast("无法获取该软件~");
	    return;
	}
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (TextUtils.isEmpty(text)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            mEmojiFragment.requestFocusInput();
            return;
        }

        handleComment(text);
    }

    class DeleteOperationResponseHandler extends OperationResponseHandler {

        DeleteOperationResponseHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccess(int code, ByteArrayInputStream is, Object[] args) {
            try {
                Result res = XmlUtils.toBean(ResultBean.class, is).getResult();
                if (res.OK()) {
                    // AppContext.showToastShort(R.string.delete_success);
                    // mAdapter.removeItem(args[0]);
                } else {
                    AppContext.showToastShort(res.getErrorMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(code, e.getMessage(), args);
            }
        }

        @Override
        public void onFailure(int code, String errorMessage, Object[] args) {
            // AppContext.showToastShort(R.string.delete_faile);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        // final Comment item = (Comment) mAdapter.getItem(position - 1);
        // if (item == null)
        // return false;
        // String[] items = new String[] {
        // getResources().getString(R.string.copy) };
        // final CommonDialog dialog = DialogHelper
        // .getPinterestDialogCancelable(getActivity());
        // dialog.setNegativeButton(R.string.cancle, null);
        // dialog.setItemsWithoutChk(items, new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id) {
        // dialog.dismiss();
        // TDevice.copyTextToBoard(HTMLSpirit.delHTMLTag(item
        // .getContent()));
        // }
        // });
        // dialog.show();
        return true;
    }

    @Override
    public void onMoreClick(Comment comment) {

    }
}
