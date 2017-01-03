package net.oschina.app.improve.user.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.user.adapter.UserSelectFriendsAdapter;
import net.oschina.app.improve.user.bean.UserFansOrFollows;
import net.oschina.app.improve.user.bean.UserFriend;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.util.TDevice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * Created by fei
 * on 2017/1/3.
 * desc:
 */
public class SyncFriendHelper {

    private static final String TAG = "SyncFriendHelper";

    private Context mContext;
    private PageBean mPageBean;
    private ArrayList<UserFansOrFollows> mNetFriends;
    private ArrayList<String> holdIndexes;

    private onSyncFriendsListener mOnSyncFriendsListener;

    public SyncFriendHelper(Context context) {
        this.mContext = context;
        Log.e(TAG, "SyncFriendHelper: ----->");
    }

    public void setOnSyncFriendsListener(onSyncFriendsListener onSyncFriendsListener) {
        mOnSyncFriendsListener = onSyncFriendsListener;
    }

    public SyncFriendHelper syncUserFriends(final long userId) {

        synchronized (SyncFriendHelper.this) {

            //检查网络
            if (!TDevice.hasInternet()) {
                //syncUserFriends(user);
                return this;
            }

            OSChinaApi.getUserFriends(userId,
                    mPageBean == null ? null : mPageBean.getNextPageToken(), OSChinaApi.REQUEST_COUNT,
                    new TextHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            //syncUserFriends(userId);
                            sortData();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String
                                responseString) {

                            Type type = new TypeToken<ResultBean<PageBean<UserFansOrFollows>>>() {
                            }.getType();

                            ResultBean<PageBean<UserFansOrFollows>> resultBean = AppOperator.createGson().fromJson(responseString, type);

                            if (resultBean.isSuccess()) {

                                final List<UserFansOrFollows> fansOrFollows = resultBean.getResult().getItems();

                                if (fansOrFollows.size() > 0) {

                                    if (mNetFriends == null)
                                        mNetFriends = new ArrayList<>();

                                    mNetFriends.addAll(fansOrFollows);

                                    mPageBean = resultBean.getResult();

                                    if (mNetFriends.size() < mPageBean.getTotalResults()) {
                                        //Log.e(TAG, "onSuccess: ----->再次请求");
                                        syncUserFriends(userId);
                                    } else {
                                        //Log.e(TAG, "onSuccess: 请求完成，可以更新");
                                        sortData();
                                    }

                                }

                            }

                        }
                    });

        }

        return this;
    }

    private void sortData() {
        AppOperator.getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                ArrayList<UserFriend> cacheFriends = new ArrayList<>();

                if (holdIndexes == null)
                    holdIndexes = new ArrayList<>();

                ArrayList<UserFansOrFollows> follows = mNetFriends;

                if (follows == null || follows.size() <= 0) {

                    if (mOnSyncFriendsListener == null) return;
                    mOnSyncFriendsListener.syncFailure();
                    return;
                }

                for (UserFansOrFollows fansOrFollow : follows) {

                    //获得字符串
                    String name = fansOrFollow.getName().trim();

                    if (TextUtils.isEmpty(name)) continue;

                    //返回小写拼音
                    String pinyin = AssimilateUtils.returnPinyin(name, true);
                    String label = pinyin.substring(0, 1);

                    label = label.matches("[a-zA-Z_]+") ? label : "#";

                    //判断是否hold住相同字母开头的数据
                    if (!holdIndexes.contains(label)) {

                        //加入hold
                        holdIndexes.add(label);

                        UserFriend userFriend = new UserFriend();
                        userFriend.setShowLabel(label);
                        userFriend.setShowViewType(UserSelectFriendsAdapter.INDEX_TYPE);

                        cacheFriends.add(userFriend);
                    }

                    UserFriend userFriend = new UserFriend();
                    userFriend.setId(fansOrFollow.getId());
                    userFriend.setShowLabel(pinyin);
                    userFriend.setName(fansOrFollow.getName());
                    userFriend.setShowViewType(UserSelectFriendsAdapter.USER_TYPE);
                    userFriend.setPortrait(fansOrFollow.getPortrait());

                    cacheFriends.add(userFriend);
                }

                mNetFriends.clear();
                holdIndexes.clear();

                //自然排序
                Collections.sort(cacheFriends);

                if (cacheFriends.size() > 0) {
                    if (mOnSyncFriendsListener == null) return;
                    mOnSyncFriendsListener.syncSuccess(cacheFriends);
                } else {
                    if (mOnSyncFriendsListener == null) return;
                    mOnSyncFriendsListener.syncFailure();
                }


                CacheManager.saveToJson(mContext, UserSelectFriendsActivity.CACHE_NAME, cacheFriends);

            }
        });
    }


    public interface onSyncFriendsListener {

        void syncSuccess(ArrayList<UserFriend> friends);

        void syncFailure();

    }


}
