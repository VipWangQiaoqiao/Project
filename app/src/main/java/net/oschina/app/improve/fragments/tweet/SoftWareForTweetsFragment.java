package net.oschina.app.improve.fragments.tweet;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.TweetAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetsList;
import net.oschina.app.improve.activities.TweetDetailActivity;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

public class SoftWareForTweetsFragment extends BaseListFragment<Tweet> implements
        OnItemLongClickListener {

    public static final String BUNDLE_KEY_ID = "BUNDLE_KEY_ID";
    //protected static final String TAG = SoftwareForTweetsFrament.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "software_tweet_list";
    private static final int MAX_TEXT_LENGTH = 160;

    private int mId;

    EditText inputEditText;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_software_tweets;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activity.findViewById(R.id.emoji_container).setVisibility(
                    View.VISIBLE);
        } catch (NullPointerException e) {
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        inputEditText = (EditText) view.findViewById(R.id.software_editText);
        if (!view.isFocusable())
            view.setFocusable(true);
        if (!view.isFocusableInTouchMode())
            view.setFocusableInTouchMode(true);
        if (!view.isFocused()) {
            view.requestFocus();
        }
        inputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    // Log.d(TAG, "onEditorAction: ----->" + inputEditText.getText().toString().trim());
                    //发布一条软件动态

                    if (!AppContext.getInstance().isLogin()) {
                        UIHelper.showLoginActivity(getActivity());
                        return false;
                    }
                    if (!TDevice.hasInternet()) {
                        AppContext.showToastShort(R.string.tip_no_internet);
                        return false;
                    }

                    String input = inputEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(input)) {
                        AppContext.showToastShort(R.string.tip_comment_content_empty);
                        return false;
                    }

                    if (input.length() > MAX_TEXT_LENGTH) {
                        AppContext.showToastShort(R.string.tip_content_too_long);
                        return false;
                    }

                    Tweet tweet = new Tweet();
                    tweet.setAuthorid(AppContext.getInstance().getLoginUid());
                    tweet.setBody(input);
                    ServerTaskUtils.pubSoftWareTweet(getActivity(), tweet, mId);
                    inputEditText.setText(null);
                    TDevice.hideSoftKeyboard(inputEditText);
                    Toast.makeText(getActivity(), "发布成功...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        inputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });
        mListView.setOnItemLongClickListener(this);
    }

    private boolean mInputDoubleEmpty = false;

    private void handleKeyDel() {
        if (TextUtils.isEmpty(inputEditText.getText().toString().trim())) {
            if (mInputDoubleEmpty) {
                inputEditText.setHint("发表评论");
            } else {
                mInputDoubleEmpty = true;
            }
        } else {
            mInputDoubleEmpty = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        return CACHE_KEY_PREFIX + "_" + mId;
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
    protected void sendRequestData() {
        OSChinaApi.getSoftTweetList(mId, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final Tweet tweet = mAdapter.getItem(position);
        if (tweet == null) {
            return;
        }
//        UIHelper.showTweetDetail(parent.getContext(), tweet, tweet.getId());
        TweetDetailActivity.show(getActivity(), tweet);
    }

    private void handleComment(String text) {
        Tweet tweet = new Tweet();
        tweet.setAuthorid(AppContext.getInstance().getLoginUid());
        tweet.setBody(text);
        ServerTaskUtils.pubSoftWareTweet(getActivity(), tweet, mId);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        return true;
    }
}
