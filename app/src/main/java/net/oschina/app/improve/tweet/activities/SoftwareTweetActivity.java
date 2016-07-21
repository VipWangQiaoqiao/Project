package net.oschina.app.improve.tweet.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.tweet.adapter.SoftwareTweetAdapter;
import net.oschina.app.util.DialogHelp;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

import static net.oschina.app.improve.base.adapter.BaseRecyclerAdapter.ONLY_FOOTER;

/**
 * Created by fei on 2016/7/20.
 */

public class SoftwareTweetActivity extends BaseRecyclerViewActivity<Tweet> {

    public static final String BUNDLE_KEY_NAME = "bundle_key_name";
    private static final String TAG = "SoftwareTweetActivity";
    private String softwareName;

    @Override
    protected boolean initBundle(Bundle bundle) {
        softwareName = bundle.getString(BUNDLE_KEY_NAME);
        return super.initBundle(bundle);
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getSoftwareTweetList(softwareName, mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Tweet>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Tweet> getRecyclerAdapter() {

        SoftwareTweetAdapter tweetAdapter = new SoftwareTweetAdapter(this, ONLY_FOOTER);
        tweetAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position, long itemId) {

                final Tweet tweet = mAdapter.getItem(position);
                final long sourceId = tweet.getId();

                DialogHelp.getConfirmDialog(SoftwareTweetActivity.this, "删除该条软件动弹吗?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OSChinaApi.delSoftwareTweet(sourceId, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Toast.makeText(SoftwareTweetActivity.this, "删除失败...1", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                try {
                                    Type type = new TypeToken<ResultBean>() {
                                    }.getType();
                                    ResultBean resultBean = AppContext.createGson().fromJson(responseString, type);
                                    Log.d(TAG, "onSuccess: ----->code=" + resultBean.getCode() + " ");
                                    if (resultBean.getCode() == 1) {
                                        Toast.makeText(SoftwareTweetActivity.this, "删除成功...", Toast.LENGTH_SHORT).show();
                                        requestData();
                                    } else {
                                        Toast.makeText(SoftwareTweetActivity.this, "删除失败...2", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    onFailure(statusCode, headers, responseString, e);
                                }
                            }
                        });

                    }
                }, null).create().show();

            }
        });
        return tweetAdapter;
    }

    @Override
    protected void onItemClick(Tweet item, int position) {
        super.onItemClick(item, position);
        TweetDetailActivity.show(this, item);
    }


}
