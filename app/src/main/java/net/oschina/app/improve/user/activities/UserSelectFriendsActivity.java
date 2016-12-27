package net.oschina.app.improve.user.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.adapter.UserSearchFriendsAdapter;
import net.oschina.app.improve.user.adapter.UserSelectFriendsAdapter;
import net.oschina.app.improve.user.bean.UserFansOrFollows;
import net.oschina.app.improve.user.bean.UserFriends;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.IndexView;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

import static net.oschina.app.api.remote.OSChinaApi.TYPE_USER_FOLOWS;

/**
 * Created by fei
 * on 2016/12/22.
 * desc:
 */

public class UserSelectFriendsActivity extends BaseBackActivity implements IndexView.OnIndexTouchListener {

    private static final String TAG = "UserSelectFriendsActivity";

    private PageBean<UserFansOrFollows> mPageBean;

    @Bind(R.id.searcher_friends)
    SearchView mSearchView;
    @Bind(R.id.recycler_friends_icon)
    HorizontalScrollView mRecyclerFriendsIcon;

    @Bind(R.id.tv_label)
    TextView mTvLabel;

    @Bind(R.id.recycler_friends)
    RecyclerView mRecyclerFriends;
    @Bind(R.id.tv_index_show)
    TextView mTvIndexShow;

    @Bind(R.id.lay_index)
    IndexView mLayIndexContainer;

    @Bind(R.id.lay_error)
    EmptyLayout mEmptyLayout;

    //网络初始化的adapter
    private UserSelectFriendsAdapter mLocalAdapter;

    //网络初始化的朋友数据
    private ArrayList<UserFriends> mNetFriends;

    private UserSearchFriendsAdapter mSearchAdapter;

    private ArrayList<UserFriends> mSearchFriends;

    private UserFriends mUserFriend;


    public static void show(Context context) {
        Intent intent = new Intent(context, UserSelectFriendsActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_main_user_select_friends;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRecyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerFriends.setAdapter(mLocalAdapter = new UserSelectFriendsAdapter(this));

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyLayout emptyLayout = mEmptyLayout;
                if (emptyLayout != null && emptyLayout.getErrorState() != EmptyLayout.HIDE_LAYOUT) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    requestData();
                }
            }
        });

        mLayIndexContainer.setOnIndexTouchListener(this);

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e(TAG, "onClose: ---->");
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: ---->" + query);
                return false;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextChange(String newText) {

                Log.e(TAG, "onQueryTextChange: -------->" + newText);
                mTvLabel.setVisibility(TextUtils.isEmpty(newText) ? View.GONE : View.VISIBLE);
                mTvLabel.setText("@" + newText);

                queryUpdateView(newText);


                return true;
            }
        });

        mSearchView.post(new Runnable() {
            @SuppressWarnings("RestrictedApi")
            @Override
            public void run() {
                //                TDevice.showSoftKeyboard(mViewSearch);
                mSearchView.clearFocus();

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestData();
    }

    private boolean checkNetIsAvailable() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(getString(R.string.tip_network_error));
            showError(EmptyLayout.NETWORK_ERROR);
            return false;
        }
        return true;
    }

    private void hideLoading() {
        final EmptyLayout emptyLayout = mEmptyLayout;
        if (emptyLayout == null)
            return;
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_to_hide);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        emptyLayout.startAnimation(animation);
    }

    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
        }
    }

    private void requestData() {

        //检查网络
        if (!checkNetIsAvailable()) {
            showError(EmptyLayout.NETWORK_ERROR);
            return;
        }

        OSChinaApi.getUserFansOrFlows(TYPE_USER_FOLOWS, AccountHelper.getUserId(), mPageBean == null ?
                null : mPageBean.getNextPageToken(), new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Type type = new TypeToken<ResultBean<PageBean<UserFansOrFollows>>>() {
                }.getType();

                ResultBean<PageBean<UserFansOrFollows>> resultBean = AppOperator.createGson().fromJson(responseString, type);

                if (resultBean.isSuccess()) {

                    List<UserFansOrFollows> fansOrFollows = resultBean.getResult().getItems();

                    if (fansOrFollows.size() > 0) {

                        updateView(fansOrFollows);

                        hideLoading();

                        mPageBean = resultBean.getResult();
                    } else {
                        showError(EmptyLayout.NODATA);
                    }


                } else {
                    showError(EmptyLayout.NODATA);
                }

            }
        });

    }

    private void queryUpdateView(String queryText) {

        if (mSearchFriends == null)
            this.mSearchFriends = new ArrayList<>();

        ArrayList<UserFriends> userFriends = this.mNetFriends;
        userFriends.trimToSize();

        if (mUserFriend == null) {
            UserFriends userFriend = new UserFriends();
            userFriend.setShowLabel("本地搜索结果");
            userFriend.setShowViewType(UserSearchFriendsAdapter.INDEX_TYPE);
            userFriends.set(0, userFriend);
            this.mUserFriend = userFriend;
        }

        if (mSearchAdapter == null) {
            this.mSearchAdapter = new UserSearchFriendsAdapter(this);
            mRecyclerFriends.setAdapter(mSearchAdapter);
        }

        for (UserFriends friend : userFriends) {
            String name = friend.getName();

            if (TextUtils.isEmpty(name)) continue;

            if (name.contains(queryText)) {
                friend.setShowLabel("");
                mSearchFriends.add(friend);
            }
        }

        mSearchAdapter.addItems(mSearchFriends);

    }

    private void updateView(List<UserFansOrFollows> fansOrFollows) {

        if (mNetFriends == null)
            mNetFriends = new ArrayList<>();

        ArrayList<String> holdIndexes = new ArrayList<>();


        for (int i = fansOrFollows.size() - 1; i > 0; i--) {
            UserFansOrFollows fansOrFollow = fansOrFollows.get(i);

            //获得字符串
            String name = fansOrFollow.getName().trim();

            if (TextUtils.isEmpty(name)) continue;

            //返回小写拼音
            String pinyin = returnPinyin(name);
            String label = pinyin.substring(0, 1);

            //判断是否hold住相同字母开头的数据
            if (!holdIndexes.contains(label)) {

                UserFriends userFriends = new UserFriends();
                userFriends.setShowLabel(label.matches("[a-zA-Z_]+") ? label : "#");
                userFriends.setShowViewType(UserSelectFriendsAdapter.INDEX_TYPE);

                mNetFriends.add(userFriends);

                //加入hold
                holdIndexes.add(label);
            }

            UserFriends userFriends = new UserFriends();
            userFriends.setId(fansOrFollow.getId());
            userFriends.setShowLabel(pinyin);
            userFriends.setName(fansOrFollow.getName());
            userFriends.setShowViewType(UserSelectFriendsAdapter.USER_TYPE);
            userFriends.setPortrait(fansOrFollow.getPortrait());

            mNetFriends.add(userFriends);
        }

        holdIndexes.clear();

        Log.e(TAG, "User Friends Size: ------->" + mNetFriends.size());
        //自然排序
        Collections.sort(mNetFriends);

        mLocalAdapter.addItems(mNetFriends);
    }

    public String returnPinyin(String input) {

        StringBuilder sb = new StringBuilder(0);

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] charArray = input.toLowerCase().toCharArray();

        for (char c : charArray) {
            String tempC = Character.toString(c);
            if (tempC.matches("[\u4E00-\u9FA5]+")) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    sb.append(temp[0]);
                    break;
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            } else {
                sb.append(tempC);
                break;
            }
        }

        //Log.e(TAG, "returnPinyin: ---->" + sb.toString());

        return sb.toString().toUpperCase();
    }


    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public void onIndexTouchMove(char indexLetter) {
        Log.e(TAG, "onIndexTouchMove: ------>" + indexLetter);

        ArrayList<UserFriends> userFriends = this.mNetFriends;
        userFriends.trimToSize();
        int position = 0;
        for (int i = userFriends.size() - 1; i > 0; i--) {
            UserFriends friend = userFriends.get(i);
            if (friend.equals(Character.toString(indexLetter))) {
                position = i;
                break;
            }

        }

        mRecyclerFriends.smoothScrollToPosition(position);

        mTvIndexShow.setText(Character.toString(indexLetter));
        mTvIndexShow.setVisibility(View.VISIBLE);
    }

    @Override
    public void onIndexTouchUp() {
        mTvIndexShow.setVisibility(View.GONE);
    }

    @SuppressWarnings("RestrictedApi")
    @Override
    protected void onStop() {
        super.onStop();
        mSearchView.clearFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed: --->");
    }
}
