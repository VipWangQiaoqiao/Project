package net.oschina.app.improve.user.activities;

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
import android.widget.Button;
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
    @Bind(R.id.bt_cancel)
    Button mBtCancel;
    @Bind(R.id.recycler_friends_icon)
    HorizontalScrollView mRecyclerFriendsIcon;
    @Bind(R.id.recycler_friends)
    RecyclerView mRecyclerFriends;
    @Bind(R.id.tv_index_show)
    TextView mTvIndexShow;

    @Bind(R.id.lay_index_container)
    IndexView mLayIndexContainer;

    @Bind(R.id.lay_error)
    EmptyLayout mEmptyLayout;
    private UserSelectFriendsAdapter adapter;
    private ArrayList<UserFriends> userFriendses;


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
        mRecyclerFriends.setAdapter(adapter = new UserSelectFriendsAdapter(this));

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

    private void updateView(List<UserFansOrFollows> fansOrFollows) {

        userFriendses = new ArrayList<>();

        List<String> holdIndexs = new ArrayList<>();

        for (int i = fansOrFollows.size() - 1; i > 0; i--) {
            UserFansOrFollows fansOrFollow = fansOrFollows.get(i);

            //获得字符串
            String name = fansOrFollow.getName().trim();

            if (!TextUtils.isEmpty(name)) {

                //返回小写拼音
                String pinyin = returnPinyin(name);
                String label = pinyin.substring(0, 1);

                //判断是否hold住相同字母开头的数据
                if (!holdIndexs.contains(label)) {

                    UserFriends userFriends = new UserFriends();
                    userFriends.setShowLabel(label.matches("[a-zA-Z_]+") ? label : "#");
                    userFriends.setShowViewType(UserSelectFriendsAdapter.INDEX_TYPE);

                    userFriendses.add(userFriends);

                    //加入hold
                    holdIndexs.add(label);
                }

                UserFriends userFriends = new UserFriends();
                userFriends.setShowLabel(pinyin);
                userFriends.setName(fansOrFollow.getName());
                userFriends.setShowViewType(UserSelectFriendsAdapter.USER_TYPE);
                userFriends.setPortrait(fansOrFollow.getPortrait());

                userFriendses.add(userFriends);
            }
        }

        holdIndexs.clear();

        Log.e(TAG, "User Friends Size: ------->" + userFriendses.size());
        //自然排序
        Collections.sort(userFriendses);

        adapter.addItems(userFriendses);
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
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            } else {
                sb.append(tempC);
            }
        }

        Log.e(TAG, "returnPinyin: ---->" + sb.toString());

        return sb.toString().toUpperCase();
    }


    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public void onIndexTouchMove(char indexLetter) {
        Log.e(TAG, "onIndexTouchMove: ------>" + indexLetter);

        ArrayList<UserFriends> userFriends = this.userFriendses;
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
}
