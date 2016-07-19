package net.oschina.app.improve.tweet.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.base.fragments.BaseListFragment;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.tweet.adapter.SoftwareTweetAdaper;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class SoftWareForTweetsFragment extends BaseListFragment<Tweet> implements AdapterView.OnItemLongClickListener {

    public static final String BUNDLE_KEY_ID = "BUNDLE_KEY_ID";
    public static final String BUNDLE_KEY_NAME = "bundle_key_name";
    private static final String CACHE_KEY_PREFIX = "software_tweet_list";
    private static final int MAX_TEXT_LENGTH = 160;
    private static final String TAG = "SoftWareForTweetsFragment";


    private EditText inputEditText;
    private String softwareName;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_software_tweets;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        if (bundle != null) {
            //mId = bundle.getInt(BUNDLE_KEY_ID, 0);
            softwareName = bundle.getString(BUNDLE_KEY_NAME, null);
            Log.d(TAG, "initBundle: ---->" + softwareName);
        }
        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);
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
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        Log.d(TAG, "onRequestSuccess: ---->");
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Tweet>>>() {
        }.getType();
    }

    @Override
    protected void initData() {
        super.initData();

        Log.d(TAG, "initData: ----->");
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        if (!root.isFocusable())
            root.setFocusable(true);
        if (!root.isFocusableInTouchMode())
            root.setFocusableInTouchMode(true);
        if (!root.isFocused()) {
            root.requestFocus();
        }

        Log.d(TAG, "initWidget: ");
        inputEditText = (EditText) root.findViewById(R.id.software_editText);
        mListView = (ListView) root.findViewById(R.id.listView);
        mListView.setOnItemLongClickListener(this);
        inputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return sendTweet(actionId);
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

    private boolean sendTweet(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {

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
                return true;
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

            OSChinaApi.pubSoftwareTweet(input + "# " + softwareName + "#", new TextHttpResponseHandler("UTF-8") {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getActivity(), "发布失败...", Toast.LENGTH_SHORT).show();
                    TDevice.showSoftKeyboard(inputEditText);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    try {
                        Type type = new TypeToken<ResultBean<Tweet>>() {
                        }.getType();
                        ResultBean<Tweet> resultBean = AppContext.createGson().fromJson(responseString, type);

                        if (resultBean != null && resultBean.isSuccess()) {
                            inputEditText.setText(null);
                            Toast.makeText(getActivity(), "发布成功...", Toast.LENGTH_SHORT).show();
                            TDevice.hideSoftKeyboard(inputEditText);
                            //sendRequestData();
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

    @Override
    protected void requestData() {
        super.requestData();
        Log.e(TAG, "requestData: ---->" + softwareName);
        OSChinaApi.getSoftwareTweetList(softwareName, mIsRefresh ? mBean.getPrevPageToken() : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected BaseListAdapter<Tweet> getListAdapter() {
        return new SoftwareTweetAdaper(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // Tweet tweet = mAdapter.getItem(position);
        //if (tweet != null)
        //  UIHelper.showTweetDetail(parent.getContext(), tweet, tweet.getId());
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        final Tweet tweet = mAdapter.getItem(position);
        final long sourceId = tweet.getId();

        DialogHelp.getConfirmDialog(getActivity(), "是否删除该条软件动弹?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                            if (resultBean.getCode() == 1) {
                                Toast.makeText(getContext(), "删除成功...", Toast.LENGTH_SHORT).show();
                                //sendRequestData();
                            } else {
                                Toast.makeText(getContext(), "删除失败...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                    }
                });

            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
        return true;
    }
}
