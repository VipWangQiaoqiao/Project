package net.oschina.app.improve.user.helper;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.OSCApplication;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.utils.parser.RichTextParser;
import net.oschina.app.util.TDevice;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ContactsCacheManager {
    private static final String USER_CACHE_NAME = "UserContactsCache";
    private static final String RECENT_CACHE_FILE = "RecentContactsCache";

    public static LinkedList<Author> getRecentCache(Context context) {
        List<User> cache = CacheManager.readListJson(context, RECENT_CACHE_FILE, User.class);
        LinkedList<Author> linkedList = new LinkedList<>();
        if (cache != null)
            linkedList.addAll(cache);
        return linkedList;
    }

    public static void addRecentCache(Author... authors) {
        if (authors == null || authors.length == 0)
            return;
        ContactsCacheManager.addRecentCache(AppContext.getInstance(), authors);
    }

    private static void addRecentCache(Context context, Author... authors) {
        final LinkedList<Author> localCache = getRecentCache(context);

        // 避免重复添加
        for (Author author : authors) {
            if (author == null
                    || author.getId() <= 0
                    || TextUtils.isEmpty(author.getName())
                    || author.getId() == AccountHelper.getUserId())
                continue;
            if (checkInContacts(localCache, author)) {
                // 移除后添加到头部
                int index = indexOfContacts(localCache, author);
                if (index >= 0) {
                    localCache.remove(index);
                    localCache.addFirst(author);
                }
            } else {
                localCache.addFirst(author);
            }
        }

        // 至多存储15条
        while (localCache.size() > 10) {
            localCache.removeLast();
        }

        CacheManager.saveToJson(context, RECENT_CACHE_FILE, localCache);
    }

    public static int indexOfContacts(List<Author> list, Author user) {
        for (Author author : list) {
            if (author.getId() == user.getId())
                return list.indexOf(author);
        }
        return -1;
    }

    public static boolean checkInContacts(List<Author> list, Author user) {
        if (list == null || user == null)
            return false;
        for (Author author : list) {
            if (author.getId() == user.getId())
                return true;
        }
        return false;
    }

    public static ArrayList<Author> getContacts() {
        return CacheManager.readListJson(AppContext.getInstance(), USER_CACHE_NAME, Author.class);
    }

    /**
     * 同步当前用户联系人信息
     */
    public static void sync() {
        SyncHelper.sync(null);
    }

    public static void sync(Runnable runnable) {
        SyncHelper.sync(runnable);
    }

    public static final String SPLIT_HEAD = "";
    public static final String DEFAULT_CHAR = "#";

    public static List<Friend> sortToFriendModel(List<Author> list) {
        ArrayList<Friend> friends = new ArrayList<>();

        if (list == null || list.size() <= 0) {
            return friends;
        }

        for (Author author : list) {
            String name = author.getName().trim();
            if (TextUtils.isEmpty(name)) continue;

            String pinyin = RichTextParser.convertToPinyin(name, SPLIT_HEAD).trim();
            String firstChar = pinyin.substring(0, 1).toUpperCase();
            firstChar = firstChar.matches("[A-Z]") ? firstChar : DEFAULT_CHAR;

            Friend friend = new Friend(author);
            friend.pinyin = pinyin;
            friend.firstChar = firstChar;

            friends.add(friend);
        }

        // 排序
        Collections.sort(friends, new Comparator<Friend>() {
            @Override
            public int compare(Friend o1, Friend o2) {
                if (o1.firstChar.equals(DEFAULT_CHAR) || o2.firstChar.equals(DEFAULT_CHAR)) {
                    return o2.firstChar.compareTo(o1.firstChar);
                }
                return o1.firstChar.compareTo(o2.firstChar);
            }
        });

        return friends;
    }


    @SuppressWarnings("WeakerAccess")
    public static class Friend {
        public Friend(Author author) {
            this.author = author;
        }

        public Friend(Author author, String firstChar) {
            this.author = author;
            this.firstChar = firstChar;
        }

        public Author author;
        public String pinyin = "";
        public String firstChar = "↑";
        public boolean isSelected;

        @Override
        public String toString() {
            return "Friend{" +
                    "author=" + author +
                    ", pinyin='" + pinyin + '\'' +
                    ", firstChar='" + firstChar + '\'' +
                    ", isSelected=" + isSelected +
                    '}';
        }
    }

    public interface SelectedTrigger<T> {
        void trigger(T t, boolean selected);

        void trigger(Author author, boolean selected);
    }

    public interface OnSelectedChangeListener {
        void tryTriggerSelected(Friend t, SelectedTrigger<Friend> trigger);
    }

    @SuppressWarnings("WeakerAccess")
    private final static class SyncHelper {
        private static SyncHelper INSTANCE = new SyncHelper();
        private boolean isSyncing = false;
        private final List<Runnable> notifies = new LinkedList<>();

        private SyncHelper() {
        }

        private void syncUserFriends(final List<Author> list, final PageBean pageBean) {
            //检查网络
            if (!TDevice.hasInternet() && !AccountHelper.isLogin()) {
                return;
            }

            long userId = AccountHelper.getUserId();
            OSChinaApi.getUserFriends(userId, pageBean == null ? null : pageBean.getNextPageToken(),
                    OSChinaApi.REQUEST_COUNT, new TextHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            syncDone(list);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String
                                responseString) {

                            Type type = new TypeToken<ResultBean<PageBean<Author>>>() {
                            }.getType();

                            ResultBean<PageBean<Author>> resultBean = AppOperator.getGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                try {
                                    List<Author> fansOrFollows = resultBean.getResult().getItems();
                                    int size = fansOrFollows.size();
                                    if (size > 0) {
                                        list.addAll(fansOrFollows);
                                        syncUserFriends(list, resultBean.getResult());
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            syncDone(list);
                        }
                    });
        }


        private void syncDone(final List<Author> list) {
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    saveCache(list);
                    notifyAllCallback();
                    isSyncing = false;
                }
            });
        }

        private void saveCache(List<Author> list) {
            CacheManager.saveToJson(OSCApplication.getInstance(), USER_CACHE_NAME, list);
        }

        public static void sync(Runnable callback) {
            // 没有网络直接返回
            if (!TDevice.hasInternet()) {
                callback.run();
                return;
            }

            if (callback != null) {
                synchronized (INSTANCE.notifies) {
                    INSTANCE.notifies.add(callback);
                }
            }

            if (INSTANCE.checkNeedLoadFromNet()) {
                if (INSTANCE.isSyncing)
                    return;
                INSTANCE.isSyncing = true;
                INSTANCE.syncUserFriends(new ArrayList<Author>(), null);
            } else {
                INSTANCE.notifyAllCallback();
            }
        }


        // 检查网络状态和缓存有效期
        private boolean checkNeedLoadFromNet() {
            String path = String.format("%s/%s.json", AppContext.getInstance().getCacheDir(), USER_CACHE_NAME);
            File file = new File(path);

            if (file.exists() && file.isFile()) {
                long lastModified = file.lastModified();
                long millis = Calendar.getInstance().getTimeInMillis();
                //天数计算
                long updateTime = (millis - lastModified) / (1000 * 60 * 60 * 24);
                //默认wifi模式下当进入联系人界面，可以2天缓存一次。其他情况下10天缓存一次
                if (TDevice.isWifiOpen()) {
                    return updateTime >= 2;
                } else {
                    return updateTime >= 10;
                }
            }

            return true;
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
}
