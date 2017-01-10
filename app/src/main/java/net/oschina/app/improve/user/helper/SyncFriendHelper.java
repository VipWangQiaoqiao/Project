package net.oschina.app.improve.user.helper;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.OSCApplication;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
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

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * Created by fei
 * on 2017/1/3.
 * desc:
 */
public class SyncFriendHelper {

    private PageBean mPageBean;
    private ArrayList<UserFansOrFollows> mNetFriends = new ArrayList<>();

    private boolean isSyncing = false;

    private static SyncFriendHelper instance = new SyncFriendHelper();

    private final List<Runnable> notifies = new LinkedList<>();

    private SyncFriendHelper() {
    }

    private void syncUserFriends() {
        //检查网络
        if (!TDevice.hasInternet() && !AccountHelper.isLogin()) {
            return;
        }

        long userId = AccountHelper.getUserId();

        OSChinaApi.getUserFriends(userId,
                mPageBean == null ? null : mPageBean.getNextPageToken(), OSChinaApi.REQUEST_COUNT,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        syncDone();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String
                            responseString) {

                        Type type = new TypeToken<ResultBean<PageBean<UserFansOrFollows>>>() {
                        }.getType();

                        ResultBean<PageBean<UserFansOrFollows>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            List<UserFansOrFollows> fansOrFollows = resultBean.getResult().getItems();
                            int size = fansOrFollows.size();
                            if (size > 0) {
                                mNetFriends.addAll(fansOrFollows);
                                mPageBean = resultBean.getResult();
                                syncUserFriends();
                                return;
                            } else {
                                mPageBean = null;
                            }
                        } else {
                            mPageBean = null;
                        }
                        syncDone();
                    }
                });
    }


    private void syncDone() {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                sortData();
                notifyAllCallback();
                isSyncing = false;
            }
        });
    }

    private void sortData() {
        ArrayList<UserFriend> cacheFriends = new ArrayList<>();
        ArrayList<String> holdIndexes = new ArrayList<>();

        ArrayList<UserFansOrFollows> follows = mNetFriends;

        if (follows == null || follows.size() <= 0) {
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

        CacheManager.saveToJson(OSCApplication.getInstance(), UserSelectFriendsActivity.CACHE_NAME, cacheFriends);
    }

    public static void load(Runnable callback) {

        if (callback != null) {
            synchronized (instance.notifies) {
                instance.notifies.add(callback);
            }
        }

        if (instance.checkNeedLoadFromNet()) {
            if (instance.isSyncing)
                return;
            instance.isSyncing = true;
            instance.syncUserFriends();
        } else {
            instance.notifyAllCallback();
        }

    }

    // 缓存拿
    public static ArrayList<UserFriend> getFriends() {
        return CacheManager.readListJson(AppContext.getInstance(), UserSelectFriendsActivity.CACHE_NAME, UserFriend.class);
    }

    // 检查网络状态和缓存有效期
    private boolean checkNeedLoadFromNet() {
        String path = AppContext.getInstance().getCacheDir() + "/" + UserSelectFriendsActivity.CACHE_NAME + ".json";
        File file = new File(path);

        if (file.isFile() && file.exists()) {

            long lastModified = file.lastModified();

            long millis = Calendar.getInstance().getTimeInMillis();

            long updateTime = (millis - lastModified) / (1000 * 60 * 60 * 24);//天数计算

            if (TDevice.isWifiOpen()) {//默认wifi模式下当进入联系人界面，可以2天缓存一次。其他情况下10天缓存一次
                return updateTime >= 2;
            } else {
                return updateTime >= 10;
            }
        } else {
            return true;
        }
    }

    private void notifyAllCallback() {
        synchronized (notifies) {
            for (Runnable notify : notifies) {
                notify.run();
            }
            notifies.clear();
        }
    }


}
