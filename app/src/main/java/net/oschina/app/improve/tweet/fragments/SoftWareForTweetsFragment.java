package net.oschina.app.improve.tweet.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.TweetAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetsList;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class SoftWareForTweetsFragment extends BaseListFragment<Tweet> implements AdapterView.OnItemLongClickListener {

    public static final String BUNDLE_KEY_ID = "BUNDLE_KEY_ID";
    public static final String BUNDLE_KEY_NAME = "bundle_key_id";
    //protected static final String TAG = SoftwareForTweetsFrament.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "software_tweet_list";
    private static final int MAX_TEXT_LENGTH = 160;
    private static final String TAG = "SoftWareForTweetsFragment";

    private int mId;

    private EditText inputEditText;
    private String softwareName;

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
        mListView.setOnItemLongClickListener(this);
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

                    final String input = inputEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(input)) {
                        AppContext.showToastShort(R.string.tip_comment_content_empty);
                        return false;
                    }

                    if (input.length() > MAX_TEXT_LENGTH) {
                        AppContext.showToastShort(R.string.tip_content_too_long);
                        return false;
                    }

                    // Tweet tweet = new Tweet();
                    //tweet.setAuthorid(AppContext.getInstance().getLoginUid());
                    //tweet.setBody(input + softwareName);
                    // ServerTaskUtils.pubSoftWareTweet(getActivity(), tweet, mId);
                    //  inputEditText.setText(null);

                    OSChinaApi.pubSoftwareTweet(input + softwareName, new TextHttpResponseHandler("UTF-8") {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getActivity(), "发布失败...", Toast.LENGTH_SHORT).show();
                            TDevice.showSoftKeyboard(inputEditText);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            try {
                                Type type = new TypeToken<ResultBean<net.oschina.app.improve.bean.Tweet>>() {
                                }.getType();
                                ResultBean<net.oschina.app.improve.bean.Tweet> resultBean = AppContext.createGson().fromJson(responseString, type);

                                if (resultBean != null && resultBean.isSuccess()) {
                                    inputEditText.setText(null);
                                    Toast.makeText(getActivity(), "发布成功...", Toast.LENGTH_SHORT).show();
                                    TDevice.hideSoftKeyboard(inputEditText);
                                } else {
                                    Toast.makeText(getActivity(), "发布失败...", Toast.LENGTH_SHORT).show();
                                    TDevice.showSoftKeyboard(inputEditText);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                onFailure(statusCode, headers, responseString, e);
                            }

                        }

                    });

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
            softwareName = args.getString(BUNDLE_KEY_NAME, null);
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


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        final Tweet tweet = mAdapter.getItem(position);
        int sourceId = tweet.getId();

        OSChinaApi.delSoftwareTweet(sourceId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), "删除失败...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        Toast.makeText(getContext(), "删除成功...", Toast.LENGTH_SHORT).show();
                        mAdapter.removeItem(tweet);
                    } else {
                        Toast.makeText(getContext(), "删除失败...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });

        return true;
    }
}
