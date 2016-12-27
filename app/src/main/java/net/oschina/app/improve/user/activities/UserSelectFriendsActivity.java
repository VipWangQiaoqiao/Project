package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

public class UserSelectFriendsActivity extends BaseBackActivity {

    private static final String TAG = "UserSelectFriendsActivity";
    private PageBean<UserFansOrFollows> mPageBean;

    @Bind(R.id.searcher_friends)
    SearchView mSearchView;
    @Bind(R.id.bt_cancel)
    Button mBtCancel;
    @Bind(R.id.recycler_friends_icon)
    RecyclerView mRecyclerFriendsIcon;
    @Bind(R.id.recycler_friends)
    RecyclerView mRecyclerFriends;
    @Bind(R.id.tv_index_show)
    TextView mTvIndexShow;

    @Bind(R.id.lay_index_container)
    IndexView mLayIndexContainer;

    @Bind(R.id.lay_error)
    EmptyLayout mEmptyLayout;
    private UserSelectFriendsAdapter adapter;


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

                    //  Log.e(TAG, "updateView: ----->" + fansOrFollows.size() + "  ");

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

        List<UserFriends> userFriendses = new ArrayList<>();

        for (int i = 0; i < fansOrFollows.size(); i++) {

            UserFansOrFollows userFansOrFollows = fansOrFollows.get(i);

            UserFriends userFriends = new UserFriends();

            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
            String name = userFansOrFollows.getName().trim();
            if (!TextUtils.isEmpty(name)) {
                char[] charArray = name.toLowerCase().toCharArray();
                for (char c : charArray) {
                    String tempC = Character.toString(c);
                    if (tempC.matches("[\u4E00-\u9FA5]+")) {
                        try {
                            String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                            // Log.e(TAG, "updateView: ---->" + temp[0] + " " + "  tempC=" + tempC);
                        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                            badHanyuPinyinOutputFormatCombination.printStackTrace();
                        }
                    } else {
                        // Log.e(TAG, "updateView: ---->" + tempC);
                    }
                }
            }

            userFriends.setCheck(false);
            userFriends.setShowViewType(UserSelectFriendsAdapter.USER_TYPE);
            userFriends.setName(userFansOrFollows.getName());
            userFriends.setPortrait(userFansOrFollows.getPortrait());

            userFriendses.add(userFriends);
        }

        //  Log.e(TAG, "User Friends Size: ------->" + userFriendses.size());
        Collections.sort(userFriendses);

        adapter.addItems(userFriendses);
    }


}
